/**
 *
 * Copyright 2005 The Apache Software Foundation
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
package org.apache.tuscany.core.extension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import org.apache.tuscany.core.builder.ContextCreationException;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.builder.impl.DefaultWireBuilder;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.context.AtomicContext;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.wire.Interceptor;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.core.wire.TargetInvocationConfiguration;
import org.apache.tuscany.core.wire.TargetInvoker;
import org.apache.tuscany.core.wire.TargetWireFactory;
import org.apache.tuscany.core.wire.WireTargetConfiguration;
import org.apache.tuscany.core.wire.jdk.JDKTargetWireFactory;
import org.apache.tuscany.core.wire.mock.MockScopeContext;
import org.apache.tuscany.model.assembly.Implementation;
import org.apache.tuscany.model.assembly.Scope;

/**
 * @version $$Rev$$ $$Date$$
 */
public class WireBuilderSupportTestCase extends TestCase {
    private Method m;

    /**
     * Tests that {@link WireBuilderSupport} only processes connect operations and sets target invokers for
     * the correct target type.
     * <p/>
     * Verifies TUSCANY-218
     *
     * @throws Exception
     */
    public void testTargetInvokerSet() throws Exception {
        FooWireBuilder fooBuilder = new FooWireBuilder();
        BarWireBuilder barBuilder = new BarWireBuilder();
        DefaultWireBuilder defaultBuilder = new DefaultWireBuilder();
        defaultBuilder.addWireBuilder(fooBuilder);
        defaultBuilder.addWireBuilder(barBuilder);
        TargetWireFactory targetFooFactory = new JDKTargetWireFactory();
        Map<Method, TargetInvocationConfiguration> fooConfigs = new HashMap<Method, TargetInvocationConfiguration>();
        TargetInvocationConfiguration fooInvocation = new TargetInvocationConfiguration(m);
        fooConfigs.put(m, fooInvocation);
        Map<Method, TargetInvocationConfiguration> barConfigs = new HashMap<Method, TargetInvocationConfiguration>();
        TargetInvocationConfiguration barInvocation = new TargetInvocationConfiguration(m);
        barConfigs.put(m, barInvocation);
        targetFooFactory.setConfiguration(new WireTargetConfiguration(null, fooConfigs, null, null));
        TargetWireFactory targetBarFactory = new JDKTargetWireFactory();
        targetBarFactory.setConfiguration(new WireTargetConfiguration(null, barConfigs, null, null));
        ScopeContext ctx = new MockScopeContext();
        defaultBuilder.completeTargetChain(targetFooFactory, FooContextFactory.class, ctx);
        defaultBuilder.completeTargetChain(targetBarFactory, BarContextFactory.class, ctx);
        assertEquals(FooInvoker.class, targetFooFactory.getConfiguration().getInvocationConfigurations().get(m).getTargetInvoker().getClass());
        assertEquals(BarInvoker.class, targetBarFactory.getConfiguration().getInvocationConfigurations().get(m).getTargetInvoker().getClass());

    }


    protected void setUp() throws Exception {
        super.setUp();
        m = SomeInterface.class.getMethod("test", (Class[]) null);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private interface SomeInterface {
        void test();
    }

    private interface Foo extends Implementation {

    }

    private interface Bar extends Implementation {

    }

    private class FooWireBuilder extends WireBuilderSupport<FooContextFactory> {

        protected TargetInvoker createInvoker(QualifiedName targetName, Method operation, ScopeContext context, boolean downScope) {
            return new FooInvoker();
        }
    }

    private class BarWireBuilder extends WireBuilderSupport<BarContextFactory> {

        protected TargetInvoker createInvoker(QualifiedName targetName, Method operation, ScopeContext context, boolean downScope) {
            return new BarInvoker();
        }
    }

    private class FooInvoker implements TargetInvoker {

        public Object invokeTarget(Object payload) throws InvocationTargetException {
            return null;
        }

        public boolean isCacheable() {
            return false;
        }

        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        public Message invoke(Message msg) {
            return null;
        }

        public void setNext(Interceptor next) {

        }
    }


    private class BarInvoker implements TargetInvoker {

        public Object invokeTarget(Object payload) throws InvocationTargetException {
            return null;
        }

        public boolean isCacheable() {
            return false;
        }

        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        public Message invoke(Message msg) {
            return null;
        }

        public void setNext(Interceptor next) {

        }
    }

    private class FooContextFactory implements ContextFactory<AtomicContext> {

        public AtomicContext createContext() throws ContextCreationException {
            return null;
        }

        public Scope getScope() {
            return null;
        }

        public String getName() {
            return null;
        }

        public void addProperty(String propertyName, Object value) {

        }

        public void addTargetWireFactory(String serviceName, TargetWireFactory factory) {

        }

        public TargetWireFactory getTargetWireFactory(String serviceName) {
            return null;
        }

        public Map getTargetWireFactories() {
            return null;
        }

        public void addSourceWireFactory(String referenceName, SourceWireFactory factory) {

        }

        public List getSourceWireFactories() {
            return null;
        }

        public void prepare(CompositeContext parent) {

        }

        public void addSourceWireFactories(String referenceName, Class referenceInterface, List factory, boolean multiplicity) {

        }
    }


    private class BarContextFactory implements ContextFactory<Context> {
        public Context createContext() throws ContextCreationException {
            return null;
        }

        public Scope getScope() {
            return null;
        }

        public String getName() {
            return null;
        }

        public void addProperty(String propertyName, Object value) {

        }

        public void addTargetWireFactory(String serviceName, TargetWireFactory factory) {

        }

        public TargetWireFactory getTargetWireFactory(String serviceName) {
            return null;
        }

        public Map getTargetWireFactories() {
            return null;
        }

        public void addSourceWireFactory(String referenceName, SourceWireFactory factory) {

        }

        public void addSourceWireFactories(String referenceName, Class referenceInterface, List factory, boolean multiplicity) {

        }

        public List getSourceWireFactories() {
            return null;
        }

        public void prepare(CompositeContext parent) {

        }
    }

}
