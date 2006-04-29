package org.apache.tuscany.core.injection;

import org.apache.tuscany.core.builder.ObjectFactory;

/**
 * Implementation of ObjectFactory that returns a single instance, typically an immutable type.
 * 
 * @version $Rev$ $Date$
 */
public class SingletonObjectFactory<T> implements ObjectFactory<T> {
    private final T instance;

    public SingletonObjectFactory(T instance) {
        this.instance = instance;
    }

    public T getInstance() {
        return instance;
    }

}
