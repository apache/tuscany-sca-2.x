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
package org.apache.tuscany.sca.implementation.java.introspect.impl;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaScopeImpl;
import org.apache.tuscany.sca.implementation.java.introspect.BaseJavaClassVisitor;

/**
 * Processes the {@link JavaScopeImpl} annotation and updates the component type with the corresponding implmentation scope
 *
 * @version $Rev$ $Date$
 */
public class ScopeProcessor extends BaseJavaClassVisitor {
    
    public ScopeProcessor(AssemblyFactory factory) {
        super(factory);
    }
    
    public ScopeProcessor(ExtensionPointRegistry registry) {
        super(registry);
    }    

    @Override
    public <T> void visitClass(Class<T> clazz,
                               JavaImplementation type)
        throws IntrospectionException {
        org.oasisopen.sca.annotation.Scope annotation = clazz.getAnnotation(org.oasisopen.sca.annotation.Scope.class);
        if (annotation == null) {
            type.setJavaScope(JavaScopeImpl.STATELESS);
            return;
        }
        
        String name = annotation.value();
        JavaScopeImpl scope;
        if ("COMPOSITE".equals(name)) {
            scope = JavaScopeImpl.COMPOSITE;
        } else if ("STATELESS".equals(name)) {
            scope = JavaScopeImpl.STATELESS;
        } else {
            scope = JavaScopeImpl.INVALID;
        }
        type.setJavaScope(scope);
        
        if (type.getJavaScope().equals(JavaScopeImpl.INVALID)) {
        	throw new IntrospectionException("Invalid scope :" + name + " for " + type.getName());
        }
    }
}
