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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.InstanceWrapper;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.extension.AtomicContextExtension;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.TargetWire;
import org.codehaus.groovy.control.CompilationFailedException;

/**
 * Groovy atomic component context.
 */
public class GroovyAtomicContext<T> extends AtomicContextExtension<T> {

    private String script;
    private List<Class<?>> services;
    private List<PropertyInjector> injectors;

    public GroovyAtomicContext(String name, String script, List<Class<?>>services, Scope scope,
                               List<PropertyInjector> injectors, CompositeContext parent) {
        super(name, parent);
        this.script = script;
        this.services = services;
        this.scope = scope;
        this.injectors = (injectors != null) ? injectors : new ArrayList<PropertyInjector>();
    }

    public String getScript() {
        return script;
    }

    public List<Class<?>> getServiceInterfaces() {
        return Collections.unmodifiableList(services);
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method method) {
        return new GroovyInvoker(method.getName(), this);
    }

    public InstanceWrapper createInstance() throws ObjectCreationException {
        try {
            ClassLoader parent = getClass().getClassLoader();
            GroovyClassLoader loader = new GroovyClassLoader(parent);
            Class groovyClass = loader.parseClass(script);
            GroovyObject object = (GroovyObject) groovyClass.newInstance();
            // inject properties
            for (PropertyInjector injector : injectors) {
                injector.inject(object);
            }
            // inject wires
            for (SourceWire wire : sourceWires) {
                object.setProperty(wire.getReferenceName(), wire.getTargetService());
            }
            return new GroovyInstanceWrapper(this, object);
        } catch (CompilationFailedException e) {
            throw new ObjectCreationException(e);
        } catch (IllegalAccessException e) {
            throw new ObjectCreationException(e);
        } catch (InstantiationException e) {
            throw new ObjectCreationException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public GroovyObject getTargetInstance() throws TargetException {
        return (GroovyObject) scopeContext.getInstance(this);
    }

    @SuppressWarnings("unchecked")
    public T getService() throws TargetException {
        //TODO this should return a default service from a wire
        return (T) getTargetInstance();
    }

    public Object getService(String service) throws TargetException {
        TargetWire<?> wire = getTargetWire(service);
        if (wire == null) {
            TargetException e =  new TargetException("Service not found"); // TODO better error message
            e.setIdentifier(service);
            throw e;
        }
        return wire.getTargetService();
    }

    public void init(Object instance) throws TargetException {
        //TODO implement - this should call some kind of init method
    }

    public void destroy(Object instance) throws TargetException {
        //TODO implement - this should call some kind of destroy method
    }
}
