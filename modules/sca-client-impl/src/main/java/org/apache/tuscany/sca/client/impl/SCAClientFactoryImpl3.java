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

import java.net.URI;
import java.util.Properties;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistryLocator;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.ExtensibleDomainRegistryFactory;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;
import org.oasisopen.sca.client.SCAClientFactory;
import org.oasisopen.sca.client.SCAClientFactoryFinder;

public class SCAClientFactoryImpl3 extends SCAClientFactory {

    private Handler2 handler2;
    
    public static void setSCAClientFactoryFinder(SCAClientFactoryFinder factoryFinder) {
        SCAClientFactory.factoryFinder = factoryFinder;
    }

    public SCAClientFactoryImpl3(URI domainURI) throws NoSuchDomainException {
        this(null, domainURI);
    }
    
    public SCAClientFactoryImpl3(Properties properties, URI domainURI) throws NoSuchDomainException {
        super(domainURI);
        if (properties == null) {
            properties = new Properties();
        }
        initRegistries(properties);
    }   
    
    private void initRegistries(Properties properties) throws NoSuchDomainException {
        String domainURI = getDomainURI().toString();
        for (ExtensionPointRegistry xpr : ExtensionPointRegistryLocator.getExtensionPointRegistries()) {
            ExtensibleDomainRegistryFactory drf = ExtensibleDomainRegistryFactory.getInstance(xpr);
            for (EndpointRegistry epr : drf.getEndpointRegistries()) {
                if (domainURI.equals(epr.getDomainURI())) {
                    this.handler2 = new Handler2(xpr, epr, properties);
                    break;
                }
            }
        }
        if (handler2 == null) {
            handler2 = new Handler2(domainURI, properties);
        }
    }

    @Override
    public <T> T getService(Class<T> serviceInterface, String serviceName) throws NoSuchServiceException, NoSuchDomainException {
        return handler2.getService(serviceInterface, serviceName);
    }
}
