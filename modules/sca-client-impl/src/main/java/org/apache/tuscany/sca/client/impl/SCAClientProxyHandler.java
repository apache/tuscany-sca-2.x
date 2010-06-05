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
import java.util.List;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.oasisopen.sca.NoSuchServiceException;

/**
 * TODO: What this wants is a way to create a generic invoker for an arbitrary binding
 *      that could mean extending the BindingProvider API to include something like a 
 *      createClient method which creates an Invoker for an Endpoint 
 */
public class SCAClientProxyHandler implements InvocationHandler {

    protected EndpointRegistry endpointRegistry;
    protected EndpointReference endpointReference;
    protected String serviceName;
    
    public SCAClientProxyHandler(String serviceName, ExtensionPointRegistry extensionsRegistry, EndpointRegistry endpointRegistry) {
        this.endpointRegistry = endpointRegistry;
        this.serviceName = serviceName;

//        RMIHostExtensionPoint rmiHosts = extensionsRegistry.getExtensionPoint(RMIHostExtensionPoint.class);
//        this.rmiHost = new ExtensibleRMIHost(rmiHosts);

        FactoryExtensionPoint factories = extensionsRegistry.getExtensionPoint(FactoryExtensionPoint.class);
        AssemblyFactory assemblyFactory = factories.getFactory(AssemblyFactory.class);

        this.endpointReference = assemblyFactory.createEndpointReference();
        endpointReference.setReference(assemblyFactory.createComponentReference());
        Endpoint targetEndpoint = assemblyFactory.createEndpoint();
        targetEndpoint.setURI(serviceName);
        endpointReference.setTargetEndpoint(targetEndpoint);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        List<Endpoint> endpoints = endpointRegistry.findEndpoint(endpointReference);
        if (endpoints.size() <1 ) {
            throw new NoSuchServiceException(serviceName);
        }

        String uri = endpoints.get(0).getBinding().getURI();
//        RMIBindingInvoker invoker = new RMIBindingInvoker(rmiHost, uri, method);
//
//        return invoker.invokeTarget(args);
        return null;
    }

}
