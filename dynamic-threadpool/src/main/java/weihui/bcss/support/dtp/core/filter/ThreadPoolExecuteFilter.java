package weihui.bcss.support.dtp.core.filter;

/**
 * 线程池runnable执行前后的过滤器
 *
 * @Description
 * @Author liulei
 * @Date 2021/6/4 15:51
 **/
public interface ThreadPoolExecuteFilter {

    /**
     * 执行前处理
     *
     * @param t
     * @param r
     */
    public void beforeExecute(Thread t, Runnable r);


    /**
     * 执行后处理
     *
     * @param r
     * @param t
     */
    public void afterExecute(Runnable r, Throwable t);
}
