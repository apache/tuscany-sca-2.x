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

import java.io.StringReader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
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
import org.apache.tuscany.sca.core.assembly.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.core.assembly.impl.RuntimeEndpointReferenceImpl;
import org.apache.tuscany.sca.core.invocation.AsyncFaultWrapper;
import org.apache.tuscany.sca.core.invocation.AsyncResponseHandler;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.provider.ImplementationProviderFactory;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.provider.RuntimeProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.Invocable;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.oasisopen.sca.ComponentContext;
import org.oasisopen.sca.ServiceReference;
import org.oasisopen.sca.ServiceRuntimeException;
import org.oasisopen.sca.annotation.AsyncInvocation;

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

    public AsyncJDKInvocationHandler(MessageFactory messageFactory, ServiceReference<?> callableReference) {
        super(messageFactory, callableReference);
    }

    public AsyncJDKInvocationHandler(MessageFactory messageFactory,
                                     Class<?> businessInterface,
                                     Invocable source) {
        super(messageFactory, businessInterface, source);
    }

    /**
     * Perform the invocation of the operation
     * - provides support for all 3 forms of client method: synchronous, polling and async callback
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isAsyncCallback(method)) {
            return doInvokeAsyncCallback(proxy, method, args);            
        } else if (isAsyncPoll(method)) {
            return doInvokeAsyncPoll(proxy, method, args);            
        } else {
        	// Regular synchronous method call
            return super.invoke(proxy, method, args);
        }
    }

    /**
     * Indicates if a supplied method has the form of an async callback method
     * @param method - the method
     * @return - true if the method has the form of an async callback
     */
    protected boolean isAsyncCallback(Method method) {
        if (method.getName().endsWith("Async") && (method.getReturnType().isAssignableFrom(Future.class))) {
            if (method.getParameterTypes().length > 0) {
                return method.getParameterTypes()[method.getParameterTypes().length-1].isAssignableFrom(AsyncHandler.class);
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
        return method.getName().endsWith("Async") && (method.getReturnType().isAssignableFrom(Response.class));
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
        Object response;
        Class<?> returnType = getNonAsyncMethod(asyncMethod).getReturnType();
        // Allocate the Future<?> / Response<?> object - note: Response<?> is a subclass of Future<?>
        AsyncInvocationFutureImpl future = AsyncInvocationFutureImpl.newInstance( returnType );
        try {
            response = invokeAsync(proxy, getNonAsyncMethod(asyncMethod), args, future);
            future.setResponse(response);
        } catch (Exception e) {
            future.setFault( new AsyncFaultWrapper(e) );
        } catch (Throwable t ) {
        	Exception e = new ServiceRuntimeException("Received Throwable: " + t.getClass().getName() + 
        			                                  " when invoking: " + asyncMethod.getName(), t);
        	future.setFault( new AsyncFaultWrapper(e) );
        } // end try 
        return future;
        //return new AsyncResponse(response, isException);
    } // end method doInvokeAsyncPoll

    /**
     * Invoke an async callback method
     * @param proxy - the reference proxy
     * @param asyncMethod - the async method to invoke
     * @param args - array of input arguments to the method
     * @return - the Future<?> object that is returned to the client application, typed by the type of
     *           the response
     */
    @SuppressWarnings("unchecked")
	private Object doInvokeAsyncCallback(Object proxy, Method asyncMethod, Object[] args) {
        AsyncHandler handler = (AsyncHandler)args[args.length-1];
        Response response = doInvokeAsyncPoll(proxy,asyncMethod,Arrays.copyOf(args, args.length-1));
        handler.handleResponse(response);
        
        return response;
    } // end method doInvokeAsyncCallback

    /**
     * Invoke the target method on 
     * @param proxy
     * @param method - the method to invoke
     * @param args - arguments for the call
     * @param future - Future for handling the response
     * @return - returns the response from the invocation
     * @throws Throwable - if an exception is thrown during the invocation
     */
    @SuppressWarnings("unchecked")
	private Object invokeAsync(Object proxy, Method method, Object[] args, AsyncInvocationFutureImpl future) throws Throwable {
        if (Object.class == method.getDeclaringClass()) {
            return invokeObjectMethod(method, args);
        }
        if (source == null) {
            throw new ServiceRuntimeException("No runtime source is available");
        }
        
        if (source instanceof RuntimeEndpointReference) {
            RuntimeEndpointReference epr = (RuntimeEndpointReference)source;
            if (epr.isOutOfDate()) {
                epr.rebuild();
                chains.clear();
            }
        }
        
        InvocationChain chain = getInvocationChain(method, source);
        
        if (chain == null) {
            throw new IllegalArgumentException("No matching operation is found: " + method);
        }
        
        RuntimeEndpoint theEndpoint = getAsyncCallback( source );
        attachFuture( theEndpoint, future );
        
        // send the invocation down the source
        Object result = super.invoke(chain, args, source);

        return result;
    } // end method invokeAsync
    
    /**
     * Attaches a future to the callback endpoint - so that the Future is triggered when a response is
     * received from the asynchronous service invocation associated with the Future
     * @param endpoint - the async callback endpoint
     * @param future - the async invocation future to attach
     */
    private void attachFuture( RuntimeEndpoint endpoint, AsyncInvocationFutureImpl future ) {
    	Implementation impl = endpoint.getComponent().getImplementation();
    	AsyncResponseHandlerImpl<?> asyncHandler = (AsyncResponseHandlerImpl<?>) impl;
    	asyncHandler.addFuture(future);
    } // end method attachFuture
    
    /**
     * Get the async callback endpoint - if not already created, create and start it
     * @param source - the RuntimeEndpointReference which needs an async callback endpoint
     * @param future 
     * @return - the RuntimeEndpoint of the async callback
     */
    private RuntimeEndpoint getAsyncCallback( Invocable source ) {
    	if( !(source instanceof RuntimeEndpointReference) ) return null;
		RuntimeEndpointReference epr = (RuntimeEndpointReference) source;
    	if( !isAsyncInvocation( epr ) ) return null;
    	RuntimeEndpoint endpoint;
    	synchronized( epr ) {
    		endpoint = (RuntimeEndpoint)epr.getCallbackEndpoint();
    		if( endpoint != null ) return endpoint;
	    	// Create the endpoint for the async callback
	    	endpoint = createAsyncCallbackEndpoint( epr );
	    	epr.setCallbackEndpoint(endpoint);
    	}
    	
    	// Activate the new callback endpoint
    	startEndpoint( epr.getCompositeContext(), endpoint );
    	endpoint.getInvocationChains();
    	
    	return endpoint;
    } // end method setupAsyncCallback
    
    /**
     * Start the callback endpoint
     * @param compositeContext - the composite context
     * @param ep - the endpoint to start
     */
    private void startEndpoint(CompositeContext compositeContext, RuntimeEndpoint ep ) {
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
    private RuntimeEndpoint createAsyncCallbackEndpoint( RuntimeEndpointReference epr ) {
    	CompositeContext compositeContext = epr.getCompositeContext();
    	RuntimeAssemblyFactory assemblyFactory = getAssemblyFactory( compositeContext );
        RuntimeEndpoint endpoint = (RuntimeEndpoint)assemblyFactory.createEndpoint();
        endpoint.bind( compositeContext );
        
        // Create a pseudo-component and pseudo-service 
        // - need to end with a chain with an invoker into the AsyncCallbackHandler class
        RuntimeComponent fakeComponent = null;
        try {
			fakeComponent = (RuntimeComponent)epr.getComponent().clone();
			applyImplementation( fakeComponent );
		} catch (CloneNotSupportedException e2) {
			// will not happen
		} // end try
        endpoint.setComponent(fakeComponent);
        
        // Create pseudo-service
        ComponentService service = assemblyFactory.createComponentService();
    	ExtensionPointRegistry registry = compositeContext.getExtensionPointRegistry();
        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        JavaInterfaceFactory javaInterfaceFactory = (JavaInterfaceFactory)modelFactories.getFactory(JavaInterfaceFactory.class);
        JavaInterfaceContract interfaceContract = javaInterfaceFactory.createJavaInterfaceContract();
        try {
			interfaceContract.setInterface(javaInterfaceFactory.createJavaInterface(AsyncResponseHandler.class));
		} catch (InvalidInterfaceException e1) {
			// Nothing to do here - will not happen
		} // end try
        service.setInterfaceContract(interfaceContract);
        String serviceName = epr.getReference().getName() + "_asyncCallback";
        service.setName(serviceName);
        endpoint.setService(service);
        // Set pseudo-service onto the pseudo-component
        List<ComponentService> services = fakeComponent.getServices();
        services.clear();
        services.add(service);
        
        Binding eprBinding = epr.getBinding();
        try {
			Binding binding = (Binding)eprBinding.clone();
			// Create a binding
			binding = createMatchingBinding( eprBinding, fakeComponent, service, registry );
					
			// Create a URI address for the callback based on the Component_Name/Reference_Name pattern
			//String callbackURI = "/" + epr.getComponent().getName() + "/" + serviceName;
			//binding.setURI(callbackURI);
			endpoint.setBinding(binding);
		} catch (CloneNotSupportedException e) {
			// will not happen
		} // end try
		
		// Need to establish policies here (binding has some...)
		endpoint.getRequiredIntents().addAll( epr.getRequiredIntents() );
		endpoint.getPolicySets().addAll( epr.getPolicySets() );
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
	private Binding createMatchingBinding( Binding matchBinding, RuntimeComponent component, 
			                               ComponentService service, ExtensionPointRegistry registry ) {
    	// Since there is no simple way to obtain a Factory for a binding where the type is not known ahead of
    	// time, the process followed here is to generate the <binding.xxx/> XML element from the binding type QName
    	// and then read the XML using the processor for that XML...
    	QName bindingName = matchBinding.getType();
    	String bindingXML = "<ns1:" + bindingName.getLocalPart() + " xmlns:ns1='" + bindingName.getNamespaceURI() + "'/>";
    	
    	StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
    	StAXArtifactProcessor<?> processor = (StAXArtifactProcessor<?>)processors.getProcessor(bindingName);
    	
    	FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
    	ValidatingXMLInputFactory inputFactory = modelFactories.getFactory(ValidatingXMLInputFactory.class);    		
    	StreamSource source = new StreamSource( new StringReader(bindingXML) );
    	
    	ProcessorContext context = new ProcessorContext();
		try {
			XMLStreamReader reader = inputFactory.createXMLStreamReader(source);
			reader.next();
			Binding newBinding = (Binding) processor.read(reader, context );
			
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
    private RuntimeAssemblyFactory getAssemblyFactory( CompositeContext compositeContext ) {
    	ExtensionPointRegistry registry = compositeContext.getExtensionPointRegistry();
        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        return (RuntimeAssemblyFactory)modelFactories.getFactory(AssemblyFactory.class);
    } // end method RuntimeAssemblyFactory
    
    private void applyImplementation( RuntimeComponent component ) {
    	AsyncResponseHandlerImpl<?> asyncHandler = new AsyncResponseHandlerImpl<Object>();
    	component.setImplementation( asyncHandler );
    	component.setImplementationProvider( asyncHandler );
        return;
    } // end method getImplementationProvider
    
    private static QName ASYNC_INVOKE = new QName( Constants.SCA11_NS, "asyncInvocation" );
    /**
     * Determines if the service invocation is asynchronous
     * @param source - the EPR involved in the invocation
     * @return - true if the invocation is async
     */
    private boolean isAsyncInvocation( RuntimeEndpointReference source ) {
		RuntimeEndpointReference epr = (RuntimeEndpointReference) source;
		// First check is to see if the EPR itself has the asyncInvocation intent marked
		for( Intent intent : epr.getRequiredIntents() ) {
			if ( intent.getName().equals(ASYNC_INVOKE) ) return true;
		} // end for
		
		// Second check is to see if the target service has the asyncInvocation intent marked
		Endpoint ep = epr.getTargetEndpoint();
		for( Intent intent : ep.getRequiredIntents() ) {
			if ( intent.getName().equals(ASYNC_INVOKE) ) return true;
		} // end for
    	return false;
    } // end isAsyncInvocation
    
    /**
     * Return the synchronous method that is the equivalent of an async method
     * @param asyncMethod - the async method
     * @return - the equivalent synchronous method
     */
    protected Method getNonAsyncMethod(Method asyncMethod) {
        String methodName = asyncMethod.getName().substring(0, asyncMethod.getName().length()-5);
        for (Method m : businessInterface.getMethods()) {
            if (methodName.equals(m.getName())) {
                return m;
            }
        }
        throw new IllegalStateException("No synchronous method matching async method " + asyncMethod.getName());
    }
}
