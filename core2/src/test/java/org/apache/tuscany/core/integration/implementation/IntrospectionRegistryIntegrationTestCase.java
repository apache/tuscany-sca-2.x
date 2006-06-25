package org.apache.tuscany.core.integration.implementation;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

import static org.apache.tuscany.spi.model.Scope.MODULE;
import org.apache.tuscany.spi.model.ServiceDefinition;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.IntrospectionRegistryImpl;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.processor.DestroyProcessor;
import org.apache.tuscany.core.implementation.processor.InitProcessor;
import org.apache.tuscany.core.implementation.processor.PropertyProcessor;
import org.apache.tuscany.core.implementation.processor.ReferenceProcessor;
import org.apache.tuscany.core.implementation.processor.ScopeProcessor;
import org.apache.tuscany.core.monitor.NullMonitorFactory;

/**
 * Sanity check of the <code>IntegrationRegistry</code> to verify operation with processors
 *
 * @version $Rev$ $Date$
 */
public class IntrospectionRegistryIntegrationTestCase extends TestCase {

    private IntrospectionRegistryImpl registry;

    public void testSimpleComponentTypeParsing() throws Exception {
        PojoComponentType<ServiceDefinition, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<ServiceDefinition, JavaMappedReference, JavaMappedProperty<?>>();
        registry.introspect(Foo.class, type, null);
        assertEquals(Foo.class.getMethod("init"), type.getInitMethod());
        assertEquals(Foo.class.getMethod("destroy"), type.getDestroyMethod());
        assertEquals(MODULE, type.getLifecycleScope());
        assertEquals(Foo.class.getMethod("setBar", String.class), type.getProperties().get("bar").getMember());
        assertEquals(Foo.class.getMethod("setTarget", Foo.class), type.getReferences().get("target").getMember());
    }

    protected void setUp() throws Exception {
        super.setUp();
        registry = new IntrospectionRegistryImpl();
        registry.setMonitor(new NullMonitorFactory().getMonitor(IntrospectionRegistryImpl.Monitor.class));
        registry.registerProcessor(new DestroyProcessor());
        registry.registerProcessor(new InitProcessor());
        registry.registerProcessor(new ScopeProcessor());
        registry.registerProcessor(new PropertyProcessor());
        registry.registerProcessor(new ReferenceProcessor());
    }

    @Scope("MODULE")
    private static class Foo {
        protected Foo target;
        protected String bar;

        private boolean initialized;
        private boolean destroyed;


        @Init
        public void init() {
            if (initialized) {
                fail();
            }
            initialized = true;
        }

        @Destroy
        public void destroy() {
            if (destroyed) {
                fail();
            }
            destroyed = true;
        }

        public Foo getTarget() {
            return target;
        }

        @Reference
        public void setTarget(Foo target) {
            this.target = target;
        }

        public String getBar() {
            return bar;
        }

        @Property
        public void setBar(String bar) {
            this.bar = bar;
        }

    }
}
