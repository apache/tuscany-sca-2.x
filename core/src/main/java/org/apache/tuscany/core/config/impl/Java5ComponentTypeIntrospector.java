/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.config.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.tuscany.core.config.ComponentTypeIntrospector;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.extension.config.ImplementationProcessor;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.config.processor.ProcessorUtils;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.system.assembly.SystemAssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.ComponentInfo;

/**
 * Introspects Java annotation-based metata data
 *
 * @version $Rev$ $Date$
 */
@org.osoa.sca.annotations.Service(interfaces = {ComponentTypeIntrospector.class})
public class Java5ComponentTypeIntrospector implements ComponentTypeIntrospector {

    private AssemblyFactory factory;

    private List<ImplementationProcessor> processors = new ArrayList<ImplementationProcessor>();

    public Java5ComponentTypeIntrospector() {
    }

    public Java5ComponentTypeIntrospector(AssemblyFactory factory) {
        this.factory = factory;
    }

    @Autowire
    public void setFactory(SystemAssemblyFactory factory) {
        this.factory = factory;
        //FIXME JFM HACK
        List<ImplementationProcessor> processors = ProcessorUtils.createCoreProcessors(factory);
        for (ImplementationProcessor processor : processors) {
            this.registerProcessor(processor);
        }
        // END hack
    }

    public void registerProcessor(ImplementationProcessor processor) {
        processors.add(processor);
    }

    public void unregisterProcessor(ImplementationProcessor processor) {
        processors.remove(processor);
    }

    /**
     * Visits the given implementation type and calls back to {@link org.apache.tuscany.core.extension.config.ImplementationProcessor}s registered with
     * this introspector to build up a {@link ComponentInfo}
     *
     * @return ComponentInfo representing the implementation type metadata
     * @throws ConfigurationLoadException if there is an error introspecting the implementation type
     */
    public ComponentInfo introspect(Class<?> implClass) throws ConfigurationLoadException {
        ComponentInfo compType = factory.createComponentInfo();
        return introspect(implClass, compType);
    }

    public ComponentInfo introspect(Class<?> implClass, ComponentInfo compType) throws ConfigurationLoadException {
        for (ImplementationProcessor processor : processors) {
            processor.visitClass(implClass, compType);
        }
        Constructor[] constructors = implClass.getConstructors();
        for (Constructor constructor : constructors) {
            for (ImplementationProcessor processor : processors) {
                processor.visitConstructor(constructor, compType);
            }
        }
        Method[] methods = implClass.getMethods();
        for (Method method : methods) {
            for (ImplementationProcessor processor : processors) {
                processor.visitMethod(method, compType);
            }
        }
        Set<Field> fields = JavaIntrospectionHelper.getAllPublicAndProtectedFields(implClass);
        for (Field field : fields) {
            for (ImplementationProcessor processor : processors) {
                processor.visitField(field, compType);
            }
        }
        Class superClass = implClass.getSuperclass();
        if (superClass != null) {
            visitSuperClass(superClass, compType);
        }
        for (ImplementationProcessor processor : processors) {
            processor.visitEnd(implClass, compType);
        }
        return compType;
    }

    private void visitSuperClass(Class<?> superClass, ComponentInfo compType) throws ConfigurationLoadException {
        if (!Object.class.equals(superClass)) {
            for (ImplementationProcessor processor : processors) {
                processor.visitSuperClass(superClass, compType);
            }
            superClass = superClass.getSuperclass();
            if (superClass != null) {
                visitSuperClass(superClass, compType);
            }
        }
    }

}
