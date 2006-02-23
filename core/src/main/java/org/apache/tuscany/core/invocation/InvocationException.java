package org.apache.tuscany.core.invocation;

import org.apache.tuscany.common.TuscanyException;

/**
 * The root checked exception for the invocation framework 
 * 
 * @version $Rev$ $Date$
 */
public abstract class InvocationException extends TuscanyException {

    public InvocationException() {
        super();
    }

    public InvocationException(String message) {
        super(message);
    }

    public InvocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvocationException(Throwable cause) {
        super(cause);
    }

}

