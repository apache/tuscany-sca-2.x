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

package org.apache.tuscany.sca.binding.rest.provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.Servlet;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.binding.rest.RESTBinding;
import org.apache.tuscany.sca.binding.rest.wireformat.json.JSONWireFormat;
import org.apache.tuscany.sca.binding.rest.wireformat.xml.XMLWireFormat;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.invocation.ExtensibleProxyFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.jaxrs.RootResourceClassGenerator;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.provider.EndpointProvider;
import org.apache.tuscany.sca.provider.OperationSelectorProvider;
import org.apache.tuscany.sca.provider.OperationSelectorProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.WireFormatProvider;
import org.apache.tuscany.sca.provider.WireFormatProviderFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.wink.server.utils.RegistrationUtils;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Implementation of an HTTP binding provider.
 *
 * @version $Rev$ $Date$
 */
public class RESTServiceBindingProvider implements EndpointProvider {
    private ExtensionPointRegistry extensionPoints;

    private RuntimeEndpoint endpoint;
    private RuntimeComponent component;
    private RuntimeComponentService service;
    private InterfaceContract serviceContract;
    private RESTBinding binding;
    private MessageFactory messageFactory;

    private OperationSelectorProvider osProvider;
    private WireFormatProvider wfProvider;

    private ServletHost servletHost;
    private String servletMapping;
    private RESTBindingListenerServlet bindingListenerServlet;

    private SimpleApplication application;

    public RESTServiceBindingProvider(RuntimeEndpoint endpoint,
                                      ExtensionPointRegistry extensionPoints,
                                      MessageFactory messageFactory,
                                      ServletHost servletHost) {

        this.endpoint = endpoint;
        this.component = (RuntimeComponent)endpoint.getComponent();
        this.service = (RuntimeComponentService)endpoint.getService();
        this.binding = (RESTBinding)endpoint.getBinding();

        this.extensionPoints = extensionPoints;
        this.messageFactory = messageFactory;
        this.servletHost = servletHost;

        // retrieve operation selector and wire format service providers

        ProviderFactoryExtensionPoint providerFactories =
            extensionPoints.getExtensionPoint(ProviderFactoryExtensionPoint.class);

        if (binding.getOperationSelector() != null) {
            // Configure the interceptors for operation selection
            OperationSelectorProviderFactory osProviderFactory =
                (OperationSelectorProviderFactory)providerFactories.getProviderFactory(binding.getOperationSelector()
                    .getClass());
            if (osProviderFactory != null) {
                this.osProvider = osProviderFactory.createServiceOperationSelectorProvider(endpoint);
            }
        }

        if (binding.getRequestWireFormat() != null && binding.getResponseWireFormat() != null) {
            // Configure the interceptors for wire format
            WireFormatProviderFactory wfProviderFactory =
                (WireFormatProviderFactory)providerFactories.getProviderFactory(binding.getRequestWireFormat()
                    .getClass());
            if (wfProviderFactory != null) {
                this.wfProvider = wfProviderFactory.createServiceWireFormatProvider(endpoint);
            }
        }

        //clone the service contract to avoid databinding issues
        try {
            this.serviceContract = (InterfaceContract)service.getInterfaceContract().clone();

            // configure data binding
            if (this.wfProvider != null) {
                wfProvider.configureWireFormatInterfaceContract(serviceContract);
            }
        } catch (CloneNotSupportedException e) {
            this.serviceContract = service.getInterfaceContract();
        }

    }

    public void start() {
        InvocationChain bindingChain = endpoint.getBindingInvocationChain();

        application = registerWithJAXRS();
        if (application != null) {
            return;
        }

        // Get the invokers for the supported operations
        Servlet servlet = null;
        Invoker bindingInvoker = bindingChain.getHeadInvoker();
        bindingListenerServlet = new RESTBindingListenerServlet(binding, bindingInvoker, messageFactory);
        for (InvocationChain invocationChain : endpoint.getInvocationChains()) {

            Operation operation = invocationChain.getTargetOperation();
            Invoker serviceInvoker = invocationChain.getHeadInvoker();
            String operationName = operation.getName();

            if (binding.getOperationSelector() != null || binding.getRequestWireFormat() != null) {
                bindingListenerServlet.setInvoker(serviceInvoker);
                servlet = bindingListenerServlet;
            } else if (operationName.equals("get")) {
                bindingListenerServlet.setGetInvoker(serviceInvoker);
                servlet = bindingListenerServlet;
            } else if (operationName.equals("conditionalGet")) {
                bindingListenerServlet.setConditionalGetInvoker(serviceInvoker);
                servlet = bindingListenerServlet;
            } else if (operationName.equals("delete")) {
                bindingListenerServlet.setDeleteInvoker(serviceInvoker);
                servlet = bindingListenerServlet;
            } else if (operationName.equals("conditionalDelete")) {
                bindingListenerServlet.setConditionalDeleteInvoker(serviceInvoker);
                servlet = bindingListenerServlet;
            } else if (operationName.equals("put")) {
                bindingListenerServlet.setPutInvoker(serviceInvoker);
                servlet = bindingListenerServlet;
            } else if (operationName.equals("conditionalPut")) {
                bindingListenerServlet.setConditionalPutInvoker(serviceInvoker);
                servlet = bindingListenerServlet;
            } else if (operationName.equals("post")) {
                bindingListenerServlet.setPostInvoker(serviceInvoker);
                servlet = bindingListenerServlet;
            } else if (operationName.equals("conditionalPost")) {
                bindingListenerServlet.setConditionalPostInvoker(serviceInvoker);
                servlet = bindingListenerServlet;
            } else if (operationName.equals("service")) {
                servlet = new RESTServiceListenerServlet(binding, serviceInvoker, messageFactory);
                break;
            }
        }
        if (servlet == null) {
            throw new IllegalStateException("No get or service method found on the service");
        }

        // Create our HTTP service listener Servlet and register it with the
        // Servlet host
        servletMapping = binding.getURI();
        if (!servletMapping.endsWith("/")) {
            servletMapping += "/";
        }
        if (!servletMapping.endsWith("*")) {
            servletMapping += "*";
        }

        servletHost.addServletMapping(servletMapping, servlet);
    }

