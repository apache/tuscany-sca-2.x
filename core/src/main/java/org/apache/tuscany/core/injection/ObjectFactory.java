package org.apache.tuscany.core.injection;

/**
 * Implementations create new instances of a particular type
 *
 * @version $Rev$ $Date$
 * @see PojoObjectFactory
 * @see SingletonObjectFactory
 * @see SDOObjectFactory
 */
public interface ObjectFactory<T> {

    /**
     * Return a instance of the type that this factory creates.
     *
     * @return a instance from this factory
     */
    T getInstance() throws ObjectCreationException;

}
