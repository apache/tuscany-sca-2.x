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
package org.apache.tuscany.implementation.java.introspect.impl;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.impl.JavaScopeImpl;
import org.apache.tuscany.implementation.java.introspect.IntrospectionException;

/**
 * Processes the {@link JavaScopeImpl} annotation and updates the component type with the corresponding implmentation scope
 *
 * @version $Rev$ $Date$
 */
public class ScopeProcessor extends BaseJavaClassVisitor {
    
    public ScopeProcessor(AssemblyFactory factory) {
        super(factory);
    }

    public <T> void visitClass(Class<T> clazz,
                               JavaImplementation type)
        throws IntrospectionException {
        org.osoa.sca.annotations.Scope annotation = clazz.getAnnotation(org.osoa.sca.annotations.Scope.class);
        if (annotation == null) {
            type.setJavaScope(JavaScopeImpl.STATELESS);
            return;
        }
        String name = annotation.value();
        JavaScopeImpl scope;
        if ("COMPOSITE".equals(name)) {
            scope = JavaScopeImpl.COMPOSITE;
        } else if ("SESSION".equals(name)) {
            scope = JavaScopeImpl.SESSION;
        } else if ("CONVERSATION".equals(name)) {
            scope = JavaScopeImpl.CONVERSATION;
        } else if ("REQUEST".equals(name)) {
            scope = JavaScopeImpl.REQUEST;
        } else {
            scope = new JavaScopeImpl(name);
        }
        type.setJavaScope(scope);
    }
}
