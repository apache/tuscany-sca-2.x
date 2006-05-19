package org.apache.tuscany.core.injection;

import org.apache.tuscany.common.ObjectCreationException;
import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.core.context.InvalidServiceTypeException;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.context.ComponentContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.context.TargetNotFoundException;

/**
 * @version $$Rev$$ $$Date$$
 */
public class InterCompositeResolver<T> implements ObjectFactory<T> {

    private CompositeContext parent;
    private QualifiedName name;
    private Class<T> serviceType;

    public InterCompositeResolver(CompositeContext parent, QualifiedName name, Class<T> serviceType) {
        assert(parent != null): "Parent context was null";
        assert(name != null): "Qualified name was null";
        this.parent = parent;
        this.name = name;
        this.serviceType = serviceType;
    }

    @SuppressWarnings("unchecked")
    public T getInstance() throws ObjectCreationException {
        Context context = parent.getContext(name.getPartName());
        if (context instanceof ComponentContext) {
            return serviceType.cast(((ComponentContext) context).getService(name.getPortName()));
        } else {
            Object o = context.getService();
            if (o == null) {
                throw new TargetNotFoundException(name.getQualifiedName());
            } else if (!(serviceType.isAssignableFrom(o.getClass()))) {
                InvalidServiceTypeException e = new InvalidServiceTypeException("Service implements a different interface");
                e.setIdentifier(serviceType.getName());
                e.addContextName(context.getName());
                throw e;
            }
            return serviceType.cast(o);
        }
    }
}
