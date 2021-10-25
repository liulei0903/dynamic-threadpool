package weihui.bcss.support.dtp.core.config.model;


import weihui.bcss.support.dtp.core.enums.QueueTypeEnum;
import weihui.bcss.support.dtp.core.enums.RejectedExecutionHandlerEnum;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 线程池配置
 *
 * @Description
 * @Author liulei
 * @Date 2021/5/25 11:02
 */
public class ThreadPoolConfig {

    /**
     * 配置前缀
     */
    public final static String KEY_PREFIX_THREADPOOL = "threadpools.";

    /**
     * 线程池名称
     */
    private String threadPoolName = null;

    /**
     * 核心线程数
     */
    private int corePoolSize = 1;

    /**
     * 最大线程数, 默认值为CPU核心数量
     */
    private int maximumPoolSize = Runtime.getRuntime().availableProcessors();

    /**
     * 队列最大数量
     */
    private int queueCapacity = 1000;

    /**
     * 队列类型
     *
     * @see QueueTypeEnum
     */
    private String queueType = QueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE.getType();

    /**
     * 队列中是否允许有重复的元素
     */
    private boolean queueAllowDuplicate = true;

    /**
     * 队列重复后是否抛出异常,抛出异常保证了原语义不被破坏 , 只有queueAllowDuplicate=false,配置该值才会生效,否则没作用;
     */
    private boolean queueDuplicatedThrows = true;

    /**
     * SynchronousQueue 是否公平策略
     */
    private boolean fair;

    /**
     * 拒绝策略
     *
     * @see RejectedExecutionHandlerEnum
     */
    private String rejectedExecutionType = RejectedExecutionHandlerEnum.ABORT_POLICY.getType();

    /**
     * 空闲线程存活时间
     */
    private long keepAliveTime;

    /**
     * 空闲线程存活时间单位
     */
    private TimeUnit unit = TimeUnit.SECONDS;


    /**
     * 是否监控线程池运行状态
     */
    private boolean monitorRunningState = true;


    /**
     * 是否监控线程池执行结果
     */
    private boolean monitorExecuteResult = false;


    /**
     * queue 实现
     */
    private BlockingQueue queue;

    /**
     * ThreadFactory 实现
     */
    private ThreadFactory threadFactory;


    /**
     *
     */
    private RejectedExecutionHandler rejectedExecutionHandler;


    /**
     * 配置中心的keys
     */
    private String corePoolSizeKey = null;

    private String maximumPoolSizeKey = null;

    private String queueCapacityKey = null;

    private String queueTypeKey = null;

    private String rejectedExecutionTypeKey = null;

    private String unitKey = null;

    private String keepAliveTimeKey = null;

    private String monitorRunningStateKey = null;

    private String monitorExecuteResultKey = null;


    public ThreadPoolConfig() {

    }

    public ThreadPoolConfig(String threadPoolName) {
        this.threadPoolName = threadPoolName;
    }


    public String getThreadPoolName() {
        return threadPoolName;
    }

