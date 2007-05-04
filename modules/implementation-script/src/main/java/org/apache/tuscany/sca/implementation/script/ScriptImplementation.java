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
import org.apache.tuscany.assembly.ComponentType;
import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.implementation.spi.AbstractImplementation;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.sca.implementation.script.engines.TuscanyJRubyScriptEngine;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.wire.Interceptor;

/**
 * Represents a Script implementation.
 */
public class ScriptImplementation extends AbstractImplementation {

    private String scriptName;
    private String scriptSrc;
    private String scriptLanguage;
    private ComponentType componentType;
    
    protected ScriptEngine scriptEngine;

    protected ScriptImplementation(String scriptName, String scriptLanguage) {
        this.scriptName = scriptName;
        this.scriptLanguage = scriptLanguage;
        setURI(scriptName);
        setUnresolved(true);
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

    public ComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public Interceptor createInterceptor(RuntimeComponent component, ComponentService service, Operation operation, boolean isCallback) {
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
            
//            ObjectFactory<?> propertyValueFactory = null;
//            for (Reference reference : getReferences()) {
//                engine.getContext().setAttribute(reference.getName(), 
//                                                 reference., 
//                                                 ScriptContext.ENGINE_SCOPE);
//            }
//            
//            for (String referenceName : references.keySet()) {
//                Object reference = references.get(referenceName);
//                //manager.put(referenceName, reference);
//                engine.getContext().setAttribute(referenceName, 
//                                                 reference, 
//                                                 ScriptContext.ENGINE_SCOPE);
//            }
//            
//            for (String propertyName : propertyValueFactories.keySet()) {
//                propertyValueFactory = propertyValueFactories.get(propertyName);
//                if ( propertyValueFactory != null) {
//                    //manager.put(propertyName, propertyValueFactory.getInstance());
//                    engine.getContext().setAttribute(propertyName, 
//                                                     propertyValueFactory.getInstance(), 
//                                                     ScriptContext.ENGINE_SCOPE);
//                }
//            }
            
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
