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

package org.apache.tuscany.sca.client.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Properties;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.context.CompositeContext;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.ModuleActivatorExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.assembly.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.core.invocation.ExtensibleProxyFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.runtime.DomainRegistryFactory;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.ExtensibleDomainRegistryFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeProperties;
import org.apache.tuscany.sca.work.WorkScheduler;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;
import org.oasisopen.sca.ServiceRuntimeException;

public class Handler2 {

    private Properties properties;
    private ExtensionPointRegistry extensionPointRegistry;
    private EndpointRegistry endpointRegistry;
    private boolean localRuntime;
    private boolean closableClient;
    private String domainURI;
    

    public Handler2(ExtensionPointRegistry extensionPointRegistry, EndpointRegistry endpointRegistry, Properties properties) {
        this.extensionPointRegistry = extensionPointRegistry;
        this.endpointRegistry = endpointRegistry;
        this.localRuntime = true;
        this.properties = properties;
        initCloseable(properties);
    }

    public Handler2(String domainURI, Properties properties) throws NoSuchDomainException {
        this.domainURI = domainURI;
        this.properties = properties;
        initCloseable(properties);
        initExtensionPointRegistry();
    }
    
    
    private void initExtensionPointRegistry() throws NoSuchDomainException {
        this.extensionPointRegistry = new DefaultExtensionPointRegistry();
        extensionPointRegistry.start();

        FactoryExtensionPoint modelFactories = extensionPointRegistry.getExtensionPoint(FactoryExtensionPoint.class);
        RuntimeAssemblyFactory assemblyFactory = new RuntimeAssemblyFactory(extensionPointRegistry);
        modelFactories.addFactory(assemblyFactory);

        UtilityExtensionPoint utilities = extensionPointRegistry.getExtensionPoint(UtilityExtensionPoint.class);
        
        properties.setProperty("client", "true");
        
        utilities.getUtility(RuntimeProperties.class).setProperties(properties);
        utilities.getUtility(WorkScheduler.class);

        // Initialize the Tuscany module activators
        // The module activators will be started
        extensionPointRegistry.getExtensionPoint(ModuleActivatorExtensionPoint.class);

        getEndpointRegistry();
    }

    private EndpointRegistry getEndpointRegistry() throws NoSuchDomainException {
        DomainRegistryFactory domainRegistryFactory = ExtensibleDomainRegistryFactory.getInstance(extensionPointRegistry);
        
        String registryURI = domainURI;

        // TODO: theres better ways to do this but this gets things working for now
        if (registryURI.indexOf(":") == -1) {
            registryURI = "tuscanyclient:" + registryURI;
        }
        if (registryURI.startsWith("uri:")) {
            registryURI = "tuscanyclient:" + registryURI.substring(4);
        }
        if (registryURI.startsWith("tuscany:")) {
            registryURI = "tuscanyclient:" + registryURI.substring(8);
        }
        
      
        try {
            return domainRegistryFactory.getEndpointRegistry(registryURI, domainURI);
        } catch (Exception e) {
            throw new NoSuchDomainException(domainURI, e);
        }
    }
    

    private void initCloseable(Properties properties) {
        String s = properties.getProperty("org.apache.tuscany.sca.client.closeable");
        closableClient = (s != null) && Boolean.parseBoolean(s);
    }

    public <T> T getService(Class<T> interfaze, String serviceURI) throws NoSuchServiceException, NoSuchDomainException {
        String serviceName = null;
        if (serviceURI.contains("/")) {
            int i = serviceURI.indexOf("/");
            if (i < serviceURI.length() - 1) {
                serviceName = serviceURI.substring(i + 1);
            }
        }

        if (localRuntime) {
            return localRuntimeService(interfaze, serviceURI, serviceName);
        } else {
            return remoteService(interfaze, serviceURI, serviceName);
        }
    }
    
    private <T> T remoteService(Class<T> interfaze, String serviceURI, String serviceName) throws NoSuchDomainException {
        InvocationHandler handler = new SCAClientHandler(domainURI, serviceURI, interfaze, properties);
        return (T)Proxy.newProxyInstance(interfaze.getClassLoader(), new Class[]{interfaze}, handler);
    }

    private void initRegistries() {
        // TODO Auto-generated method stub
    }

    private <T> T localRuntimeService(Class<T> interfaze, String serviceURI, String serviceName)
        throws NoSuchServiceException {
        List<Endpoint> endpoints = endpointRegistry.findEndpoint(serviceURI);
        if (endpoints.size() < 1) {
            throw new NoSuchServiceException(serviceURI);
        }

        Endpoint ep = endpoints.get(0);
        if (((RuntimeComponent)ep.getComponent()).getComponentContext() != null) {
            return ((RuntimeComponent)ep.getComponent()).getServiceReference(interfaze, serviceName).getService();
        } else {
            return getRemoteProxy(interfaze, ep);
        }
    }

