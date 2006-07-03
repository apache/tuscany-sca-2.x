package org.apache.tuscany.service.jetty;

import java.io.File;
import java.io.IOException;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkManager;
import javax.servlet.Servlet;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.handler.RequestLogHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.BoundedThreadPool;
import org.mortbay.thread.ThreadPool;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.annotation.Monitor;
import org.apache.tuscany.spi.host.ServletHost;

/**
 * Implements an HTTP transport service using Jetty
 *
 * @version $$Rev$$ $$Date$$
 */
@Scope("MODULE")
@Service(ServletHost.class)
public class JettyServiceImpl implements JettyService {

    private TransportMonitor monitor;
    private WorkManager workManager;
    private Server server;
    private int port = 8080;

    public JettyServiceImpl() {
    }

    public JettyServiceImpl(TransportMonitor monitor) {
        this.monitor = monitor;
    }

    @Monitor
    public void setMonitor(TransportMonitor monitor) {
        this.monitor = monitor;
    }

    @Autowire
    public void setWorkManager(WorkManager workManager) {
        this.workManager = workManager;
    }

    @Property
    public void setPort(int port) {
        this.port = port;
    }

    @Init
    public void init() throws Exception {

        server = new Server();

        if (workManager == null) {
            BoundedThreadPool threadPool = new BoundedThreadPool();
            threadPool.setMaxThreads(100);
            server.setThreadPool(threadPool);
        } else {
            server.setThreadPool(new TuscanyThreadPool());
        }
        Connector connector = new SelectChannelConnector();
        connector.setPort(port);
        server.setConnectors(new Connector[]{connector});

        HandlerCollection handlers = new HandlerCollection();
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        RequestLogHandler requestLogHandler = new RequestLogHandler();
        handlers.setHandlers(new Handler[]{contexts, new DefaultHandler(), requestLogHandler});
        server.setHandler(handlers);

/*
         WebAppContext.addWebApplications(server, "./webapps", "org/mortbay/jetty/webapp/webdefault.xml", true, false);

        HashUserRealm userRealm = new HashUserRealm();
        userRealm.setName("Test Realm");
        userRealm.setConfig("./etc/realm.properties");
        server.setUserRealms(new UserRealm[]{userRealm});

        NCSARequestLog requestLog = new NCSARequestLog("./logs/jetty-yyyy-mm-dd.log");
        requestLog.setExtended(false);
        requestLogHandler.setRequestLog(requestLog);
        requestLogHandler.setRequestLog(monitor);
*/
        server.setStopAtShutdown(true);
        server.setSendServerVersion(true);
        server.start();
        monitor.started(port);
    }

    @Destroy
    public void destroy() {
        monitor.shutdown(port);
    }

    public void registerMapping(String string, Servlet servlet) {

    }

    public void unregisterMapping(String string) {

    }

    public void registerComposite(File compositeLocation) throws IOException {
        WebAppContext.addWebApplications(server, compositeLocation.getAbsolutePath(),
                "org/mortbay/jetty/webapp/webdefault.xml",
                false,
                false);
    }

    public Server getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }

    private class TuscanyThreadPool implements ThreadPool {

        public boolean dispatch(Runnable job) {
            try {
                workManager.doWork(new TuscanyWork(job));
            } catch (WorkException e) {
                //FIXME
                monitor.requestHandleError(e);
            }
            return true;
        }

        public void join() throws InterruptedException {
            throw new UnsupportedOperationException();
        }

        public int getThreads() {
            throw new UnsupportedOperationException();
        }

        public int getIdleThreads() {
            throw new UnsupportedOperationException();
        }

        public boolean isLowOnThreads() {
            throw new UnsupportedOperationException();
        }

        public void start() throws Exception {

        }

        public void stop() throws Exception {

        }

        public boolean isRunning() {
            return false;
        }

        public boolean isStarted() {
            return false;
        }

        public boolean isStarting() {
            return false;
        }

        public boolean isStopping() {
            return false;
        }

        public boolean isFailed() {
            return false;
        }
    }

    private class TuscanyWork implements Work {

        Runnable job;

        public TuscanyWork(Runnable job) {
            this.job = job;
        }

        public void release() {
        }

        public void run() {
            job.run();
        }
    }

}
