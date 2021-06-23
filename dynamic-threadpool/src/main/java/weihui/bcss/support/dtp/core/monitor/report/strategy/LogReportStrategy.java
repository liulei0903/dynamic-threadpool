package weihui.bcss.support.dtp.core.monitor.report.strategy;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import io.micrometer.core.instrument.logging.LoggingRegistryConfig;
import io.micrometer.core.instrument.util.DoubleFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weihui.bcss.support.dtp.core.exception.ThreadPoolMonitorException;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Log
 *
 * @Description
 * @Author liulei
 * @Date 2021/5/30 22:27
 **/
public class LogReportStrategy implements ReportStrategy {

    private final Logger logger = LoggerFactory.getLogger(LogReportStrategy.class);

    /**
     * 间隔多少秒打印一次metrics到log中
     */
    private int logDurationOfSeconds = 60;

    private LoggingRegistryConfig config = null;

    public Runnable publishListenerRunnable;

    @Override
    public MeterRegistry getMeterRegistry() throws ThreadPoolMonitorException {
        return createLogRegistry();
    }

    @Override
    public void regPublishListener(Runnable runnable) throws ThreadPoolMonitorException {
        this.publishListenerRunnable = runnable;
    }

    private MeterRegistry createLogRegistry() {
        config = new LoggingRegistryConfig() {
            @Override
            public Duration step() {
                return Duration.ofSeconds(logDurationOfSeconds);//间隔多少秒输出一次
            }

            @Override
            public String get(String key) {
                return null;
            }
        };
        MeterRegistry registry = new GroupByTagsLoggingRegistry(config, Clock.SYSTEM);
        return registry;
    }

    public void setLogDurationOfSeconds(int logDurationOfSeconds) {
        this.logDurationOfSeconds = logDurationOfSeconds;
    }

    class GroupByTagsLoggingRegistry extends LoggingMeterRegistry {

        Function<Meter, String> meterIdPrinter = null;

        public GroupByTagsLoggingRegistry(LoggingRegistryConfig config, Clock clock) {
            super(config, clock);
            meterIdPrinter = this.defaultMeterIdPrinter();
        }

        @Override
        protected void publish() {

            Map<String, StringBuilder> printByThreadPoolMap = new HashMap<String, StringBuilder>(16);

            this.getMeters().stream().sorted((m1, m2) -> {
                int typeComp = m1.getId().getType().compareTo(m2.getId().getType());
                return typeComp == 0 ? m1.getId().getName().compareTo(m2.getId().getName()) : typeComp;
            }).forEach((m) -> {
                if (m instanceof Gauge) {
                    // 原格式 一行输出一个 metrics ，如： thread.pool.static.coreSize{thread.pool.name=testThreadPoolExecutor} value=8
                    // printLog.append(id(m) + " value=" + value(m, ((Gauge) m).value()) +" ");
                    // 新格式，按 tags 分组
                    String tags = getTags(m);
                    StringBuilder printLog = null;
                    if (printByThreadPoolMap.containsKey(tags)) {
                        printLog = printByThreadPoolMap.get(tags);
                    } else {
                        printLog = new StringBuilder(tags);
                        printByThreadPoolMap.put(tags, printLog);
                    }
                    printLog.append(super.getConventionName(m.getId()) + "=" + value(m, ((Gauge) m).value()) + " ");
                }
            });

            //按tags分组打印到log
            printByThreadPoolMap.values().forEach(log -> {
                logger.info(log.toString());
            });

            //通知观察则 publish 事件
            publishListenerRunnable.run();
        }

        private String getTags(Meter meter) {
            return super.getConventionTags(meter.getId()).stream().map((t) -> {
                return t.getKey() + "=" + t.getValue();
            }).collect(Collectors.joining(",", "{", "}")) + " ";
        }


        String id(Meter meter) {
            return meterIdPrinter.apply(meter);
        }

        String value(Meter meter, double value) {
            return this.humanReadableBaseUnit(meter, value);
        }

        String humanReadableByteCount(double bytes) {
            int unit = 1024;
            if (!(bytes < (double) unit) && !Double.isNaN(bytes)) {
                int exp = (int) (Math.log(bytes) / Math.log((double) unit));
                String pre = "KMGTPE".charAt(exp - 1) + "i";
                return DoubleFormat.decimalOrNan(bytes / Math.pow((double) unit, (double) exp)) + " " + pre + "B";
            } else {
                return DoubleFormat.decimalOrNan(bytes) + " B";
            }
        }

        String humanReadableBaseUnit(Meter meter, double value) {
            String baseUnit = meter.getId().getBaseUnit();
            return "bytes".equals(baseUnit) ? this.humanReadableByteCount(value) : DoubleFormat.decimalOrNan(value) + (baseUnit != null ? " " + baseUnit : "");
        }

        private Function<Meter, String> defaultMeterIdPrinter() {
            return (meter) -> {
                return super.getConventionName(meter.getId()) + (String) super.getConventionTags(meter.getId()).stream().map((t) -> {
                    return t.getKey() + "=" + t.getValue();
                }).collect(Collectors.joining(",", "{", "}"));
            };
        }

    }
}
