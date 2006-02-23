package org.apache.tuscany.common;

import java.util.ArrayList;
import java.util.List;

/**
 * The root unchecked exception for the Tuscany runtime
 * 
 * @version $Rev: 368822 $ $Date: 2006-01-13 10:54:38 -0800 (Fri, 13 Jan 2006) $
 */

public abstract class TuscanyRuntimeException extends RuntimeException {

    protected List<String> contextStack;

    protected String moduleComponentName;

    protected String componentName;

    public TuscanyRuntimeException() {
        super();
    }

    public TuscanyRuntimeException(String message) {
        super(message);
    }

    public TuscanyRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public TuscanyRuntimeException(Throwable cause) {
        super(cause);
    }

    /**
     * Returns a collection of names representing the context call stack where the error occured. The top of the stack
     * is the first element in the collection.
     */
    public List<String> returnContextNames(String name) {
        if (contextStack == null) {
            contextStack = new ArrayList();
        }
        return contextStack;
    }

    /**
     * Pushes a context name where an error occured onto the call stack
     */
    public void addContextName(String name) {
        if (contextStack == null) {
            contextStack = new ArrayList();
        }
        contextStack.add(name);
    }

    private String identifier;

    /**
     * Returns a string representing additional error information referred to in the error message
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets an additional error information referred to in the error message
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getMessage() {
        if (identifier == null && contextStack == null) {
            return super.getMessage();
        }
        StringBuffer b = new StringBuffer();
        if (identifier != null) {
            b.append(" [" + identifier + "]");
        }
        if (contextStack != null) {
            b.append("\nContext stack trace: ");
            for (int i = contextStack.size() - 1; i >= 0; i--) {
                b.append("[" + contextStack.get(i) + "]");
            }
        }
        return super.getMessage() + b.toString();

    }

}