    private <T> T getRemoteProxy(Class<T> serviceInterface, Endpoint endpoint) throws NoSuchServiceException {
        FactoryExtensionPoint factories = extensionPointRegistry.getExtensionPoint(FactoryExtensionPoint.class);
        AssemblyFactory assemblyFactory = factories.getFactory(AssemblyFactory.class);
        JavaInterfaceFactory javaInterfaceFactory = factories.getFactory(JavaInterfaceFactory.class);
        ProxyFactory proxyFactory =
            new ExtensibleProxyFactory(extensionPointRegistry.getExtensionPoint(ProxyFactoryExtensionPoint.class));

        CompositeContext compositeContext =
            new CompositeContext(extensionPointRegistry, endpointRegistry, null, null, null, null);

        RuntimeEndpointReference epr;
        try {
            epr =
                createEndpointReference(javaInterfaceFactory,
                                        compositeContext,
                                        assemblyFactory,
                                        endpoint,
                                        serviceInterface);
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }

        return proxyFactory.createProxy(serviceInterface, epr);
    }

    private RuntimeEndpointReference createEndpointReference(JavaInterfaceFactory javaInterfaceFactory,
                                                             CompositeContext compositeContext,
                                                             AssemblyFactory assemblyFactory,
                                                             Endpoint endpoint,
                                                             Class<?> businessInterface)
        throws CloneNotSupportedException, InvalidInterfaceException {
        Component component = endpoint.getComponent();
        ComponentService service = endpoint.getService();
        ComponentReference componentReference = assemblyFactory.createComponentReference();
        componentReference.setName("sca.client." + service.getName());

        componentReference.setCallback(service.getCallback());
        componentReference.getTargets().add(service);
        componentReference.getPolicySets().addAll(service.getPolicySets());
        componentReference.getRequiredIntents().addAll(service.getRequiredIntents());
        componentReference.getBindings().add(endpoint.getBinding());

        InterfaceContract interfaceContract = service.getInterfaceContract();
        Service componentTypeService = service.getService();
        if (componentTypeService != null && componentTypeService.getInterfaceContract() != null) {
            interfaceContract = componentTypeService.getInterfaceContract();
        }
        interfaceContract = getInterfaceContract(javaInterfaceFactory, interfaceContract, businessInterface);
        componentReference.setInterfaceContract(interfaceContract);
        componentReference.setMultiplicity(Multiplicity.ONE_ONE);
        // component.getReferences().add(componentReference);

        // create endpoint reference
        EndpointReference endpointReference = assemblyFactory.createEndpointReference();
        endpointReference.setComponent(component);
        endpointReference.setReference(componentReference);
        endpointReference.setBinding(endpoint.getBinding());
        endpointReference.setUnresolved(false);
        endpointReference.setStatus(EndpointReference.Status.WIRED_TARGET_FOUND_AND_MATCHED);

        endpointReference.setTargetEndpoint(endpoint);

        componentReference.getEndpointReferences().add(endpointReference);
        ((RuntimeComponentReference)componentReference).setComponent((RuntimeComponent)component);
        ((RuntimeEndpointReference)endpointReference).bind(compositeContext);

        return (RuntimeEndpointReference)endpointReference;
    }

    private InterfaceContract getInterfaceContract(JavaInterfaceFactory javaInterfaceFactory,
                                                   InterfaceContract interfaceContract,
                                                   Class<?> businessInterface) 
                                                   throws CloneNotSupportedException, InvalidInterfaceException {
        if (businessInterface == null) {
            return interfaceContract;
        }
        boolean compatible = false;
        if (interfaceContract != null && interfaceContract.getInterface() != null) {
            Interface interfaze = interfaceContract.getInterface();
            if (interfaze instanceof JavaInterface) {
                Class<?> cls = ((JavaInterface)interfaze).getJavaClass();
                if (cls != null && businessInterface.isAssignableFrom(cls)) {
                    compatible = true;
                }
            }
        }

        if (!compatible) {
            // The interface is not assignable from the interface contract
            interfaceContract = javaInterfaceFactory.createJavaInterfaceContract();
            JavaInterface callInterface = javaInterfaceFactory.createJavaInterface(businessInterface);
            interfaceContract.setInterface(callInterface);
            if (callInterface.getCallbackClass() != null) {
                interfaceContract.setCallbackInterface(javaInterfaceFactory.createJavaInterface(callInterface
                    .getCallbackClass()));
            }
        }

        return interfaceContract;
    }
}
