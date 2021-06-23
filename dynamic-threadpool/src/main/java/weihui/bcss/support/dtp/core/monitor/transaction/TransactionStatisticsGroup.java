package weihui.bcss.support.dtp.core.monitor.transaction;

import java.io.Serializable;
import java.util.Objects;

/**
 * 执行统计
 * @Description
 * @Author liulei
 * @Date 2021/6/7 18:28
 **/
public class TransactionStatisticsGroup implements Serializable {


    private String threadPoolName;

    private String taskType;

    public TransactionStatisticsGroup(String threadPoolName, String taskType) {
        this.threadPoolName = threadPoolName;
        this.taskType = taskType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TransactionStatisticsGroup that = (TransactionStatisticsGroup) o;
        return Objects.equals(threadPoolName, that.threadPoolName) && Objects.equals(taskType, that.taskType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(threadPoolName, taskType);
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "threadPoolName='" + threadPoolName + '\'' +
                ", taskType='" + taskType + '\'' +
                '}';
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


    public static final class Builder {
        private String threadPoolName;
        private String taskType;

        private Builder() {
        }

        public static Builder aTransactionStatisticsGroup() {
            return new Builder();
        }

        public Builder withThreadPoolName(String threadPoolName) {
            this.threadPoolName = threadPoolName;
            return this;
        }

        public Builder withTaskType(String taskType) {
            this.taskType = taskType;
            return this;
        }

        public TransactionStatisticsGroup build() {
            return new TransactionStatisticsGroup(threadPoolName, taskType);
        }
    }
}
