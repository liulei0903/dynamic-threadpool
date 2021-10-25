package weihui.bcss.support.dtp.core.enums;

/**
 * 队列类型
 *
 * @Description
 * @Author liulei
 * @Date 2021年5月24日18:12:10
 */
public enum QueueTypeEnum {

    /**
     * LinkedBlockingQueue
     */
    LINKED_BLOCKING_QUEUE("LinkedBlockingQueue"),

    /**
     * SynchronousQueue
     */
    SYNCHRONOUS_QUEUE("SynchronousQueue"),

    /**
     * ArrayBlockingQueue
     */
    ARRAY_BLOCKING_QUEUE("ArrayBlockingQueue"),

    /**
     * DelayQueue
     */
    DELAY_QUEUE("DelayQueue"),

    /**
     * LinkedTransferQueue
     */
    LINKED_TRANSFER_DEQUE("LinkedTransferQueue"),

    /**
     * LinkedBlockingDeque
     */
    LINKED_BLOCKING_DEQUE("LinkedBlockingDeque"),

    /**
     * PriorityBlockingQueue
     */
    PRIORITY_BLOCKING_QUEUE("PriorityBlockingQueue"),

    /**
     * ResizableLinkedBlockIngQueue
     */
    RESIZABLE_LINKED_BLOCKING_QUEUE("ResizableLinkedBlockIngQueue");

    QueueTypeEnum(String type) {
        this.type = type;
    }

    private final String type;

    public String getType() {
        return type;
    }

    public static boolean exists(String type) {
        for (QueueTypeEnum typeEnum : QueueTypeEnum.values()) {
            if (typeEnum.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }
}
