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

import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.Property;
import org.apache.tuscany.assembly.Reference;
import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.implementation.spi.AbstractImplementation;
import org.apache.tuscany.implementation.spi.PropertyValueObjectFactory;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.invocation.Interceptor;
import org.apache.tuscany.sca.implementation.script.engines.TuscanyJRubyScriptEngine;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;

/**
 * Represents a Script implementation.
 */
public class ScriptImplementation extends AbstractImplementation {

    protected String scriptName;
    protected String scriptSrc;
    protected String scriptLanguage;

    protected PropertyValueObjectFactory propertyFactory;

    protected ScriptEngine scriptEngine;

    public ScriptImplementation(String scriptName, String scriptLanguage, String scriptSrc, PropertyValueObjectFactory propertyFactory) {
        this.scriptName = scriptName;
        this.scriptLanguage = scriptLanguage;
        this.scriptSrc = scriptSrc;
        this.propertyFactory = propertyFactory;
    }

    public String getScriptName() {
        return scriptName;
    }

    public String getScriptLanguage() {
        return scriptLanguage;
    }

    public String getScriptSrc() {
        return scriptSrc;
    }

    public void setScriptSrc(String scriptSrc) {
        this.scriptSrc = scriptSrc;
    }

    public Interceptor createInterceptor(RuntimeComponent component, ComponentService service, Operation operation) {
        return new ScriptInvoker(this, operation.getName());
    }

    public Interceptor createCallbackInterceptor(RuntimeComponent component, Operation operation) {
        return new ScriptInvoker(this, operation.getName());
    }
    
    public void start(RuntimeComponent component) {
        try {
            scriptEngine = getScriptEngineByExtension(getScriptLanguage());
            if (scriptEngine == null) {
                throw new ObjectCreationException("no script engine found for language: " + getScriptLanguage());
            }
            if (!(scriptEngine instanceof Invocable)) {
                throw new ObjectCreationException("script engine does not support Invocable: " + scriptEngine);
            }
            
            for (Reference reference : getReferences()) {
                Object referenceProxy = createReferenceProxy(reference.getName(), component);
                scriptEngine.put(reference.getName(), referenceProxy);
            }

            for (Property property : getProperties()) {
                ObjectFactory<?> propertyValueFactory = propertyFactory.createValueFactory(property);
                if ( propertyValueFactory != null) {
                    scriptEngine.put(property.getName(), propertyValueFactory.getInstance());
                }
            }
            
            scriptEngine.eval(new StringReader(getScriptSrc()));

        } catch (ScriptException e) {
            throw new ObjectCreationException(e);
        }
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
