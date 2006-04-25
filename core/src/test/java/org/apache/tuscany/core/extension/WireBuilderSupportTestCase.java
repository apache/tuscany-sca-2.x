package org.apache.tuscany.core.extension;

import junit.framework.TestCase;
import org.apache.tuscany.core.builder.impl.DefaultWireBuilder;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.wire.Interceptor;
import org.apache.tuscany.core.wire.TargetInvocationConfiguration;
import org.apache.tuscany.core.wire.TargetInvoker;
import org.apache.tuscany.core.wire.TargetWireFactory;
import org.apache.tuscany.core.wire.WireTargetConfiguration;
import org.apache.tuscany.core.wire.jdk.JDKTargetWireFactory;
import org.apache.tuscany.core.wire.mock.MockScopeContext;
import org.apache.tuscany.model.assembly.Implementation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @version $$Rev$$ $$Date$$
 */
public class WireBuilderSupportTestCase extends TestCase {
    private Method m;

    /**
     * Tests that {@link WireBuilderSupport} only processes connect operations and sets target invokers for the correct target
     * type.
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
        defaultBuilder.completeTargetChain(targetFooFactory, Foo.class, ctx);
        defaultBuilder.completeTargetChain(targetBarFactory, Bar.class, ctx);
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

    private class FooWireBuilder extends WireBuilderSupport {

        protected boolean handlesTargetType(Class targetType) {
            return Foo.class.isAssignableFrom(targetType);
        }

        protected TargetInvoker createInvoker(QualifiedName targetName, Method operation, ScopeContext context, boolean downScope) {
            return new FooInvoker();
        }
    }

    private class BarWireBuilder extends WireBuilderSupport {

        protected boolean handlesTargetType(Class targetType) {
            return Bar.class.isAssignableFrom(targetType);
        }

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
            return null;
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
            return null;
        }

        public Message invoke(Message msg) {
            return null;
        }

        public void setNext(Interceptor next) {

        }
    }


}
