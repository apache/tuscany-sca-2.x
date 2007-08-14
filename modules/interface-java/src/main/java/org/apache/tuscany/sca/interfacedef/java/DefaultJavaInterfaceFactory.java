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

import org.apache.tuscany.sca.interfacedef.impl.TempServiceDeclarationUtil;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceFactoryImpl;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceVisitor;

/**
 * A factory for the Java interface model.
 */
public class DefaultJavaInterfaceFactory extends JavaInterfaceFactoryImpl implements JavaInterfaceFactory {
    
    private boolean loadedVisitors; 
    
    public DefaultJavaInterfaceFactory() {
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

        // Get the databinding service declarations
        ClassLoader classLoader = JavaInterfaceVisitor.class.getClassLoader();
        Set<String> visitorDeclarations; 
        try {
            visitorDeclarations = TempServiceDeclarationUtil.getServiceClassNames(classLoader, JavaInterfaceVisitor.class.getName());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        
        // Load data bindings
        for (String visitorDeclaration: visitorDeclarations) {
            JavaInterfaceVisitor visitor;
            try {
                Class<JavaInterfaceVisitor> visitorClass = (Class<JavaInterfaceVisitor>)Class.forName(visitorDeclaration, true, classLoader);
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
