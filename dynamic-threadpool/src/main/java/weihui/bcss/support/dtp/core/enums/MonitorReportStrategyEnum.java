package weihui.bcss.support.dtp.core.enums;

/**
 * 线程池监控metrics上报策略
 * @author liulei
 */
public enum MonitorReportStrategyEnum {

    //log策略
    LOG("Log"),
    //普罗米修斯策略
    PROMETHEUS("Prometheus");


    MonitorReportStrategyEnum(String type) {
        this.type = type;
    }

    private String type;

    public String getType() {
        return type;
    }

    public static boolean exists(String type) {
        for (MonitorReportStrategyEnum typeEnum : MonitorReportStrategyEnum.values()) {
            if (typeEnum.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }

    public static MonitorReportStrategyEnum get(String type) {
        for (MonitorReportStrategyEnum typeEnum : MonitorReportStrategyEnum.values()) {
            if (typeEnum.getType().equals(type)) {
                return typeEnum;
            }
        }
        return null;
    }
}
