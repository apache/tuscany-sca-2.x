/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.apache.tuscany.sca.core.invocation.impl;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.BindingBuilder;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.BuilderExtensionPoint;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.context.CompositeContext;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ValidatingXMLInputFactory;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.assembly.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.core.invocation.AsyncFaultWrapper;
import org.apache.tuscany.sca.core.invocation.AsyncResponseException;
import org.apache.tuscany.sca.core.invocation.AsyncResponseHandler;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.util.FaultException;
import org.apache.tuscany.sca.interfacedef.util.WrapperInfo;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.Invocable;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.apache.tuscany.sca.work.WorkScheduler;
import org.oasisopen.sca.ServiceReference;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * An InvocationHandler which deals with JAXWS-defined asynchronous client Java API method calls
 * 
 * 2 asynchronous mappings exist for any given synchronous service operation, as shown in this example:
 *  public interface StockQuote {
 *      float getPrice(String ticker);
 *      Response<Float> getPriceAsync(String ticker);
 *      Future<?> getPriceAsync(String ticker, AsyncHandler<Float> handler);
 *  }
 *
 * - the second method is called the "polling method", since the returned Response<?> object permits
 *   the client to poll to see if the async call has completed
 * - the third method is called the "async callback method", since in this case the client application can specify
 *   a callback operation that is automatically called when the async call completes
 */
public class AsyncJDKInvocationHandler extends JDKInvocationHandler {

    private static final long serialVersionUID = 1L;

    private static int invocationCount = 10; // # of threads to use
    private static long maxWaitTime = 30; // Max wait time for completion = 30sec

    // Run the async service invocations using a ThreadPoolExecutor
    private ExecutorService theExecutor;

    public AsyncJDKInvocationHandler(ExtensionPointRegistry registry,
                                     MessageFactory messageFactory,
                                     ServiceReference<?> callableReference) {
        super(messageFactory, callableReference);
        initExecutorService(registry);
    }

    public AsyncJDKInvocationHandler(ExtensionPointRegistry registry,
                                     MessageFactory messageFactory,
                                     Class<?> businessInterface,
                                     Invocable source) {
        super(messageFactory, businessInterface, source);
        initExecutorService(registry);
    }

    private final void initExecutorService(ExtensionPointRegistry registry) {
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        WorkScheduler scheduler = utilities.getUtility(WorkScheduler.class);
        theExecutor = scheduler.getExecutorService();

        /*
        synchronized (AsyncJDKInvocationHandler.class) {
            theExecutor = utilities.getUtility(ExecutorService.class);
            if (theExecutor == null) {
                theExecutor =
                    new ThreadPoolExecutor(invocationCount, invocationCount, maxWaitTime, TimeUnit.SECONDS,
                                           new ArrayBlockingQueue<Runnable>(invocationCount));
                utilities.addUtility(ExecutorService.class, theExecutor);
            }
        }
        */
    }

    /**
     * Perform the invocation of the operation
     * - provides support for all 3 forms of client method: synchronous, polling and async callback
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // force the bind of the reference so that we can look at the 
        // target contract to see if it's asynchronous 
        source.getInvocationChains();

        if (isAsyncCallback(method)) {
            return doInvokeAsyncCallback(proxy, method, args);
        } else if (isAsyncPoll(method)) {
            return doInvokeAsyncPoll(proxy, method, args);
        } else {
            // Regular synchronous method call
            return doInvokeSync(proxy, method, args);
        }
    }

    /**
     * Indicates if a supplied method has the form of an async callback method
     * @param method - the method
     * @return - true if the method has the form of an async callback
     */
    protected boolean isAsyncCallback(Method method) {
        if (method.getName().endsWith("Async") && (method.getReturnType() == Future.class)) {
            if (method.getParameterTypes().length > 0) {
                return method.getParameterTypes()[method.getParameterTypes().length - 1] == AsyncHandler.class;
            }
        }
        return false;
    }

    /**
     * Indicates is a supplied method has the form of an async polling method
     * @param method - the method
     * @return - true if the method has the form of an async polling method
     */
    protected boolean isAsyncPoll(Method method) {
        return method.getName().endsWith("Async") && (method.getReturnType() == Response.class);
    }

