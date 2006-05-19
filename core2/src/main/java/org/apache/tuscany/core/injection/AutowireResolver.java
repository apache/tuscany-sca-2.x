package org.apache.tuscany.core.injection;

import org.apache.tuscany.common.ObjectCreationException;
import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.core.context.AutowireContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class AutowireResolver<T> implements ObjectFactory<T> {

    private AutowireContext<?> parent;

    private Class<T> autowireType;

    public AutowireResolver(Class<T> autowireType, AutowireContext parent) {
        assert (autowireType != null) : "Autwire type was null";
        this.autowireType = autowireType;
        this.parent = parent;
    }

    public T getInstance() throws ObjectCreationException {
        return parent.resolveInstance(autowireType);
    }

}
