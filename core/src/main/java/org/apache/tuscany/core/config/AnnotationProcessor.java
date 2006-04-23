/**
 *
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.config;

import org.apache.tuscany.model.assembly.ComponentInfo;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Implementations process annotations on a Java class and contribute to a {@link org.apache.tuscany.model.assembly.ComponentInfo}
 *
 * @version $$Rev$$ $$Date$$
 */
public interface AnnotationProcessor {

    public void visitClass(Class clazz, Annotation annotation, ComponentInfo type);

    public void visitMethod(Method method, Annotation annotation, ComponentInfo type);

    public void visitConstructor(Constructor constructor, Annotation annotation, ComponentInfo type);

    public void visitField(Field field, Annotation annotation, ComponentInfo type);

}
