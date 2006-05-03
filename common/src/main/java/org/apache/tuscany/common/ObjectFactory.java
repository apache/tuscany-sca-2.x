package org.apache.tuscany.common;

/**
 * Implementations create new instances of a particular type
 *
 * @version $Rev: 380903 $ $Date: 2006-02-25 00:53:26 -0800 (Sat, 25 Feb 2006) $
 */
public interface ObjectFactory<T> {

    /**
     * Return a instance of the type that this factory creates.
     *
     * @return a instance from this factory
     */
    T getInstance() throws ObjectCreationException;

}
