package org.apache.tuscany.container.script.helper;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import junit.framework.TestCase;

import org.apache.tuscany.container.script.helper.ScriptHelperComponentBuilder;
import org.apache.tuscany.container.script.helper.ScriptHelperComponentType;
import org.apache.tuscany.container.script.helper.ScriptHelperImplementation;
import org.apache.tuscany.core.component.scope.ModuleScopeObjectFactory;
import org.apache.tuscany.core.component.scope.ScopeRegistryImpl;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.PropertyValue;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.easymock.IAnswer;

public class ScriptHelperComponentBuilderTestCase extends TestCase {

    public void testGetImplementationType() {
        ScriptHelperComponentBuilder builder = new ScriptHelperComponentBuilder();
        assertEquals(ScriptHelperImplementation.class, builder.getImplementationType());
    }

    @SuppressWarnings("unchecked")
    public void testBuild() {
        ScriptHelperComponentBuilder builder = new ScriptHelperComponentBuilder();
        ScopeRegistry scopeRegistry = new ScopeRegistryImpl();
        scopeRegistry.registerFactory(Scope.COMPOSITE, new ModuleScopeObjectFactory(scopeRegistry));
        builder.setScopeRegistry(scopeRegistry);
        DeploymentContext deploymentContext = createMock(DeploymentContext.class);
        final ScopeContainer scopeContainer = createMock(ScopeContainer.class);
        expect(scopeContainer.getScope()).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return Scope.COMPOSITE;
            }
        });
        expect(deploymentContext.getModuleScope()).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return scopeContainer;
            }
        });
        replay(deploymentContext);
        ComponentDefinition<ScriptHelperImplementation> impl = new ComponentDefinition<ScriptHelperImplementation>(new ScriptHelperImplementation());
        ScriptHelperComponentType componentType = new ScriptHelperComponentType();
        componentType.setLifecycleScope(Scope.COMPOSITE);
        ServiceDefinition service = new ServiceDefinition();
        ServiceContract serviceContract = new JavaServiceContract();
        service.setServiceContract(serviceContract);
        componentType.add(service);
        impl.getImplementation().setComponentType(componentType);
        
        PropertyValue pv = new PropertyValue("foo", "", "");
        ObjectFactory pvFactory = createMock(ObjectFactory.class);
        expect(pvFactory.getInstance()).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return null;
            }
        });
        replay(pvFactory);
        pv.setValueFactory(pvFactory);
        impl.add(pv);
        
        Component component = builder.build(null, impl, deploymentContext);
        assertNotNull(component);
    }

    @SuppressWarnings("unchecked")
    public void testBuildModuleScope() {
        ScriptHelperComponentBuilder builder = new ScriptHelperComponentBuilder();
        DeploymentContext deploymentContext = createMock(DeploymentContext.class);
        final ScopeContainer scopeContainer = createMock(ScopeContainer.class);
        expect(scopeContainer.getScope()).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return Scope.MODULE;
            }
        });
        expect(deploymentContext.getModuleScope()).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return scopeContainer;
            }
        });
        replay(deploymentContext);
        ComponentDefinition<ScriptHelperImplementation> impl = new ComponentDefinition<ScriptHelperImplementation>(new ScriptHelperImplementation());
        ScriptHelperComponentType componentType = new ScriptHelperComponentType();
        ServiceDefinition service = new ServiceDefinition();
        ServiceContract serviceContract = new JavaServiceContract();
        service.setServiceContract(serviceContract);
        componentType.add(service);
        impl.getImplementation().setComponentType(componentType);
        Component component = builder.build(null, impl, deploymentContext);
        assertNotNull(component);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
}
