package org.apache.tuscany.common;

import java.util.ArrayList;
import java.util.List;

/**
 * The root checked exception for the Tuscany runtime.
 * 
 * @version $Rev: 368822 $ $Date: 2006-01-13 10:54:38 -0800 (Fri, 13 Jan 2006) $
 */
public abstract class TuscanyException extends Exception {

    protected List<String> contextStack;

    public TuscanyException() {
        super();
    }

    public TuscanyException(String message) {
        super(message);
    }

    public TuscanyException(String message, Throwable cause) {
        super(message, cause);
    }

    public TuscanyException(Throwable cause) {
        super(cause);
    }

    /**
     * Returns a collection of names representing the context call stack where the error occured. The top of the stack
     * is the first element in the collection.
     */
    public List<String> returnContextNames(String name) {
        if (contextStack == null) {
            contextStack = new ArrayList<String>();
        }
        return contextStack;
    }

    /**
     * Pushes a context name where an error occured onto the call stack
     */
    public void addContextName(String name) {
        if (contextStack == null) {
            contextStack = new ArrayList<String>();
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
            b.append(" [").append(identifier).append("]");
        }
        if (contextStack != null) {
            b.append("\nContext stack trace: ");
            for (int i = contextStack.size() - 1; i >= 0; i--) {
                b.append("[").append(contextStack.get(i)).append("]");
            }
        }
        return super.getMessage() + b.toString();

    }
}
