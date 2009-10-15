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
package org.apache.tuscany.sca.interfacedef.java;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceFactoryImpl;
import org.apache.tuscany.sca.interfacedef.java.impl.PolicyJavaInterfaceVisitor;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceVisitor;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.policy.PolicyFactory;

/**
 * A factory for the Java interface model.
 *
 * @version $Rev$ $Date$
 */
public class DefaultJavaInterfaceFactory extends JavaInterfaceFactoryImpl implements JavaInterfaceFactory {
    private static final Logger logger = Logger.getLogger(DefaultJavaInterfaceFactory.class.getName());
    
    private ExtensionPointRegistry extensionPointRegistry;
    private FactoryExtensionPoint modelFactoryExtensionPoint;
    private Monitor monitor = null;
    private boolean loadedVisitors; 
    
    public DefaultJavaInterfaceFactory() {
        super();
        this.extensionPointRegistry = new DefaultExtensionPointRegistry();
        
        UtilityExtensionPoint utilities = this.extensionPointRegistry.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        if (monitorFactory != null) {
            this.monitor = monitorFactory.createMonitor();
        }

    }
    
    /*
    public DefaultJavaInterfaceFactory(FactoryExtensionPoint modelFactoryExtensionPoint) {
        this.extensionPointRegistry = new DefaultExtensionPointRegistry();
        
        this.extensionPointRegistry = new DefaultExtensionPointRegistry();
        this.modelFactoryExtensionPoint = modelFactoryExtensionPoint;
        
        UtilityExtensionPoint utilities = this.extensionPointRegistry.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        if (monitorFactory != null) {
            this.monitor = monitorFactory.createMonitor();
        }

    }
    */
    
    @Override
    public List<JavaInterfaceVisitor> getInterfaceVisitors() {
        loadVisitors();
        return super.getInterfaceVisitors();
    }
    
    /**
     * Load visitors declared under META-INF/services
     */
    @SuppressWarnings("unchecked")
    private synchronized void loadVisitors() {
        if (loadedVisitors)
            return;
        
        if (modelFactoryExtensionPoint != null) {
            PolicyFactory policyFactory = modelFactoryExtensionPoint.getFactory(PolicyFactory.class);
            if (policyFactory != null) {
                addInterfaceVisitor(new PolicyJavaInterfaceVisitor(policyFactory));
            }
        }
        
        // Get the databinding service declarations
        Collection<ServiceDeclaration> visitorDeclarations; 
        try {
            visitorDeclarations = ServiceDiscovery.getInstance().getServiceDeclarations(JavaInterfaceVisitor.class.getName());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        
        // Load data bindings
        for (ServiceDeclaration visitorDeclaration: visitorDeclarations) {
            JavaInterfaceVisitor visitor = null;
            try {
                Class<JavaInterfaceVisitor> visitorClass = (Class<JavaInterfaceVisitor>)visitorDeclaration.loadClass();
                
                try {
                    Constructor<JavaInterfaceVisitor> constructor = visitorClass.getConstructor(ExtensionPointRegistry.class);
                    visitor = constructor.newInstance(extensionPointRegistry);
                } catch (NoSuchMethodException e) {
                    visitor = visitorClass.newInstance();
                }
                
                
            } catch (Exception e) {
                IllegalStateException ie = new IllegalStateException(e);
                error("IllegalStateException", visitor, ie);
                throw ie;
            }
            
            logger.fine("Adding Java Interface visitor: " + visitor.getClass().getName());
            
            addInterfaceVisitor(visitor);
        }
        
        loadedVisitors = true;
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
            Problem problem =
                monitor.createProblem(this.getClass().getName(),
                                      "interface-javaxml-validation-messages.properties",
                                      Severity.ERROR,
                                      model,
                                      message,
                                      ex);
            monitor.problem(problem);
        }
    }
}
