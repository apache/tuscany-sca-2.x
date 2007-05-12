/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.sca.implementation.script;

import java.io.StringReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.tuscany.implementation.spi.PropertyValueObjectFactory;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.core.RuntimeComponent;
import org.apache.tuscany.sca.core.RuntimeComponentService;
import org.apache.tuscany.sca.implementation.script.engines.TuscanyJRubyScriptEngine;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.spi.ObjectCreationException;
import org.apache.tuscany.sca.spi.ObjectFactory;

/**
 * Represents a Script implementation.
 */
public class ScriptImplementationProvider implements ImplementationProvider {

    protected RuntimeComponent component;
    protected ScriptImplementation implementation;
    protected ScriptEngine scriptEngine;
    protected PropertyValueObjectFactory propertyFactory;

    public ScriptImplementationProvider(RuntimeComponent component, ScriptImplementation implementation, PropertyValueObjectFactory propertyFactory) {
        this.component = component;
        this.implementation = implementation;
        this.propertyFactory = propertyFactory;
    }

    public Invoker createInvoker(RuntimeComponentService service, Operation operation) {
        return new ScriptInvoker(this, operation.getName());
    }

    public Invoker createCallbackInvoker(Operation operation) {
        return new ScriptInvoker(this, operation.getName());
    }
    
    public void start() {
        try {
            scriptEngine = getScriptEngineByExtension(implementation.getScriptLanguage());
            if (scriptEngine == null) {
                throw new ObjectCreationException("no script engine found for language: " + implementation.getScriptLanguage());
            }
            if (!(scriptEngine instanceof Invocable)) {
                throw new ObjectCreationException("script engine does not support Invocable: " + scriptEngine);
            }
            
            for (Reference reference : implementation.getReferences()) {
                scriptEngine.put(reference.getName(), createReferenceProxy(reference.getName(), component));
            }

            for (Property property : implementation.getProperties()) {
                ObjectFactory<?> propertyValueFactory = propertyFactory.createValueFactory(property);
                if ( propertyValueFactory != null) {
                    scriptEngine.put(property.getName(), propertyValueFactory.getInstance());
                }
            }
            
            scriptEngine.eval(new StringReader(implementation.getScriptSrc()));

        } catch (ScriptException e) {
            throw new ObjectCreationException(e);
        }
    }
    
    public void stop() {
    }

    @SuppressWarnings("unchecked")
    protected Object createReferenceProxy(String name, RuntimeComponent component) {
        for (ComponentReference reference : component.getReferences()) {
            if (reference.getName().equals(name)) {
                Class iface = ((JavaInterface)reference.getInterfaceContract().getInterface()).getJavaClass();
                return component.getService(iface, name);
            }
        }
        throw new IllegalArgumentException("reference " + name + " not found on component: " + component);
    }

    /**
     * Hack for now to work around a problem with the JRuby script engine
     */
    private ScriptEngine getScriptEngineByExtension(String scriptExtn) {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        if ("rb".equals(scriptExtn)) {
            return new TuscanyJRubyScriptEngine();
        } else {
            return scriptEngineManager.getEngineByExtension(scriptExtn);
        }
    }
}
