package org.apache.tuscany.core.implementation.java;

import java.lang.reflect.Constructor;
import java.net.URI;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.composite.CompositeComponentImpl;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.SourceImpl;
import org.apache.tuscany.core.mock.component.Target;
import org.easymock.EasyMock;

/**
 * Verifies component type metadata is properly applied to the component
 *
 * @version $$Rev$$ $$Date$$
 */
public class JavaComponentBuilderMetadataTestCase extends TestCase {
    private DeploymentContext deploymentContext;
    private Constructor<SourceImpl> constructor;
    private CompositeComponent parent;
    private PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type;
    private ComponentDefinition<JavaImplementation> definition;
    private ScopeContainer scopeContainer;

    public void testInitLevel() throws Exception {
        type.setInitLevel(1);
        JavaComponentBuilder builder = new JavaComponentBuilder();
        JavaAtomicComponent component = (JavaAtomicComponent) builder.build(parent, definition, deploymentContext);
        assertEquals(1, component.getInitLevel());
    }

    public void testMaxAge() throws Exception {
        type.setMaxAge(100);
        JavaComponentBuilder builder = new JavaComponentBuilder();
        JavaAtomicComponent component = (JavaAtomicComponent) builder.build(parent, definition, deploymentContext);
        assertEquals(100, component.getMaxAge());
    }

    public void testMaxIdleTime() throws Exception {
        type.setMaxIdleTime(100);
        JavaComponentBuilder builder = new JavaComponentBuilder();
        JavaAtomicComponent component = (JavaAtomicComponent) builder.build(parent, definition, deploymentContext);
        assertEquals(100, component.getMaxIdleTime());
    }

    public void testNoMaxAgeNoMaxIdleTime() throws Exception {
        JavaComponentBuilder builder = new JavaComponentBuilder();
        JavaAtomicComponent component = (JavaAtomicComponent) builder.build(parent, definition, deploymentContext);
        assertEquals(-1, component.getMaxAge());
        assertEquals(-1, component.getMaxIdleTime());
    }

    public void testScope() throws Exception {
        JavaComponentBuilder builder = new JavaComponentBuilder();
        JavaAtomicComponent component = (JavaAtomicComponent) builder.build(parent, definition, deploymentContext);
        component.setScopeContainer(scopeContainer);
        assertEquals(Scope.COMPOSITE, component.getScope());
    }

    protected void setUp() throws Exception {
        super.setUp();
        parent = new CompositeComponentImpl(URI.create("parent"), null, null);
        constructor = SourceImpl.class.getConstructor((Class[]) null);
        createDeploymentContext();
        createComponentDefinitionAndType();
    }


    private void createDeploymentContext() {
        scopeContainer = EasyMock.createMock(ScopeContainer.class);
        scopeContainer.start();
        scopeContainer.stop();
        scopeContainer.register(EasyMock.isA(AtomicComponent.class));
        EasyMock.expectLastCall().atLeastOnce();
        EasyMock.expect(scopeContainer.getScope()).andReturn(Scope.COMPOSITE).atLeastOnce();
        EasyMock.replay(scopeContainer);
        deploymentContext = EasyMock.createMock(DeploymentContext.class);
        EasyMock.replay(deploymentContext);
    }

    private void createComponentDefinitionAndType() throws Exception {
        type = new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        type.setImplementationScope(Scope.COMPOSITE);
        JavaMappedReference reference = new JavaMappedReference();
        reference.setUri(URI.create("#target"));
        reference.setMember(SourceImpl.class.getMethod("setTarget", Target.class));
        type.add(reference);
        ServiceContract<?> contract = new JavaServiceContract(Source.class);
        JavaMappedService serviceDefinition = new JavaMappedService();
        serviceDefinition.setUri(URI.create("component#Source"));
        serviceDefinition.setServiceContract(contract);
        type.add(serviceDefinition);
        type.setConstructorDefinition(new ConstructorDefinition<SourceImpl>(constructor));
        JavaImplementation sourceImpl = new JavaImplementation(SourceImpl.class, type);
        definition = new ComponentDefinition<JavaImplementation>(sourceImpl);
        definition.setUri(URI.create("component"));
    }

}
