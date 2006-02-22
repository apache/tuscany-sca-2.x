package org.apache.tuscany.core.invocation.jdk;

import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.invocation.ProxyConfiguration;
import org.apache.tuscany.core.invocation.spi.ProxyCreationException;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.invocation.spi.ProxyInitializationException;

/**
 * Returns an actual implementation instance as opposed to a proxy. Used in cases where proxying may be optimized away.
 * 
 * @version $Rev$ $Date$
 */
public class NullProxyFactory implements ProxyFactory {

    private AggregateContext parentContext;

    private String targetName;

    public NullProxyFactory(String componentName, AggregateContext parentContext) {
        assert (parentContext != null) : "Parent context was null";
        this.targetName = componentName;
        this.parentContext = parentContext;
    }

    public void initialize(Class businessInterface, ProxyConfiguration config) throws ProxyInitializationException {
    }

    public Object createProxy() throws ProxyCreationException {
        return parentContext.getContext(targetName);
    }

    public void initialize() throws ProxyInitializationException {
    }

    public ProxyConfiguration getProxyConfiguration() {
        return null;
    }

    public void setProxyConfiguration(ProxyConfiguration config) {
    }

    public void setBusinessInterface(Class interfaze) {
    }

    public Class getBusinessInterface() {
        return null;
    }

    public void addInterface(Class claz) {
    }

    public Class[] getImplementatedInterfaces() {
        return null;
    }

}
