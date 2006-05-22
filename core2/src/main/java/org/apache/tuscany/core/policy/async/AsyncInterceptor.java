package org.apache.tuscany.core.policy.async;

import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkManager;

import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageChannel;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.osoa.sca.CurrentModuleContext;
import org.osoa.sca.ModuleContext;
import org.osoa.sca.SCA;
import org.osoa.sca.ServiceRuntimeException;

/**
 * Uses a WorkManager to schedule asynchronous execution of invocations
 *
 * @version $$Rev$$ $$Date$$
 */
public class AsyncInterceptor implements Interceptor {

    private static final ContextBinder BINDER = new ContextBinder();
    private static final Message RESPONSE = new ImmutableMessage();

    private WorkManager workManager;
    private Interceptor next;
    private AsyncMonitor monitor;

    public AsyncInterceptor(WorkManager workManager, AsyncMonitor monitor) {
        this.workManager = workManager;
        this.monitor = monitor;
    }

    public Message invoke(final Message message) {
        final ModuleContext currentModuleContext = CurrentModuleContext.getContext();
        // Schedule the invocation of the next interceptor in a new Work instance
        try {
            workManager.scheduleWork(new Work() {
                public void run() {
                    ModuleContext oldModuleContext = CurrentModuleContext.getContext();
                    try {
                        AsyncInterceptor.BINDER.setContext(currentModuleContext);
                        next.invoke(message); // Invoke the next interceptor
                    } catch (Exception e) {
                        monitor.executionError(e);
                    } finally {
                        AsyncInterceptor.BINDER.setContext(oldModuleContext);
                    }
                }

                public void release() {
                }

            });
        } catch (WorkException e) {
            throw new ServiceRuntimeException(e);
        }
        return RESPONSE; // No return on a OneWay invocation.
    }

    public void setNext(Interceptor next) {
        this.next = next;
    }

    private static class ContextBinder extends SCA {
        public void setContext(ModuleContext context) {
            setModuleContext(context);
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
