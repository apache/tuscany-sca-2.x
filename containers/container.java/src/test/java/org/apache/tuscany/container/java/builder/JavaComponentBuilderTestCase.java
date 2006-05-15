package org.apache.tuscany.container.java.builder;

import junit.framework.TestCase;
import org.apache.tuscany.container.java.context.JavaAtomicContext;
import org.apache.tuscany.container.java.mock.components.Source;
import org.apache.tuscany.container.java.mock.components.SourceImpl;
import org.apache.tuscany.container.java.mock.components.Target;
import org.apache.tuscany.container.java.model.JavaImplementation;
import org.apache.tuscany.core.context.CompositeContextImpl;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.scope.ModuleScopeObjectFactory;
import org.apache.tuscany.core.context.scope.ScopeRegistryImpl;
import org.apache.tuscany.core.model.PojoComponentType;
import org.apache.tuscany.core.wire.jdk.JDKWireFactoryService;
import org.apache.tuscany.core.wire.system.WireServiceImpl;
import org.apache.tuscany.model.Component;
import org.apache.tuscany.model.JavaServiceContract;
import org.apache.tuscany.model.Scope;
import org.apache.tuscany.model.Service;
import org.apache.tuscany.model.ServiceContract;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.ScopeRegistry;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.spi.wire.WireService;

/**
 * @version $$Rev$$ $$Date$$
 */
public class JavaComponentBuilderTestCase extends TestCase {

    private WireService wireService;

    public void testBuild() throws Exception {
        CompositeContext parent = new CompositeContextImpl();
        WorkContext workContext = new WorkContextImpl();
        workContext.setRemoteContext(parent);
        ScopeRegistry scopeRegistry = new ScopeRegistryImpl(workContext);
        scopeRegistry.registerFactory(Scope.MODULE, new ModuleScopeObjectFactory());

        PojoComponentType sourceType = new PojoComponentType();
        sourceType.setLifecycleScope(Scope.MODULE);
        sourceType.setReferenceMember("target", SourceImpl.class.getMethod("setTarget", Target.class));

        ServiceContract sourceContract = new JavaServiceContract();
        sourceContract.setIntefaze(Source.class);
        Service sourceService = new Service();
        sourceService.setName("Source");
        sourceService.setServiceContract(sourceContract);

        sourceType.add(sourceService);
        JavaImplementation sourceImpl = new JavaImplementation();
        sourceImpl.setComponentType(sourceType);
        sourceImpl.setImplementationClass(SourceImpl.class);
        Component<JavaImplementation> sourceComponent = new Component<JavaImplementation>(sourceImpl);

        JavaComponentBuilder builder = new JavaComponentBuilder();
        builder.setWireService(wireService);
        builder.setScopeRegistry(scopeRegistry);
        JavaAtomicContext<Source> ctx = (JavaAtomicContext<Source>) builder.build(parent, sourceComponent);
        ctx.start();
        Source source = ctx.getService();
        assertNotNull(source);
        ctx.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        wireService = new WireServiceImpl(new JDKWireFactoryService());
    }


}
