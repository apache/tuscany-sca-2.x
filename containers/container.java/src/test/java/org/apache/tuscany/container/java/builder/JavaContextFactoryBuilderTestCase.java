package org.apache.tuscany.container.java.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.container.java.invocation.mock.MockSyncInterceptor;
import org.apache.tuscany.container.java.mock.MockFactory;
import org.apache.tuscany.container.java.mock.components.GenericComponent;
import org.apache.tuscany.container.java.mock.components.ModuleScopeComponent;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.builder.impl.DefaultWireBuilder;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.DefaultScopeStrategy;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.context.ScopeStrategy;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.wire.WireConfiguration;
import org.apache.tuscany.core.wire.jdk.JDKProxyFactoryFactory;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.Module;

public class JavaContextFactoryBuilderTestCase extends TestCase {

    public JavaContextFactoryBuilderTestCase() {
    }

    public void testBuilder() throws Exception {
        JavaContextFactoryBuilder builder = new JavaContextFactoryBuilder();
        builder.setMessageFactory(new MessageFactoryImpl());
       // HierarchicalBuilder refBuilder = new HierarchicalBuilder();
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        builder.addPolicyBuilder(new MockInterceptorBuilder(interceptor, true));
        //builder.setPolicyBuilder(refBuilder);
        builder.setProxyFactoryFactory(new JDKProxyFactoryFactory());
        JavaTargetWireBuilder javaWireBuilder = new JavaTargetWireBuilder();
        ScopeStrategy strategy = new DefaultScopeStrategy();
        DefaultWireBuilder wireBuilder = new DefaultWireBuilder();
        wireBuilder.addWireBuilder(javaWireBuilder);
        Module module = MockFactory.createModule();
        EventContext eCtx = new EventContextImpl();
        ScopeContext scopeContext = new ModuleScopeContext(eCtx);
        scopeContext.start();
        scopeContext.onEvent(new ModuleStart(this));
        List<Component> components = module.getComponents();
        Map<String, Component> compMap = new HashMap<String, Component>(components.size());

        for (Component component : components) {
            compMap.put(component.getName(), component);
            builder.build(component);
            ContextFactory contextFactory = (ContextFactory) component.getContextFactory();
            Assert.assertNotNull(contextFactory);
        }
        for (Component component : components) {
            ContextFactory<Context> source = (ContextFactory<Context>) component.getContextFactory();
            Assert.assertNotNull(source);
            for (SourceWireFactory pFactory : source.getSourceProxyFactories()) {
                WireConfiguration pConfig = pFactory.getConfiguration();
                Component target = compMap.get(pConfig.getTargetName().getPartName());

                if (target != null) {
                    ContextFactory targetConfig = (ContextFactory) target.getContextFactory();
                    boolean downScope = strategy.downScopeReference(source.getScope(), targetConfig.getScope());
                    wireBuilder.connect(pFactory, targetConfig.getTargetProxyFactory(pFactory.getConfiguration().getTargetName()
                            .getPortName()), targetConfig.getClass(), downScope, scopeContext);
                }
                pFactory.initialize();
            }

            scopeContext.registerFactory(source);
        }
        for (Component component : components) {
            ContextFactory config = (ContextFactory) component.getContextFactory();
            Context context = config.createContext();
            if ("source".equals(component.getName())) {
                ModuleScopeComponent source = (ModuleScopeComponent) context.getInstance(null);
                Assert.assertNotNull(source);
                GenericComponent gComp = source.getGenericComponent();
                gComp.getString();
            }
        }
    }


}
