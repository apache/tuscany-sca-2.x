package org.apache.tuscany.core.launcher;

import org.osoa.sca.CompositeContext;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.RequestContext;
import org.osoa.sca.SCA;

import org.apache.tuscany.spi.component.CompositeComponent;


public class CompositeContextImpl extends SCA implements CompositeContext {
    protected final CompositeComponent<?> composite;

    public CompositeContextImpl(final CompositeComponent<?> composite) {
        this.composite = composite;
    }

    public void start() {
        setCompositeContext(this);
    }

    public void stop() {
        setCompositeContext(null);
    }

    public ServiceReference createServiceReferenceForSession(Object arg0) {
        return null;
    }

    public ServiceReference createServiceReferenceForSession(Object arg0, String arg1) {
        return null;
    }

    public String getCompositeName() {
        return null;
    }

    public String getCompositeURI() {
        return null;
    }

    public RequestContext getRequestContext() {
        return null;
    }

    public <T> T locateService(Class<T> serviceInterface, String serviceName) {
        return serviceInterface.cast(composite.getChild(serviceName).getServiceInstance());
    }

    public ServiceReference newSession(String arg0) {
        return null;
    }

    public ServiceReference newSession(String arg0, Object arg1) {
        return null;
    }

}
