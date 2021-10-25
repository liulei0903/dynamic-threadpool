package weihui.bcss.support.dtp.core.threadpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;
import weihui.bcss.support.dtp.core.config.ThreadPoolConfigCenter;
import weihui.bcss.support.dtp.core.config.model.ThreadPoolConfig;
import weihui.bcss.support.dtp.core.monitor.AbstractMonitorServiceBase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * spring FactoryBean-工厂方法模式
 *
 * @Description
 * @Author liulei
 * @Date 2021/5/31 10:21
 **/
public class DynamicThreadPoolFactoryBean implements FactoryBean<ExecutorService>, InitializingBean, DisposableBean, BeanNameAware {

    private static final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolFactoryBean.class);


    /**
     * 暴露的bean
     */
    private ExecutorService exposedExecutor;

    /**
     * 配置中心
     */
    private ThreadPoolConfigCenter configCenter;

    /**
     * 监控
     */
    private AbstractMonitorServiceBase threadPoolMonitor;

    /**
     * 线程池配置信息
     */
    private ThreadPoolConfig threadPoolConfig;

    /**
     * beanName 等于 threadPoolName
     */
    private String beanName;

    private boolean waitForTasksToCompleteOnShutdown = false;

    private int awaitTerminationSeconds = 0;

    @Override
    public ExecutorService getObject() {
        return this.exposedExecutor;
    }

    @Override
    public Class<? extends ExecutorService> getObjectType() {
        return (this.exposedExecutor != null ? this.exposedExecutor.getClass() : ExecutorService.class);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        initialize();
    }

    private void initialize() {
        //若未设置 threadPoolName,则使用 beanName 作为 threadPoolName
        if (StringUtils.isEmpty(threadPoolConfig.getThreadPoolName())) {
            threadPoolConfig.setThreadPoolName(beanName);
        }
        ThreadPoolConfig finalConfig = configCenter.getThreadPoolConfig(threadPoolConfig.getThreadPoolName(), threadPoolConfig);
        //实例化
        exposedExecutor = new DynamicThreadPoolExecutor(finalConfig, configCenter, threadPoolMonitor);
        //如果需要去重,则使用AntiDuplicateThreadPoolExecutor装饰原对象
        if (!finalConfig.isQueueAllowDuplicate()) {
            exposedExecutor = new AntiDuplicateThreadPoolExecutor((ThreadPoolExecutor) exposedExecutor);
        }
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }


    public void setConfigCenter(ThreadPoolConfigCenter configCenter) {
        this.configCenter = configCenter;
    }

    public void setThreadPoolConfig(ThreadPoolConfig threadPoolConfig) {
        this.threadPoolConfig = threadPoolConfig;
    }

    public void setThreadPoolMonitor(AbstractMonitorServiceBase threadPoolMonitor) {
        this.threadPoolMonitor = threadPoolMonitor;
    }


    @Override
    public void destroy() {
        shutdown();
    }


    public void shutdown() {
        if (logger.isInfoEnabled()) {
            logger.info("Shutting down ExecutorService" + (this.beanName != null ? " '" + this.beanName + "'" : ""));
        }
        if (this.waitForTasksToCompleteOnShutdown) {
            this.exposedExecutor.shutdown();
        } else {
            this.exposedExecutor.shutdownNow();
        }
        awaitTerminationIfNecessary();
    }

    /**
     * Wait for the executor to terminate, according to the value of the
     * {@link #setAwaitTerminationSeconds "awaitTerminationSeconds"} property.
     */
    private void awaitTerminationIfNecessary() {
        if (this.awaitTerminationSeconds > 0) {
            try {
                if (!this.exposedExecutor.awaitTermination(this.awaitTerminationSeconds, TimeUnit.SECONDS)) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("Timed out while waiting for executor" +
                                (this.beanName != null ? " '" + this.beanName + "'" : "") + " to terminate");
                    }
                }
            } catch (InterruptedException ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Interrupted while waiting for executor" +
                            (this.beanName != null ? " '" + this.beanName + "'" : "") + " to terminate");
                }
                Thread.currentThread().interrupt();
            }
        }
    }

    public void setAwaitTerminationSeconds(int awaitTerminationSeconds) {
        this.awaitTerminationSeconds = awaitTerminationSeconds;
    }

    public void setWaitForTasksToCompleteOnShutdown(boolean waitForTasksToCompleteOnShutdown) {
        this.waitForTasksToCompleteOnShutdown = waitForTasksToCompleteOnShutdown;
    }
}
