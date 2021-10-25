package weihui.bcss.support.dtp.core.monitor.transaction;

import java.util.Objects;

/**
 * 线程池中的一次 Runnable、Callable 对应一个 Transaction
 *
 * @Description
 * @Author liulei
 * @Date 2021/6/7 18:38
 **/
public class Transaction {

    /**
     * 线程池名称
     */
    private String threadPoolName;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 状态
     */
    private boolean status = false;

    /**
     * 线程池分配线程开始执行到结束的执行耗时,不包括在队列中等待的时间. 单位:毫秒
     */
    private int executedElapsed = 0;

    /**
     * 添加到线程池到执行完成总耗时,包括在队列中等待的时间. 单位:毫秒
     */
    private int finishedElapsed = 0;

    /**
     * 创建时间
     */
    private long created = 0;

    /**
     * 开始执行时间
     */
    private long start = 0;


    public Transaction(String threadPoolName, String taskType, long created) {
        this.threadPoolName = threadPoolName;
        this.taskType = taskType;
        this.created = created;
        start = System.currentTimeMillis();
    }


    /**
     * 任务完成
     */
    public void complete() {
        // 计算调用耗时
        executedElapsed = Long.valueOf(System.currentTimeMillis() - this.start).intValue();
        // 计算调用耗时
        finishedElapsed = Long.valueOf(System.currentTimeMillis() - this.created).intValue();
    }

    public String getThreadPoolName() {
        return threadPoolName;
    }

    public void setThreadPoolName(String threadPoolName) {
        this.threadPoolName = threadPoolName;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


    public int getExecutedElapsed() {
        return executedElapsed;
    }

    public int getFinishedElapsed() {
        return finishedElapsed;
    }

    public long getCreated() {
        return created;
    }

    public long getStart() {
        return start;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Transaction that = (Transaction) o;
        return status == that.status && created == that.created &&  start == that.start && threadPoolName.equals(that.threadPoolName) && taskType.equals(that.taskType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(threadPoolName, taskType, status, executedElapsed, finishedElapsed, created, start);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "threadPoolName='" + threadPoolName + '\'' +
                ", taskType='" + taskType + '\'' +
                ", status=" + status +
                ", executedElapsed=" + executedElapsed +
                ", finishedElapsed=" + finishedElapsed +
                ", created=" + created +
                ", start=" + start +
                '}';
    }
}
