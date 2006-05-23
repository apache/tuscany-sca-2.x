package org.apache.tuscany.container.java.builder;

import org.apache.tuscany.container.java.context.JavaAtomicContext;
import org.apache.tuscany.container.java.mock.components.Source;
import org.apache.tuscany.container.java.mock.components.SourceImpl;
import org.apache.tuscany.container.java.mock.components.Target;
import org.apache.tuscany.container.java.model.JavaImplementation;
import org.apache.tuscany.core.context.CompositeContextImpl;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.model.PojoComponentType;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.model.Component;
import org.apache.tuscany.spi.model.JavaServiceContract;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.Service;
import org.apache.tuscany.spi.model.ServiceContract;
import org.jmock.MockObjectTestCase;

/**
 * @version $$Rev$$ $$Date$$
 */
public class JavaComponentBuilderTestCase extends MockObjectTestCase {

    @SuppressWarnings("unchecked")
    public void testBuild() throws Exception {
        CompositeContext parent = new CompositeContextImpl(null, null, null);
        ModuleScopeContext scope = new ModuleScopeContext(null);
        scope.start();
        PojoComponentType sourceType = new PojoComponentType();
        sourceType.setLifecycleScope(Scope.MODULE);
        sourceType.addReferenceMember("target", SourceImpl.class.getMethod("setTarget", Target.class));

        ServiceContract sourceContract = new JavaServiceContract();
        sourceContract.setInterfaceClass(Source.class);
        Service sourceService = new Service();
        sourceService.setName("Source");
        sourceService.setServiceContract(sourceContract);

        sourceType.add(sourceService);
        JavaImplementation sourceImpl = new JavaImplementation();
        sourceImpl.setComponentType(sourceType);
        sourceImpl.setImplementationClass(SourceImpl.class);
        Component<JavaImplementation> sourceComponent = new Component<JavaImplementation>(sourceImpl);

        JavaComponentBuilder builder = new JavaComponentBuilder();
        JavaAtomicContext<Source> ctx = (JavaAtomicContext<Source>) builder.build(parent, sourceComponent);
        ctx.setScopeContext(scope);
        ctx.start();
        Source source = ctx.getService();
        assertNotNull(source);
        ctx.stop();
    }


}
