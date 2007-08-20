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

import org.apache.axiom.om.OMElement;
import org.apache.bsf.xml.XMLHelper;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.core.factory.ObjectCreationException;
import org.apache.tuscany.sca.core.factory.ObjectFactory;
import org.apache.tuscany.sca.implementation.script.engines.TuscanyJRubyScriptEngine;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.spi.InvokerFactory;
import org.apache.tuscany.sca.spi.utils.PropertyValueObjectFactory;

public class ScriptInvokerFactory implements InvokerFactory {

    protected ScriptEngine scriptEngine;
    protected XMLHelper xmlHelper;
    
    protected RuntimeComponent rc; 
    protected ComponentType ct; 
    protected ScriptImplementation implementation;
    protected PropertyValueObjectFactory propertyFactory;

    
    /**
     * @param rc
     * @param ct
     * @param implementation
     * @param propertyFactory
     */
    public ScriptInvokerFactory(RuntimeComponent rc,
                                ComponentType ct,
                                ScriptImplementation implementation,
                                PropertyValueObjectFactory propertyFactory) {
        super();
        this.rc = rc;
        this.ct = ct;
        this.implementation = implementation;
        this.propertyFactory = propertyFactory;
    }

    public Invoker createInvoker(Operation operation) {
        init(rc, ct, implementation, propertyFactory);
        return new ScriptInvoker(scriptEngine, xmlHelper, operation);
    }
    
    protected synchronized void init(RuntimeComponent rc, ComponentType ct, ScriptImplementation implementation, PropertyValueObjectFactory propertyFactory) {
        if(scriptEngine!=null) {
            return;
        }
        try {
            scriptEngine = getScriptEngineByExtension(implementation.getScriptLanguage());
            if (scriptEngine == null) {
                throw new ObjectCreationException("no script engine found for language: " + implementation.getScriptLanguage());
            }
            if (!(scriptEngine instanceof Invocable)) {
                throw new ObjectCreationException("script engine does not support Invocable: " + scriptEngine);
            }
            
            for (Reference reference : ct.getReferences()) {
                scriptEngine.put(reference.getName(), createReferenceProxy(reference.getName(), rc));
            }

            for (Property property : ct.getProperties()) {
                ObjectFactory<?> propertyValueFactory = propertyFactory.createValueFactory(property);
                if ( propertyValueFactory != null) {
                    scriptEngine.put(property.getName(), propertyValueFactory.getInstance());
                }
            }
            
            scriptEngine.eval(new StringReader(implementation.getScriptSrc()));

        } catch (ScriptException e) {
            throw new ObjectCreationException(e);
        }

        // set the databinding and xmlhelper for wsdl interfaces
        for (Service service : rc.getServices()) {
            InterfaceContract ic = service.getInterfaceContract();
            if (ic instanceof WSDLInterfaceContract) {
                // Set to use the Axiom data binding
                ic.getInterface().setDefaultDataBinding(OMElement.class.getName());
                xmlHelper = XMLHelper.getArgHelper(scriptEngine);
            }
        }
    }

    /**
     * TODO: RuntimeComponent should provide a method like this
     */
    @SuppressWarnings("unchecked")
    protected Object createReferenceProxy(String name, RuntimeComponent component) {
        for (ComponentReference reference : component.getReferences()) {
            if (reference.getName().equals(name)) {
                Class iface = ((JavaInterface)reference.getInterfaceContract().getInterface()).getJavaClass();
                return component.getComponentContext().getService(iface, name);
            }
        }
        throw new IllegalArgumentException("reference " + name + " not found on component: " + component);
    }

    /**
     * Hack for now to work around a problem with the JRuby script engine
     */
    protected ScriptEngine getScriptEngineByExtension(String scriptExtn) {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        if ("rb".equals(scriptExtn)) {
            return new TuscanyJRubyScriptEngine();
        } else {
            return scriptEngineManager.getEngineByExtension(scriptExtn);
        }
    }
}
