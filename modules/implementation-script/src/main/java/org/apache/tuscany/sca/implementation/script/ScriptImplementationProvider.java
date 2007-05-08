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
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.Property;
import org.apache.tuscany.assembly.Reference;
import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.core.RuntimeComponentReference;
import org.apache.tuscany.core.RuntimeComponentService;
import org.apache.tuscany.core.RuntimeWire;
import org.apache.tuscany.core.invocation.JDKProxyService;
import org.apache.tuscany.interfacedef.Interface;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.interfacedef.java.JavaInterface;
import org.apache.tuscany.invocation.Invoker;
import org.apache.tuscany.provider.ImplementationProvider;
import org.apache.tuscany.sca.implementation.script.engines.TuscanyJRubyScriptEngine;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.WorkContextTunnel;

/**
 * Represents a Script implementation.
 */
public class ScriptImplementationProvider implements ImplementationProvider {

    protected ScriptImplementation implementation;
    protected ScriptEngine scriptEngine;

    public ScriptImplementationProvider(ScriptImplementation implementation) {
        this.implementation = implementation;
    }

    public Invoker createInvoker(RuntimeComponent component, RuntimeComponentService service, Operation operation) {
        return new ScriptInvoker(this, operation.getName());
    }

    public Invoker createCallbackInvoker(RuntimeComponent component, Operation operation) {
        return new ScriptInvoker(this, operation.getName());
    }
    
    public void start(RuntimeComponent component) {
        try {
            scriptEngine = getScriptEngineByExtension(implementation.getScriptLanguage());
            if (scriptEngine == null) {
                throw new ObjectCreationException("no script engine found for language: " + implementation.getScriptLanguage());
            }
            if (!(scriptEngine instanceof Invocable)) {
                throw new ObjectCreationException("script engine does not support Invocable: " + scriptEngine);
            }
            
            for (Reference reference : implementation.getReferences()) {
                Object referenceProxy = createReferenceProxy(reference.getName(), component);
                scriptEngine.put(reference.getName(), referenceProxy);
            }

            for (Property property : implementation.getProperties()) {
                ObjectFactory<?> propertyValueFactory = implementation.propertyFactory.createValueFactory(property);
                if ( propertyValueFactory != null) {
                    scriptEngine.put(property.getName(), propertyValueFactory.getInstance());
                }
            }
            
            scriptEngine.eval(new StringReader(implementation.getScriptSrc()));

        } catch (ScriptException e) {
            throw new ObjectCreationException(e);
        }
    }
    
    public void stop(RuntimeComponent component) {
    }
    
    public void configure(RuntimeComponent component) {
    }
    
    public InterfaceContract getImplementationInterfaceContract(RuntimeComponentService service) {
        return null;
    }

    /**
     * TODO: yuk yuk yuk
     * Maybe RuntimeComponentReference could have a createProxy method?
     */
    private Object createReferenceProxy(String name, RuntimeComponent component) {
        for (ComponentReference reference : component.getReferences()) {
            if (reference.getName().equals(name)) {
                List<RuntimeWire> wireList = ((RuntimeComponentReference)reference).getRuntimeWires();
                RuntimeWire wire = wireList.get(0);
                JDKProxyService ps = new JDKProxyService(WorkContextTunnel.getThreadWorkContext(), null);
                Interface iface = reference.getInterfaceContract().getInterface();
                return ps.createProxy(((JavaInterface)iface).getJavaClass(), wire);
            }
        }
        throw new IllegalStateException("reference " + name + " not found on component: " + component);
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
