package weihui.bcss.support.dtp.core.threadpool;

/**
 * 对Runnable增加了返回taskType的功能
 *
 * @Description
 * @Author liulei
 * @Date 2021/6/7 17:32
 **/
public class RunnableTypeDecorator implements Runnable, TaskType {

    private Runnable runnable = null;

    private String taskType = null;


    public RunnableTypeDecorator(String taskType, Runnable runnable) {
        this.taskType = taskType;
        this.runnable = runnable;
    }

    public RunnableTypeDecorator(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        runnable.run();
    }

    @Override
    public String getType() {
        return (taskType == null || taskType.length() == 0) ? DEFAULT_TYPE : taskType;
    }
}
