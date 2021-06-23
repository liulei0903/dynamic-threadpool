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

    protected ThreadPoolConfigs threadPoolConfigs = new ThreadPoolConfigs();

    /**
     * 初始化线程池配置
     */
    abstract public void initialize();

    @Override
    public ThreadPoolConfigs getThreadPoolConfigs() {
        return threadPoolConfigs;
    }

    @Override
    public ThreadPoolConfig getThreadPoolConfig(String threadPoolName, ThreadPoolConfig appConfg) {
        return mergeConfig(appConfg, threadPoolConfigs.getThreadPoolConfigMap().get(threadPoolName));
    }


    /**
     * 合并配置
     * 目前有三个地方可以设置ThreadPoolConfig值 , 优先级应该按以下顺序使用
     * 1、配置中心
     * 2、spring xml 配置 bean 注入的值
     * 2、class 实例化的初始值
     * TODO 这里很乱，需要重构，关键是配置中心的配置与线程池配置之间的对象关系。
     *
     * @param appConfig    应用注入的默认值
     * @param centerConfig 配置中心同步的值
     * @return
     */
    private ThreadPoolConfig mergeConfig(ThreadPoolConfig appConfig, ThreadPoolConfig centerConfig) {
        //1.配置中心未配置，以app设置为准
        if (centerConfig == null) {
            return appConfig;
        }
        //2.以下三个参数以客户端为准
        centerConfig.setQueue(appConfig.getQueue());
        centerConfig.setRejectedExecutionHandler(appConfig.getRejectedExecutionHandler());
        centerConfig.setThreadFactory(appConfig.getThreadFactory());
        return centerConfig;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initialize();
    }

}
