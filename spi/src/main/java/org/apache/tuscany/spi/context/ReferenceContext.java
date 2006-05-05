package org.apache.tuscany.spi.context;

/**
 * Manages a bound reference
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
 */
public interface ReferenceContext extends Context {

    /**
     * Returns the handler responsible for flowing a request through the reference
     * @throws org.apache.tuscany.spi.context.TargetException
     */
     public Object getHandler() throws TargetException;

}
