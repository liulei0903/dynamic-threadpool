package weihui.bcss.support.dtp.core.monitor.report.strategy;

import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weihui.bcss.support.dtp.core.exception.ThreadPoolMonitorException;
import weihui.bcss.support.dtp.core.threadpool.DynamicThreadPoolExecutor;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.*;

/**
 * Prometheus
 *
 * @Description
 * @Author liulei
 * @Date 2021/5/30 22:27
 **/
public class PrometheusReportStrategy implements ReportStrategy {

    private final Logger logger = LoggerFactory.getLogger(PrometheusReportStrategy.class);

    private String premetheusWebContext = "/prometheus";

    private ExecutorService singleExecutor = new ThreadPoolExecutor(1, 1, 1000, TimeUnit.HOURS, new SynchronousQueue(), new DynamicThreadPoolExecutor.DefaultThreadFactory("dynamic-threadpool-monitor"));

    public Runnable publishListenerRunnable;

    /**
     * premetheus http 端口，默认为 9090
     */
    private int premetheusPort = 9090;

    @Override
    public MeterRegistry getMeterRegistry() throws ThreadPoolMonitorException {
        return createPrometheusRegistry();
    }

    @Override
    public void regPublishListener(Runnable runnable) throws ThreadPoolMonitorException {
        this.publishListenerRunnable = runnable;
    }

    private MeterRegistry createPrometheusRegistry() throws ThreadPoolMonitorException {
        try {
            MeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
            HttpServer server = HttpServer.create(new InetSocketAddress(premetheusPort), 0);
            server.createContext(premetheusWebContext, httpExchange -> {
                String response = ((PrometheusMeterRegistry) registry).scrape();
                httpExchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                publishListenerRunnable.run();
            });
            singleExecutor.submit(server::start);
            return registry;
        } catch (Exception e) {
            throw new ThreadPoolMonitorException("Init PrometheusRegistry Fail ", e);
        }
    }

    public void setPremetheusWebContext(String premetheusWebContext) {
        this.premetheusWebContext = premetheusWebContext;
    }

    public void setPremetheusPort(int premetheusPort) {
        this.premetheusPort = premetheusPort;
    }
}
