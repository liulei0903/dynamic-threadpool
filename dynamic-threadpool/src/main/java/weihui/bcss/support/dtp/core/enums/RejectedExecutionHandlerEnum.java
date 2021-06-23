package weihui.bcss.support.dtp.core.enums;

/**
 * 拒绝策略类型
 *
 * @Description
 * @Author liulei
 * @Date 2021年5月24日18:12:10
 */
public enum RejectedExecutionHandlerEnum {

    /**
     * CallerRunsPolicy
     */
    CALLER_RUNS_POLICY("CallerRunsPolicy"),

    /**
     * AbortPolicy
     */
    ABORT_POLICY("AbortPolicy"),

    /**
     * DiscardPolicy
     */
    DISCARD_POLICY("DiscardPolicy"),

    /**
     * DiscardOldestPolicy
     */
    DISCARD_OLDEST_POLICY("DiscardOldestPolicy");

    RejectedExecutionHandlerEnum(String type) {
        this.type = type;
    };

    private String type;

    public String getType() {
        return type;
    }

    public static boolean exists(String type) {
        for (RejectedExecutionHandlerEnum typeEnum : RejectedExecutionHandlerEnum.values()) {
            if (typeEnum.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }
}
