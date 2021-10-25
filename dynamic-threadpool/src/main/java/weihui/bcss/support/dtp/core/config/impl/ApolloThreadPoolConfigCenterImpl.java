package weihui.bcss.support.dtp.core.config.impl;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.build.ApolloInjector;
import com.ctrip.framework.apollo.enums.PropertyChangeType;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.util.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import weihui.bcss.support.dtp.core.config.AbstractThreadPoolConfigCenterBase;
import weihui.bcss.support.dtp.core.config.model.ThreadPoolConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * apollo 配置中心实现
 *
 * @Description 负责从apollo获取线程池配置，并注册动态配置监听事件
 * @Author liulei
 * @Date 2021/5/25 13:38
 **/
public class ApolloThreadPoolConfigCenterImpl extends AbstractThreadPoolConfigCenterBase {


    private static final Logger logger = LoggerFactory.getLogger(ApolloThreadPoolConfigCenterImpl.class);

    /**
     * config nameSpace
     */
    private String nameSpace = null;

    /**
     * key的分隔符
     */
    private static final String THREAD_KEY_FLAG = "\\.";

    /**
     *
     */
    private static final int THREAD_KEY_SPLIT_MAX_LENGTH = 3;

    /**
     * apollo config
     */
    private Config config = null;

    /**
     * 是否启动成功
     */
    private boolean startOpSuccess = false;

    /**
     * 初始化配置中心的配置到jvm中
     */
    @Override
    public void initialize() {
        try {
            //1.初始化 apollo 配置
            initApollo();
            //2.从配置中心同步配置到ThreadPoolConfigs 配置载体中
            initThreadPoolConfigs();
            startOpSuccess = true;
        } catch (Exception e) {
            logger.warn("Apollo initialize fail", e);
        }
    }

    @Override
    public boolean addChangeListener(String threadPoolName, Set<String> keys, Consumer<Map<String, String>> consumer) {
        if (!startOpSuccess) {
            logger.warn("Start Fail,Allow addChangeListener to {}", threadPoolName);
            return false;
        }
        ThreadPoolConfig threadPoolConfig = threadPoolConfigs.getThreadPoolConfigMap().get(threadPoolName);
        if (threadPoolConfig == null) {
            logger.warn("Config Center not found {} Config", threadPoolName);
            return false;
        }
        config.addChangeListener(changeEvent -> {
            //TODO 暂时未同步更新 threadPoolConfig中的配置
            // map保存changeEvent中修改or新增的 key ：newValue
            Map<String, String> changeKeys = new HashMap<>(changeEvent.changedKeys().size());
            changeEvent.changedKeys().stream().forEach(key -> {
                ConfigChange configChange = changeEvent.getChange(key);
                if (configChange.getChangeType() == PropertyChangeType.ADDED || configChange.getChangeType() == PropertyChangeType.MODIFIED) {
                    changeKeys.put(key, configChange.getNewValue());
                    consumer.accept(changeKeys);
                }
            });
        }, keys);
        logger.info("{} add listener success", threadPoolName);
        return true;
    }

    /**
     * 初始化apollo Config
     */
    private void initApollo() {
        //1.优先取用户自定义的nameSpace
        if (!StringUtils.isEmpty(nameSpace)) {
            config = ConfigService.getConfig(nameSpace);
        } else {
            //2.取私有的nameSpace PMD.threadPool
            nameSpace = "PMD.threadPool";
            config = ConfigService.getConfig(nameSpace);
            //3.私有也不存在则按规则取公有 nameSpace
            if (config == null || config.getPropertyNames() == null || config.getPropertyNames().isEmpty()) {
                nameSpace = "PMD." + getAppName() + ".threadPool";
                config = ConfigService.getConfig(nameSpace);
            }
        }
        logger.info("Init ThreadPoolConfig from apollo {}", nameSpace);
    }

    /**
     *
     */
    private void initThreadPoolConfigs() {
        Set<String> propertyNames = config.getPropertyNames();
        Map<String, ThreadPoolConfig> threadPoolConfigMap = threadPoolConfigs.getThreadPoolConfigMap();
        propertyNames.forEach(pn -> {
            logger.info("Apollo NameSpace {}-{}", nameSpace, pn);
            if (pn.startsWith(ThreadPoolConfig.KEY_PREFIX_THREADPOOL)) {
                String[] pns = pn.split(THREAD_KEY_FLAG);
                if (pns.length == THREAD_KEY_SPLIT_MAX_LENGTH && !threadPoolConfigMap.containsKey(pns[1])) {
                    String threadPoolName = parserToThreadPoolName(pn);
                    //添加线程池配置
                    threadPoolConfigMap.putIfAbsent(threadPoolName, new ThreadPoolConfig(threadPoolName));
                }
            }
        });

        //3.同步配置信息到 ThreadPoolConfig 中
        threadPoolConfigMap.values().forEach(threadPoolConfig -> {
            String threadPoolName = threadPoolConfig.getThreadPoolName();
            threadPoolConfig.setCorePoolSize(config.getIntProperty(threadPoolConfig.getCorePoolSizeKey(), threadPoolConfig.getCorePoolSize()));
            threadPoolConfig.setMaximumPoolSize(config.getIntProperty(threadPoolConfig.getMaximumPoolSizeKey(), threadPoolConfig.getMaximumPoolSize()));
            threadPoolConfig.setQueueCapacity(config.getIntProperty(threadPoolConfig.getQueueCapacityKey(), threadPoolConfig.getQueueCapacity()));
            threadPoolConfig.setQueueType(config.getProperty(threadPoolConfig.getQueueTypeKey(), threadPoolConfig.getQueueType()));
            threadPoolConfig.setRejectedExecutionType(config.getProperty(threadPoolConfig.getRejectedExecutionTypeKey(), threadPoolConfig.getRejectedExecutionType()));
            threadPoolConfig.setUnit(config.getEnumProperty(threadPoolConfig.getUnitKey(), TimeUnit.class, threadPoolConfig.getUnit()));
            threadPoolConfig.setKeepAliveTime(config.getLongProperty(threadPoolConfig.getKeepAliveTimeKey(), threadPoolConfig.getKeepAliveTime()));
            threadPoolConfig.setMonitorRunningState(config.getBooleanProperty(threadPoolConfig.getMonitorRunningStateKey(), threadPoolConfig.isMonitorRunningState()));
            threadPoolConfig.setMonitorExecuteResult(config.getBooleanProperty(threadPoolConfig.getMonitorExecuteResultKey(), threadPoolConfig.isMonitorExecuteResult()));
            logger.info("Apollo ThreadPoolConfig is {} ", threadPoolConfig);
        });
    }

    private String parserToThreadPoolName(String propertiesKey) {
        String threadPoolName = propertiesKey.split(THREAD_KEY_FLAG)[1];
        return threadPoolName;
    }

    private String getAppName() {
        return ApolloInjector.getInstance(ConfigUtil.class).getAppId();
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

}
