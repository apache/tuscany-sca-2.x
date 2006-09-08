package org.apache.tuscany.test.binding;

import org.apache.tuscany.api.TuscanyRuntimeException;

/**
 * @version $Rev$ $Date$
 */
public class TestBindingRuntimeException extends TuscanyRuntimeException {

    public TestBindingRuntimeException() {
    }

    public TestBindingRuntimeException(String message) {
        super(message);
    }

    public TestBindingRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public TestBindingRuntimeException(Throwable cause) {
        super(cause);
    }
}
