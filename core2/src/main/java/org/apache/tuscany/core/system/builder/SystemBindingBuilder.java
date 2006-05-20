package org.apache.tuscany.core.system.builder;

import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.system.context.SystemReferenceContextImpl;
import org.apache.tuscany.core.system.context.SystemServiceContextImpl;
import org.apache.tuscany.core.system.model.SystemBinding;
import org.apache.tuscany.core.system.wire.SystemSourceWire;
import org.apache.tuscany.core.system.wire.SystemTargetAutowire;
import org.apache.tuscany.model.BoundReference;
import org.apache.tuscany.model.BoundService;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.builder.BindingBuilder;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.wire.TargetWire;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SystemBindingBuilder implements BindingBuilder<SystemBinding> {

    public Context build(CompositeContext parent, BoundService<SystemBinding> boundService) {
        Class<?> interfaze = boundService.getServiceContract().getInterface();
        QualifiedName targetName = new QualifiedName(boundService.getTarget().getPath());
        SystemSourceWire<?> wire = new SystemSourceWire(boundService.getName(), targetName, interfaze);
        return new SystemServiceContextImpl(boundService.getName(), interfaze, wire, parent);
    }

    public Context build(CompositeContext parent, BoundReference<SystemBinding> boundReference) {
        assert(parent.getParent() instanceof AutowireContext):"Grandparent not an instance of "+AutowireContext.class.getName();
        AutowireContext autowireContext = (AutowireContext)parent.getParent();
        Class<?> interfaze = boundReference.getServiceContract().getInterface();
        SystemReferenceContextImpl ctx = new SystemReferenceContextImpl(boundReference.getName(), interfaze, parent);
        TargetWire<?> wire = new SystemTargetAutowire(interfaze, autowireContext);
        ctx.setTargetWire(wire);
        return ctx;
    }
}
