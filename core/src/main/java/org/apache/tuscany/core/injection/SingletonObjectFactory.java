package org.apache.tuscany.core.injection;

/**
 * Implementation of ObjectFactory that returns a single instance, typically an immutable type.
 * 
 * @version $Rev$ $Date$
 */
public class SingletonObjectFactory<T> implements ObjectFactory<T> {
    private final T instance;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public SingletonObjectFactory(T instance) {
        this.instance = instance;
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public T getInstance() {
        return instance;
    }

}
