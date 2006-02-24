package org.apache.tuscany.container.js.rhino;

import java.lang.reflect.InvocationTargetException;

import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.invocation.Interceptor;
import org.apache.tuscany.core.invocation.TargetInvoker;
import org.apache.tuscany.core.message.Message;

public class RhinoTargetInvoker implements TargetInvoker {

    private ScopeContext container;

    private QualifiedName name;

    private String operation;

    private RhinoScript target;

    public RhinoTargetInvoker(String serviceName, String operation, ScopeContext container) {
        assert (serviceName != null) : "No service name specified";
        assert (container != null) : "No scope container specified";
        assert (operation != null) : "No operation specified";
        this.name = new QualifiedName(serviceName);
        this.container = container;
        this.operation = operation;
    }

    public Object invokeTarget(Object payload) throws InvocationTargetException {
        if (cacheable) {
            if (target == null) {
                target = (RhinoScript) container.getContext(name.getPartName()).getImplementationInstance();
            }
            return target.invoke(operation, payload);
        } else {
            return ((RhinoScript) container.getContext(name.getPartName()).getImplementationInstance()).invoke(operation,
                    payload);
        }
    }

    private boolean cacheable;

    public boolean isCacheable() {
        return cacheable;
    }

    public void setCacheable(boolean val) {
        cacheable = val;
    }

    public Message invoke(Message msg) {
        try {
            Object resp = invokeTarget(msg.getBody());
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            msg.setBody(e.getCause());
        } catch (Throwable e) {
            msg.setBody(e);
        }
        return msg;
    }

    public void setNext(Interceptor next) {
        throw new IllegalStateException("This interceptor must be the last interceptor in an interceptor chain");
    }

}
