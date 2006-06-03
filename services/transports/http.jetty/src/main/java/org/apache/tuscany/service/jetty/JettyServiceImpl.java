package org.apache.tuscany.service.jetty;

import javax.resource.spi.work.WorkManager;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkException;
import javax.servlet.Servlet;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.annotation.Monitor;
import org.apache.tuscany.spi.host.ServletHost;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

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

    private int port = 8080;

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
    public void init() {

//        Server server = new Server();
//
//        BoundedThreadPool threadPool = new BoundedThreadPool();
//        threadPool.setMaxThreads(100);
//        server.setThreadPool(threadPool);
//
//        Connector connector=new SelectChannelConnector();
//        connector.setPort(port);
//        server.setConnectors(new Connector[]{connector});
//
//        HandlerCollection handlers = new HandlerCollection();
//        ContextHandlerCollection contexts = new ContextHandlerCollection();
//        RequestLogHandler requestLogHandler = new RequestLogHandler();
//        handlers.setHandlers(new Handler[]{contexts,new DefaultHandler(),requestLogHandler});
//        server.setHandler(handlers);
//
//        // TODO add javadoc context to contexts
//
//        WebAppContext.addWebApplications(server, "./webapps", "org/mortbay/jetty/webapp/webdefault.xml", true, false);
//
//        HashUserRealm userRealm = new HashUserRealm();
//        userRealm.setName("Test Realm");
//        userRealm.setConfig("./etc/realm.properties");
//        server.setUserRealms(new UserRealm[]{userRealm});
//
//        NCSARequestLog requestLog = new NCSARequestLog("./logs/jetty-yyyy-mm-dd.log");
//        requestLog.setExtended(false);
//        requestLogHandler.setRequestLog(requestLog);
//
//        server.setStopAtShutdown(true);
//        server.setSendServerVersion(true);
//
//        server.start();
//        server.join();

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

    private class TuscanyThreadPool { //implements ThreadPool{

        public boolean dispatch(Runnable job){
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

    }

    private class TuscanyWork implements Work{

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
