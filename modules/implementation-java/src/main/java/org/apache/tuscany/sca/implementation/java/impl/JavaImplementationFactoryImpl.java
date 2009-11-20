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
package org.apache.tuscany.sca.implementation.java.impl;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.introspect.JavaClassVisitor;


/**
 * A factory for the Java model.
 *
 * @version $Rev$ $Date$
 */
public abstract class JavaImplementationFactoryImpl implements JavaImplementationFactory {
    
    private List<JavaClassVisitor> visitors = new ArrayList<JavaClassVisitor>();
    private JavaClassIntrospectorImpl introspector;
    private boolean loaded;
    protected ExtensionPointRegistry registry;
    
    public JavaImplementationFactoryImpl(ExtensionPointRegistry registry) {
        this.registry = registry;
    }

    public JavaImplementation createJavaImplementation() {
        JavaImplementation javaImplementation = new JavaImplementationImpl();
        return javaImplementation;
    }
    
    public JavaImplementation createJavaImplementation(Class<?> implementationClass) throws IntrospectionException {
        JavaImplementation javaImplementation = createJavaImplementation();
        getIntrospector().introspectClass(javaImplementation, implementationClass);
        return javaImplementation;
    }
    
    public void createJavaImplementation(JavaImplementation javaImplementation, Class<?> implementationClass) throws IntrospectionException {
        getIntrospector().introspectClass(javaImplementation, implementationClass);
    }

    public void addClassVisitor(JavaClassVisitor visitor) {
        for (JavaClassVisitor tmpVisitor : visitors){
            if (tmpVisitor.getClass() == visitor.getClass()){
                // trying to add a duplicate visitor so
                // ignore it 
                return;
            }
        }
        visitors.add(visitor);
    }

    public void removeClassVisitor(JavaClassVisitor visitor) {
        visitors.remove(visitor);
    }
    
    public List<JavaClassVisitor> getClassVisitors() {
        loadVisitors();
        return visitors;
    }
    
    /**
     * Load visitors declared under META-INF/services
     */
    @SuppressWarnings("unchecked")
    private synchronized void loadVisitors() {
        if (loaded)
            return;
        
        // Get the databinding service declarations
        Collection<ServiceDeclaration> visitorDeclarations; 
        try {
            visitorDeclarations = registry.getServiceDiscovery().getServiceDeclarations(JavaClassVisitor.class, true);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        
        // Load data bindings
        for (ServiceDeclaration visitorDeclaration: visitorDeclarations) {
            JavaClassVisitor visitor = null;
            try {
                Class<JavaClassVisitor> visitorClass = (Class<JavaClassVisitor>)visitorDeclaration.loadClass();
                
                try {
                    Constructor<JavaClassVisitor> constructor = visitorClass.getConstructor(ExtensionPointRegistry.class);
                    visitor = constructor.newInstance(registry);
                } catch (NoSuchMethodException e) {
                    visitor = visitorClass.newInstance();
                }
                
                
            } catch (Exception e) {
                IllegalStateException ie = new IllegalStateException(e);
                throw ie;
            }
            
            addClassVisitor(visitor);
        }
        
        loaded = true;
    }

    private synchronized JavaClassIntrospectorImpl getIntrospector() {
        if (introspector != null) {
            return introspector;
        }
        introspector = new JavaClassIntrospectorImpl(getClassVisitors());
        return introspector;
    }

}
