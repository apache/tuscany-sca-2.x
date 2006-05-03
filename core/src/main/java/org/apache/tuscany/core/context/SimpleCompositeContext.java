/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.core.context;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.impl.AbstractContext;
import org.apache.tuscany.model.assembly.Composite;
import org.apache.tuscany.model.assembly.Extensible;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.QualifiedName;

/**
 * @version $Rev$ $Date$
 */
public class SimpleCompositeContext extends AbstractContext implements CompositeContext {
    private CompositeContext parent;
    private final Map<String, Context> children = new HashMap<String, Context>();

    public SimpleCompositeContext(String name) {
        super(name);
    }

    public void start() throws CoreRuntimeException {
        for (Context context : children.values()) {
            context.start();
        }
        publish(new ModuleStart(this));
    }

    public void stop() throws CoreRuntimeException {
        publish(new ModuleStop(this));
        for (Context context : children.values()) {
            context.stop();
        }
    }

    public CompositeContext getParent() {
        return parent;
    }

    public void setParent(CompositeContext parent) {
        this.parent = parent;
    }

    public void registerContext(Context context) {
        if (context.getParent() != null) {
            throw new IllegalStateException("Already registered");
        }
        String name = context.getName();
        if (children.containsKey(name)) {
            DuplicateNameException e = new DuplicateNameException(name);
            e.setIdentifier(name);
            e.addContextName(getName());
            throw e;
        }
        context.setParent(this);
        children.put(name, context);
    }

    public Context getContext(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        return children.get(name);
    }

    public Object getInstance(QualifiedName name) throws TargetException {
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        Context context = getContext(name.getPortName());
        if (context == null) {
            return null;
        }
        return context.getInstance(null);
    }

    @Deprecated
    public void registerModelObject(Extensible model) throws ConfigurationException {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public Composite getComposite() {
        throw new UnsupportedOperationException();
    }
}
