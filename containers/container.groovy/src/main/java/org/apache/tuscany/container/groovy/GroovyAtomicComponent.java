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
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.extension.AtomicComponentExtension;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;
import org.codehaus.groovy.control.CompilationFailedException;

/**
 * The Groovy atomic component implementation. Groovy implementations may be "scripts" or classes.
 */
public class GroovyAtomicComponent<T> extends AtomicComponentExtension<T> {

    private String script;
    private List<Class<?>> services;
    private List<PropertyInjector> injectors;

    public GroovyAtomicComponent(String name, String script,
                                 List<Class<?>>services,
                                 Scope scope,
                                 List<PropertyInjector> injectors,
                                 CompositeComponent parent,
                                 ScopeContainer scopeContainer,
                                 WireService wireService) {
        super(name, parent, scopeContainer, wireService);
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

    public Object createInstance() throws ObjectCreationException {
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
            for (List<OutboundWire> referenceWires : getOutboundWires().values()) {
                for (OutboundWire<?> wire : referenceWires) {
                    object.setProperty(wire.getReferenceName(), wireService.createProxy(wire));
                }
            }
            return object;
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
        return (GroovyObject) scopeContainer.getInstance(this);
    }

    @SuppressWarnings("unchecked")
    public T getServiceInstance() throws TargetException {
        //TODO this should return a default service from a wire
        return (T) getTargetInstance();
    }

    public Object getServiceInstance(String service) throws TargetException {
        InboundWire<?> wire = getInboundWire(service);
        if (wire == null) {
            TargetException e = new TargetException("ServiceDefinition not found"); // TODO better error message
            e.setIdentifier(service);
            throw e;
        }
        return wireService.createProxy(wire);
    }

    public void init(Object instance) throws TargetException {
        //TODO implement - this should call some kind of init method
    }

    public void destroy(Object instance) throws TargetException {
        //TODO implement - this should call some kind of destroy method
    }
}
