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
package org.apache.tuscany.core.config.processor;

import org.apache.tuscany.core.config.AnnotationProcessor;
import org.apache.tuscany.core.config.ComponentTypeIntrospector;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.system.assembly.SystemAssemblyFactory;
import org.apache.tuscany.model.assembly.ComponentInfo;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * A base implementation of an <code>AnnotationProcess</code>
 *
 * @version $$Rev$$ $$Date$$
 */
@Scope("MODULE")
public abstract class AnnotationProcessorSupport implements AnnotationProcessor {

    protected ComponentTypeIntrospector introspector;
    protected SystemAssemblyFactory factory;

    @Init(eager = true)
    public void init() throws Exception {
        introspector.registerProcessor(this);
    }

    @Autowire
    public void setIntrospector(ComponentTypeIntrospector introspector) {
        this.introspector = introspector;
    }

    @Autowire
    public void setFactory(SystemAssemblyFactory factory) {
        this.factory = factory;
    }

    public void visitClass(Class clazz, Annotation annotation, ComponentInfo type) {

    }

    public void visitMethod(Method method, Annotation annotation, ComponentInfo type) {

    }

    public void visitConstructor(Constructor constructor, Annotation annotation, ComponentInfo type) {

    }

    public void visitField(Field field, Annotation annotation, ComponentInfo type) {
    }
}
