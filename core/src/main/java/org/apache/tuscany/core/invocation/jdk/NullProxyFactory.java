package org.apache.tuscany.core.invocation.jdk;

import org.apache.tuscany.core.context.TuscanyModuleComponentContext;
import org.apache.tuscany.core.invocation.ProxyConfiguration;
import org.apache.tuscany.core.invocation.spi.ProxyCreationException;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.invocation.spi.ProxyInitializationException;

/**
 * Passes back an actual instance as opposed to a proxy. Used in cases where proxying may be optimized away.
 * 
 * @FIXME optimize to support scope containers
 * @version $Rev$ $Date$
 */
public class NullProxyFactory implements ProxyFactory {

    private TuscanyModuleComponentContext ctx;

    private String serviceName;

    public NullProxyFactory(String serviceName, TuscanyModuleComponentContext ctx) {
        assert (serviceName != null) : "Service name was null";
        assert (ctx != null) : "Module component context was null";
        this.serviceName = serviceName;
        this.ctx = ctx;
    }

    public void initialize(Class businessInterface, ProxyConfiguration config) throws ProxyInitializationException {
    }

    public Object createProxy() throws ProxyCreationException {
        return ctx.locateService(serviceName);
    }

}