    /**
     * Invoke an async polling method
     * @param proxy - the reference proxy
     * @param asyncMethod - the async method to invoke
     * @param args - array of input arguments to the method
     * @return - the Response<?> object that is returned to the client application, typed by the 
     *           type of the response
     */
    @SuppressWarnings("unchecked")
    protected Response doInvokeAsyncPoll(Object proxy, Method asyncMethod, Object[] args) {
        Method method = getNonAsyncMethod(asyncMethod);
        Class<?> returnType = method.getReturnType();
        // Allocate the Future<?> / Response<?> object - note: Response<?> is a subclass of Future<?>
        AsyncInvocationFutureImpl future = AsyncInvocationFutureImpl.newInstance(returnType, getInterfaceClassloader());
        try {
            invokeAsync(proxy, method, args, future, asyncMethod);
        } catch (Exception e) {
            future.setFault(new AsyncFaultWrapper(e));
        } catch (Throwable t) {
            Exception e =
                new ServiceRuntimeException("Received Throwable: " + t.getClass().getName()
                    + " when invoking: "
                    + asyncMethod.getName(), t);
            future.setFault(new AsyncFaultWrapper(e));
        } // end try 
        return future;
    } // end method doInvokeAsyncPoll

    /**
     * Provide a synchronous invocation of a service operation that is either synchronous or asynchronous
     * @return
     */
    protected Object doInvokeSync(Object proxy, Method method, Object[] args) throws Throwable {
        if (isAsyncInvocation(source)) {
            // Target service is asynchronous
            Class<?> returnType = method.getReturnType();
            AsyncInvocationFutureImpl future =
                AsyncInvocationFutureImpl.newInstance(returnType, getInterfaceClassloader());
            invokeAsync(proxy, method, args, future, method);
            // Wait for some maximum time for the result - 1000 seconds here
            // Really, if the service is async, the client should use async client methods to invoke the service
            // - and be prepared to wait a *really* long time
            Object response = null;
            try {
                response = future.get(1000, TimeUnit.SECONDS);
            } catch (ExecutionException ex) {
                throw ex.getCause();
            }
            return response;
        } else {
            // Target service is not asynchronous, so perform sync invocation
            return super.invoke(proxy, method, args);
        } // end if
    } // end method doInvokeSync

    /**
     * Invoke an async callback method - note that this form of the async client API has as its final parameter
     * an AsyncHandler method, used for callbacks to the client code
     * @param proxy - the reference proxy
     * @param asyncMethod - the async method to invoke
     * @param args - array of input arguments to the method
     * @return - the Future<?> object that is returned to the client application, typed by the type of
     *           the response
     */
    @SuppressWarnings("unchecked")
    private Object doInvokeAsyncCallback(final Object proxy, final Method asyncMethod, final Object[] args)
        throws Exception {
        Future<Response> future = theExecutor.submit(new Callable<Response>() {

            @Override
            public Response call() {
                AsyncHandler handler = (AsyncHandler)args[args.length - 1];
                Response response = doInvokeAsyncPoll(proxy, asyncMethod, Arrays.copyOf(args, args.length - 1));
                // Invoke the callback handler, if present
                if (handler != null) {
                    handler.handleResponse(response);
                } // end if
                return response;
            }
        });
        return future.get();

    } // end method doInvokeAsyncCallback

    /**
     * Invoke the target (synchronous) method asynchronously 
     * @param proxy - the reference proxy object
     * @param method - the method to invoke
     * @param args - arguments for the call
     * @param future - Future for handling the response
     * @return - returns the response from the invocation
     * @throws Throwable - if an exception is thrown during the invocation
     */
    @SuppressWarnings("unchecked")
    private void invokeAsync(Object proxy,
                             Method method,
                             Object[] args,
                             AsyncInvocationFutureImpl future,
                             Method asyncMethod) throws Throwable {
        if (source == null) {
            throw new ServiceRuntimeException("No runtime source is available");
        }

        if (source instanceof RuntimeEndpointReference) {
            RuntimeEndpointReference epr = (RuntimeEndpointReference)source;
            if (epr.isOutOfDate()) {
                epr.rebuild();
                chains.clear();
            }
        } // end if

        InvocationChain chain = getInvocationChain(method, source);

        if (chain == null) {
            throw new IllegalArgumentException("No matching operation is found: " + method);
        }

        // Organize for an async service
        RuntimeEndpoint theEndpoint = getAsyncCallback(source);
        boolean isAsyncService = false;
        if (theEndpoint != null) {
            // ... the service is asynchronous ...
            attachFuture(theEndpoint, future);
            isAsyncService = true;
        } else {
            // ... the service is synchronous ...
        } // end if

        // Perform the invocations on separate thread...
        theExecutor.submit(new separateThreadInvoker(chain, args, source, future, asyncMethod, isAsyncService));

        return;
    } // end method invokeAsync

