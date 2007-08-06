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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.introspect.JavaClassIntrospectorExtensionPoint;
import org.apache.tuscany.sca.implementation.java.introspect.JavaClassVisitor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.JavaIntrospectionHelper;

/**
 * An extensible Java class introspector implementation.
 * 
 * @version $Rev$ $Date$
 */
public class JavaClassIntrospectorImpl {
    
    private List<JavaClassVisitor> visitors;

    public JavaClassIntrospectorImpl(JavaClassIntrospectorExtensionPoint visitors) {
        this.visitors = visitors.getClassVisitors();
    }

    /**
     * JSR-250 PFD recommends the following guidelines for how annotations
     * interact with inheritance in order to keep the resulting complexity in
     * control:
     * <ol>
     * <li>Class-level annotations only affect the class they annotate and
     * their members, that is, its methods and fields. They never affect a
     * member declared by a superclass, even if it is not hidden or overridden
     * by the class in question.
     * <li>In addition to affecting the annotated class, class-level
     * annotations may act as a shorthand for member-level annotations. If a
     * member carries a specific member-level annotation, any annotations of the
     * same type implied by a class-level annotation are ignored. In other
     * words, explicit member-level annotations have priority over member-level
     * annotations implied by a class-level annotation.
     * <li>The interfaces implemented by a class never contribute annotations
     * to the class itself or any of its members.
     * <li>Members inherited from a superclass and which are not hidden or
     * overridden maintain the annotations they had in the class that declared
     * them, including member-level annotations implied by class-level ones.
     * <li>Member-level annotations on a hidden or overridden member are always
     * ignored.
     * </ol>
     */
    public void introspectClass(JavaImplementation type, Class<?> clazz)
        throws IntrospectionException {
        for (JavaClassVisitor extension : visitors) {
            extension.visitClass(clazz, type);
        }

        for (Constructor<?> constructor : clazz.getConstructors()) {
            for (JavaClassVisitor extension : visitors) {
                extension.visitConstructor(constructor, type);
                // Assuming the visitClass or visitConstructor will populate the
                // type.getConstructors
                JavaConstructorImpl<?> definition = type.getConstructors().get(constructor);
                if (definition != null) {
                    for (JavaParameterImpl p : definition.getParameters()) {
                        extension.visitConstructorParameter(p, type);
                    }
                }
            }
        }

        Set<Method> methods = JavaIntrospectionHelper.getAllUniquePublicProtectedMethods(clazz);
        for (Method method : methods) {
            for (JavaClassVisitor processor : visitors) {
                processor.visitMethod(method, type);
            }
        }

        Set<Field> fields = JavaIntrospectionHelper.getAllPublicAndProtectedFields(clazz);
        for (Field field : fields) {
            for (JavaClassVisitor extension : visitors) {
                extension.visitField(field, type);
            }
        }

        Class superClass = clazz.getSuperclass();
        if (superClass != null) {
            visitSuperClass(superClass, type);
        }

        for (JavaClassVisitor extension : visitors) {
            extension.visitEnd(clazz, type);
        }
    }

    private void visitSuperClass(Class<?> clazz, JavaImplementation type) throws IntrospectionException {
        if (!Object.class.equals(clazz)) {
            for (JavaClassVisitor extension : visitors) {
                extension.visitSuperClass(clazz, type);
            }
            clazz = clazz.getSuperclass();
            if (clazz != null) {
                visitSuperClass(clazz, type);
            }
        }
    }

}
