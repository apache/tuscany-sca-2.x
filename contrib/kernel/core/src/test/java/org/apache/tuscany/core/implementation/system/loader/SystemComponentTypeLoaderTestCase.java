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
package org.apache.tuscany.core.implementation.system.loader;

import junit.framework.TestCase;

import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.core.implementation.IntrospectionRegistryImpl;
import org.apache.tuscany.core.implementation.processor.ConstructorProcessor;
import org.apache.tuscany.core.implementation.processor.DestroyProcessor;
import org.apache.tuscany.core.implementation.processor.HeuristicPojoProcessor;
import org.apache.tuscany.core.implementation.processor.InitProcessor;
import org.apache.tuscany.core.implementation.processor.PropertyProcessor;
import org.apache.tuscany.core.implementation.processor.ReferenceProcessor;
import org.apache.tuscany.core.implementation.processor.ScopeProcessor;
import org.apache.tuscany.core.implementation.processor.ServiceProcessor;
import org.apache.tuscany.core.implementation.system.model.SystemImplementation;
import org.apache.tuscany.core.mock.component.BasicInterface;
import org.apache.tuscany.core.mock.component.BasicInterfaceImpl;
import org.apache.tuscany.core.monitor.NullMonitorFactory;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * @version $Rev$ $Date$
 */
public class SystemComponentTypeLoaderTestCase extends TestCase {
    private SystemComponentTypeLoader loader;

    public void testIntrospectUnannotatedClass() throws ProcessingException {
        SystemImplementation impl = new SystemImplementation(BasicInterfaceImpl.class);
        PojoComponentType<?, ?, ?> componentType = loader.loadByIntrospection(impl, null);
        ServiceDefinition service = componentType.getServices().get(BasicInterface.class.getSimpleName());
        assertEquals(BasicInterface.class, service.getServiceContract().getInterfaceClass());
        Property<?> property = componentType.getProperties().get("publicProperty");
        assertEquals(String.class, property.getJavaType());
        ReferenceDefinition referenceDefinition = componentType.getReferences().get("protectedReference");
        assertEquals(BasicInterface.class, referenceDefinition.getServiceContract().getInterfaceClass());
    }

    protected void setUp() throws Exception {
        super.setUp();
        JavaInterfaceProcessorRegistryImpl interfaceProcessorRegistry = new JavaInterfaceProcessorRegistryImpl();
        IntrospectionRegistryImpl registry = new IntrospectionRegistryImpl();
        registry.setMonitor(new NullMonitorFactory().getMonitor(IntrospectionRegistryImpl.Monitor.class));
        registry.registerProcessor(new ConstructorProcessor());
        registry.registerProcessor(new DestroyProcessor());
        registry.registerProcessor(new InitProcessor());
        registry.registerProcessor(new ScopeProcessor());
        registry.registerProcessor(new PropertyProcessor());
        ReferenceProcessor referenceProcessor = new ReferenceProcessor();
        referenceProcessor.setInterfaceProcessorRegistry(interfaceProcessorRegistry);
        registry.registerProcessor(referenceProcessor);
        registry.registerProcessor(new ServiceProcessor());
        ServiceProcessor serviceProcessor = new ServiceProcessor();
        serviceProcessor.setInterfaceProcessorRegistry(interfaceProcessorRegistry);
        registry.registerProcessor(serviceProcessor);
        HeuristicPojoProcessor pojoProcessor = new HeuristicPojoProcessor();
        pojoProcessor.setInterfaceProcessorRegistry(interfaceProcessorRegistry);
        registry.registerProcessor(pojoProcessor);
        loader = new SystemComponentTypeLoader(registry);
    }
}