    public void setThreadPoolName(String threadPoolName) {
        this.threadPoolName = threadPoolName;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public String getQueueType() {
        return queueType;
    }

    public void setQueueType(String queueType) {
        this.queueType = queueType;
    }

    public boolean isFair() {
        return fair;
    }

    public void setFair(boolean fair) {
        this.fair = fair;
    }

    public String getRejectedExecutionType() {
        return rejectedExecutionType;
    }

    public void setRejectedExecutionType(String rejectedExecutionType) {
        this.rejectedExecutionType = rejectedExecutionType;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    public boolean isMonitorRunningState() {
        return monitorRunningState;
    }

    public void setMonitorRunningState(boolean monitorRunningState) {
        this.monitorRunningState = monitorRunningState;
    }

    public boolean isMonitorExecuteResult() {
        return monitorExecuteResult;
    }

    public void setMonitorExecuteResult(boolean monitorExecuteResult) {
        this.monitorExecuteResult = monitorExecuteResult;
    }

    public String getCorePoolSizeKey() {
        if (corePoolSizeKey == null) {
            corePoolSizeKey = KEY_PREFIX_THREADPOOL + threadPoolName + ".corePoolSize";
        }
        return corePoolSizeKey;
    }

    public void setCorePoolSizeKey(String corePoolSizeKey) {
        this.corePoolSizeKey = corePoolSizeKey;
    }

    public String getMaximumPoolSizeKey() {
        if (maximumPoolSizeKey == null) {
            maximumPoolSizeKey = KEY_PREFIX_THREADPOOL + threadPoolName + ".maximumPoolSize";
        }
        return maximumPoolSizeKey;
    }

    public void setMaximumPoolSizeKey(String maximumPoolSizeKey) {
        this.maximumPoolSizeKey = maximumPoolSizeKey;
    }

    public String getQueueCapacityKey() {
        if (queueCapacityKey == null) {
            queueCapacityKey = KEY_PREFIX_THREADPOOL + threadPoolName + ".queueCapacity";
        }
        return queueCapacityKey;
    }

    public void setQueueCapacityKey(String queueCapacityKey) {
        this.queueCapacityKey = queueCapacityKey;
    }

    public String getQueueTypeKey() {
        if (queueTypeKey == null) {
            queueTypeKey = KEY_PREFIX_THREADPOOL + threadPoolName + ".queueType";
        }
        return queueTypeKey;
    }

    public void setQueueTypeKey(String queueTypeKey) {
        this.queueTypeKey = queueTypeKey;
    }

    public String getRejectedExecutionTypeKey() {
        if (rejectedExecutionTypeKey == null) {
            rejectedExecutionTypeKey = KEY_PREFIX_THREADPOOL + threadPoolName + ".rejectedExecutionType";
        }
        return rejectedExecutionTypeKey;
    }

    public void setRejectedExecutionTypeKey(String rejectedExecutionTypeKey) {
        this.rejectedExecutionTypeKey = rejectedExecutionTypeKey;
    }

    public String getUnitKey() {
        if (unitKey == null) {
            unitKey = KEY_PREFIX_THREADPOOL + threadPoolName + ".unit";
        }
        return unitKey;
    }

    public void setUnitKey(String unitKey) {
        this.unitKey = unitKey;
    }

    public String getKeepAliveTimeKey() {
        if (keepAliveTimeKey == null) {
            keepAliveTimeKey = KEY_PREFIX_THREADPOOL + threadPoolName + ".keepAliveTime";
        }
        return keepAliveTimeKey;
    }

    public void setKeepAliveTimeKey(String keepAliveTimeKey) {
        this.keepAliveTimeKey = keepAliveTimeKey;
    }

    public String getMonitorRunningStateKey() {
        if (monitorRunningStateKey == null) {
            monitorRunningStateKey = KEY_PREFIX_THREADPOOL + threadPoolName + ".monitorRunningState";
        }
        return monitorRunningStateKey;
    }

    public void setMonitorRunningStateKey(String monitorRunningStateKey) {
        this.monitorRunningStateKey = monitorRunningStateKey;
    }

    public String getMonitorExecuteResultKey() {
        if (monitorExecuteResultKey == null) {
            monitorExecuteResultKey = KEY_PREFIX_THREADPOOL + threadPoolName + ".monitorExecuteResult";
        }
        return monitorExecuteResultKey;
    }

    public void setMonitorExecuteResultKey(String monitorExecuteResultKey) {
        this.monitorExecuteResultKey = monitorExecuteResultKey;
    }

    public BlockingQueue getQueue() {
        return queue;
    }

    public void setQueue(BlockingQueue queue) {
        this.queue = queue;
    }

    public ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public void setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return rejectedExecutionHandler;
    }

    public void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
    }

    public boolean isQueueAllowDuplicate() {
        return queueAllowDuplicate;
    }

    public void setQueueAllowDuplicate(boolean queueAllowDuplicate) {
        this.queueAllowDuplicate = queueAllowDuplicate;
    }

    public boolean isQueueDuplicatedThrows() {
        return queueDuplicatedThrows;
    }

    public void setQueueDuplicatedThrows(boolean queueDuplicatedThrows) {
        this.queueDuplicatedThrows = queueDuplicatedThrows;
    }

    @Override
    public String toString() {
        return "ThreadPoolConfig{" +
                "threadPoolName='" + threadPoolName + '\'' +
                ", corePoolSize=" + corePoolSize +
                ", maximumPoolSize=" + maximumPoolSize +
                ", queueCapacity=" + queueCapacity +
                ", queueType='" + queueType + '\'' +
                ", queueAllowDuplicate=" + queueAllowDuplicate +
                ", queueDuplicatedThrows=" + queueDuplicatedThrows +
                ", fair=" + fair +
                ", rejectedExecutionType='" + rejectedExecutionType + '\'' +
                ", keepAliveTime=" + keepAliveTime +
                ", unit=" + unit +
                ", monitorRunningState=" + monitorRunningState +
                ", monitorExecuteResult=" + monitorExecuteResult +
                ", queue=" + queue +
                ", threadFactory=" + threadFactory +
                ", rejectedExecutionHandler=" + rejectedExecutionHandler +
                ", corePoolSizeKey='" + corePoolSizeKey + '\'' +
                ", maximumPoolSizeKey='" + maximumPoolSizeKey + '\'' +
                ", queueCapacityKey='" + queueCapacityKey + '\'' +
                ", queueTypeKey='" + queueTypeKey + '\'' +
                ", rejectedExecutionTypeKey='" + rejectedExecutionTypeKey + '\'' +
                ", unitKey='" + unitKey + '\'' +
                ", keepAliveTimeKey='" + keepAliveTimeKey + '\'' +
                ", monitorRunningStateKey='" + monitorRunningStateKey + '\'' +
                ", monitorExecuteResultKey='" + monitorExecuteResultKey + '\'' +
                '}';
    }
}
