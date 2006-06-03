package org.apache.tuscany.service.jetty;

import javax.servlet.Servlet;

import org.apache.tuscany.spi.annotation.Monitor;
import org.apache.tuscany.spi.host.ServletHost;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;
import org.osoa.sca.annotations.Property;

/**
 * @version $$Rev$$ $$Date$$
 */
@Scope("MODULE")
@Service(ServletHost.class)
public class JettyServiceImpl implements JettyService {

    private TransportMonitor monitor;
    private int port = 8080;

    @Monitor
    public void setMonitor(TransportMonitor monitor) {
        this.monitor = monitor;
    }

    @Property
    public void setPort(int port) {
        this.port = port;
    }

    @Init
    public void init() {
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

}
