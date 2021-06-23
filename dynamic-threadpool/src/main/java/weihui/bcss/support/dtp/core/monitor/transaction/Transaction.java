package weihui.bcss.support.dtp.core.monitor.transaction;

import java.util.Objects;

/**
 * 线程池中的一次 Runnable、Callable 对应一个 Transaction
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
     * 执行耗时
     */
    private int elapsed = 0;

    private long start = 0;


    public Transaction(String threadPoolName, String taskType) {
        this.threadPoolName = threadPoolName;
        this.taskType = taskType;
        start = System.currentTimeMillis();
    }


    /**
     * 任务完成
     */
    public void complete() {
        // 计算调用耗时
        elapsed = Long.valueOf(System.currentTimeMillis() - this.start).intValue();
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

    public int getElapsed() {
        return elapsed;
    }

    public void setElapsed(int elapsed) {
        this.elapsed = elapsed;
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
        return status == that.status && elapsed == that.elapsed && threadPoolName.equals(that.threadPoolName) && taskType.equals(that.taskType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(threadPoolName, taskType, status, elapsed);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "threadPoolName='" + threadPoolName + '\'' +
                ", taskType='" + taskType + '\'' +
                ", status=" + status +
                ", elapsed=" + elapsed +
                '}';
    }


}
