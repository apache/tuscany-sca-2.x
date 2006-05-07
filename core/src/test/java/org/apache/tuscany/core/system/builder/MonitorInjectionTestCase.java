/**
 *
 * Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.system.builder;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.common.monitor.MonitorFactory;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.impl.CompositeContextImpl;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.DefaultScopeStrategy;
import org.apache.tuscany.core.mock.MockConfigContext;
import org.apache.tuscany.core.mock.MockFactory;
import org.apache.tuscany.core.system.annotation.Monitor;
import org.apache.tuscany.core.system.assembly.SystemAssemblyFactory;
import org.apache.tuscany.core.system.assembly.impl.SystemAssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.Scope;

/**
 * @version $Rev$ $Date$
 */
public class MonitorInjectionTestCase extends TestCase {
    private SystemContextFactoryBuilder builder;
    private Component component;

    public static interface TestService {
    }

    public static class TestComponent implements TestService {
        @Monitor
        protected Monitor1 monitor1;
        Monitor2 monitor2;

        @Monitor
        public void setMonitor2(Monitor2 monitor2) {
            this.monitor2 = monitor2;
        }
    }

    public static interface Monitor1 {
    }

    public static interface Monitor2 {
    }

    public void testMonitorInjection() {
        builder.build(component);
        ContextFactory<?> contextFactory = (ContextFactory<?>) component.getContextFactory();
        Assert.assertNotNull(contextFactory);
        contextFactory.prepare(createContext());
        Context ctx = contextFactory.createContext();

        ctx.start();
        TestComponent instance = (TestComponent) ctx.getInstance(null);
        assertSame(MONITOR1, instance.monitor1);
        assertSame(MONITOR2, instance.monitor2);
    }

    protected void setUp() throws Exception {
        super.setUp();
        SystemAssemblyFactory factory = new SystemAssemblyFactoryImpl();
        MockMonitorFactory monitorFactory = new MockMonitorFactory();
        builder = new SystemContextFactoryBuilder(monitorFactory);
        component = factory.createSystemComponent("test", TestService.class, TestComponent.class, Scope.MODULE);
        component.getImplementation().setComponentType(MockFactory.getIntrospector().introspect(TestComponent.class));
    }

    private static final Monitor1 MONITOR1 = new Monitor1() {
    };
    private static final Monitor2 MONITOR2 = new Monitor2() {
    };

    public static class MockMonitorFactory implements MonitorFactory {
        public <T> T getMonitor(Class<T> monitorInterface) {
            if (Monitor1.class.equals(monitorInterface)) {
                return monitorInterface.cast(MONITOR1);
            } else if (Monitor2.class.equals(monitorInterface)) {
                return monitorInterface.cast(MONITOR2);
            } else {
                throw new AssertionError();
            }
        }
    }

    private static CompositeContext createContext() {
        return new CompositeContextImpl("test.parent", null, new DefaultScopeStrategy(), new EventContextImpl(), new MockConfigContext(null));
    }
}
