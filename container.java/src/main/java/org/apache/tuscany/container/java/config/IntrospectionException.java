package org.apache.tuscany.container.java.config;

import org.apache.tuscany.common.TuscanyRuntimeException;

public class IntrospectionException extends TuscanyRuntimeException {

    public IntrospectionException() {
        super();
    }

    public IntrospectionException(String message) {
        super(message);
    }

    public IntrospectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public IntrospectionException(Throwable cause) {
        super(cause);
    }

}

