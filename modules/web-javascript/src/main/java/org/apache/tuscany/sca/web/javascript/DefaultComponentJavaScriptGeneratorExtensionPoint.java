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

package org.apache.tuscany.sca.web.javascript;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.monitor.impl.ProblemImpl;


/**
 * Default extension point for widget component script generator
 * 
 * @version $Rev$ $Date$
 */
public class DefaultComponentJavaScriptGeneratorExtensionPoint implements ComponentJavaScriptGeneratorExtensionPoint {
    private final List<ComponentJavaScriptGenerator> generators = new ArrayList<ComponentJavaScriptGenerator>();
    private final Map<QName, ComponentJavaScriptGenerator> generatorsByQName = new HashMap<QName, ComponentJavaScriptGenerator>();

    private ExtensionPointRegistry extensionPoints;
    private Monitor monitor = null;

    private boolean loaded = false;

    public DefaultComponentJavaScriptGeneratorExtensionPoint(ExtensionPointRegistry extensionPoints) {
        this.extensionPoints = extensionPoints;
        
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        if (monitorFactory != null) {
                this.monitor = monitorFactory.createMonitor();
        }
    }
    
    /**
     * Report a exception.
     * 
     * @param problems
     * @param message
     * @param model
    */
    private void error(String message, Object model, Exception ex) {
        if (monitor != null) {
            Problem problem = new ProblemImpl(this.getClass().getName(), "web-javascript-validation-messages", Severity.ERROR, model, message, ex);
            monitor.problem(problem);
        }        
    }
    
    public void addComponentJavaScriptGenerator(ComponentJavaScriptGenerator componentScriptGenerator) {
        if (componentScriptGenerator.getQName() != null) {
            generatorsByQName.put(componentScriptGenerator.getQName(), componentScriptGenerator);
        }
        
        generators.add(componentScriptGenerator);
    }

    public void removeComponentJavaScriptGenerator(ComponentJavaScriptGenerator componentScriptGenerator) {
        if (componentScriptGenerator.getQName() != null) {
            generatorsByQName.remove(componentScriptGenerator.getQName());
        }
        
        generators.remove(componentScriptGenerator);
    }
    

    public ComponentJavaScriptGenerator getComponentJavaScriptGenerator(QName bindingName) {
        loadFactories();
        return generatorsByQName.get(bindingName);
    }
    
    public List<ComponentJavaScriptGenerator> getComponentJavaScriptGenerators() {
        loadFactories();
        return this.generators;
    }
    
    /**
     * Private Utility methods
     */
    
    
    /**
     * Lazily load artifact processors registered in the extension point.
     */
    @SuppressWarnings("unchecked")
    private synchronized void loadFactories() {
        if (loaded) {
            return;
        }

        // Get the proxy factories declarations
        Set<ServiceDeclaration> factoryDeclarations = null;
        try {
            factoryDeclarations = ServiceDiscovery.getInstance().getServiceDeclarations(ComponentJavaScriptGenerator.class);
        } catch (IOException e) {
            IllegalStateException ie = new IllegalStateException(e);
            error("IllegalStateException", factoryDeclarations, ie);
            throw ie;
        }

        for (ServiceDeclaration processorDeclaration : factoryDeclarations) {
            // Create a factory, and register it
            ComponentJavaScriptGenerator generator = null;
            try {
                Class generatorClass = processorDeclaration.loadClass();
                
                Constructor<ComponentJavaScriptGenerator> constructor = generatorClass.getConstructor(ExtensionPointRegistry.class);
                generator = constructor.newInstance(extensionPoints);
                
            } catch (Exception e) {
                IllegalStateException ie = new IllegalStateException(e);
                error("IllegalStateException", generator, ie);
                throw ie;
            }

            addComponentJavaScriptGenerator(generator);
        }

        loaded = true;
    }


}
