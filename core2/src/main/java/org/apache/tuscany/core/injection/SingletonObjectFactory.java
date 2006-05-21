package org.apache.tuscany.core.injection;

import org.apache.tuscany.spi.ObjectFactory;

/**
 * Implementation of ObjectFactory that returns a single instance, typically an immutable type.
 *
 * @version $Rev: 399488 $ $Date: 2006-05-03 16:20:27 -0700 (Wed, 03 May 2006) $
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
