package org.apache.tuscany.core.system.builder;

import org.apache.tuscany.core.system.context.AutowireReferenceContextImpl;
import org.apache.tuscany.core.system.context.SystemServiceContext;
import org.apache.tuscany.core.system.context.SystemServiceContextImpl;
import org.apache.tuscany.core.system.model.SystemBinding;
import org.apache.tuscany.core.system.wire.SystemTargetWire;
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
        String targetName = boundService.getTarget().getPath();
//        SystemServiceContext context = new SystemServiceContextImpl(boundService.getName(),interfaze,
//                targetName,parent);

        return null;
    }

    public Context build(CompositeContext parent, BoundReference<SystemBinding> boundReference) {
        Class<?> interfaze = boundReference.getServiceContract().getInterface();
        AutowireReferenceContextImpl ctx = new AutowireReferenceContextImpl(boundReference.getName(), interfaze, parent);
        TargetWire<?> wire = new SystemTargetWire(interfaze, ctx);
        ctx.setTargetWire(wire);
        return ctx;
    }
}
