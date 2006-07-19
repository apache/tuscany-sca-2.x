package org.apache.tuscany.core.integration.implementation.java.builder;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import org.apache.tuscany.core.component.AutowireComponent;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.scope.ScopeRegistryImpl;
import org.apache.tuscany.core.component.scope.StatelessScopeObjectFactory;
import org.apache.tuscany.core.deployer.RootDeploymentContext;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.ConstructorDefinition;
import org.apache.tuscany.core.implementation.java.JavaComponentBuilder;
import org.apache.tuscany.core.implementation.java.JavaImplementation;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * Verifies that the system builder handles configured properties correctly
 *
 * @version $Rev$ $Date$
 */
public class JavaBuilderPropertyTestCase extends MockObjectTestCase {

    private DeploymentContext deploymentContext;
    private CompositeComponent<?> parent;
    private ScopeRegistry registry;

    public void testPropertyHandling() throws Exception {
        JavaComponentBuilder builder = new JavaComponentBuilder();
        builder.setScopeRegistry(registry);
        PojoComponentType<ServiceDefinition, ReferenceDefinition, JavaMappedProperty<?>> type =
            new PojoComponentType<ServiceDefinition, ReferenceDefinition, JavaMappedProperty<?>>();
        JavaMappedProperty<String> property = new JavaMappedProperty<String>();
        property.setName("test");
        property.setDefaultValueFactory(new SingletonObjectFactory<String>("foo"));
        property.setMember(JavaBuilderPropertyTestCase.Foo.class.getMethod("setTest", String.class));
        type.add(property);
        type.setConstructorDefinition(new ConstructorDefinition(Foo.class.getConstructor((Class[])null)));
        type.setImplementationScope(Scope.STATELESS);
        JavaImplementation impl = new JavaImplementation();
        impl.setComponentType(type);
        impl.setImplementationClass(Foo.class);
        ComponentDefinition<JavaImplementation> definition = new ComponentDefinition<JavaImplementation>(impl);
        AtomicComponent<?> component = builder.build(parent, definition, deploymentContext);
        JavaBuilderPropertyTestCase.Foo foo = (JavaBuilderPropertyTestCase.Foo) component.createInstance();
        assertEquals("foo", foo.getTest());
    }

    protected void setUp() throws Exception {
        super.setUp();
        deploymentContext = new RootDeploymentContext(null, null, null, null);
        Mock mock = mock(AutowireComponent.class);
        parent = (CompositeComponent<?>) mock.proxy();
        registry = new ScopeRegistryImpl(new WorkContextImpl());
        registry.registerFactory(Scope.STATELESS, new StatelessScopeObjectFactory());
    }

    private static class Foo {
        private String test;

        public Foo() {
        }

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }
    }
}
