package org.apache.tuscany.core.context;

import java.lang.reflect.Method;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.ServiceContext;
import org.apache.tuscany.spi.context.ReferenceContext;
import org.osoa.sca.ModuleContext;
import org.osoa.sca.RequestContext;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.ServiceUnavailableException;

/**
 * The standard implementation of an composite context. Autowiring is performed by delegating to the parent
 * context.
 *
 * @version $Rev: 399348 $ $Date: 2006-05-03 09:33:22 -0700 (Wed, 03 May 2006) $
 */
public class CompositeContextImpl<T> extends AbstractCompositeContext<T> implements ModuleContext {

    public CompositeContextImpl() {
        super();
    }

    public CompositeContextImpl(String name, CompositeContext parent, AutowireContext autowireContext) {
        super(name, parent, autowireContext);
    }

    public void onEvent(Event event) {
       publish(event); // propagate event to children
    }

    // ----------------------------------
    // ModuleContext methods
    // ----------------------------------

    private String uri;

    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public Object locateService(String name) throws ServiceUnavailableException {
        checkInit();
        QualifiedName qName = new QualifiedName(name);
        Context context = children.get(qName.getPartName());
        if (context == null){
            throw new ServiceUnavailableException("Service not found ["+name+"]");
        }
        try {
            if(context instanceof AtomicContext){
                return ((AtomicContext)context).getService(qName.getPortName());
            }else if(context instanceof ServiceContext || context instanceof ReferenceContext){
                return context.getService();
            }else{
                throw new ServiceUnavailableException("Illegal target type ["+name+"]");
            }
        } catch (TargetException e) {
            e.addContextName(getName());
            throw new ServiceUnavailableException(e);
        }
    }

    public ServiceReference createServiceReference(String serviceName) {
        throw new UnsupportedOperationException();
    }

    public RequestContext getRequestContext() {
        throw new UnsupportedOperationException();
    }

    public ServiceReference createServiceReferenceForSession(Object self) {
        throw new UnsupportedOperationException();
    }

    public ServiceReference createServiceReferenceForSession(Object self, String serviceName) {
        throw new UnsupportedOperationException();
    }

    public ServiceReference newSession(String serviceName) {
        throw new UnsupportedOperationException();
    }

    public ServiceReference newSession(String serviceName, Object sessionId) {
        throw new UnsupportedOperationException();
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
        return null;
    }
}
