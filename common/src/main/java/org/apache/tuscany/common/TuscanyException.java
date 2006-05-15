/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.common;

import java.util.ArrayList;
import java.util.List;

/**
 * The root checked exception for the Tuscany runtime.
 *
 * @version $Rev: 368822 $ $Date: 2006-01-13 10:54:38 -0800 (Fri, 13 Jan 2006) $
 */
public abstract class TuscanyException extends Exception {
    private static final long serialVersionUID = -7847121698339635268L;
    private List<String> contextStack;
    private String identifier;

    /**
     * Override constructor from Exception.
     *
     * @see Exception
     */
    public TuscanyException() {
        super();
    }

    /**
     * Override constructor from Exception.
     *
     * @param message passed to Exception
     * @see Exception
     */
    public TuscanyException(String message) {
        super(message);
    }

    /**
     * Override constructor from Exception.
     *
     * @param message passed to Exception
     * @param cause   passed to Exception
     * @see Exception
     */
    public TuscanyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Override constructor from Exception.
     *
     * @param cause passed to Exception
     * @see Exception
     */
    public TuscanyException(Throwable cause) {
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
