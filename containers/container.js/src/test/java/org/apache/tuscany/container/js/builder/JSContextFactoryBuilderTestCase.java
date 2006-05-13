package org.apache.tuscany.container.js.builder;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;
import org.apache.tuscany.container.js.assembly.mock.HelloWorldService;
import org.apache.tuscany.container.js.config.JavaScriptContextFactory;
import org.apache.tuscany.container.js.mock.MockAssemblyFactory;
import org.apache.tuscany.container.js.invocation.mock.MockSyncInterceptor;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.builder.system.PolicyBuilderRegistry;
import org.apache.tuscany.core.builder.system.DefaultPolicyBuilderRegistry;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.wire.jdk.JDKWireFactoryService;
import org.apache.tuscany.core.wire.TargetWireFactory;
import org.apache.tuscany.core.wire.service.WireFactoryService;
import org.apache.tuscany.core.wire.service.DefaultWireFactoryService;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.AtomicComponent;
import org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.impl.AssemblyContextImpl;
import org.apache.tuscany.model.scdl.loader.impl.SCDLAssemblyModelLoaderImpl;

public class JSContextFactoryBuilderTestCase extends TestCase {

    public void testBasicInvocation() throws Exception {
        MockSyncInterceptor mockInterceptor = new MockSyncInterceptor();
        MockInterceptorBuilder interceptorBuilder = new MockInterceptorBuilder(mockInterceptor);
        PolicyBuilderRegistry policyRegistry = new DefaultPolicyBuilderRegistry();
        policyRegistry.registerSourceBuilder(interceptorBuilder);
        WireFactoryService wireService = new DefaultWireFactoryService(new MessageFactoryImpl(), new JDKWireFactoryService(), policyRegistry);
        JavaScriptContextFactoryBuilder jsBuilder = new JavaScriptContextFactoryBuilder(wireService);


        JavaScriptTargetWireBuilder jsWireBuilder = new JavaScriptTargetWireBuilder();
        AtomicComponent component = MockAssemblyFactory.createComponent("foo",
                "org/apache/tuscany/container/js/assembly/mock/HelloWorldImpl.js", HelloWorldService.class, Scope.MODULE);
        component.initialize(new AssemblyContextImpl(new AssemblyFactoryImpl(), new SCDLAssemblyModelLoaderImpl(), new ResourceLoaderImpl(Thread.currentThread().getContextClassLoader())));
        jsBuilder.build(component);
        ModuleScopeContext context = new ModuleScopeContext(new EventContextImpl());
        ContextFactory<Context> contextFactory = (ContextFactory<Context>) component.getContextFactory();
        context.registerFactory(contextFactory);
        context.start();
        context.onEvent(new ModuleStart(this));
        for (TargetWireFactory proxyFactory : contextFactory.getTargetWireFactories().values()) {
            jsWireBuilder.completeTargetChain(proxyFactory, JavaScriptContextFactory.class, context);
            proxyFactory.initialize();
        }
        Context ctx = contextFactory.createContext();
        HelloWorldService hello = (HelloWorldService) ctx.getInstance(new QualifiedName("foo/HelloWorldService"));
        Assert.assertNotNull(hello);
        Assert.assertEquals("Hello foo", hello.hello("foo"));
    }
}
