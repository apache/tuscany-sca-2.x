package org.apache.tuscany.container.java.builder;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;
import org.apache.tuscany.container.java.invocation.mock.MockSyncInterceptor;
import org.apache.tuscany.container.java.mock.MockAssemblyFactory;
import org.apache.tuscany.container.java.mock.MockConfigContext;
import org.apache.tuscany.container.java.mock.components.GenericComponent;
import org.apache.tuscany.container.java.mock.components.ModuleScopeComponent;
import org.apache.tuscany.container.java.mock.components.ModuleScopeComponentImpl;
import org.apache.tuscany.core.builder.RuntimeConfiguration;
import org.apache.tuscany.core.builder.impl.DefaultWireBuilder;
import org.apache.tuscany.core.builder.impl.HierarchicalBuilder;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.context.ScopeStrategy;
import org.apache.tuscany.core.context.impl.AggregateContextImpl;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.DefaultScopeStrategy;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.invocation.ProxyConfiguration;
import org.apache.tuscany.core.invocation.jdk.JDKProxyFactoryFactory;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.impl.AssemblyModelContextImpl;
import org.apache.tuscany.model.types.java.JavaServiceContract;

public class JavaComponentContextBuilderTestCase extends TestCase {

    private AssemblyFactory factory = new AssemblyFactoryImpl();
    
    private AssemblyModelContext assemblyContext = new AssemblyModelContextImpl(null,null); 
    
    public JavaComponentContextBuilderTestCase() {
    }

    public void testBuilder() throws Exception {
        JavaComponentContextBuilder builder = new JavaComponentContextBuilder();
        builder.setMessageFactory(new MessageFactoryImpl());
        HierarchicalBuilder refBuilder = new HierarchicalBuilder();
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        refBuilder.addBuilder(new MockInterceptorBuilder(interceptor, true));
        builder.setReferenceBuilder(refBuilder);
        AggregateContext ctx = createContext();
        builder.setProxyFactoryFactory(new JDKProxyFactoryFactory());
        JavaTargetWireBuilder javaWireBuilder = new JavaTargetWireBuilder();
        ScopeStrategy strategy = new DefaultScopeStrategy();
        DefaultWireBuilder wireBuilder = new DefaultWireBuilder();
        wireBuilder.addWireBuilder(javaWireBuilder);
        Module module = createModule();
        EventContext eCtx = new EventContextImpl();
        ScopeContext scopeContext = new ModuleScopeContext(eCtx);
        scopeContext.start();
        scopeContext.onEvent(EventContext.MODULE_START, null);
        List<Component> components = module.getComponents();
        Map<String, Component> compMap = new HashMap(components.size());

        for (Component component : components) {
            compMap.put(component.getName(), component);
            builder.build(component, ctx);
            RuntimeConfiguration config = (RuntimeConfiguration) component.getComponentImplementation().getRuntimeConfiguration();
            Assert.assertNotNull(config);
        }
        for (Component component : components) {
            RuntimeConfiguration source = (RuntimeConfiguration) component.getComponentImplementation().getRuntimeConfiguration();
            Assert.assertNotNull(source);
            for (ProxyFactory pFactory : (Collection<ProxyFactory>) source.getSourceProxyFactories().values()) {
                ProxyConfiguration pConfig = pFactory.getProxyConfiguration();
                Component target = compMap.get(pConfig.getTargetName().getPartName());

                if (target != null) {
                    RuntimeConfiguration targetConfig = (RuntimeConfiguration) target.getComponentImplementation()
                            .getRuntimeConfiguration();
                    boolean downScope = strategy.downScopeReference(source.getScope(), targetConfig.getScope());
                    wireBuilder.wire(pFactory, targetConfig.getTargetProxyFactory(pFactory.getProxyConfiguration().getTargetName()
                            .getPortName()), targetConfig.getClass(), downScope, scopeContext);
                }
                pFactory.initialize();
            }

            scopeContext.registerConfiguration(source);
        }
        for (Component component : components) {
            RuntimeConfiguration config = (RuntimeConfiguration) component.getComponentImplementation().getRuntimeConfiguration();
            InstanceContext context = (InstanceContext) config.createInstanceContext();
            if ("source".equals(component.getName())) {
                ModuleScopeComponent source = (ModuleScopeComponent) context.getInstance(null);
                Assert.assertNotNull(source);
                GenericComponent gComp = (GenericComponent) source.getGenericComponent();
                gComp.getString();
            }
        }
    }

    
    
    
    
    public Module createModule() throws Exception {
        Component sourceComponent = MockAssemblyFactory.createComponent("source", ModuleScopeComponentImpl.class,Scope.MODULE);
        Component targetComponent = MockAssemblyFactory.createComponent("target", ModuleScopeComponentImpl.class,Scope.MODULE);

        Service targetService = factory.createService();
        JavaServiceContract targetContract = factory.createJavaServiceContract();
        targetContract.setInterface(GenericComponent.class);
        targetService.setServiceContract(targetContract);
        targetService.setName("GenericComponent");
        ConfiguredService cTargetService = factory.createConfiguredService();
        cTargetService.setService(targetService);
        cTargetService.initialize(assemblyContext);
        targetComponent.getConfiguredServices().add(cTargetService);
        targetComponent.initialize(assemblyContext);
        
        Reference ref = factory.createReference();
        ConfiguredReference cref = factory.createConfiguredReference();
        ref.setName("setGenericComponent");
        JavaServiceContract inter = factory.createJavaServiceContract();
        inter.setInterface(GenericComponent.class);
        ref.setServiceContract(inter);
        cref.setReference(ref);
        cref.getTargetConfiguredServices().add(cTargetService);
        cref.initialize(assemblyContext);
        sourceComponent.getConfiguredReferences().add(cref);
        sourceComponent.initialize(assemblyContext);

        Module module = factory.createModule();
        module.setName("test.module");
        module.getComponents().add(sourceComponent);
        module.getComponents().add(targetComponent);
        return module;
    }

    private static AggregateContext createContext() {
        return new AggregateContextImpl("test.parent", null, new DefaultScopeStrategy(), new EventContextImpl(),
                new MockConfigContext(null), new NullMonitorFactory());
    }

}
