package threadpool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description
 * @Author liulei
 * @Date 2021/5/31 19:12
 **/
public class TestThreadFactory  implements ThreadFactory {

    private ThreadGroup group = null;

    /**
     * 线程递增编号
     */
    private AtomicInteger threadNumber = new AtomicInteger(1);

    private String namePrefix = null;

    public TestThreadFactory(String threadPoolName) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        namePrefix = "TestThreadFactory "+threadPoolName+" ";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}
