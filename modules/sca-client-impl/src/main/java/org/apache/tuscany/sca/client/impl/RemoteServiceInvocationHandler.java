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
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.context.CompositeContext;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.invocation.ExtensibleProxyFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.runtime.ContributionDescription;
import org.apache.tuscany.sca.runtime.DomainRegistry;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * An InvocationHandler for invoking services where the component is not running locally.
 * It has two modes of operation for the cases where there either is or is not an existing 
 * Tuscany runtime locally. The SCAClient API has no close so when there is no existing
 * local runtime then one must be created and destroyed for each service invocation.
 */
public class RemoteServiceInvocationHandler implements InvocationHandler {

    private String domainURI;
    private String serviceName;
    public Class<?> serviceInterface;
    
    private ExtensionPointRegistry extensionsRegistry;
    private DomainRegistry domainRegistry;
    
    private InvocationHandler handler;
    private boolean reuse;

    /**
     * Constructor for when there is an existing Tuscany runtime for the domain
     */
    public RemoteServiceInvocationHandler(ExtensionPointRegistry extensionsRegistry, DomainRegistry domainRegistry, String serviceName, Class<?> serviceInterface) {
        this.extensionsRegistry = extensionsRegistry;
        this.domainRegistry = domainRegistry;
        this.domainURI = domainRegistry.getDomainURI();
        this.serviceName = serviceName;
        this.serviceInterface = serviceInterface;
        this.reuse = true;
    }
    
    /**
     * Constructor for when there is no existing Tuscany runtime for the domain
     * @param endpointRegistry2 
     * @param extensionPointRegistry 
     * @throws NoSuchServiceException 
     */
    public RemoteServiceInvocationHandler(ExtensionPointRegistry extensionsRegistry, DomainRegistry domainRegistry, String domainURI, String serviceName, Class<?> serviceInterface) throws NoSuchDomainException, NoSuchServiceException {
        this.extensionsRegistry = extensionsRegistry;
        this.domainRegistry = domainRegistry;
        this.domainURI = domainURI;
        this.serviceName = serviceName;
        this.serviceInterface = serviceInterface;
        this.reuse = false;
        if (serviceInterface == null) {
            getHandler();
        }
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            
            return getHandler().invoke(proxy, method, args);
            
        } finally {
            if (!reuse) {
                extensionsRegistry.stop();
                extensionsRegistry = null;
                handler = null;
            }
        }
    }

    private InvocationHandler getHandler() throws NoSuchDomainException, NoSuchServiceException {
        if (handler == null) {
            if (extensionsRegistry == null) {
                extensionsRegistry = RuntimeUtils.createExtensionPointRegistry();
            }
            if (domainRegistry == null) {
                domainRegistry = RuntimeUtils.getClientEndpointRegistry(extensionsRegistry, domainURI);
            }

            FactoryExtensionPoint factories = extensionsRegistry.getExtensionPoint(FactoryExtensionPoint.class);
            AssemblyFactory assemblyFactory = factories.getFactory(AssemblyFactory.class);
            JavaInterfaceFactory javaInterfaceFactory = factories.getFactory(JavaInterfaceFactory.class);
            ProxyFactory proxyFactory = new ExtensibleProxyFactory(extensionsRegistry.getExtensionPoint(ProxyFactoryExtensionPoint.class));

            CompositeContext compositeContext = new CompositeContext(extensionsRegistry, domainRegistry, null, domainURI, null, null);

            EndpointFinder endpointFinder = RuntimeUtils.getEndpointFinder(extensionsRegistry);
            Endpoint endpoint = endpointFinder.findEndpoint(domainRegistry, serviceName);
             
            if (serviceInterface == null) {
                try {
                    findInterface(endpoint);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            
            RuntimeEndpointReference epr;
            try {
                epr = createEndpointReference(javaInterfaceFactory, compositeContext, assemblyFactory, endpoint, serviceInterface);
            } catch (Exception e) {
                throw new ServiceRuntimeException(e);
            }

            this.handler = Proxy.getInvocationHandler(proxyFactory.createProxy(serviceInterface, epr));
        }
        return handler;
    }

    private void findInterface(Endpoint endpoint) throws MalformedURLException, ClassNotFoundException {
        Interface iface = endpoint.getService().getInterfaceContract().getInterface();
        if (iface instanceof JavaInterface) {
            String curi = domainRegistry.getContainingCompositesContributionURI(endpoint.getComponent().getName());
            if (curi != null) {
                ContributionDescription ic = domainRegistry.getInstalledContribution(curi);
                ClassLoader cl = new URLClassLoader(new URL[]{new URL(ic.getURL())});
                serviceInterface = cl.loadClass(((JavaInterface)iface).getName());
            }
        }
    }

    private RuntimeEndpointReference createEndpointReference(JavaInterfaceFactory javaInterfaceFactory, CompositeContext compositeContext, AssemblyFactory assemblyFactory, Endpoint endpoint, Class<?> businessInterface) throws CloneNotSupportedException, InvalidInterfaceException {
        Component component = endpoint.getComponent();
        ComponentService service = endpoint.getService();
        ComponentReference componentReference = assemblyFactory.createComponentReference();
        componentReference.setName("$sca.client$." + service.getName());

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

        return (RuntimeEndpointReference) endpointReference;
    }

    /**
     * @param interfaceContract
     * @param businessInterface
     * @return
     * @throws CloneNotSupportedException
     * @throws InvalidInterfaceException
     */
    private InterfaceContract getInterfaceContract(JavaInterfaceFactory javaInterfaceFactory, InterfaceContract interfaceContract, Class<?> businessInterface)
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
