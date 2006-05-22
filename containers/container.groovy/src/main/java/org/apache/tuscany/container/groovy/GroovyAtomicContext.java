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
package org.apache.tuscany.container.groovy;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.context.InstanceWrapper;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.extension.AtomicContextExtension;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import groovy.lang.GroovyObject;

/**
 * Groovy atomic component context.
 */
public class GroovyAtomicContext extends AtomicContextExtension {

    private URI script;
    private List<Class<?>> services;

    public GroovyAtomicContext(String name, URI script, List<Class<?>>services) {
        this.name = name;
        this.script = script;
        this.services = services;
    }

    public URI getScript() {
        return script;
    }

    public List getServiceInterfaces() {
        return Collections.unmodifiableList(services);
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method method) {
        return new GroovyInvoker(method.getName(),this);
    }

    public InstanceWrapper createInstance() throws ObjectCreationException {
        return new GroovyInstanceWrapper(name, script);
    }

    @SuppressWarnings("unchecked")
    public GroovyObject getTargetInstance() throws TargetException {
        return (GroovyObject)scopeContext.getInstance(this);
    }

    public Object getService() throws TargetException {
        return getTargetInstance();
    }

    public Object getService(String s) throws TargetException {
        return getTargetInstance();
    }

    public void init(Object instance) throws TargetException {
        GroovyObject object = (GroovyObject)instance;
        //for (SourceWire wire : sourceWires) {
            // wire from the groovy script to targets
        //    object.setProperty(wire.getReferenceName(), wire.getTargetService());
        //}
    }

    public void destroy(Object instance) throws TargetException {

    }
}
