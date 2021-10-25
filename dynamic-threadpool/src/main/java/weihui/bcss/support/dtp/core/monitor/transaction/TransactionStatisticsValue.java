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

    /**
     * 总耗时
     */
    private long finishedElapsed;

    /**
     * 平均耗时
     */
    private long finishedElapsedAvg;

    /**
     * 最小耗时
     */
    private long finishedElapsedMin;

    /**
     * 最大耗时
     */
    private long finishedElapsedMax;


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

    public long getFinishedElapsed() {
        return finishedElapsed;
    }

    public void setFinishedElapsed(long finishedElapsed) {
        this.finishedElapsed = finishedElapsed;
    }

    public long getFinishedElapsedAvg() {
        return finishedElapsedAvg;
    }

    public void setFinishedElapsedAvg(long finishedElapsedAvg) {
        this.finishedElapsedAvg = finishedElapsedAvg;
    }

    public long getFinishedElapsedMin() {
        return finishedElapsedMin;
    }

    public void setFinishedElapsedMin(long finishedElapsedMin) {
        this.finishedElapsedMin = finishedElapsedMin;
    }

    public long getFinishedElapsedMax() {
        return finishedElapsedMax;
    }

    public void setFinishedElapsedMax(long finishedElapsedMax) {
        this.finishedElapsedMax = finishedElapsedMax;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionStatisticsValue that = (TransactionStatisticsValue) o;
        return success == that.success && failure == that.failure && elapsed == that.elapsed && elapsedAvg == that.elapsedAvg && elapsedMin == that.elapsedMin && elapsedMax == that.elapsedMax && finishedElapsed == that.finishedElapsed && finishedElapsedAvg == that.finishedElapsedAvg && finishedElapsedMin == that.finishedElapsedMin && finishedElapsedMax == that.finishedElapsedMax;
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, failure, elapsed, elapsedAvg, elapsedMin, elapsedMax, finishedElapsed, finishedElapsedAvg, finishedElapsedMin, finishedElapsedMax);
    }

    @Override
    public String toString() {
        return "TransactionStatisticsValue{" +
                "success=" + success +
                ", failure=" + failure +
                ", elapsed=" + elapsed +
                ", elapsedAvg=" + elapsedAvg +
                ", elapsedMin=" + elapsedMin +
                ", elapsedMax=" + elapsedMax +
                ", finishedElapsed=" + finishedElapsed +
                ", finishedElapsedAvg=" + finishedElapsedAvg +
                ", finishedElapsedMin=" + finishedElapsedMin +
                ", finishedElapsedMax=" + finishedElapsedMax +
                '}';
    }
}
