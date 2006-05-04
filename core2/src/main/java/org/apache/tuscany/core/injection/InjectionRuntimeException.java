package org.apache.tuscany.core.injection;

import org.apache.tuscany.common.TuscanyRuntimeException;

/**
 * Root unchecked exception for the injection package
 *
 * @version $Rev: 380032 $ $Date: 2006-02-22 19:19:11 -0800 (Wed, 22 Feb 2006) $
 */
public abstract class InjectionRuntimeException extends TuscanyRuntimeException {

    public InjectionRuntimeException() {
        super();
    }

    public InjectionRuntimeException(String message) {
        super(message);
    }

    public InjectionRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InjectionRuntimeException(Throwable cause) {
        super(cause);
    }

}