    /**
     * An inner class which acts as a runnable task for invoking services asynchronously on threads that are separate from
     * those used to execute operations of components
     * 
     * This supports both synchronous services and asynchronous services
     */
    private class separateThreadInvoker implements Runnable {

        private AsyncInvocationFutureImpl future;
        private Method asyncMethod;
        private InvocationChain chain;
        private Object[] args;
        private Invocable invocable;
        private boolean isAsyncService;

        public separateThreadInvoker(InvocationChain chain,
                                     Object[] args,
                                     Invocable invocable,
                                     AsyncInvocationFutureImpl future,
                                     Method asyncMethod,
                                     boolean isAsyncService) {
            super();
            this.chain = chain;
            this.asyncMethod = asyncMethod;
            this.args = args;
            this.invocable = invocable;
            this.future = future;
            this.isAsyncService = isAsyncService;
        } // end constructor

        public void run() {
            Object result;

            try {
                if (isAsyncService) {
                    invoke(chain, args, invocable, future.getUniqueID());
                    // The result is returned asynchronously via the future...
                } else {
                    // ... the service is synchronous ...
                    result = invoke(chain, args, invocable);
                    Type type = null;
                    if (asyncMethod.getReturnType() == Future.class) {
                        // For callback async menthod
                        Type[] types = asyncMethod.getGenericParameterTypes();
                        if (types.length > 0 && asyncMethod.getParameterTypes()[types.length - 1] == AsyncHandler.class) {
                            // Last paremeter, AsyncHandler<T>
                            type = types[types.length - 1];
                        }
                    } else if (asyncMethod.getReturnType() == Response.class) {
                        // For the polling method, Response<T>
                        type = asyncMethod.getGenericReturnType();
                    }
                    if (type instanceof ParameterizedType) {
                        // Check if the parameterized type of Response<T> is a doc-lit-wrapper class
                        Class<?> wrapperClass = (Class<?>)((ParameterizedType)type).getActualTypeArguments()[0];
                        WrapperInfo wrapperInfo = chain.getSourceOperation().getWrapper();
                        if (wrapperInfo != null && wrapperInfo.getOutputWrapperClass() == wrapperClass) {
                            Object wrapper = wrapperClass.newInstance();
                            // Find the 1st matching property
                            for (PropertyDescriptor p : Introspector.getBeanInfo(wrapperClass).getPropertyDescriptors()) {
                                if (p.getWriteMethod() == null) {
                                    // There is a "class" property ...
                                    continue;
                                }
                                if (p.getWriteMethod().getParameterTypes()[0].isInstance(result)) {
                                    p.getWriteMethod().invoke(wrapper, result);
                                    result = wrapper;
                                    break;
                                }
                            }

                        }
                    }
                    future.setResponse(result);
                } // end if
            } catch (ServiceRuntimeException s) {
                Throwable e = s.getCause();
                if (e != null && e instanceof FaultException) {
                    if ("AsyncResponse".equals(e.getMessage())) {
                        // Do nothing...
                    } else {
                        future.setFault(new AsyncFaultWrapper(s));
                    } // end if 
                } // end if
            } catch (AsyncResponseException ar) {
                // do nothing			
            } catch (Throwable t) {
                System.out.println("Async invoke got exception: " + t.toString());
                future.setFault(new AsyncFaultWrapper(t));
            } // end try

        } // end method run

    } // end class separateThreadInvoker

    /**
     * Attaches a future to the callback endpoint - so that the Future is triggered when a response is
     * received from the asynchronous service invocation associated with the Future
     * @param endpoint - the async callback endpoint
     * @param future - the async invocation future to attach
     */
    private void attachFuture(RuntimeEndpoint endpoint, AsyncInvocationFutureImpl<?> future) {
        Implementation impl = endpoint.getComponent().getImplementation();
        AsyncResponseHandlerImpl<?> asyncHandler = (AsyncResponseHandlerImpl<?>)impl;
        asyncHandler.addFuture(future);
    } // end method attachFuture

    /**
     * Get the async callback endpoint - if not already created, create and start it
     * @param source - the RuntimeEndpointReference which needs an async callback endpoint
     * @param future 
     * @return - the RuntimeEndpoint of the async callback
     */
    private RuntimeEndpoint getAsyncCallback(Invocable source) {
        if (!(source instanceof RuntimeEndpointReference))
            return null;
        RuntimeEndpointReference epr = (RuntimeEndpointReference)source;
        if (!isAsyncInvocation(epr))
            return null;
        RuntimeEndpoint endpoint;
        synchronized (epr) {
            endpoint = (RuntimeEndpoint)epr.getCallbackEndpoint();
            // If the async callback endpoint is already created, return it...
            if (endpoint != null)
                return endpoint;
            // Create the endpoint for the async callback
            endpoint = createAsyncCallbackEndpoint(epr);
            epr.setCallbackEndpoint(endpoint);
        } // end synchronized

        // Activate the new callback endpoint
        startEndpoint(epr.getCompositeContext(), endpoint);
        endpoint.getInvocationChains();

        return endpoint;
    } // end method setupAsyncCallback

