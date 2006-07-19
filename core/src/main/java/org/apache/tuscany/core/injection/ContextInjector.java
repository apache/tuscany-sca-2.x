package org.apache.tuscany.core.injection;

import org.apache.tuscany.spi.ObjectCreationException;

/**
 * Implementations inject a pre-configured context type (interface) on an instance.
 *
 * @version $Rev$ $Date$
 */
public interface ContextInjector<S, T> extends Injector<T> {

    void setContext(S context) throws ObjectCreationException;

}
