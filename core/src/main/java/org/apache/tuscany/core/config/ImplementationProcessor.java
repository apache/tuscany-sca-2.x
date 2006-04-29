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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.tuscany.model.assembly.ComponentInfo;

/**
 * Implementations process a Java class and contribute to a {@link org.apache.tuscany.model.assembly.ComponentInfo}
 * or provide some validation function. Implementations may contribute to defined <code>ComponentInfo</code>
 * metadata, a general <code>ComponentType</code> extensibility element, or a more specific Java extensibility
 * element, which is associated with {@link org.apache.tuscany.core.assembly.JavaExtensibilityElement} and
 * stored in the <code>ComponentInfo</code>'s extensibility collection. Processors will typically use {@link
 * JavaExtensibilityHelper#getExtensibilityElement(org.apache.tuscany.model.assembly.Extensible)}, which
 * provides methods for retrieving the Java extensibility element.
 * <p/>
 * In the runtime, a {@link ComponentTypeIntrospector} system service introspects component implementation
 * types when an assembly is loaded, calling out to registered processors in the order defined by {@link
 * ComponentTypeIntrospector#introspect(Class<?>)}. Generally, processors are also system services which
 * register themeselves with a <code>ComponentTypeIntrospector</code>. For convenience, a processor
 * implementation can extend <@link org.apache.tuscany.core.config.processor.ImplementationProcessorSupport},
 * which provides mechanisms for doing this.
 * <p/>
 * There are a series of bootsrap, or primordial, processors configured in the runtime, and they serve as
 * examples of how an <code>ImplementationProcessor</code> can be implemented.
 *
 * @see org.apache.tuscany.core.config.processor.PropertyProcessor
 * @see org.apache.tuscany.core.config.processor.ReferenceProcessor
 * @see org.apache.tuscany.core.config.processor.InitProcessor
 * @see org.apache.tuscany.core.config.processor.DestroyProcessor
 * @see org.apache.tuscany.core.config.processor.ComponentNameProcessor
 *
 * @version $$Rev$$ $$Date$$
 * @see org.apache.tuscany.core.config.processor.ImplementationProcessorSupport
 */
public interface ImplementationProcessor {

    public void visitClass(Class<?> clazz, ComponentInfo type) throws ConfigurationLoadException;

    public void visitSuperClass(Class<?> clazz, ComponentInfo type) throws ConfigurationLoadException;

    public void visitMethod(Method method, ComponentInfo type) throws ConfigurationLoadException;

    public void visitConstructor(Constructor<?> constructor, ComponentInfo type) throws ConfigurationLoadException;

    public void visitField(Field field, ComponentInfo type) throws ConfigurationLoadException;

    public void visitEnd(Class<?> clazz, ComponentInfo type) throws ConfigurationLoadException;

}
