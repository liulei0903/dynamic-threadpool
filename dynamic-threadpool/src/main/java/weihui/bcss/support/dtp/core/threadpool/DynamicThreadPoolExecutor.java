package weihui.bcss.support.dtp.core.threadpool;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import weihui.bcss.support.dtp.core.config.ThreadPoolConfigCenter;
import weihui.bcss.support.dtp.core.config.model.ThreadPoolConfig;
import weihui.bcss.support.dtp.core.enums.QueueTypeEnum;
import weihui.bcss.support.dtp.core.enums.RejectedExecutionHandlerEnum;
import weihui.bcss.support.dtp.core.monitor.AbstractMonitorServiceBase;
import weihui.bcss.support.dtp.core.monitor.MonitorService;
import weihui.bcss.support.dtp.core.monitor.transaction.Transaction;
import weihui.bcss.support.dtp.core.monitor.transaction.TransactionStatisticsGroup;
import weihui.bcss.support.dtp.core.queue.ResizableCapacityLinkedBlockIngQueue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 动态线程池
 *
 * @Description
 * @Author liulei
 * @Date 2021年5月24日18:12:10
 **/
public class DynamicThreadPoolExecutor extends ThreadPoolExecutor {

    private static final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolExecutor.class);

    /**
     * 线程池名称
     */
    private String threadPoolName;

    /**
     * 默认任务名称
     */
    private String defaultTaskName = "defaultTask";

    /**
     * 拒绝任务数
     */
    private AtomicLong rejectCount = new AtomicLong(0);

    /**
     * 配置中心
     */
    private ThreadPoolConfigCenter configCenter;

    /**
     * 线程池配置
     */
    private ThreadPoolConfig threadPoolConfig;

    private MonitorService runningStateMonitor;


    /**
     * 记录 Runnable/Callable 的Class全路径 与 taskName的映射关系
     * 问：为什么不能用 ThreadLocal 来存 Runnable/Callable 与 taskName 的映射关系？
     * 答：因为 execute 与 beforeExecute 不在一个线程中, execute 是业务线程， beforeExecute 是线程池中的线程 ， ThreadLocal会丢失上下文
     */
    private Map<String, String> runnableNameMap = new ConcurrentHashMap<String, String>();


    public DynamicThreadPoolExecutor(ThreadPoolConfig threadPoolConfig, ThreadPoolConfigCenter configCenter, AbstractMonitorServiceBase runningStateMonitor) {
        super(threadPoolConfig.getCorePoolSize(),
                threadPoolConfig.getMaximumPoolSize(),
                threadPoolConfig.getKeepAliveTime(),
                threadPoolConfig.getUnit(),
                getBlockingQueue(threadPoolConfig),
                getThreadFactory(threadPoolConfig),
                getRejectedExecutionHandler(threadPoolConfig));

        logger.info("DynamicThreadPoolExecutor ThreadPoolConfig is {} ", threadPoolConfig);

        this.configCenter = configCenter;
        this.threadPoolConfig = threadPoolConfig;
        this.threadPoolName = threadPoolConfig.getThreadPoolName();
        this.runningStateMonitor = runningStateMonitor;
        //1.判断是否要监控运行时状态
        if (this.threadPoolConfig.isMonitorRunningState()) {
            runningStateMonitor.monitorRunningStatus(this);
        }
        //2.添加核心参数变更通知
        addChangeListeners();
        logger.info("DynamicThreadPoolExecutor {} instantiation success", this.threadPoolName);
    }

    /**
     * 向注册中心添加listener监听器
     */
    private void addChangeListeners() {
        // interestedKeys 中的任意个key值变动都会触发通知
        Set<String> interestedKeys = new HashSet<String>();
        interestedKeys.add(this.threadPoolConfig.getCorePoolSizeKey());
        interestedKeys.add(this.threadPoolConfig.getMaximumPoolSizeKey());
        interestedKeys.add(this.threadPoolConfig.getQueueCapacityKey());
        configCenter.addChangeListener(threadPoolConfig.getThreadPoolName(), interestedKeys, changeKeys -> {
                    //注意：MaximumPoolSize 必须在 CorePoolSize 之前更新
                    //最大线程数动态更新
                    if (!StringUtils.isEmpty(changeKeys.get(this.threadPoolConfig.getMaximumPoolSizeKey()))) {
                        String newValue = changeKeys.get(this.threadPoolConfig.getMaximumPoolSizeKey());
                        threadPoolConfig.setMaximumPoolSize(Integer.valueOf(newValue));
                        logger.info("DynamicThreadPool {} maximumPoolSize  change to {}", this.getThreadPoolName(), newValue);
                        this.setMaximumPoolSize(Integer.valueOf(newValue));
                    }

                    //核心线程动态更新
                    if (!StringUtils.isEmpty(changeKeys.get(this.threadPoolConfig.getCorePoolSizeKey()))) {
                        String newValue = changeKeys.get(this.threadPoolConfig.getCorePoolSizeKey());
                        logger.info("DynamicThreadPool {} corePoolSize  change to {}", this.getThreadPoolName(), newValue);
                        threadPoolConfig.setCorePoolSize(Integer.valueOf(newValue));
                        this.setCorePoolSize(Integer.parseInt(newValue));
                    }

                    //队列大小动态更新
                    if (!StringUtils.isEmpty(changeKeys.get(this.threadPoolConfig.getQueueCapacityKey()))) {
                        String newValue = changeKeys.get(this.threadPoolConfig.getQueueCapacityKey());
                        logger.info("DynamicThreadPool {} queueCapacity  change to {}", this.getThreadPoolName(), newValue);
                        if (this.getQueue() instanceof ResizableCapacityLinkedBlockIngQueue) {
                            threadPoolConfig.setQueueCapacity(Integer.valueOf(newValue));
                            ((ResizableCapacityLinkedBlockIngQueue) this.getQueue()).setCapacity(Integer.parseInt(newValue));
                        }
                    }
                }
        );

    }

    @Override
    public void execute(Runnable command) {
        super.execute(new RunnableTypeDecorator(command));
    }

    public void execute(Runnable command, String taskName) {
        super.execute(new RunnableTypeDecorator(taskName, command));
    }

    public Future<?> submit(Runnable task, String taskName) {
        return super.submit(new RunnableTypeDecorator(taskName, task));
    }

    public <T> Future<T> submit(Callable<T> task, String taskName) {
        return super.submit(new CallableTypeDecorator(taskName, task));
    }

    public <T> Future<T> submit(Runnable task, T result, String taskName) {
        return super.submit(new RunnableTypeDecorator(taskName, task), result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(new RunnableTypeDecorator(task));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(new CallableTypeDecorator(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return super.submit(new RunnableTypeDecorator(task), result);
    }

    private ThreadLocal<Transaction> tranLocal = new ThreadLocal<Transaction>();

    /**
     * beforeExecute
     *
     * @param t
     * @param r
     */
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        TransactionStatisticsGroup tsg = TransactionStatisticsGroup.Builder.aTransactionStatisticsGroup().withThreadPoolName(getThreadPoolName()).withTaskType(getTaskTypeByRunnable(r)).build();
        Transaction transaction = runningStateMonitor.newTransaction(tsg);
        tranLocal.set(transaction);
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        try {
            Transaction transaction = tranLocal.get();
            //设置执行结果
            transaction.setStatus(t == null ? true : false);
            //任务结束
            transaction.complete();
            //收集任务
            runningStateMonitor.collectTransaction(transaction);
        } finally {
            //清空 ThreadLocal
            tranLocal.remove();
        }
    }

    /**
     * 是否要监控执行结果
     *
     * @return
     */
    public boolean isMonitorExecuteReuslt() {
        return threadPoolConfig.isMonitorExecuteResult();
    }

    /**
     *
     */
    protected void incrementRejectCount() {
        this.rejectCount.incrementAndGet();
    }

    /**
     * 该非线程安全,在并发场景,可能会导致rejectCount少统计
     * TODO 什么时机该触发 clean?
     */
    public void cleanRejectCount() {
        this.rejectCount.set(0);
    }


    /**
     * 包装一个带记录拒绝次数的 RejectedExecutionHandler
     *
     * @param handler
     * @return
     */
    public static RejectedExecutionHandler warpperExecutionHandler(RejectedExecutionHandler handler) {
        return new RejectedCountDecorateExecutionHandler(handler);
    }

    /**
     * RejectedExecutionHandler 装饰类
     * 作用:统计拒绝次数
     */
    public static class RejectedCountDecorateExecutionHandler implements RejectedExecutionHandler {

        private RejectedExecutionHandler handler;


        public RejectedCountDecorateExecutionHandler(RejectedExecutionHandler handler) {
            this.handler = handler;
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            if (e instanceof DynamicThreadPoolExecutor) {
                //递增拒绝任务数
                ((DynamicThreadPoolExecutor) e).incrementRejectCount();
            }
            handler.rejectedExecution(r, e);
        }
    }

    public String getThreadPoolName() {
        return threadPoolName;
    }

    public AtomicLong getRejectCount() {
        return rejectCount;
    }


    /**
     * @param threadPoolConfig
     * @return
     */
    public static RejectedExecutionHandler getRejectedExecutionHandler(ThreadPoolConfig threadPoolConfig) {
        if (threadPoolConfig.getRejectedExecutionHandler() != null) {
            return warpperExecutionHandler(threadPoolConfig.getRejectedExecutionHandler());
        }
        return warpperExecutionHandler(getRejectedExecutionHandler(threadPoolConfig.getRejectedExecutionType()));
    }

    /**
     * 获取拒绝策略
     *
     * @param rejectedExecutionType
     * @return
     */
    public static RejectedExecutionHandler getRejectedExecutionHandler(String rejectedExecutionType) {
        if (RejectedExecutionHandlerEnum.CALLER_RUNS_POLICY.getType().equals(rejectedExecutionType)) {
            return new ThreadPoolExecutor.CallerRunsPolicy();
        }
        if (RejectedExecutionHandlerEnum.DISCARD_OLDEST_POLICY.getType().equals(rejectedExecutionType)) {
            return new ThreadPoolExecutor.DiscardOldestPolicy();
        }
        if (RejectedExecutionHandlerEnum.DISCARD_POLICY.getType().equals(rejectedExecutionType)) {
            return new ThreadPoolExecutor.DiscardPolicy();
        }
        return new ThreadPoolExecutor.AbortPolicy();
    }


    /**
     * @param threadPoolConfig
     * @return
     */
    public static BlockingQueue getBlockingQueue(ThreadPoolConfig threadPoolConfig) {
        if (threadPoolConfig.getQueue() != null) {
            return threadPoolConfig.getQueue();
        }
        return getBlockingQueue(threadPoolConfig.getQueueType(), threadPoolConfig.getQueueCapacity(), threadPoolConfig.isFair());
    }

    /**
     * 获取阻塞队列
     *
     * @param queueType
     * @param queueCapacity
     * @param fair
     * @return
     */
    public static BlockingQueue getBlockingQueue(String queueType, int queueCapacity, boolean fair) {
        if (!QueueTypeEnum.exists(queueType)) {
            throw new RuntimeException("队列不存在 " + queueType);
        }
        if (QueueTypeEnum.ARRAY_BLOCKING_QUEUE.getType().equals(queueType)) {
            return new ArrayBlockingQueue(queueCapacity);
        }
        if (QueueTypeEnum.SYNCHRONOUS_QUEUE.getType().equals(queueType)) {
            return new SynchronousQueue(fair);
        }
        if (QueueTypeEnum.PRIORITY_BLOCKING_QUEUE.getType().equals(queueType)) {
            return new PriorityBlockingQueue(queueCapacity);
        }
        if (QueueTypeEnum.DELAY_QUEUE.getType().equals(queueType)) {
            return new DelayQueue();
        }
        if (QueueTypeEnum.LINKED_BLOCKING_DEQUE.getType().equals(queueType)) {
            return new LinkedBlockingDeque(queueCapacity);
        }
        if (QueueTypeEnum.LINKED_TRANSFER_DEQUE.getType().equals(queueType)) {
            return new LinkedTransferQueue();
        }
        //可弹性伸缩的有界队列
        return new ResizableCapacityLinkedBlockIngQueue(queueCapacity);
    }

    /**
     * @param threadPoolConfig
     * @return
     */
    public static ThreadFactory getThreadFactory(ThreadPoolConfig threadPoolConfig) {
        if (threadPoolConfig.getThreadFactory() != null) {
            return threadPoolConfig.getThreadFactory();
        }
        return new DefaultThreadFactory(threadPoolConfig.getThreadPoolName());
    }

    private String getTaskTypeByRunnable(Runnable r) {
        return ((TaskType) r).getType();
    }


    /**
     * 默认的ThreadFactory
     * 创建的Thread Name如下： "testThreadPoolExecutor-1-thread-4"
     * "testThreadPoolExecutor" = ${threadPoolName}
     * "1" = ${poolNumber}
     * "thread" = 固定值
     * "4" = ${threadNumber}
     */
    public static class DefaultThreadFactory implements ThreadFactory {
        /**
         * 线程池递增编号
         */
        private static AtomicInteger poolNumber = new AtomicInteger(1);

        private final ThreadGroup group;

        /**
         * 线程递增编号
         */
        private AtomicInteger threadNumber = new AtomicInteger(1);

        private final String namePrefix;

        public DefaultThreadFactory(String threadPoolName) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = threadPoolName + "-" + poolNumber.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    public int getQueueCapacity() {
        return threadPoolConfig.getQueueCapacity();
    }
}
