package threadpool;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import weihui.bcss.support.dtp.core.exception.QueueDuplicateEntryException;
import weihui.bcss.support.dtp.core.queue.AntiDuplicateBlockingQueue;
import weihui.bcss.support.dtp.core.threadpool.command.CommandIdentity;

import java.util.concurrent.*;

/**
 * @Description
 * @Author liulei
 * @Date 2021/5/25 16:06
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/META-INF/spring/dynamic-threadpool-test.xml"})
public class DynamicThreadPoolTest extends AbstractJUnit4SpringContextTests {

    private static final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolTest.class);

    @Autowired
    ExecutorService testThreadPoolExecutor;

    @Test
    public void test() throws InterruptedException {
        int taskCount = 100;
        final CountDownLatch latch = new CountDownLatch(taskCount);
        for (int i = 0; i < taskCount; i++) {
            int finalI = i;
            testThreadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("i = " + finalI);
                    latch.countDown();
                }
            });
        }
        latch.await();
        System.out.println("等待线程池执行完毕，执行完 " + taskCount + "个任务");
        //等10秒钟把日志打完
        //Thread.sleep(30 * 1000);
    }

    @Test
    public void testAntiDuplicateQueue() throws InterruptedException {
        /**
         * 背景:
         * <property name="queueAllowDuplicate" value="false"/>
         * threadpools.testThreadPoolExecutor.corePoolSize = 1
         * threadpools.testThreadPoolExecutor.maximumPoolSize = 1
         * threadpools.testThreadPoolExecutor.queueCapacity = 500
         */
        int taskCount = 4;
        final CountDownLatch latch = new CountDownLatch(taskCount);
        for (int i = 1; i <= 2; i++) {
            int finalI = i;
            // runnable 休眠, 1直接处理, 2进入queue等待
            testThreadPoolExecutor.execute(new AntiDuplicateRunnable<String>(String.valueOf(i), latch));
        }

        for (int i = 1; i <= 3; i++) {
            int finalI = i;
            try {
                // 再次提交 1 - 3的任务
                // 1会进入queue等待,排在2的后面;
                // 2会抛出 QueueDuplicateEntryException 异常
                // 3会进入队列瞪大,排在1的后面;
                testThreadPoolExecutor.execute(new AntiDuplicateRunnable<String>(String.valueOf(i), latch));
            } catch (QueueDuplicateEntryException e) {
                System.out.println(i + " 已存在");
            }
        }

        //最终结果 1,1,2,3
        latch.await();
        System.out.println("等待线程池执行完毕，执行完 " + taskCount + "个任务");
    }

    @Test
    public void testDuplicateQueue() throws InterruptedException {
        /**
         * 背景:
         * <property name="queueAllowDuplicate" value="true"/>
         * threadpools.testThreadPoolExecutor.corePoolSize = 1
         * threadpools.testThreadPoolExecutor.maximumPoolSize = 1
         * threadpools.testThreadPoolExecutor.queueCapacity = 500
         */
        int taskCount = 5;
        final CountDownLatch latch = new CountDownLatch(taskCount);
        for (int i = 1; i <= 2; i++) {
            int finalI = i;
            // runnable 休眠, 1直接处理, 2进入queue等待
            testThreadPoolExecutor.execute(new AntiDuplicateRunnable<String>(String.valueOf(i), latch));
        }

        for (int i = 1; i <= 3; i++) {
            int finalI = i;
            try {
                // 再次提交 1 - 3的任务
                // 1会进入queue等待,排在2的后面;
                // 2会抛出 QueueDuplicateEntryException 异常
                // 3会进入队列瞪大,排在1的后面;
                testThreadPoolExecutor.execute(new AntiDuplicateRunnable<String>(String.valueOf(i), latch));
            } catch (QueueDuplicateEntryException e) {
                System.out.println(i + " 已存在 , " + e.getMessage());
                latch.countDown();
            }
        }

        //最终结果 1,1,2,2,3
        latch.await();
        System.out.println("等待线程池执行完毕，执行完 " + taskCount + "个任务");
    }

    @Test
    public void testAntiDuplicateQueue2() throws InterruptedException {
        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(10);

        BlockingQueue<AntiDuplicateRunnable<String>> antiQueue = new AntiDuplicateBlockingQueue<AntiDuplicateRunnable<String>>(queue, 10, true);

        System.out.println(antiQueue.offer(new AntiDuplicateRunnable<String>("1", null)));

        System.out.println(antiQueue.offer(new AntiDuplicateRunnable<String>("1", null)));

    }


    class AntiDuplicateRunnable<S> implements Runnable, CommandIdentity<String> {
        String identity = null;
        CountDownLatch latch;

        public AntiDuplicateRunnable(String identity, CountDownLatch latch) {
            this.identity = identity;
            this.latch = latch;
        }

        @Override
        public void run() {
            System.out.println("开始处理 i = " + identity);
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latch.countDown();
        }

        @Override
        public String toString() {
            return "AntiDuplicateRunnable{" +
                    "identity='" + identity + '\'' +
                    '}';
        }

        @Override
        public String getIdentity() {
            return identity;
        }
    }


    /**
     * 测试队列扩容
     */
    @Test
    public void testQueueCapacityDilatation() {

    }

    /**
     * 测试队列扩缩容
     */
    @Test
    public void testQueueCapacityDiminish() {

    }


    /**
     * 测试线程池coreSize扩容
     */
    @Test
    public void testThreadPoolCoreSizeDilatation() {

    }

    /**
     * 测试线程池coreSize缩容
     */
    @Test
    public void testThreadPoolCoreSizeDiminish() {

    }

    /**
     * 测试线程池maxSize扩容
     */
    @Test
    public void testThreadPoolMaxSizeDilatation() {

    }

    /**
     * 测试线程池maxSize缩容
     */
    @Test
    public void testThreadPoolMaxSizeDiminish() {

    }


    /**
     * 测试线程池拒绝任务数统计
     */
    @Test
    public void testThreadPoolRejectCount() {

    }


    /**
     * 准备阶段:
     * 1.可以指定task执行的期望时间，让mock渠道 sleep 指定时间
     * 2.批量创建出款订单
     */

    /**
     * 场景1：调增 coreSize、maximumPoolSize【不需要queue中存在任务】
     *
     * 1.初始值：
     * SEND_AWATING_MAX_SIZE = 1000
     * corePoolSize = 5
     * maximumPoolSize = 5
     * queueCapacity = 100
     *
     * 2.修改 corePoolSize = 10 ， maximumPoolSize = 10
     *
     * 3.创建100个出款任务
     *
     * 验证
     * grep 'thread.pool.static.coreSize' default.log 期望结果：value=10
     * grep 'thread.pool.static.maxSize' default.log 期望解雇：value=10
     * grep 'thread.pool.dynamic.largestCount' default.log 其实结果：value > 5
     */

    /**
     * 场景2：调增 queueCapacity
     *
     * 1.初始值：
     * SEND_AWATING_MAX_SIZE = 1000
     * corePoolSize = 5
     * maximumPoolSize = 5
     * queueCapacity = 100
     *
     * 2.修改 queueCapacity 配置为 200
     *
     * 3.创建200个出款任务
     *
     * 验证
     * grep 'thread.pool.dynamic.waitTaskCount' default.log  期望结果: value > 100
     * grep 'thread.pool.dynamic.largestCount' default.log   期望结果: value = 0
     *
     */


    /**
     * 场景3：测试拒绝任务数
     *
     * 1.初始值：
     * SEND_AWATING_MAX_SIZE = 1000
     * corePoolSize = 5
     * maximumPoolSize = 5
     * queueCapacity = 100
     *
     * 2.创建200个出款任务
     *
     * 验证
     * grep 'thread.pool.dynamic.rejeuctCount' default.log  期望结果: value > 0
     */

    /**
     * 场景4：自定义 ThreadFactory 、BlockingQueue 、RejectedExecutionHandler 【不需要队列中存在任务】
     */
}
