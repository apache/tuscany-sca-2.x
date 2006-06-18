package org.apache.tuscany.container.java;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.container.java.mock.components.Source;
import org.apache.tuscany.container.java.mock.components.SourceImpl;
import org.apache.tuscany.container.java.mock.components.Target;
import org.apache.tuscany.core.component.CompositeComponentImpl;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.JavaServiceContract;
import org.apache.tuscany.spi.model.PojoComponentType;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.test.ArtifactFactory;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Invocation;
import org.jmock.core.Stub;

/**
 * @version $$Rev$$ $$Date$$
 */
public class JavaComponentBuilderTestCase extends MockObjectTestCase {
    private DeploymentContext deploymentContext;

    @SuppressWarnings("unchecked")
    public void testBuild() throws Exception {
        CompositeComponent parent = new CompositeComponentImpl(null, null, null, ArtifactFactory.createWireService());

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
        deploymentContext = new DeploymentContext(null, null, createMock());
    }

    private ScopeContainer createMock() {
        Mock mock = mock(ScopeContainer.class);
        mock.expects(once()).method("start");
        mock.expects(atLeastOnce()).method("register");
        mock.expects(atLeastOnce()).method("getScope").will(returnValue(Scope.MODULE));
        mock.expects(atLeastOnce()).method("getInstance").will(new Stub() {
            private Map<AtomicComponent, Object> cache = new HashMap<AtomicComponent, Object>();

            public Object invoke(Invocation invocation) throws Throwable {
                AtomicComponent component = (AtomicComponent) invocation.parameterValues.get(0);
                Object instance = cache.get(component);
                if (instance == null) {
                    instance = component.createInstance();
                    cache.put(component, instance);
                }
                return instance;
            }

            public StringBuffer describeTo(StringBuffer stringBuffer) {
                return null;
            }
        });
        return (ScopeContainer) mock.proxy();
    }

}
