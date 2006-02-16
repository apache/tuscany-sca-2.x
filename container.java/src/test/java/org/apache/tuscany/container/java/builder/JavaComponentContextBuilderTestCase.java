package org.apache.tuscany.container.java.builder;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;
import org.apache.tuscany.container.java.assembly.pojo.PojoJavaOperationType;
import org.apache.tuscany.container.java.invocation.mock.MockSyncInterceptor;
import org.apache.tuscany.container.java.mock.MockAssemblyFactory;
import org.apache.tuscany.container.java.mock.MockConfigContext;
import org.apache.tuscany.container.java.mock.components.GenericComponent;
import org.apache.tuscany.container.java.mock.components.ModuleScopeComponent;
import org.apache.tuscany.container.java.mock.components.ModuleScopeComponentImpl;
import org.apache.tuscany.core.builder.RuntimeConfiguration;
import org.apache.tuscany.core.builder.impl.DefaultWireBuilder;
import org.apache.tuscany.core.builder.impl.HierarchicalBuilder;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
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
import org.apache.tuscany.core.message.impl.PojoMessageFactory;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ScopeEnum;
import org.apache.tuscany.model.assembly.pojo.PojoConfiguredReference;
import org.apache.tuscany.model.assembly.pojo.PojoConfiguredService;
import org.apache.tuscany.model.assembly.pojo.PojoInterface;
import org.apache.tuscany.model.assembly.pojo.PojoInterfaceType;
import org.apache.tuscany.model.assembly.pojo.PojoJavaInterface;
import org.apache.tuscany.model.assembly.pojo.PojoModule;
import org.apache.tuscany.model.assembly.pojo.PojoPort;
import org.apache.tuscany.model.assembly.pojo.PojoReference;
import org.apache.tuscany.model.assembly.pojo.PojoService;

public class JavaComponentContextBuilderTestCase extends TestCase {

    public JavaComponentContextBuilderTestCase() {
    }

    public void testBuilder() throws Exception {
        JavaComponentContextBuilder2 builder = new JavaComponentContextBuilder2();
        builder.setMessageFactory(new PojoMessageFactory());
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
            for (ProxyFactory factory : (Collection<ProxyFactory>) source.getSourceProxyFactories().values()) {
                ProxyConfiguration pConfig = factory.getProxyConfiguration();
                Component target = compMap.get(pConfig.getTargetName().getPartName());

                if (target != null) {
                    RuntimeConfiguration targetConfig = (RuntimeConfiguration) target.getComponentImplementation()
                            .getRuntimeConfiguration();
                    boolean downScope = strategy.downScopeReference(source.getScope(), targetConfig.getScope());
                    wireBuilder.wire(factory, targetConfig.getTargetProxyFactory(factory.getProxyConfiguration().getTargetName()
                            .getPortName()), targetConfig.getClass(), downScope, scopeContext);
                }
                factory.initialize();
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
        Component sourceComponent = MockAssemblyFactory.createComponent("source", ModuleScopeComponentImpl.class,
                ScopeEnum.MODULE_LITERAL);
        Component targetComponent = MockAssemblyFactory.createComponent("target", ModuleScopeComponentImpl.class,
                ScopeEnum.MODULE_LITERAL);
        PojoReference ref = new PojoReference();
        PojoConfiguredReference cref = new PojoConfiguredReference();
        ref.setName("setGenericComponent");
        PojoInterface inter = new PojoJavaInterface();
        PojoInterfaceType type = new PojoInterfaceType();
        type.setInstanceClass(GenericComponent.class);
        PojoJavaOperationType oType = new PojoJavaOperationType();
        oType.setName("getString");
        oType.setJavaMethod((Method) JavaIntrospectionHelper.getBeanProperty(GenericComponent.class, "getString", null));
        type.addOperationType(oType);
        inter.setInterfaceType(type);
        ref.setInterfaceContract(inter);
        cref.setReference(ref);
        cref.setPart(targetComponent);
        PojoPort port = new PojoPort();
        port.setName("GenericComponent");
        cref.setPort(port);
        sourceComponent.getConfiguredReferences().add(cref);
        PojoService sourceService = new PojoService();
        sourceService.setInterfaceContract(inter);
        sourceService.setName("GenericComponent");
        PojoConfiguredService cService = new PojoConfiguredService();
        cService.setService(sourceService);
        cService.setPart(sourceComponent);
        cService.setPort(sourceService);

        sourceComponent.getComponentImplementation().getServices().add(sourceService);
        sourceComponent.getConfiguredServices().add(cService);

        PojoService targetService = new PojoService();
        targetService.setInterfaceContract(inter);
        targetService.setName("GenericComponent");
        PojoConfiguredService cTargetService = new PojoConfiguredService();
        cTargetService.setService(targetService);
        cTargetService.setPart(targetComponent);
        cTargetService.setPort(targetService);
        targetComponent.getComponentImplementation().getServices().add(targetService);
        targetComponent.getConfiguredServices().add(cTargetService);

        PojoModule module = new PojoModule();
        module.setName("test.module");
        module.addComponent(sourceComponent);
        module.addComponent(targetComponent);
        return module;
    }

    private static AggregateContext createContext() {
        return new AggregateContextImpl("test.parent", null, new DefaultScopeStrategy(), new EventContextImpl(),
                new MockConfigContext(null), new NullMonitorFactory());
    }

}
