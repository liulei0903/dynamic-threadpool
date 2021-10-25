package weihui.bcss.support.dtp.core.config;

import org.springframework.beans.factory.InitializingBean;
import weihui.bcss.support.dtp.core.config.model.ThreadPoolConfig;
import weihui.bcss.support.dtp.core.config.model.ThreadPoolConfigs;

/**
 * 线程池配置中心
 *
 * @Description
 * @Author liulei
 * @Date 2021/5/25 11:28
 **/
public abstract class AbstractThreadPoolConfigCenterBase implements ThreadPoolConfigCenter, InitializingBean {

    /**
     * 保存配置中心的线程池配置
     */
    protected ThreadPoolConfigs threadPoolConfigs = new ThreadPoolConfigs();

    @Override
    public void afterPropertiesSet() throws Exception {
        initialize();
    }

    @Override
    public ThreadPoolConfigs getThreadPoolConfigs() {
        return threadPoolConfigs;
    }

    @Override
    public ThreadPoolConfig getThreadPoolConfig(String threadPoolName, ThreadPoolConfig clientConfig) {
        return mergeConfig(threadPoolName, clientConfig);
    }

    /**
     * 合并配置
     * 目前有三个地方可以设置ThreadPoolConfig值 , 优先级应该按以下顺序使用
     * 1、配置中心
     * 2、spring xml 配置 bean 注入的值
     * 3、class 实例化的初始
     *
     * @param clientConfig 应用注入的默认值
     * @return ThreadPoolConfig
     */
    private ThreadPoolConfig mergeConfig(String threadPoolName, ThreadPoolConfig clientConfig) {
        ThreadPoolConfig centerConfig = threadPoolConfigs.getThreadPoolConfigMap().get(threadPoolName);
        //1.配置中心未配置，以app设置为准
        if (centerConfig == null) {
            //intern() 的作用是保证同一个threadPoolName只实例化一次
            synchronized (threadPoolName.intern()) {
                centerConfig = threadPoolConfigs.getThreadPoolConfigMap().get(threadPoolName);
                //double check
                if (centerConfig == null) {
                    //TODO 这里不直接引用,使用深拷贝是否更好些?
                    centerConfig = clientConfig;
                    threadPoolConfigs.getThreadPoolConfigMap().putIfAbsent(threadPoolName, centerConfig);
                }
            }
            return centerConfig;
        } else {
            //2.以下三个参数以客户端为准,覆盖到 centerConfig 对象上
            centerConfig.setQueue(clientConfig.getQueue());
            centerConfig.setRejectedExecutionHandler(clientConfig.getRejectedExecutionHandler());
            centerConfig.setThreadFactory(clientConfig.getThreadFactory());
            centerConfig.setQueueType(clientConfig.getQueueType());
            centerConfig.setQueueAllowDuplicate(clientConfig.isQueueAllowDuplicate());
            centerConfig.setQueueDuplicatedThrows(clientConfig.isQueueDuplicatedThrows());
            return centerConfig;
        }
    }

    /**
     * 初始化,将配置中心的配置同步到jvm中
     */
    abstract public void initialize();
}
