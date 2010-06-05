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
import java.util.List;
import java.util.Properties;
import java.util.UUID;

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
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.invocation.ExtensibleProxyFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.impl.NodeFactoryImpl;
import org.apache.tuscany.sca.runtime.DomainRegistryFactory;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.ExtensibleDomainRegistryFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeProperties;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;
import org.oasisopen.sca.ServiceRuntimeException;

public class SCAClientHandler implements InvocationHandler {

    private String domainURI;
    private String serviceName;
    private Class<?> serviceInterface;
    
    public SCAClientHandler(String domainURI, String serviceName, Class<?> serviceInterface) {
        this.domainURI = domainURI;
        this.serviceName = serviceName;
        this.serviceInterface = serviceInterface;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        NodeFactoryImpl nodeFactory = (NodeFactoryImpl)NodeFactory.newInstance(domainURI);
        try {
            nodeFactory.init();
            
            ExtensionPointRegistry extensionsRegistry = nodeFactory.getExtensionPointRegistry();
            Properties props = extensionsRegistry.getExtensionPoint(UtilityExtensionPoint.class).getUtility(RuntimeProperties.class).getProperties();
            props.setProperty("client", "true");
            DomainRegistryFactory domainRegistryFactory = ExtensibleDomainRegistryFactory.getInstance(extensionsRegistry);
            
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
            
            EndpointRegistry endpointRegistry;
            try {
                endpointRegistry = domainRegistryFactory.getEndpointRegistry(registryURI, domainURI);
            } catch (Exception e) {
                throw new NoSuchDomainException(domainURI, e);
            }
            
            FactoryExtensionPoint factories = extensionsRegistry.getExtensionPoint(FactoryExtensionPoint.class);
            AssemblyFactory assemblyFactory = factories.getFactory(AssemblyFactory.class);
            JavaInterfaceFactory javaInterfaceFactory = factories.getFactory(JavaInterfaceFactory.class);
            ProxyFactory proxyFactory = new ExtensibleProxyFactory(extensionsRegistry.getExtensionPoint(ProxyFactoryExtensionPoint.class));

            String client = "sca.client." + UUID.randomUUID();
            CompositeContext compositeContext =
                new CompositeContext(extensionsRegistry, endpointRegistry, null, domainURI.toString(), client, nodeFactory.getDeployer().getSystemDefinitions());

            List<Endpoint> eps = endpointRegistry.findEndpoint(serviceName);
            if (eps == null || eps.size() < 1) {
                throw new NoSuchServiceException(serviceName);
            }
            Endpoint endpoint = eps.get(0); // TODO: what should be done with multiple endpoints?
           
            RuntimeEndpointReference epr;
            try {
                epr = createEndpointReference(javaInterfaceFactory, compositeContext, assemblyFactory, endpoint, serviceInterface);
            } catch (Exception e) {
                throw new ServiceRuntimeException(e);
            }

            InvocationHandler handler = Proxy.getInvocationHandler(proxyFactory.createProxy(serviceInterface, epr));
            return handler.invoke(proxy, method, args);
            
        } finally {
            nodeFactory.destroy();
        }
    }

    private RuntimeEndpointReference createEndpointReference(JavaInterfaceFactory javaInterfaceFactory, CompositeContext compositeContext, AssemblyFactory assemblyFactory, Endpoint endpoint, Class<?> businessInterface) throws CloneNotSupportedException, InvalidInterfaceException {
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
