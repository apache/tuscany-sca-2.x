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
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.osoa.sca.annotations.EagerInit;

/**
 * Handles processing of {@link org.osoa.sca.annotations.EagerInit}
 *
 * @version $Rev$ $Date$
 */
public class EagerInitProcessor extends BaseJavaClassVisitor {
    
    public EagerInitProcessor(AssemblyFactory factory) {
        super(factory);
    }

    public <T> void visitClass(Class<T> clazz,
                               JavaImplementation type) throws IntrospectionException {
        super.visitClass(clazz, type);
        EagerInit annotation = clazz.getAnnotation(EagerInit.class);
        if (annotation == null) {
            Class<?> superClass = clazz.getSuperclass();
            while (!Object.class.equals(superClass)) {
                annotation = superClass.getAnnotation(EagerInit.class);
                if (annotation != null) {
                    break;
                }
                superClass = superClass.getSuperclass();
            }
            if (annotation == null) {
                return;
            }
        }
        type.setEagerInit(true);
    }
}
