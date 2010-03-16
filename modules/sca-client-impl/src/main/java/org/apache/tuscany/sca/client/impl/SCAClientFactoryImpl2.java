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

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.impl.NodeFactoryImpl;
import org.apache.tuscany.sca.node.impl.NodeImpl;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;
import org.oasisopen.sca.client.SCAClientFactory;
import org.oasisopen.sca.client.SCAClientFactoryFinder;

public class SCAClientFactoryImpl2 extends SCAClientFactory {

    public static void setSCAClientFactoryFinder(SCAClientFactoryFinder factoryFinder) {
        SCAClientFactory.factoryFinder = factoryFinder;
    }

    public SCAClientFactoryImpl2(URI domainURI) throws NoSuchDomainException {
        super(domainURI);
    }   
    
    @Override
    public <T> T getService(Class<T> serviceInterface, String serviceName) throws NoSuchServiceException, NoSuchDomainException {
        
        for (NodeFactory nodeFactory : NodeFactory.getNodeFactories()) {
            for (Node node : ((NodeFactoryImpl)nodeFactory).getNodes().values()) {
                NodeImpl nodeImpl = (NodeImpl) node;
                for (Endpoint ep : nodeImpl.getServiceEndpoints()) {
                    if (ep.matches(serviceName)) {
                        return node.getService(serviceInterface, serviceName);
                    }
                }
            }
        }
        
        InvocationHandler handler = new SCAClientHandler(getDomainURI().toString(), serviceName, serviceInterface);
        return (T)Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[]{serviceInterface}, handler);
    }
    
}
