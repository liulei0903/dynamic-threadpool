package weihui.bcss.support.dtp.core.threadpool;

import java.util.concurrent.Callable;

/**
 * 对Callable增加了返回taskType的功能
 *
 * @Description
 * @Author liulei
 * @Date 2021/6/7 17:32
 **/
public class CallableTypeDecorator<V> implements Callable, TaskType {

    private Callable callable = null;

    private String taskType = null;


    public CallableTypeDecorator(String taskType, Callable callable) {
        this.taskType = taskType;
        this.callable = callable;
    }

    public CallableTypeDecorator(Callable callable) {
        this.callable = callable;
    }


    @Override
    public String getType() {
        return (taskType == null || taskType.length() == 0) ? DEFAULT_TYPE : taskType;
    }

    @Override
    public V call() throws Exception {
        return (V) callable.call();
    }
}
