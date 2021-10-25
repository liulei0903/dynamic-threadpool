package weihui.bcss.support.dtp.core.monitor;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weihui.bcss.support.dtp.core.monitor.report.strategy.ReportStrategy;
import weihui.bcss.support.dtp.core.monitor.transaction.Transaction;
import weihui.bcss.support.dtp.core.monitor.transaction.TransactionStatisticsGroup;
import weihui.bcss.support.dtp.core.monitor.transaction.TransactionStatisticsValue;
import weihui.bcss.support.dtp.core.threadpool.DynamicThreadPoolExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 基于指标(metrics)的默认监控服务实现类
 *
 * @Description
 * @Author liulei
 * @Date 2021/6/9 14:12
 **/
public class DefaultMonitorServiceImpl extends AbstractMonitorServiceBase {

    private final Logger logger = LoggerFactory.getLogger(DefaultMonitorServiceImpl.class);

    /**
     * 获取 ReportStrategy 的策略实现
     * API与SPI分离,使用组合的方式关联
     */
    private ReportStrategy reportStrategy = null;

    /**
     * meter注册
     */
    private MeterRegistry registry = null;


    @Override
    public void initialize() {
        registry = reportStrategy.getMeterRegistry();
        //注册发布的监听事件，发布后将transaction的监控统计清空
        reportStrategy.regPublishListener(() -> {
                    cleanTransactionStatistics();
                    cleanRunningStatus();
                }
        );
    }

    @Override
    public void doMonitorRunningStatus(DynamicThreadPoolExecutor executor) {
        regThreadPoolStatusMeter(executor);
    }

    @Override
    public void doCollectTransaction(Transaction t, AtomicReference<TransactionStatisticsValue> tsv) {
        regTransactionMeter(t, tsv);
    }

    /**
     * 注册线程池运行状态的meter
     *
     * @param executor
     */
    private void regThreadPoolStatusMeter(DynamicThreadPoolExecutor executor) {
        //build tag
        List<Tag> threadPollTags = buildThreadPoolStatusTags(executor.getThreadPoolName());
        //set gavge type metrics
        //设置的核心线程池大小
        registry.gauge(METRICS_CORE_SIZE, threadPollTags, executor, ThreadPoolExecutor::getCorePoolSize);
        //历史最高线程数
        registry.gauge(METRICS_LARGEST_SIZE, threadPollTags, executor, ThreadPoolExecutor::getLargestPoolSize);
        //设置的最大线程数
        registry.gauge(METRICS_MAX_SIZE, threadPollTags, executor, ThreadPoolExecutor::getMaximumPoolSize);
        //当前活动线程数
        registry.gauge(METRICS_ACTIVE_SIZE, threadPollTags, executor, ThreadPoolExecutor::getActiveCount);
        //当前存在的线程数 (当前活动线程数 + 未回收的线程数 )
        registry.gauge(METRICS_THREAD_COUNT, threadPollTags, executor, ThreadPoolExecutor::getPoolSize);
        //等待执行任务数(队列堆积数)
        registry.gauge(METRICS_WAIT_TASK_COUNT, threadPollTags, executor, e -> e.getQueue().size());
        //拒绝任务数
        registry.gauge(METRICS_REJECT_COUNT, threadPollTags, executor, e -> e.getRejectCount().longValue());
        //已执行任务数
        registry.gauge(METRICS_COMPLETED_TASK_COUNT, threadPollTags, executor, ThreadPoolExecutor::getCompletedTaskCount);
        //已执行任务数+等待执行任务数
        registry.gauge(METRICS_TASK_COUNT, threadPollTags, executor, ThreadPoolExecutor::getTaskCount);
        //队列容量
        registry.gauge(METRICS_QUEUE_CAPACITY, threadPollTags, executor, e -> e.getQueueCapacity());
    }


    /**
     * 注册任务执行情况的meter
     *
     * @param t
     * @param reference
     */
    private void regTransactionMeter(Transaction t, AtomicReference<TransactionStatisticsValue> reference) {
        //build tag
        List<Tag> transactionTags = buildTransactionTags(t);

        registry.gauge(TRANSACTION_SUCCESS_COUNT, transactionTags, reference, r -> {
            return r.get().getSuccess();
        });

        registry.gauge(TRANSACTION_FAILURE_COUNT, transactionTags, reference, r -> {
            return r.get().getFailure();
        });
        registry.gauge(TRANSACTION_ELAPSED_AVG, transactionTags, reference, r -> {
            return r.get().getElapsedAvg();
        });
        registry.gauge(TRANSACTION_ELAPSED_MIN, transactionTags, reference, r -> {
            return r.get().getElapsedMin();
        });
        registry.gauge(TRANSACTION_ELAPSED_MAX, transactionTags, reference, r -> {
            return r.get().getElapsedMax();
        });
        registry.gauge(TRANSACTION_FINISHED_ELAPSED_AVG, transactionTags, reference, r -> {
            return r.get().getFinishedElapsedAvg();
        });
        registry.gauge(TRANSACTION_FINISHED_ELAPSED_MIN, transactionTags, reference, r -> {
            return r.get().getFinishedElapsedMin();
        });
        registry.gauge(TRANSACTION_FINISHED_ELAPSED_MAX, transactionTags, reference, r -> {
            return r.get().getFinishedElapsedMax();
        });
    }

    private List<Tag> buildThreadPoolStatusTags(String threadPoolName) {
        List<Tag> tags = new ArrayList<Tag>(2);
        //指标类型-线程池运行状态
        tags.add(Tag.of(TAGS_TYPE, TAGS_VALUE_THREAD_POOL_RUNNING_STATUS));
        tags.add(Tag.of(TAGS_THREAD_POOL, threadPoolName));
        return tags;
    }

    private List<Tag> buildTransactionTags(Transaction t) {
        List<Tag> tags = new ArrayList<Tag>(3);
        //指标类型-线程池执行统计
        tags.add(Tag.of(TAGS_TYPE, TAGS_VALUE_THREAD_POOL_TRANSACTION_STATUS));
        tags.add(Tag.of(TAGS_THREAD_POOL, t.getThreadPoolName()));
        tags.add(Tag.of(TAGS_TASK_TYPE, t.getTaskType()));
        return tags;
    }

    public void setReportStrategy(ReportStrategy reportStrategy) {
        this.reportStrategy = reportStrategy;
    }


}
