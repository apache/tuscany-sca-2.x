package org.apache.tuscany.core.injection;

import org.apache.tuscany.common.ObjectCreationException;
import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.context.ComponentContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.context.ReferenceContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class LazyIntraCompositeResolver<T> implements ObjectFactory<T> {

    private CompositeContext parent;
    private QualifiedName name;
    private Context context;

    public LazyIntraCompositeResolver(CompositeContext parent, QualifiedName name) {
        assert(parent != null): "Parent context was null";
        assert(name != null): "Qualified name was null";
        this.parent = parent;
        this.name = name;
    }

    @SuppressWarnings("unchecked")
    public T getInstance() throws ObjectCreationException {
        if (context == null) {
            context = parent.getContext(name.getPartName());
            if (context == null) {
                ObjectCreationException e = new ObjectCreationException("Target not found");
                e.setIdentifier(name.getQualifiedName());
                throw e;
            }
        }
        if (context instanceof ComponentContext) {
            return (T) ((CompositeContext) context).getService(name.getPortName());
        } else if (context instanceof ReferenceContext) {
            return (T) context.getService();
        } else {
            ObjectCreationException e = new ObjectCreationException("Invalid target type");
            e.setIdentifier(context.getName());
            throw e;
        }
    }
}
