package org.apache.tuscany.sca.implementation.bpel.impl;

/**
 * Thrown when a process can't be compiled properly or when its descriptors
 * are invalid.
 */
public class ODEProcessException extends RuntimeException {
    private static final long serialVersionUID = 1047893235216756186L;

    public ODEProcessException(String message) {
        super(message);
    }

    public ODEProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public ODEProcessException(Throwable cause) {
        super(cause);
    }
}
