package org.apache.tuscany.core.injection;

import org.apache.tuscany.common.ObjectCreationException;

/**
 * Implementations inject a pre-configured value on an instance
 * 
 * @version $Rev$ $Date$
 * @see MethodInjector
 * @see FieldInjector
 */
public interface Injector<T> {

    /**
     * Inject a value on the given instance
     */
    void inject(T instance) throws ObjectCreationException;

}
