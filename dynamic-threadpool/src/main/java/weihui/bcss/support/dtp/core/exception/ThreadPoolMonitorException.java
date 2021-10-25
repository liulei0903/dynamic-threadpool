package weihui.bcss.support.dtp.core.exception;

/**
 * 线程池监控异常
 * @Description
 * @Author liulei
 * @Date 2021/6/9 14:39
 **/
public class ThreadPoolMonitorException extends RuntimeException {

    public ThreadPoolMonitorException(String message, Throwable cause) {
        super(message, cause);
    }
}
