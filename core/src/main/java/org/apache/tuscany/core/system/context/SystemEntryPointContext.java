/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.system.context;

import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.context.AbstractContext;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.core.context.TargetException;

/**
 * Manages an entry point into a system module. System entry points cache a direct (i.e. non-proxied) reference to a
 * component instance.
 * 
 * @version $Rev$ $Date$
 */
public class SystemEntryPointContext extends AbstractContext implements EntryPointContext {

    // responsible for resolving the component implementation instance exposed by the entry point
    private ObjectFactory factory;

    // a reference to the component's implementation instance exposed by the entry point
    private Object cachedInstance;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public SystemEntryPointContext(String name, ObjectFactory factory) {
        super(name);
        assert (factory != null) : "Object factory was null";
        this.factory = factory;
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public Object getInstance(QualifiedName qName) throws TargetException {
        return getInstance(qName, true);
    }

    public Object getInstance(QualifiedName qName, boolean notify) throws TargetException {
        try {
            if (cachedInstance == null) {
                cachedInstance = factory.getInstance();
            }
            return cachedInstance;
        } catch (TargetException e) {
            e.addContextName(getName());
            throw e;
        }
    }

    public void start() throws CoreRuntimeException {
        lifecycleState = RUNNING;
    }

    public void stop() throws CoreRuntimeException {
        lifecycleState = STOPPED;
    }

    public Object getImplementationInstance() throws TargetException{
        return getInstance(null);
    }

    public Object getImplementationInstance(boolean notify) throws TargetException{
        return getInstance(null,notify);
    }
}
