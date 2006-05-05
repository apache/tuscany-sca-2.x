package org.apache.tuscany.spi.context;

/**
 * The runtime artifact representing an entry point, <code>ServiceContext</code> manages wire handler
 * instances that expose service operations offered by a component in the parent composite. The wire handler
 * instance is responsible for dispatching the request down an wire chain to the target instance. The wire
 * chain may contain {@link org.apache.tuscany.spi.wire.Interceptor}s and
 * {@link org.apache.tuscany.spi.wire.MessageHandler}s that implement policies or perform mediations on the
 * wire.
 * <p>
 * Service contexts are used by transport binding artifacts to invoke an operation on a service. The transport
 * binding uses an {@link java.lang.reflect.InvocationHandler} instance obtained from the <code>ServiceContext</code>
 * to perform the wire as in:
 *
 * <pre>
 *              CompositeContext compositeContext = ...
 *              ServiceContext ctx = (ServiceContext) compositeContext.getContext(&quot;source&quot;);
 *              Assert.assertNotNull(ctx);
 *              InvocationHandler handler = (InvocationHandler) ctx.getHandler();
 *              Object response = handler.invoke(null, operation, new Object[] { param });
 * </pre>
 *
 * The <code>Proxy</code> instance passed to <code>InvocationHandler</code> may be null as the client is invoking
 * directly on the handler.
 * <p>
 * Alternatively, the following will return a proxy implementing the service interface exposed by the entry point:
 *
 * <pre>
 *              CompositeContext compositeContext = ...
 *              ServiceContext ctx = (ServiceContext) compositeContext.getContext(&quot;source&quot;);
 *              Assert.assertNotNull(ctx);
 *              HelloWorld proxy = (Helloworld) ctx.getInstance(null); // service name not necessary
 * </pre>
 *
 * The proxy returned will be backed by the entry point wire chain.
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
 */
public interface ServiceContext<T extends Class> extends Context {

    /**
     * Returns the handler responsible for flowing a request through the entry point
     * @throws org.apache.tuscany.spi.context.TargetException
     */
    public Object getHandler() throws TargetException;

    /**
     * Returns the service interface configured for the entry poitn
     */
    public T getServiceInterface();
}
