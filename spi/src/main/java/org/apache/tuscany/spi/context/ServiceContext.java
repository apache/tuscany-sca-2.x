package org.apache.tuscany.spi.context;

import java.lang.reflect.InvocationHandler;

import org.apache.tuscany.spi.wire.TargetWire;

/**
 * Manages the context for a service configured for a binding. Bindings uses an {@link
 * java.lang.reflect.InvocationHandler} to perform an invocation as in:
 * <p/>
 * <pre>
 *              CompositeContext compositeContext = ...
 *              ServiceContext ctx = compositeContext.getServiceContext(&quot;source&quot;);
 *              InvocationHandler handler = (InvocationHandler) ctx.getHandler();
 *              Object response = handler.invoke(null, operation, new Object[] { param });
 * </pre>
 * <p/>
 * The <code>Proxy</code> instance passed to <code>InvocationHandler</code> may be null as the client is
 * invoking directly on the handler.
 * <p/>
 * Alternatively, the following will return a proxy implementing the service interface exposed by the entry
 * point:
 * <p/>
 * <pre>
 *              CompositeContext compositeContext = ...
 *              ServiceContext<HelloWorld> ctx = (ServiceContext<HelloWorld>)compositeContext.getServiceContext(&quot;source&quot;);
 *              HelloWorld proxy = ctx.getService();
 * </pre>
 * <p/>
 * The proxy returned will be backed by the context wire chain.
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
 */
public interface ServiceContext<T> extends Context<T> {

    /**
     * Returns the handler responsible for flowing a request through the service context
     *
     * @throws TargetException
     */
    public InvocationHandler getHandler() throws TargetException;

    /**
     * Returns the service interface configured for the service
     */
    public Class<T> getInterface();

    public TargetWire<T> getTargetWire();

    public void setTargetWire(TargetWire<T> wire);


}
