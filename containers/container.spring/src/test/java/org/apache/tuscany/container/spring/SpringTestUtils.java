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
package org.apache.tuscany.container.spring;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.extension.ServiceExtension;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireService;

import org.apache.tuscany.container.spring.mock.TestBeanImpl;
import org.apache.tuscany.test.ArtifactFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @version $$Rev$$ $$Date$$
 */

public final class SpringTestUtils {
    private SpringTestUtils() {
    }

    public static <T> Service<T> createService(String name,
                                               Class<T> serviceInterface,
                                               CompositeComponent parent,
                                               WireService wireService) throws InvalidServiceContractException {
        Service<T> service = new ServiceExtension<T>(name, serviceInterface, parent, wireService);
        InboundWire<T> inboundWire = ArtifactFactory.createInboundWire(name, serviceInterface);
        OutboundWire<T> outboundWire = ArtifactFactory.createOutboundWire(name, serviceInterface);
        ArtifactFactory.terminateWire(outboundWire);
        service.setInboundWire(inboundWire);
        service.setOutboundWire(outboundWire);
        outboundWire.setTargetName(new QualifiedName("foo"));
        Connector connector = ArtifactFactory.createConnector();
        connector.connect(inboundWire, outboundWire, true);
        ArtifactFactory.terminateWire(inboundWire);
        return service;
    }


    public static GenericApplicationContext createContext() {
        GenericApplicationContext ctx = new GenericApplicationContext();
        BeanDefinition definition = new RootBeanDefinition(TestBeanImpl.class);
        ctx.registerBeanDefinition("foo", definition);
        return ctx;
    }

}
