package weihui.bcss.support.dtp.core.monitor;

/**
 * 监控指标
 * @Description
 * @Author liulei
 * @Date 2021/5/28 15:22
 */
public interface MonitorMetrics {

    /**
     * meter tags
     */
    public final String TAGS_THREAD_POOL = "thread.pool.name";

    public final String TAGS_TASK_TYPE = "thread.task.type";

    public final String TAGS_TYPE = "monitor.type";

    public final String TAGS_VALUE_THREAD_POOL_RUNNING_STATUS = "TPRS";

    public final String TAGS_VALUE_THREAD_POOL_TRANSACTION_STATUS = "TPTS";

    /**
     * 设置的核心线程池大小
     * ThreadPoolExecutor::getCorePoolSize
     */
    public final String METRICS_CORE_SIZE = "thread.pool.static.coreSize";

    /**
     * 设置的最大线程数
     * ThreadPoolExecutor::getMaximumPoolSize
     */
    public final String METRICS_MAX_SIZE = "thread.pool.static.maxSize";


    /**
     * 历史最高线程数
     * ThreadPoolExecutor::getLargestPoolSize
     */
    public final String METRICS_LARGEST_SIZE = "thread.pool.dynamic.largestCount";


    /**
     * 当前活动线程数
     * ThreadPoolExecutor::getActiveCount
     */
    public final String METRICS_ACTIVE_SIZE = "thread.pool.dynamic.activeCount";

    /**
     * 当前存在的线程数 (当前活动线程数 + 未回收的线程数 )
     */
    public final String METRICS_THREAD_COUNT = "thread.pool.dynamic.threadCount";

    /**
     * waitTaskCount 等待执行任务数(工作队列堆积任务数)
     * ThreadPoolExecutor::getPoolSize
     */
    public final String METRICS_WAIT_TASK_COUNT = "thread.pool.dynamic.waitTaskCount";

    /**
     * rejectCount 拒绝任务数
     * DynamicThreadPoolExecutor::getRejectCount().longValue()
     */
    public final String METRICS_REJECT_COUNT = "thread.pool.dynamic.rejeuctCount";

    /**
     * completedTaskCount 已执行任务数
     */
    public final String METRICS_COMPLETED_TASK_COUNT = "thread.pool.dynamic.completedTaskCount";


    /**
     * taskCount 已执行任务数+等待执行任务数
     */
    public final String METRICS_TASK_COUNT = "thread.pool.dynamic.taskCount";

    /**
     * taskCount 队列容量
     */
    public final String METRICS_QUEUE_CAPACITY = "thread.pool.dynamic.queue.capacity";

    /**
     * 成功任务总数
     */
    public final String TRANSACTION_SUCCESS_COUNT = "transaction.success.count";

    /**
     * 异常任务总数
     */
    public final String TRANSACTION_FAILURE_COUNT = "transaction.failure.count";

    /**
     * 任务平均耗时
     */
    public final String TRANSACTION_ELAPSED_AVG = "transaction.elapsed.avg";

    /**
     * 任务最小号耗时
     */
    public final String TRANSACTION_ELAPSED_MIN = "transaction.elapsed.min";

    /**
     * 任务最大耗时
     */
    public final String TRANSACTION_ELAPSED_MAX = "transaction.elapsed.max";


}
