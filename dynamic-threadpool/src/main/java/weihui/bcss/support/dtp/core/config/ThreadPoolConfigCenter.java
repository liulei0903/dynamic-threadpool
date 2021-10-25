package weihui.bcss.support.dtp.core.config;

import weihui.bcss.support.dtp.core.config.model.ThreadPoolConfig;
import weihui.bcss.support.dtp.core.config.model.ThreadPoolConfigs;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 线程池配置中心
 * @Description
 * @Author liulei
 * @Date 2021/5/25 11:28
 **/
public interface ThreadPoolConfigCenter {

    /**
     * 添加listener
     * @param threadPoolName
     * @param interestedKeys
     * @param consumer
     * @return
     */
    boolean addChangeListener(String threadPoolName, Set<String> interestedKeys, Consumer<Map<String, String>> consumer);

    /**
     * 获取配置
     * @return
     */
    ThreadPoolConfigs getThreadPoolConfigs();


    /**
     * 获取配置
     * @param threadPollName
     * @param clientConfig
     * @return
     */
    ThreadPoolConfig getThreadPoolConfig(String threadPollName, ThreadPoolConfig clientConfig);



}
