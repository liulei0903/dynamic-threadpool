package weihui.bcss.support.dtp.core.monitor.transaction;

import java.io.Serializable;
import java.util.Objects;

/**
 * 执行统计
 *
 * @Description
 * @Author liulei
 * @Date 2021/6/7 18:28
 **/
public class TransactionStatisticsValue implements Serializable {

    /**
     * 成功总数
     */
    private long success;

    /**
     * 失败总数
     */
    private long failure;

    /**
     * 总耗时
     */
    private long elapsed;

    /**
     * 平均耗时
     */
    private long elapsedAvg;

    /**
     * 最小耗时
     */
    private long elapsedMin;

    /**
     * 最大耗时
     */
    private long elapsedMax;

    public long getSuccess() {
        return success;
    }

    public void setSuccess(long success) {
        this.success = success;
    }

    public long getFailure() {
        return failure;
    }

    public void setFailure(long failure) {
        this.failure = failure;
    }

    public long getElapsed() {
        return elapsed;
    }

    public void setElapsed(long elapsed) {
        this.elapsed = elapsed;
    }

    public long getElapsedAvg() {
        return elapsedAvg;
    }

    public void setElapsedAvg(long elapsedAvg) {
        this.elapsedAvg = elapsedAvg;
    }

    public long getElapsedMin() {
        return elapsedMin;
    }

    public void setElapsedMin(long elapsedMin) {
        this.elapsedMin = elapsedMin;
    }

    public long getElapsedMax() {
        return elapsedMax;
    }

    public void setElapsedMax(long elapsedMax) {
        this.elapsedMax = elapsedMax;
    }

    @Override
    public String toString() {
        return "StatisticsValue{" +
                "success=" + success +
                ", failure=" + failure +
                ", elapsed=" + elapsed +
                ", elapsedAvg=" + elapsedAvg +
                ", elapsedMin=" + elapsedMin +
                ", elapsedMax=" + elapsedMax +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TransactionStatisticsValue that = (TransactionStatisticsValue) o;
        return success == that.success && failure == that.failure && elapsed == that.elapsed && elapsedAvg == that.elapsedAvg && elapsedMin == that.elapsedMin && elapsedMax == that.elapsedMax;
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, failure, elapsed, elapsedAvg, elapsedMin, elapsedMax);
    }
}
