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
package org.apache.tuscany.container.js.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.container.js.context.JavaScriptComponentContext;
import org.apache.tuscany.container.js.rhino.RhinoScript;
import org.apache.tuscany.core.builder.ContextCreationException;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.AtomicContext;
import org.apache.tuscany.core.wire.TargetWireFactory;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.model.assembly.Scope;

/**
 * Creates instance contexts for JavaScript component types
 * 
 * @version $Rev$ $Date$
 */
public class JavaScriptContextFactory implements ContextFactory<AtomicContext> {

    private Scope scope;

    private String name;

    private Map<String, Class> services;

    private Map<String, Object> properties;

    private RhinoScript invoker;

    private CompositeContext parentContext;
    
    public JavaScriptContextFactory(String name, Scope scope, Map<String, Class> services,
            Map<String, Object> properties, RhinoScript invoker) {
        this.name = name;
        this.scope = scope;
        this.services = services;
        this.properties = properties;
        this.invoker = invoker;
    }

    public AtomicContext createContext() throws ContextCreationException {
        return new JavaScriptComponentContext(name, services, properties, sourceProxyFactories, targetProxyFactories, invoker
                .copy());
    }

    public Scope getScope() {
        return scope;
    }

    public String getName() {
        return name;
    }

    public void addProperty(String propertyName, Object value) {

    }

    private Map<String, TargetWireFactory> targetProxyFactories = new HashMap<String, TargetWireFactory>();

    public void addTargetWireFactory(String serviceName, TargetWireFactory factory) {
        targetProxyFactories.put(serviceName, factory);
    }

    public TargetWireFactory getTargetWireFactory(String serviceName) {
        return targetProxyFactories.get(serviceName);
    }

    public Map<String, TargetWireFactory> getTargetWireFactories() {
        return targetProxyFactories;
    }

    private List<SourceWireFactory> sourceProxyFactories = new ArrayList<SourceWireFactory>();

    public void addSourceWireFactory(String referenceName, SourceWireFactory factory) {
        sourceProxyFactories.add(factory);
    }

    public void addSourceWireFactories(String referenceName, Class referenceInterface, List<SourceWireFactory> factory, boolean multiplicity) {
        //TODO implement
    }

    public List<SourceWireFactory> getSourceWireFactories() {
        return sourceProxyFactories;
    }

    public void prepare(CompositeContext parent) {
        parentContext = parent;
    }


}
