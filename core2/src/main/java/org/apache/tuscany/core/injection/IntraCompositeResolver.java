package org.apache.tuscany.core.injection;

import org.apache.tuscany.common.ObjectCreationException;
import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.spi.context.ComponentContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.context.IllegalTargetException;
import org.apache.tuscany.spi.context.ReferenceContext;

/**
 * Resolves a target within the same composite as a source. The target may be an atomic context, a service
 * offered by a child composite, or a reference.
 * 
 * @version $$Rev$$ $$Date$$
 */
public class IntraCompositeResolver<T> implements ObjectFactory<T> {

    private Context target;
    private String name;

    public IntraCompositeResolver(Context target, String serviceName) {
        assert(target != null): "Target was null";
        if (!(target instanceof ComponentContext) && !(target instanceof ReferenceContext)) {
            IllegalTargetException e = new IllegalTargetException("Invalid target type");
            e.setIdentifier(target.getName());
            throw e;

        }
        name = serviceName;
        this.target = target;
    }

    @SuppressWarnings("unchecked")
    public T getInstance() throws ObjectCreationException {
        if (target instanceof ComponentContext) {
            return (T) ((ComponentContext) target).getService(name);
        } else {
            return (T) target.getService();
        }
    }
}
