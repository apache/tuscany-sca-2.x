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

import org.apache.tuscany.core.builder.ContextResolver;
import org.apache.tuscany.core.context.impl.AbstractContext;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.context.TargetException;

/**
 * Manages an entry point into a system module. System entry points cache a direct (i.e. non-proxied) reference to a
 * component instance.
 * 
 * @version $Rev$ $Date$
 */
public class SystemEntryPointContext extends AbstractContext implements EntryPointContext {

    // a reference to the component's implementation instance exposed by the entry point
    private Object cachedInstance;

    private ContextResolver resolver;

    private QualifiedName targetName;

    private  Class serviceInterface;

    public SystemEntryPointContext(String name, String targetName,  Class serviceInterface, ContextResolver resolver) {
        super(name);
        assert (resolver != null) : "Context resolver was null";
        assert (targetName != null) : "Target name was null";
        assert (serviceInterface != null) : "Service interface is null";
        this.serviceInterface = serviceInterface;
        this.resolver = resolver;
        this.targetName = new QualifiedName(targetName);
    }

    public Object getInstance(QualifiedName qName) throws TargetException {
        try {
            if (cachedInstance == null) {
                Context ctx = resolver.getCurrentContext().getContext(targetName.getPartName());
                if (ctx == null){
                    return null;
                }
                cachedInstance = ctx.getInstance(targetName);
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

    public Object getHandler() throws TargetException {
        return getInstance(null);
    }

    public Class getServiceInterface() {
        return serviceInterface;
    }
}
