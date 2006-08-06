package org.apache.tuscany.container.groovy;

import java.lang.reflect.InvocationTargetException;

import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;
import org.osoa.sca.ServiceRuntimeException;
import org.osoa.sca.SCA;

import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.MessageChannel;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.component.TargetException;

import org.apache.tuscany.core.policy.async.AsyncMonitor;

/**
 * Responsible for performing a non-blocking dispatch on a Groovy component implementation instance
 *
 * @version $Rev$ $Date$
 */
public class AsyncGroovyInvoker extends GroovyInvoker {

    private static final ContextBinder BINDER = new ContextBinder();
    private static final Message RESPONSE = new AsyncGroovyInvoker.ImmutableMessage();

    private OutboundWire wire;
    private WorkScheduler workScheduler;
    private AsyncMonitor monitor;
    private WorkContext workContext;
    private Object target;

    /**
     * Creates a new invoker
     *
     * @param operation     the operation the invoker is associated with
     * @param wire
     * @param component     the target component
     * @param workScheduler the work scheduler to run the invocation
     * @param monitor       the monitor to pass events to
     * @param workContext
     */
    public AsyncGroovyInvoker(String operation,
                              OutboundWire wire,
                              GroovyAtomicComponent component,
                              WorkScheduler workScheduler,
                              AsyncMonitor monitor,
                              WorkContext workContext) {
        super(operation, component);
        this.wire = wire;
        this.workScheduler = workScheduler;
        this.monitor = monitor;
        this.workContext = workContext;
    }

    // Override invocation methods to defer invocation to work item
    // Both methods return null to indicate asynchrony; result will
    // be conveyed by callback
    @Override
    public Object invokeTarget(final Object payload) throws InvocationTargetException {
        final CompositeContext currentContext = CurrentCompositeContext.getContext();
        // Schedule the invocation of the next interceptor in a new Work instance
        try {
            workScheduler.scheduleWork(new Runnable() {
                public void run() {
                    workContext.setCurrentInvocationWire(wire);
                    CompositeContext oldContext = CurrentCompositeContext.getContext();
                    try {
                        BINDER.setContext(currentContext);
                        // REVIEW response must be null for one-way and non-null for callback
                        AsyncGroovyInvoker.super.invokeTarget(payload);
                    } catch (Exception e) {
                        // REVIEW uncomment when it is available
                        // monitor.executionError(e);
                        e.printStackTrace();
                    } finally {
                        BINDER.setContext(oldContext);
                    }
                }
            });
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
        return RESPONSE;
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        // can't just call overriden invoke because it would bypass async
        try {
            Object resp = invokeTarget(msg.getBody());
            return (Message) resp;
        } catch (InvocationTargetException e) {
            // FIXME need to log exceptions
            e.printStackTrace();
            return null;
        } catch (Throwable e) {
            // FIXME need to log exceptions
            e.printStackTrace();
            return null;
        }
    }

    public AsyncGroovyInvoker clone() throws CloneNotSupportedException {
        AsyncGroovyInvoker invoker = (AsyncGroovyInvoker) super.clone();
        invoker.workScheduler = this.workScheduler;
        invoker.monitor = this.monitor;
        return invoker;
    }

    /**
     * Resolves the target service instance or returns a cached one
     */
    protected Object getInstance() throws TargetException {
        if (!cacheable) {
            return component.getTargetInstance();
        } else {
            if (target == null) {
                target = component.getTargetInstance();
            }
            return target;
        }
    }

    private static class ContextBinder extends SCA {
        public void setContext(CompositeContext context) {
            setCompositeContext(context);
        }

        public void start() {
            throw new AssertionError();
        }

        public void stop() {
            throw new AssertionError();
        }
    }

    /**
     * A dummy message passed back on an invocation
     */
    private static class ImmutableMessage implements Message {

        public Object getBody() {
            return null;
        }

        public void setBody(Object body) {
            throw new UnsupportedOperationException();
        }

        public void setTargetInvoker(TargetInvoker invoker) {
            throw new UnsupportedOperationException();
        }

        public TargetInvoker getTargetInvoker() {
            return null;
        }

        public MessageChannel getCallbackChannel() {
            return null;
        }

        public Message getRelatedCallbackMessage() {
            return null;
        }
    }
}
