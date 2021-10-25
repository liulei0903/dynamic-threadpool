package weihui.bcss.support.dtp.core.monitor;

import weihui.bcss.support.dtp.core.monitor.transaction.TransactionStatisticsGroup;
import weihui.bcss.support.dtp.core.monitor.transaction.Transaction;
import weihui.bcss.support.dtp.core.threadpool.DynamicThreadPoolExecutor;

import java.util.concurrent.ExecutorService;

/**
 * 动态线程运行状态监控
 *
 * @Description
 * @Author liulei
 * @Date 2021/5/28 15:22
 **/
public interface MonitorService {

    /**
     * 监控线程池运行状态
     * @param executor
     * @return
     */
    boolean monitorRunningStatus(DynamicThreadPoolExecutor executor);


    /**
     * 创建一个线程池执行任务的监控载体
     * @param transactionStatisticsGroup
     * @return
     */
    public Transaction newTransaction(TransactionStatisticsGroup transactionStatisticsGroup, Runnable r);

    /**
     * 收集执行结果
     * @param t
     */
    void collectTransaction(Transaction t);

}
