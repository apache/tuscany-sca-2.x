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

package org.apache.tuscany.sca.implementation.script.provider;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.axiom.om.OMElement;
import org.apache.bsf.xml.XMLHelper;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.core.factory.ObjectCreationException;
import org.apache.tuscany.sca.core.factory.ObjectFactory;
import org.apache.tuscany.sca.implementation.script.ScriptImplementation;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * An ImplementationProvider for Script implementations.
 *
 * @version $Rev: $ $Date: $
 */
public class ScriptImplementationProvider implements ImplementationProvider {
    
    private RuntimeComponent component;
    private ScriptImplementation implementation;
    private ScriptPropertyFactory propertyFactory;
    private ScriptEngine scriptEngine;
    private XMLHelper xmlHelper;
    
    public ScriptImplementationProvider(RuntimeComponent component, ScriptImplementation implementation, ScriptPropertyFactory propertyFactory) {
        this.component = component;
        this.implementation = implementation;
        this.propertyFactory = propertyFactory;

        // Set the databinding and XMLHelper for WSDL interfaces
        for (Service service : component.getServices()) {
            InterfaceContract ic = service.getInterfaceContract();
            if (ic instanceof WSDLInterfaceContract) {
                ic.getInterface().resetDataBinding(OMElement.class.getName());
                xmlHelper = XMLHelper.getArgHelper(scriptEngine);
            }
        }
    }

    public void start() {
        try {
            String language = implementation.getLanguage();
            if (language == null) {
                language = implementation.getScript();
                language = language.substring(language.lastIndexOf('.') +1);
            }
            scriptEngine = scriptEngine(language);
            if (scriptEngine == null) {
                throw new ObjectCreationException("no script engine found for language: " + implementation.getLanguage());
            }
            if (!(scriptEngine instanceof Invocable)) {
                throw new ObjectCreationException("script engine does not support Invocable: " + scriptEngine);
            }
            
            for (Reference reference : implementation.getReferences()) {
                scriptEngine.put(reference.getName(), getProxy(reference.getName()));
            }

            for (Property property : implementation.getProperties()) {
                ObjectFactory<?> valueFactory = propertyFactory.createValueFactory(property);
                if (valueFactory != null) {
                    scriptEngine.put(property.getName(), valueFactory.getInstance());
                }
            }

            URL url = new URL(implementation.getLocation());
            InputStreamReader reader = new InputStreamReader(url.openStream());
            scriptEngine.eval(reader);
            reader.close();

        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } catch (ScriptException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void stop() {
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

    public Invoker createInvoker(RuntimeComponentService service, Operation operation) {
        return new ScriptInvoker(scriptEngine, xmlHelper, operation);
    }

    private Object getProxy(String name) {
        for (ComponentReference reference : component.getReferences()) {
            if (reference.getName().equals(name)) {
                Class<?> iface = ((JavaInterface)reference.getInterfaceContract().getInterface()).getJavaClass();
                return component.getComponentContext().getService(iface, name);
            }
        }
        throw new IllegalArgumentException("reference " + name + " not found on component: " + component);
    }

    /**
     * Returns the script engine for the given language.
     * 
     * @param language
     * @return
     */
    private ScriptEngine scriptEngine(String language) {
        if ("rb".equals(language)) {

            // Hack for now to work around a problem with the JRuby script engine
            // return new TuscanyJRubyScriptEngine();
        } else {
            if ("py".equals(language)) {
                pythonCachedir();
            }
        }
        // Allow privileged access to run access classes. Requires RuntimePermission
        // for accessClassInPackage.sun.misc.
        ScriptEngineManager scriptEngineManager =
            AccessController.doPrivileged(new PrivilegedAction<ScriptEngineManager>() {
                public ScriptEngineManager run() {
                    return new ScriptEngineManager();
                }
            });
        return scriptEngineManager.getEngineByExtension(language);
    }

    /**
     * If the Python home isn't set then let Tuscany suppress messages other than errors
     * See TUSCANY-1950
     */
    private static void pythonCachedir() {
        if (System.getProperty("python.home") == null) {
          System.setProperty("python.verbose", "error");
        }
    }
}
