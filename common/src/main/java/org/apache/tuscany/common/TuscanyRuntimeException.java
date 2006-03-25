package org.apache.tuscany.common;

import java.util.ArrayList;
import java.util.List;

/**
 * The root unchecked exception for the Tuscany runtime.
 *
 * @version $Rev: 368822 $ $Date: 2006-01-13 10:54:38 -0800 (Fri, 13 Jan 2006) $
 */

public abstract class TuscanyRuntimeException extends RuntimeException {
    private static final long serialVersionUID = -759677431966121786L;
    private List<String> contextStack;
    private String identifier;

    /**
     * Override constructor from RuntimeException.
     *
     * @see RuntimeException
     */
    public TuscanyRuntimeException() {
        super();
    }

    /**
     * Override constructor from RuntimeException.
     *
     * @param message passed to RuntimeException
     * @see RuntimeException
     */
    public TuscanyRuntimeException(String message) {
        super(message);
    }

    /**
     * Override constructor from RuntimeException.
     *
     * @param message passed to RuntimeException
     * @param cause   passed to RuntimeException
     * @see RuntimeException
     */
    public TuscanyRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Override constructor from RuntimeException.
     *
     * @param cause passed to RuntimeException
     * @see RuntimeException
     */
    public TuscanyRuntimeException(Throwable cause) {
        super(cause);
    }

    /**
     * Returns a collection of names representing the context call stack where the error occured.
     * The top of the stack is the first element in the collection.
     *
     * @return a collection of names representing the context call stack
     */
    public List<String> returnContextNames() {
        if (contextStack == null) {
            contextStack = new ArrayList<String>();
        }
        return contextStack;
    }

    /**
     * Pushes a context name where an error occured onto the call stack.
     *
     * @param name the name of a context to push on the stack
     */
    public void addContextName(String name) {
        if (contextStack == null) {
            contextStack = new ArrayList<String>();
        }
        contextStack.add(name);
    }

    /**
     * Returns a string representing additional error information referred to in the error message.
     *
     * @return additional error information
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets an additional error information referred to in the error message.
     *
     * @param identifier additional error information
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getMessage() {
        if (identifier == null && contextStack == null) {
            return super.getMessage();
        }
        StringBuilder b = new StringBuilder(256);
        b.append(super.getMessage());

        if (identifier != null) {
            b.append(" [").append(identifier).append(']');
        }
        if (contextStack != null) {
            b.append("\nContext stack trace: ");
            for (int i = contextStack.size() - 1; i >= 0; i--) {
                b.append('[').append(contextStack.get(i)).append(']');
            }
        }
        return b.toString();
    }
}
