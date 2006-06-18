package org.apache.tuscany.container.java;

import org.apache.tuscany.container.java.mock.components.Source;
import org.apache.tuscany.container.java.mock.components.SourceImpl;
import org.apache.tuscany.container.java.mock.components.Target;
import org.apache.tuscany.core.component.CompositeComponentImpl;
import org.apache.tuscany.core.component.scope.ModuleScopeContainer;
import org.apache.tuscany.core.model.PojoComponentType;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.JavaServiceContract;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.test.ArtifactFactory;
import org.jmock.MockObjectTestCase;

/**
 * @version $$Rev$$ $$Date$$
 */
public class JavaComponentBuilderTestCase extends MockObjectTestCase {
    private DeploymentContext deploymentContext;

    @SuppressWarnings("unchecked")
    public void testBuild() throws Exception {
        CompositeComponent parent = new CompositeComponentImpl(null, null, null, ArtifactFactory.createWireService());
        ModuleScopeContainer scope = new ModuleScopeContainer(null);
        scope.start();
        PojoComponentType sourceType = new PojoComponentType();
        sourceType.setLifecycleScope(Scope.MODULE);
        sourceType.addReferenceMember("target", SourceImpl.class.getMethod("setTarget", Target.class));

        ServiceContract sourceContract = new JavaServiceContract();
        sourceContract.setInterfaceClass(Source.class);
        ServiceDefinition sourceServiceDefinition = new ServiceDefinition();
        sourceServiceDefinition.setName("Source");
        sourceServiceDefinition.setServiceContract(sourceContract);

        sourceType.add(sourceServiceDefinition);
        JavaImplementation sourceImpl = new JavaImplementation();
        sourceImpl.setComponentType(sourceType);
        sourceImpl.setImplementationClass(SourceImpl.class);
        ComponentDefinition<JavaImplementation> sourceComponentDefinition =
            new ComponentDefinition<JavaImplementation>(sourceImpl);

        JavaComponentBuilder builder = new JavaComponentBuilder();
        JavaAtomicComponent<Source> ctx =
            (JavaAtomicComponent<Source>) builder.build(parent, sourceComponentDefinition, deploymentContext);
        deploymentContext.getModuleScope().start();
        ctx.start();
        Source source = ctx.getServiceInstance();
        assertNotNull(source);
        ctx.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        deploymentContext = new DeploymentContext(null, null, new ModuleScopeContainer());
    }
}
