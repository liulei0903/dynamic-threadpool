package threadpool;

import com.ctrip.framework.apollo.build.ApolloInjector;
import com.ctrip.framework.apollo.util.ConfigUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import weihui.bcss.support.dtp.core.threadpool.DynamicThreadPoolExecutor;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

/**
 * @Description
 * @Author liulei
 * @Date 2021/5/25 16:06
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/META-INF/spring/dynamic-threadpool-test.xml"})
public class DynamicThreadPoolTest extends AbstractJUnit4SpringContextTests {

    private static Logger logger = LoggerFactory.getLogger(DynamicThreadPoolTest.class);


    @Resource(name = "testThreadPoolExecutor")
    DynamicThreadPoolExecutor testThreadPoolExecutor;

    @Test
    public void test() throws InterruptedException {
        Assert.assertNotNull(testThreadPoolExecutor.getCorePoolSize());
        int taskCount = 100;
        execute(taskCount);
        //等10秒钟把日志打完
        Thread.sleep(30 * 1000);
    }

    private void execute(int taskCount) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(taskCount);
        for (int i = 0; i < taskCount; i++) {
            testThreadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    latch.countDown();
                }
            });
        }
        latch.await();
        logger.info("等待线程池执行完毕，执行完 " + taskCount + "个任务");
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
