package org.apache.tuscany.spi.context;

import java.lang.reflect.InvocationHandler;

/**
 * Manages a reference configured for a binding
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
 */
public interface ReferenceContext<T> extends Context<T> {

    /**
     * Returns the handler responsible for flowing a request through the reference
     *
     * @throws TargetException
     *
     */
    public InvocationHandler getHandler() throws TargetException;

    /**
     * Returns the service interface configured for the reference
     */
    public Class<T> getInterface();


}
