package org.apache.tuscany.core.injection;

import org.apache.tuscany.common.ObjectCreationException;
import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.context.ServiceContext;
import org.apache.tuscany.spi.context.TargetNotFoundException;
import org.apache.tuscany.spi.context.UnknownTargetTypeException;
import org.apache.tuscany.spi.context.ReferenceContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class DynamicTargetResolver<T> implements ObjectFactory<T> {

    private CompositeContext parent;
    private QualifiedName targetName;

    public DynamicTargetResolver(CompositeContext parent, String targetName) {
        assert(parent != null): "Parent context was null";
        assert(targetName != null): "Target name not specified";
        this.parent = parent;
        this.targetName = new QualifiedName(targetName);
    }

    @SuppressWarnings("unchecked")
    public T getInstance() throws ObjectCreationException {
        Context ctx = parent.getContext(targetName.getPartName());
        if (ctx == null) {
            TargetNotFoundException e = new TargetNotFoundException("No target component with name");
            e.setIdentifier(targetName.getPartName());
            throw e;
        }
        if (ctx instanceof AtomicContext) {
            return (T) ((AtomicContext) ctx).getService(targetName.getPortName());
        } else if (ctx instanceof ServiceContext) {
            return (T) ctx.getService();
        } else if (ctx instanceof CompositeContext) {
            ServiceContext serviceContext = ((CompositeContext) ctx).getServiceContext(targetName.getPortName());
            if (serviceContext == null) {
                TargetNotFoundException e = new TargetNotFoundException("Service not found");
                e.setIdentifier(targetName.getPortName());
                e.addContextName(targetName.getPortName());
                throw e;
            }
            return (T) serviceContext.getService();
        } else if (ctx instanceof ReferenceContext){
            return (T) ctx.getService();
        }else {
            UnknownTargetTypeException e = new UnknownTargetTypeException(ctx.getClass().getName());
            e.setIdentifier(targetName.getQualifiedName());
            throw e;
        }
    }
}
