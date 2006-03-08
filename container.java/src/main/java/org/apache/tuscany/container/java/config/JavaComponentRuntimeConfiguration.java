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
package org.apache.tuscany.container.java.config;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.container.java.context.JavaComponentContext;
import org.apache.tuscany.core.builder.ContextCreationException;
import org.apache.tuscany.core.builder.RuntimeConfiguration;
import org.apache.tuscany.core.context.SimpleComponentContext;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.model.assembly.Scope;

/**
 * A RuntimeConfiguration that handles POJO component implementation types
 * 
 * @version $Rev$ $Date$
 */
public class JavaComponentRuntimeConfiguration implements RuntimeConfiguration<SimpleComponentContext> {

    // the component name as configured in the hosting module
    private String name;

    // the implementation type constructor
    private Constructor ctr;

    // injectors for properties, references and other metadata values such as
    private List<Injector> setters;

    // an invoker for a method decorated with @Init
    private EventInvoker init;

    // whether the component should be eagerly initialized when its scope starts
    private boolean eagerInit;

    // an invoker for a method decorated with @Destroy
    private EventInvoker destroy;

    // the scope of the implementation instance
    private Scope scope;

    private boolean stateless;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    /**
     * Creates the runtime configuration
     * 
     * @param name the SCDL name of the component the context refers to
     * @param ctr the implementation type constructor
     * @param setters a collection of <code>Injectors</code> used to configure properties, references and other meta
     *        data values on implementation instances
     * @param eagerInit whether the component should be eagerly initialized
     * @param init an <code>Invoker</code> pointing to a method on the implementation type decorated with
     *        <code>@Init</code>
     * @param destroy an <code>Invoker</code> pointing to a method on the implementation type decorated with
     *        <code>@Destroy</code>
     * @param scope the scope of the component implementation type
     */
    public JavaComponentRuntimeConfiguration(String name, Constructor ctr, List<Injector> setters, boolean eagerInit,
            EventInvoker init, EventInvoker destroy, Scope scope) {
        assert (name != null) : "Name was null";
        assert (ctr != null) : "Constructor was null";
        this.name = name;
        this.ctr = ctr;
        this.setters = setters;
        this.eagerInit = eagerInit;
        this.init = init;
        this.destroy = destroy;
        this.scope = scope;
        stateless = (scope == Scope.INSTANCE);
    }

    public JavaComponentRuntimeConfiguration(String name, Constructor ctr, boolean eagerInit, EventInvoker init,
            EventInvoker destroy, Scope scope) {
        this(name, ctr, null, eagerInit, init, destroy, scope);
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public String getName() {
        return name;
    }

    public Scope getScope() {
        return scope;
    }

    public SimpleComponentContext createInstanceContext() throws ContextCreationException {
        PojoObjectFactory objectFactory = new PojoObjectFactory(ctr, null, setters);
        return new JavaComponentContext(name, objectFactory, eagerInit, init, destroy, stateless);
    }

    private Map<String, ProxyFactory> targetProxyFactories = new HashMap();

    public void addTargetProxyFactory(String serviceName, ProxyFactory factory) {
        targetProxyFactories.put(serviceName, factory);
    }

    public ProxyFactory getTargetProxyFactory(String serviceName) {
        return targetProxyFactories.get(serviceName);
    }

    public Map<String, ProxyFactory> getTargetProxyFactories() {
        return targetProxyFactories;
    }

    private List<ProxyFactory> sourceProxyFactories = new ArrayList();

    public void addSourceProxyFactory(String referenceName, ProxyFactory factory) {
        sourceProxyFactories.add(factory);
    }

    public List<ProxyFactory> getSourceProxyFactories() {
        return sourceProxyFactories;
    }

    public void setSetters(List<Injector> setters) {
        this.setters = setters;
    }

    public void prepare(){
        
    }
  
}
