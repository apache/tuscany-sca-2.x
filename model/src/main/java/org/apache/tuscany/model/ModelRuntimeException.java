/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The root runtime exception for the model.
 * 
 * FIXME this will extend from TuscanyRuntimeException when we move the latter to common 
 * 
 * @version $Rev$ $Date$
 */
public abstract class ModelRuntimeException extends RuntimeException {

    public ModelRuntimeException() {
        super();
    }

    public ModelRuntimeException(String message) {
        super(message);
    }

    public ModelRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModelRuntimeException(Throwable cause) {
        super(cause);
    }

    
    protected List<String> contextStack;

    protected String moduleComponentName;

    protected String componentName;

    //FIXME duplicate methods below to be eliminated when extends TuscanyRuntimeException
    
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

