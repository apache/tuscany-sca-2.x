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
package org.apache.tuscany.core.implementation;

import org.apache.tuscany.spi.implementation.java.ImplementationProcessor;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;

import org.apache.tuscany.core.implementation.IntrospectionRegistryImpl.Monitor;
import org.apache.tuscany.core.monitor.NullMonitorFactory;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $Rev$ $Date$
 */
public class IntrospectionRegistryTestCase extends MockObjectTestCase {

    private Monitor monitor;

    public void testRegister() throws Exception {
        IntrospectionRegistryImpl registry = new IntrospectionRegistryImpl(monitor);
        Mock mock = mock(ImplementationProcessor.class);
        registry.registerProcessor((ImplementationProcessor) mock.proxy());
    }

    public void testUnegister() throws Exception {
        IntrospectionRegistryImpl registry = new IntrospectionRegistryImpl(monitor);
        Mock mock = mock(ImplementationProcessor.class);
        ImplementationProcessor processor = (ImplementationProcessor) mock.proxy();
        registry.registerProcessor(processor);
        registry.unregisterProcessor(processor);
    }

    public void testWalk() throws Exception {
        IntrospectionRegistryImpl registry = new IntrospectionRegistryImpl(monitor);
        Mock mock = mock(ImplementationProcessor.class);
        mock.expects(once()).method("visitClass");
        mock.expects(once()).method("visitMethod");
        mock.expects(once()).method("visitField");
        mock.expects(once()).method("visitConstructor");
        mock.expects(once()).method("visitSuperClass");
        mock.expects(once()).method("visitEnd");
        ImplementationProcessor processor = (ImplementationProcessor) mock.proxy();
        registry.registerProcessor(processor);
        registry.introspect(null, Bar.class, new PojoComponentType(), null);
    }


    protected void setUp() throws Exception {
        super.setUp();
        monitor = new NullMonitorFactory().getMonitor(Monitor.class);
    }

    private class Baz {

    }

    private class Bar extends Baz {

        protected String bar;

        public Bar() {
        }

        public void bar() {
        }

    }

}
