package weihui.bcss.support.dtp.core.config.model;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 线程池配置
 *
 * @Description
 * @Author liulei
 * @Date 2021/5/25 11:02
 */
public class ThreadPoolConfigs {

    private Map<String, ThreadPoolConfig> threadPoolConfigMap = new ConcurrentHashMap<String, ThreadPoolConfig>();

    public Map<String, ThreadPoolConfig> getThreadPoolConfigMap() {
        return threadPoolConfigMap;
    }

    public void setThreadPoolConfigMap(Map<String, ThreadPoolConfig> threadPoolConfigMap) {
        this.threadPoolConfigMap = threadPoolConfigMap;
    }
}
