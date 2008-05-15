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
import java.util.List;
import java.util.Set;

import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceFactoryImpl;
import org.apache.tuscany.sca.interfacedef.java.impl.PolicyJavaInterfaceVisitor;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceVisitor;
import org.apache.tuscany.sca.policy.PolicyFactory;

/**
 * A factory for the Java interface model.
 *
 * @version $Rev$ $Date$
 */
public class DefaultJavaInterfaceFactory extends JavaInterfaceFactoryImpl implements JavaInterfaceFactory {
    private ModelFactoryExtensionPoint modelFactoryExtensionPoint;
    private boolean loadedVisitors; 
    
    public DefaultJavaInterfaceFactory() {
    }
    
    public DefaultJavaInterfaceFactory(ModelFactoryExtensionPoint modelFactoryExtensionPoint) {
        this.modelFactoryExtensionPoint = modelFactoryExtensionPoint;
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
    private void loadVisitors() {
        if (loadedVisitors)
            return;
        
        if (modelFactoryExtensionPoint != null) {
            PolicyFactory policyFactory = modelFactoryExtensionPoint.getFactory(PolicyFactory.class);
            if (policyFactory != null) {
                addInterfaceVisitor(new PolicyJavaInterfaceVisitor(policyFactory));
            }
        }
        
        // Get the databinding service declarations
        Set<ServiceDeclaration> visitorDeclarations; 
        try {
            visitorDeclarations = ServiceDiscovery.getInstance().getServiceDeclarations(JavaInterfaceVisitor.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        
        // Load data bindings
        for (ServiceDeclaration visitorDeclaration: visitorDeclarations) {
            JavaInterfaceVisitor visitor;
            try {
                Class<JavaInterfaceVisitor> visitorClass = (Class<JavaInterfaceVisitor>)visitorDeclaration.loadClass();
                visitor = visitorClass.newInstance();
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            } catch (InstantiationException e) {
                throw new IllegalArgumentException(e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            }
            addInterfaceVisitor(visitor);
        }
        
        loadedVisitors = true;
    }

    

}
