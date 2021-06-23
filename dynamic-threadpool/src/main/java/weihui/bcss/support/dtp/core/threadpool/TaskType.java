package weihui.bcss.support.dtp.core.threadpool;


/**
 * 线程池中的任务分类
 *
 * @Description
 * @Author liulei
 * @Date 2021年5月24日18:12:10
 **/
public interface TaskType {

    public static final String DEFAULT_TYPE = "defaultTask";

    /**
     * 返回任务类型
     * @return 任务类型
     */
    public String getType();
}
