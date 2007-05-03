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

package org.apache.tuscany.implementation.script;

import java.io.StringReader;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.tuscany.assembly.ComponentProperty;
import org.apache.tuscany.core.component.ComponentContextImpl;
import org.apache.tuscany.core.component.ComponentContextProvider;
import org.apache.tuscany.core.component.scope.InstanceWrapperBase;
import org.apache.tuscany.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.implementation.script.engines.TuscanyJRubyScriptEngine;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.extension.AtomicComponentExtension;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;

public class ScriptComponent extends AtomicComponentExtension implements ComponentContextProvider {

    private ScriptImplementation impl;
    private ComponentContext componentContext;
    private Map<String, Object> references;
    private Map<String, ObjectFactory<?>> propertyValueFactories;
    
    private ScriptPropertyValueObjectFactory propertyValueObjectFactory = null;
    private DataBindingExtensionPoint dataBindingRegistry;
    private static ScriptEngineManager scriptEngineManager = new ScriptEngineManager();

    public ScriptComponent(URI uri, URI groupId, ScriptImplementation impl) {
        super(uri, null, null, groupId, 50);
        this.impl = impl;
        componentContext = new ComponentContextImpl(this);
        references = new HashMap<String, Object>();
        propertyValueFactories = new HashMap<String, ObjectFactory<?>>();
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation, boolean callback)
        throws TargetInvokerCreationException {
        return new ScriptInvoker(operation.getName(), this, scopeContainer, workContext);
    }

    // --

    @Override
    public ComponentContext getComponentContext() {
        return componentContext;
    }

    @SuppressWarnings("unchecked")
    public InstanceWrapper createInstanceWrapper() throws ObjectCreationException {
        return new InstanceWrapperBase(createInstance());
    }

    public Object createInstance() throws ObjectCreationException {
        try {
            ObjectFactory<?> propertyValueFactory = null;
            ScriptEngine engine = getScriptEngineByExtension(impl.getScriptLanguage());
            if (engine == null) {
                throw new ObjectCreationException("no script engine found for language: " + impl.getScriptLanguage());
            }
            
            for (String referenceName : references.keySet()) {
                Object reference = references.get(referenceName);
                //manager.put(referenceName, reference);
                engine.getContext().setAttribute(referenceName, 
                                                 reference, 
                                                 ScriptContext.ENGINE_SCOPE);
            }
            
            for (String propertyName : propertyValueFactories.keySet()) {
                propertyValueFactory = propertyValueFactories.get(propertyName);
                if ( propertyValueFactory != null) {
                    //manager.put(propertyName, propertyValueFactory.getInstance());
                    engine.getContext().setAttribute(propertyName, 
                                                     propertyValueFactory.getInstance(), 
                                                     ScriptContext.ENGINE_SCOPE);
                }
            }
            
            engine.eval(new StringReader(impl.getScriptSrc()));
           
            return engine;
           
        } catch (ScriptException e) {
            throw new ObjectCreationException(e);
        }
    }

    public Object getTargetInstance() throws TargetResolutionException {
        throw new UnsupportedOperationException();
    }

    public void attachCallbackWire(Wire arg0) {
    }

    public void attachWire(Wire wire) {
        references.put(wire.getSourceUri().getFragment(), createWireProxy(wire));
    }

    protected Object createWireProxy(Wire wire) {
        // TODO: this is completly wrong :) Need to create a proxy wraping the wire
        Object ref;
        try {
            ref = wire.getTargetInstance();
        } catch (TargetResolutionException e) {
            throw new RuntimeException(e);
        }
        return ref;
    }

    public void attachWires(List<Wire> arg0) {
    }

    public List<Wire> getWires(String arg0) {
        return null;
    }

    public <B, R extends CallableReference<B>> R cast(B arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public <B> B getProperty(Class<B> arg0, String arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    public <B> B getService(Class<B> arg0, String arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    public <B> ServiceReference<B> getServiceReference(Class<B> arg0, String arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    public void setDataBindingRegistry(DataBindingExtensionPoint dataBindingRegistry) {
        this.dataBindingRegistry = dataBindingRegistry;
    }

    public void setPropertyValueObjectFactory(ScriptPropertyValueObjectFactory propertyValueObjectFactory) {
        this.propertyValueObjectFactory = propertyValueObjectFactory;
    }
    
    public void initializePropertyValueFactories(List<ComponentProperty> properties) {
        ObjectFactory<?> propertyObjectFactory = null;
        
        for (ComponentProperty aProperty : properties) {
            if (aProperty.getValue() != null) {
                propertyObjectFactory = propertyValueObjectFactory.createValueFactory(aProperty);
                propertyValueFactories.put(aProperty.getName(), propertyObjectFactory);
            }
        }
    }
    
    private ScriptEngine getScriptEngineByExtension(String scriptExtn) {
        if ("rb".equals(scriptExtn)) {
            return new TuscanyJRubyScriptEngine();
        } else {
            return scriptEngineManager.getEngineByExtension(scriptExtn);
        }
    }
}
