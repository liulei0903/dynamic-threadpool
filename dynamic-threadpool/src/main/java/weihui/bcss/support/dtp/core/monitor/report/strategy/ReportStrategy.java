package weihui.bcss.support.dtp.core.monitor.report.strategy;

import io.micrometer.core.instrument.MeterRegistry;
import weihui.bcss.support.dtp.core.exception.ThreadPoolMonitorException;

/**
 * MeterRegistry 策略
 *
 * @Description
 * @Author liulei
 * @Date 2021/5/28 15:22
 */
public interface ReportStrategy {

    /**
     * 获取 MeterRegistry 实现
     * @return
     * @throws ThreadPoolMonitorException
     */
    MeterRegistry getMeterRegistry() throws ThreadPoolMonitorException;

    /**
     * 注册发布监听
     * @param runnable
     * @throws ThreadPoolMonitorException
     */
    void regPublishListener(Runnable runnable) throws ThreadPoolMonitorException;
}
