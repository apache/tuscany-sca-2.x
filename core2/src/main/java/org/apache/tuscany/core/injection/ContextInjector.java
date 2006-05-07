package org.apache.tuscany.core.injection;

import org.apache.tuscany.common.ObjectCreationException;

/**
 * Implementations inject a pre-configured context type (interface) on an instance. 
 *
 *
 * @version $Rev: 399488 $ $Date: 2006-05-03 16:20:27 -0700 (Wed, 03 May 2006) $
 * @see org.apache.tuscany.core.injection.MethodInjector
 * @see org.apache.tuscany.core.injection.FieldInjector
 */
public interface ContextInjector<S,T> extends Injector<T>{

    void setContext(S context) throws ObjectCreationException;

}