    private static Map<QName, String> wireFormatToMediaTypeMapping = new HashMap<QName, String>();
    static {
        wireFormatToMediaTypeMapping.put(JSONWireFormat.REST_WIREFORMAT_JSON_QNAME, MediaType.APPLICATION_JSON);
        wireFormatToMediaTypeMapping.put(XMLWireFormat.REST_WIREFORMAT_XML_QNAME, MediaType.APPLICATION_XML);
    }

    private SimpleApplication registerWithJAXRS() {
        try {
            SimpleApplication application = null;

            JavaInterface javaInterface = (JavaInterface)endpoint.getComponentServiceInterfaceContract().getInterface();
            Class<?> interfaze = javaInterface.getJavaClass();

            boolean isJAXRS = isJAXRSResource(interfaze);
            if (isJAXRS) {
                application = new SimpleApplication(interfaze);

                TuscanyRESTServlet restServlet = new TuscanyRESTServlet(extensionPoints, application.resourceClass);

                // Create our HTTP service listener Servlet and register it with the
                // Servlet host
                servletMapping = binding.getURI();
                if (!servletMapping.endsWith("/")) {
                    servletMapping += "/";
                }
                if (!servletMapping.endsWith("*")) {
                    servletMapping += "*";
                }

                servletHost.addServletMapping(servletMapping, restServlet);
                RegistrationUtils.registerApplication(application, restServlet.getServletContext());
                return application;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    private class SimpleApplication extends Application {
        private Class<?> resourceClass;

        public SimpleApplication(Class<?> resourceClass) {
            super();
            // boolean isJAXRS = isJAXRSResource(resourceClass);
            // if (isJAXRS) {
            if (resourceClass.isInterface()) {
                this.resourceClass = generateResourceClass(resourceClass);
            } else {
                this.resourceClass = resourceClass;
            }
            // } else {
            //    throw new ServiceRuntimeException(resourceClass+" is not a JAX-RS resource class.");
            // }
        }

        @Override
        public Set<Class<?>> getClasses() {
            Set<Class<?>> classes = new HashSet<Class<?>>();
            classes.add(resourceClass);
            return classes;
        }

        private Class<?> generateResourceClass(Class<?> interfaze) {
            try {
                QName requestWireFormat = null;
                if (binding.getRequestWireFormat() != null) {
                    requestWireFormat = binding.getRequestWireFormat().getSchemaName();
                }
                QName responeWireFormat = null;
                if (binding.getResponseWireFormat() != null) {
                    responeWireFormat = binding.getRequestWireFormat().getSchemaName();
                }
                String requestMediaType = wireFormatToMediaTypeMapping.get(requestWireFormat);
                String responseMediaType = wireFormatToMediaTypeMapping.get(responeWireFormat);

                String uri = endpoint.getBinding().getURI();
                String path = URI.create(uri).getPath();
                Class<?> cls =
                    RootResourceClassGenerator.generateRootResourceClass(interfaze,
                                                                         path,
                                                                         requestMediaType,
                                                                         responseMediaType);
                ProxyFactory proxyFactory = ExtensibleProxyFactory.getInstance(extensionPoints);
                Object proxy = proxyFactory.createProxy(interfaze, endpoint);
                RootResourceClassGenerator.injectProxy(cls, proxy);
                return cls;
            } catch (Exception e) {
                throw new ServiceRuntimeException(e);
            }
        }

        public void destroy() {
            resourceClass = null;
        }
    }

    public static boolean isJAXRSResource(Class<?> cls) {
        for (Annotation a : cls.getAnnotations()) {
            if (a.annotationType().getName().startsWith("javax.ws.rs.")) {
                return true;
            }
        }
        for (Method method : cls.getMethods()) {
            for (Annotation a : method.getAnnotations()) {
                if (a.annotationType().getName().startsWith("javax.ws.rs.")) {
                    return true;
                }
            }
            
            /*
            for (Annotation[] annotations : method.getParameterAnnotations()) {
                for (Annotation a : annotations) {
                    if (a.annotationType().getName().startsWith("javax.ws.rs.")) {
                        return true;
                    }
                }

            }
            */
        }
        return false;
    }

    public void stop() {
        if (application != null) {
            application.destroy();
        }
        // Unregister the Servlet from the Servlet host
        servletHost.removeServletMapping(servletMapping);
    }

    public InterfaceContract getBindingInterfaceContract() {
        return serviceContract;
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

    /**
     * Add specific rest interceptor to invocation chain
     */
    public void configure() {

        InvocationChain bindingChain = endpoint.getBindingInvocationChain();

        if (wfProvider != null) {
            Interceptor interceptor = wfProvider.createInterceptor();
            if (interceptor != null) {
                bindingChain.addInterceptor(Phase.SERVICE_BINDING_WIREFORMAT, interceptor);
            }
        }

        if (osProvider != null) {
            Interceptor interceptor = osProvider.createInterceptor();
            if (interceptor != null) {
                bindingChain.addInterceptor(Phase.SERVICE_BINDING_OPERATION_SELECTOR, interceptor);
            }
        }

    }

}
