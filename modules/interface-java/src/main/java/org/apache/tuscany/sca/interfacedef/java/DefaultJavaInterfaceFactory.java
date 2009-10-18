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

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceFactoryImpl;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceVisitor;

/**
 * A factory for the Java interface model.
 *
 * @version $Rev$ $Date$
 */
public class DefaultJavaInterfaceFactory extends JavaInterfaceFactoryImpl implements JavaInterfaceFactory {
    private static final Logger logger = Logger.getLogger(DefaultJavaInterfaceFactory.class.getName());
    
    private ExtensionPointRegistry extensionPointRegistry;
    // private Monitor monitor = null;
    private boolean loadedVisitors; 
    
    public DefaultJavaInterfaceFactory(ExtensionPointRegistry registry) {
        super();
        this.extensionPointRegistry = registry;
    }
    
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
        
        // Get the databinding service declarations
        Collection<ServiceDeclaration> visitorDeclarations; 
        try {
            visitorDeclarations = ServiceDiscovery.getInstance().getServiceDeclarations(JavaInterfaceVisitor.class, true);
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
                throw ie;
            }
            
            logger.fine("Adding Java Interface visitor: " + visitor.getClass().getName());
            
            addInterfaceVisitor(visitor);
        }
        
        loadedVisitors = true;
    }


}
