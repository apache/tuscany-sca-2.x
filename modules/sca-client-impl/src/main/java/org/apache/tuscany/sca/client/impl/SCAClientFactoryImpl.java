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
import java.net.URI;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistryLocator;
import org.apache.tuscany.sca.runtime.DomainRegistry;
import org.apache.tuscany.sca.runtime.ExtensibleDomainRegistryFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;
import org.oasisopen.sca.client.SCAClientFactory;
import org.oasisopen.sca.client.SCAClientFactoryFinder;

public class SCAClientFactoryImpl extends SCAClientFactory {

    protected ExtensionPointRegistry extensionPointRegistry;
    protected DomainRegistry domainRegistry;
    protected boolean remoteClient;
    protected boolean onlySCABinding;
    private EndpointFinder endpointFinder;
    
    public static URI default_domainURI = URI.create("default");
    
    public static SCAClientFactoryFinder getSCAClientFactoryFinder() {
        return SCAClientFactory.factoryFinder;
    }

    public static void setSCAClientFactoryFinder(SCAClientFactoryFinder factoryFinder) {
        SCAClientFactory.factoryFinder = factoryFinder;
    }

    public SCAClientFactoryImpl(URI domainURI) throws NoSuchDomainException {
        super(domainURI == null ? default_domainURI : domainURI);
        findLocalRuntime();

        endpointFinder = RuntimeUtils.getEndpointFinder(extensionPointRegistry);
    }

    public SCAClientFactoryImpl(URI domainURI, Properties properties) throws NoSuchDomainException {
    	this(domainURI);
    }

    protected void findLocalRuntime() throws NoSuchDomainException {
        String domainURI = getDomainURI().toString();
        for (ExtensionPointRegistry xpr : ExtensionPointRegistryLocator.getExtensionPointRegistries()) {
            ExtensibleDomainRegistryFactory drf = ExtensibleDomainRegistryFactory.getInstance(xpr);
            for (DomainRegistry epr : drf.getEndpointRegistries()) {
                if (domainURI.equals(epr.getDomainName())) {
                    this.extensionPointRegistry = xpr;
                    this.domainRegistry = epr;
                    return;
                }
            }
        }

        remoteClient = true;
        extensionPointRegistry = RuntimeUtils.createExtensionPointRegistry();
        domainRegistry = RuntimeUtils.getClientEndpointRegistry(extensionPointRegistry, domainURI);
    }

    @Override
    public <T> T getService(Class<T> serviceInterface, String serviceURI) throws NoSuchServiceException{
                
        // The service is a component in a local runtime
        if (!remoteClient) {
            Endpoint ep = endpointFinder.findEndpoint(domainRegistry, serviceURI);
            if (((RuntimeComponent)ep.getComponent()).getComponentContext() != null) {
                String serviceName = ep.getService().getName() + '/' + ep.getBinding().getName();
                return ((RuntimeComponent)ep.getComponent()).getServiceReference(serviceInterface, serviceName).getService();
            }
        }

        InvocationHandler handler;
        if (!remoteClient) {
            // There is a local runtime but the service is a remote component
            handler = new RemoteServiceInvocationHandler(extensionPointRegistry, domainRegistry, serviceURI, serviceInterface);
        } else {
            // no local runtime
            // TUSCANY-3590 - convert NoSuchDomainException to NoSuchService exception while 
            //                we findout why this interface has changed
            try {
                handler = new RemoteServiceInvocationHandler(extensionPointRegistry, domainRegistry, getDomainURI().toString(), serviceURI, serviceInterface);
            } catch (NoSuchDomainException ex){
                throw new NoSuchServiceException(ex);
            }
        }
        if (serviceInterface == null) {
            serviceInterface = (Class<T>)((RemoteServiceInvocationHandler)handler).serviceInterface;
        }

        final Class _serviceInterface = serviceInterface;
        ClassLoader cl = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
               return _serviceInterface.getClassLoader();
            }
        });

        return (T)Proxy.newProxyInstance(cl, new Class[]{serviceInterface}, handler);
    }    

}