    /**
     * Start the callback endpoint
     * @param compositeContext - the composite context
     * @param ep - the endpoint to start
     */
    private void startEndpoint(CompositeContext compositeContext, RuntimeEndpoint ep) {
        for (PolicyProvider policyProvider : ep.getPolicyProviders()) {
            policyProvider.start();
        } // end for

        final ServiceBindingProvider bindingProvider = ep.getBindingProvider();
        if (bindingProvider != null) {
            // Allow bindings to add shutdown hooks. Requires RuntimePermission shutdownHooks in policy.
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                public Object run() {
                    bindingProvider.start();
                    return null;
                }
            });
            compositeContext.getEndpointRegistry().addEndpoint(ep);
        }
    } // end method startEndpoint

    /**
     * Create the async callback endpoint for a reference that is going to invoke an asyncInvocation service
     * @param epr - the RuntimeEndpointReference for which the callback is created
     * @return - a RuntimeEndpoint representing the callback endpoint
     */
    private RuntimeEndpoint createAsyncCallbackEndpoint(RuntimeEndpointReference epr) {
        CompositeContext compositeContext = epr.getCompositeContext();
        RuntimeAssemblyFactory assemblyFactory = getAssemblyFactory(compositeContext);
        RuntimeEndpoint endpoint = (RuntimeEndpoint)assemblyFactory.createEndpoint();
        endpoint.bind(compositeContext);

        // Create a pseudo-component and pseudo-service 
        // - need to end with a chain with an invoker into the AsyncCallbackHandler class
        RuntimeComponent fakeComponent = null;
        try {
            fakeComponent = (RuntimeComponent)epr.getComponent().clone();
            applyImplementation(fakeComponent);
        } catch (CloneNotSupportedException e2) {
            // will not happen
        } // end try
        endpoint.setComponent(fakeComponent);

        // Create pseudo-service
        ComponentService service = assemblyFactory.createComponentService();
        ExtensionPointRegistry registry = compositeContext.getExtensionPointRegistry();
        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        JavaInterfaceFactory javaInterfaceFactory =
            (JavaInterfaceFactory)modelFactories.getFactory(JavaInterfaceFactory.class);
        JavaInterfaceContract interfaceContract = javaInterfaceFactory.createJavaInterfaceContract();
        try {
            interfaceContract.setInterface(javaInterfaceFactory.createJavaInterface(AsyncResponseHandler.class));
        } catch (InvalidInterfaceException e1) {
            // Nothing to do here - will not happen
        } // end try
        service.setInterfaceContract(interfaceContract);
        String serviceName = epr.getReference().getName() + "_asyncCallback";
        service.setName(serviceName);
        // MJE 06/12/2010 - fixup for JMS binding code which looks at the implementation service
        // as well as the component service...
        // Create a pseudo implementation service...
        Service implService = assemblyFactory.createService();
        implService.setName(serviceName);
        implService.setInterfaceContract(interfaceContract);
        service.setService(implService);
        //
        endpoint.setService(service);
        // Set pseudo-service onto the pseudo-component
        List<ComponentService> services = fakeComponent.getServices();
        services.clear();
        services.add(service);

        // Create a binding
        Binding binding = createMatchingBinding(epr.getBinding(), fakeComponent, service, registry);
        endpoint.setBinding(binding);

        // Need to establish policies here (binding has some...)
        endpoint.getRequiredIntents().addAll(epr.getRequiredIntents());
        endpoint.getPolicySets().addAll(epr.getPolicySets());
        String epURI = epr.getComponent().getName() + "#service-binding(" + serviceName + "/" + serviceName + ")";
        endpoint.setURI(epURI);
        endpoint.setUnresolved(false);
        return endpoint;
    }

    /**
     * Create a matching binding to a supplied binding
     * - the matching binding has the same binding type, but is for the supplied component and service
     * @param matchBinding - the binding to match
     * @param component - the component 
     * @param service - the service
     * @param registry - registry for extensions
     * @return - the matching binding, or null if it could not be created
     */
    @SuppressWarnings("unchecked")
    private Binding createMatchingBinding(Binding matchBinding,
                                          RuntimeComponent component,
                                          ComponentService service,
                                          ExtensionPointRegistry registry) {
        // Since there is no simple way to obtain a Factory for a binding where the type is not known ahead of
        // time, the process followed here is to generate the <binding.xxx/> XML element from the binding type QName
        // and then read the XML using the processor for that XML...
        QName bindingName = matchBinding.getType();
        String bindingXML =
            "<ns1:" + bindingName.getLocalPart() + " xmlns:ns1='" + bindingName.getNamespaceURI() + "'/>";

        StAXArtifactProcessorExtensionPoint processors =
            registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        StAXArtifactProcessor<?> processor = (StAXArtifactProcessor<?>)processors.getProcessor(bindingName);

        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        ValidatingXMLInputFactory inputFactory = modelFactories.getFactory(ValidatingXMLInputFactory.class);
        StreamSource source = new StreamSource(new StringReader(bindingXML));

        ProcessorContext context = new ProcessorContext();
        try {
            XMLStreamReader reader = inputFactory.createXMLStreamReader(source);
            reader.next();
            Binding newBinding = (Binding)processor.read(reader, context);

            // Create a URI address for the callback based on the Component_Name/Reference_Name pattern
            String callbackURI = "/" + component.getName() + "/" + service.getName();
            newBinding.setURI(callbackURI);

            BuilderExtensionPoint builders = registry.getExtensionPoint(BuilderExtensionPoint.class);
            BindingBuilder builder = builders.getBindingBuilder(newBinding.getType());
            if (builder != null) {
                org.apache.tuscany.sca.assembly.builder.BuilderContext builderContext = new BuilderContext(registry);
                builder.build(component, service, newBinding, builderContext, true);
            } // end if

            return newBinding;
        } catch (ContributionReadException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }

        return null;
    } // end method createMatchingBinding

    /**
     * Gets a RuntimeAssemblyFactory from the CompositeContext
     * @param compositeContext
     * @return the RuntimeAssemblyFactory
     */
    private RuntimeAssemblyFactory getAssemblyFactory(CompositeContext compositeContext) {
        ExtensionPointRegistry registry = compositeContext.getExtensionPointRegistry();
        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        return (RuntimeAssemblyFactory)modelFactories.getFactory(AssemblyFactory.class);
    } // end method RuntimeAssemblyFactory

    /**
     * Applies an AsyncResponseHandlerImpl as the implementation of a RuntimeComponent
     * - the AsyncResponseHandlerImpl acts as both the implementation class and the implementation provider...
     * @param component - the component
     */
    private void applyImplementation(RuntimeComponent component) {
        AsyncResponseHandlerImpl<?> asyncHandler = new AsyncResponseHandlerImpl<Object>();
        component.setImplementation(asyncHandler);
        component.setImplementationProvider(asyncHandler);
        return;
    } // end method getImplementationProvider

    private static QName ASYNC_INVOKE = new QName(Constants.SCA11_NS, "asyncInvocation");

    /**
     * Determines if the service invocation is asynchronous
     * @param source - the EPR involved in the invocation
     * @return - true if the invocation is async
     */
    private boolean isAsyncInvocation(Invocable source) {
        if (!(source instanceof RuntimeEndpointReference))
            return false;
        RuntimeEndpointReference epr = (RuntimeEndpointReference)source;
        // First check is to see if the EPR itself has the asyncInvocation intent marked
        for (Intent intent : epr.getRequiredIntents()) {
            if (intent.getName().equals(ASYNC_INVOKE))
                return true;
        } // end for

        // Second check is to see if the target service has the asyncInvocation intent marked
        Endpoint ep = epr.getTargetEndpoint();
        for (Intent intent : ep.getRequiredIntents()) {
            if (intent.getName().equals(ASYNC_INVOKE))
                return true;
        } // end for
        return false;
    } // end isAsyncInvocation

    /**
     * Return the synchronous method that is the equivalent of an async method
     * @param asyncMethod - the async method
     * @return - the equivalent synchronous method
     */
    protected Method getNonAsyncMethod(Method asyncMethod) {
        String methodName = asyncMethod.getName().substring(0, asyncMethod.getName().length() - 5);
        for (Method m : businessInterface.getMethods()) {
            if (methodName.equals(m.getName())) {
                return m;
            }
        }
        throw new IllegalStateException("No synchronous method matching async method " + asyncMethod.getName());
    } // end method getNonAsyncMethod

    /**
     * Gets the classloader of the business interface
     * @return
     */
    private ClassLoader getInterfaceClassloader() {
        return businessInterface.getClassLoader();
    }
}
