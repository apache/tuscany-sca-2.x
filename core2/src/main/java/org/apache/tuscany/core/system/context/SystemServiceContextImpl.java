package org.apache.tuscany.core.system.context;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;

import org.apache.tuscany.spi.context.AbstractContext;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.context.IllegalTargetException;
import org.apache.tuscany.spi.context.ReferenceContext;
import org.apache.tuscany.spi.context.TargetNotFoundException;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SystemServiceContextImpl<T> extends AbstractContext<T> implements SystemServiceContext<T> {

    // a reference to the component's implementation instance exposed by the entry point
    private T cachedInstance;
    private Class<T> interfaze;
    private QualifiedName target;

    public SystemServiceContextImpl(String name, Class<T> interfaze, String targetName, CompositeContext parent) throws CoreRuntimeException {
        super(name);
        this.interfaze = interfaze;
        target = new QualifiedName(targetName);
        setParent(parent);
    }

    @SuppressWarnings("unchecked")
    public T getService() {
        if (cachedInstance == null) {
            Context ctx = getParent().getContext(target.getPartName());
            if ((ctx instanceof AtomicContext)) {
                cachedInstance = (T) ((AtomicContext) ctx).getService(target.getPortName());
            } else if ((ctx instanceof ReferenceContext)) {
                cachedInstance = (T) ctx.getService();
            } else if (ctx == null){
                TargetNotFoundException e = new TargetNotFoundException(name);
                e.addContextName(getName());
                throw e;
            }else {
                IllegalTargetException e = new IllegalTargetException("Reference target must be a component or reference context");
                e.setIdentifier(name);
                e.addContextName(getName());
                throw e;
            }
        }
        return cachedInstance;
    }


    public InvocationHandler getHandler() {
        return null;
    }

    public Class<T> getInterface() {
        return interfaze;
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
        return null; //TOD implement
    }

}
