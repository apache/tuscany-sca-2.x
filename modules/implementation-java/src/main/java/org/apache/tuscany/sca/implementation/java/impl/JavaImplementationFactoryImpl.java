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

/**
 * A factory for the Java model.
 */
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.introspect.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.introspect.JavaClassIntrospectorExtensionPoint;

public abstract class JavaImplementationFactoryImpl implements JavaImplementationFactory {
    
    private JavaClassIntrospectorImpl introspector;
    
    public JavaImplementationFactoryImpl(JavaClassIntrospectorExtensionPoint visitors) {
        introspector = new JavaClassIntrospectorImpl(visitors);
    }

    public JavaImplementation createJavaImplementation() {
        JavaImplementation javaImplementation = new JavaImplementationImpl();
        return javaImplementation;
    }
    
    public JavaImplementation createJavaImplementation(Class<?> implementationClass) throws IntrospectionException {
        JavaImplementation javaImplementation = createJavaImplementation();
        introspector.introspectClass(javaImplementation, implementationClass);
        return javaImplementation;
    }
    
    public void createJavaImplementation(JavaImplementation javaImplementation, Class<?> implementationClass) throws IntrospectionException {
        introspector.introspectClass(javaImplementation, implementationClass);
    }

}
