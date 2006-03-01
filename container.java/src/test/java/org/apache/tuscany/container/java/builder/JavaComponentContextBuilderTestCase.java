package org.apache.tuscany.container.java.builder;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;
import org.apache.tuscany.container.java.invocation.mock.MockSyncInterceptor;
import org.apache.tuscany.container.java.mock.MockConfigContext;
import org.apache.tuscany.container.java.mock.MockFactory;
import org.apache.tuscany.container.java.mock.components.GenericComponent;
import org.apache.tuscany.container.java.mock.components.ModuleScopeComponent;
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
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.impl.AssemblyModelContextImpl;

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
        builder.setPolicyBuilder(refBuilder);
        AggregateContext ctx = createContext();
        builder.setProxyFactoryFactory(new JDKProxyFactoryFactory());
        JavaTargetWireBuilder javaWireBuilder = new JavaTargetWireBuilder();
        ScopeStrategy strategy = new DefaultScopeStrategy();
        DefaultWireBuilder wireBuilder = new DefaultWireBuilder();
        wireBuilder.addWireBuilder(javaWireBuilder);
        Module module = MockFactory.createModule();
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
                    wireBuilder.connect(pFactory, targetConfig.getTargetProxyFactory(pFactory.getProxyConfiguration().getTargetName()
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
    
    private static AggregateContext createContext() {
        return new AggregateContextImpl("test.parent", null, new DefaultScopeStrategy(), new EventContextImpl(),
                new MockConfigContext(null), new NullMonitorFactory());
    }

}
