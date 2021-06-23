package weihui.bcss.support.dtp.core.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import weihui.bcss.support.dtp.core.monitor.transaction.TransactionStatisticsGroup;
import weihui.bcss.support.dtp.core.monitor.transaction.TransactionStatisticsValue;
import weihui.bcss.support.dtp.core.monitor.transaction.Transaction;
import weihui.bcss.support.dtp.core.threadpool.DynamicThreadPoolExecutor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 动态线程运行状态监控
 *
 * @Description
 * @Author liulei
 * @Date 2021/5/28 15:22
 **/
public abstract class AbstractMonitorServiceBase implements MonitorService, MonitorMetrics, InitializingBean, DisposableBean {

    private final Logger logger = LoggerFactory.getLogger(AbstractMonitorServiceBase.class);

    /**
     * 是否启动成功
     */
    private boolean startOpSuccess = false;

    private final ConcurrentMap<String, DynamicThreadPoolExecutor> executorMap = new ConcurrentHashMap<String, DynamicThreadPoolExecutor>();

    private final ConcurrentMap<TransactionStatisticsGroup, AtomicReference<TransactionStatisticsValue>> transactionStatisticsMap = new ConcurrentHashMap<TransactionStatisticsGroup, AtomicReference<TransactionStatisticsValue>>();

    /**
     * 初始化
     */
    public abstract void initialize();

    /**
     * doMonitorRunningStatus
     * @param executor
     */
    protected abstract void doMonitorRunningStatus(DynamicThreadPoolExecutor executor);

    /**
     * doCollectTransaction
     * @param t
     * @param tsv
     */
    protected abstract void doCollectTransaction(Transaction t, AtomicReference<TransactionStatisticsValue> tsv);


    @Override
    public boolean monitorRunningStatus(DynamicThreadPoolExecutor executor) {
        if (!startOpSuccess) {
            logger.warn("Start Fail,Not allow add monitor to {}", executor.getThreadPoolName());
            return false;
        }
        //已经添加过监控直接返回
        if (executorMap.containsKey(executor.getThreadPoolName())) {
            return true;
        }
        executorMap.putIfAbsent(executor.getThreadPoolName(), executor);
        //实现类去处理具体的逻辑
        doMonitorRunningStatus(executor);
        return true;
    }


    /**
     * 创建新的任务执行记录
     *
     * @param transactionStatisticsGroup
     * @return
     */
    @Override
    public Transaction newTransaction(TransactionStatisticsGroup transactionStatisticsGroup) {
        return new Transaction(transactionStatisticsGroup.getThreadPoolName(), transactionStatisticsGroup.getTaskType());
    }

    /**
     * 收集任务执行情况
     *
     * @param t
     */
    @Override
    public void collectTransaction(Transaction t) {
        //统计分组
        TransactionStatisticsGroup statistics = new TransactionStatisticsGroup(t.getThreadPoolName(), t.getTaskType());
        //使用AtomicReference的原因是防止并发线程安全问题,那success使用AtomicInteger不能解决原子递增吗？是可以解决原子递增，但无法解决 elapsedAvg 这类非递增的更新，所有这里使用 AtomicReference
        AtomicReference<TransactionStatisticsValue> tsv = transactionStatisticsMap.get(statistics.getThreadPoolName() + ":" + statistics.getTaskType());
        if (tsv == null) {
            transactionStatisticsMap.putIfAbsent(statistics, new AtomicReference<TransactionStatisticsValue>());
            // 这里没加同步锁的原因是执行多次 registry.gauge是没有影响的,因为使用的putIfAbsent方法,所以并发get返回的也是同一个AtomicReference对象,
            tsv = transactionStatisticsMap.get(statistics);
            doCollectTransaction(t, tsv);
        }
        // 读写统计变量
        int success = t.isStatus() ? 1 : 0;
        int failure = t.isStatus() ? 0 : 1;
        // 本次耗时
        int elapsed = t.getElapsed();
        // CompareAndSet并发加入统计数据
        TransactionStatisticsValue current;
        TransactionStatisticsValue update = new TransactionStatisticsValue();
        do {
            current = tsv.get();
            if (current == null) {
                //第一次赋值
                update.setSuccess(success);
                update.setFailure(failure);
                update.setElapsed(elapsed);
                update.setElapsedAvg(elapsed);
                update.setElapsedMin(elapsed);
                update.setElapsedMax(elapsed);
            } else {
                //成功总数
                update.setSuccess(current.getSuccess() + success);
                //失败总数
                update.setFailure(current.getFailure() + failure);
                //总耗时
                update.setElapsed(current.getElapsed() + elapsed);
                if ((current.getSuccess() + current.getFailure()) > 0) {
                    //平均耗时
                    update.setElapsedAvg(current.getElapsed() / (current.getSuccess() + current.getFailure()));
                }
                //最小耗时
                update.setElapsedMin(current.getElapsedMin() != 0 && current.getElapsedMin() < elapsed ? current.getElapsedMin() : elapsed);
                //最大耗时
                update.setElapsedMax(current.getElapsedMax() > elapsed ? current.getElapsedMax() : elapsed);
            }
        } while (!tsv.compareAndSet(current, update));
    }

    protected synchronized void cleanTransactionStatistics() {
        transactionStatisticsMap.values().forEach(tsv -> {
            //重置一个新对象, 相当于clean的效果
            tsv.set(new TransactionStatisticsValue());
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            initialize();
            startOpSuccess = true;
        } catch (Exception e) {
            //吃掉异常 , 不影响spring 容器启动
            //TODO 可以定义一个 execptionHandler , 可以自定义异常处理策略
            logger.warn("Initialize Fail", e);
        }
    }

    @Override
    public void destroy() throws Exception {
        executorMap.clear();
        transactionStatisticsMap.clear();
    }

    public ConcurrentMap<TransactionStatisticsGroup, AtomicReference<TransactionStatisticsValue>> getTransactionStatisticsMap() {
        return transactionStatisticsMap;
    }

    public ConcurrentMap<String, DynamicThreadPoolExecutor> getExecutorMap() {
        return executorMap;
    }
}
