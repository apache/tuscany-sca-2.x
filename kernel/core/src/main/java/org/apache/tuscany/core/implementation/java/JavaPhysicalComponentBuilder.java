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
package org.apache.tuscany.core.implementation.java;

import java.net.URI;

import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.core.component.InstanceFactoryProvider;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalComponentDefinition;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalWireSourceDefinition;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalWireTargetDefinition;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilder;
import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilderRegistry;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.services.classloading.ClassLoaderRegistry;
import org.apache.tuscany.spi.wire.physical.WireAttacher;

/**
 * Java physical component builder.
 *
 * @version $Rev$ $Date$
 * @param <T> the implementation class for the defined component
 */
public class JavaPhysicalComponentBuilder<T>
    implements PhysicalComponentBuilder<JavaPhysicalComponentDefinition<T>, JavaComponent<T>>,
    WireAttacher<JavaComponent, JavaPhysicalWireSourceDefinition, JavaPhysicalWireTargetDefinition> {

    // Classloader registry
    private ClassLoaderRegistry classLoaderRegistry;

    // SCope registry
    private ScopeRegistry scopeRegistry;

    /**
     * Injects builder registry.
     * @param registry PhysicalComponentBuilder registry.
     */
    @Reference
    public void setBuilderRegistry(PhysicalComponentBuilderRegistry registry) {
        registry.register(JavaPhysicalComponentDefinition.class, this);
    }

    /**
     * Builds a component from its physical component definition.
     *
     * @param componentDefinition Physical component definition of the component
     *                            to be built.
     * @return A component instance that is ready to go live.
     * @throws BuilderException If unable to build the component.
     */
    public JavaComponent<T> build(JavaPhysicalComponentDefinition<T> componentDefinition) throws BuilderException {

        URI componentId = componentDefinition.getComponentId();
        InstanceFactoryProvider<T> provider = componentDefinition.getProvider();
        JavaComponent<T> component = new JavaComponent<T>(componentId, provider, null, 0, -1, -1);

        setScopeContainer(componentDefinition, component);

        setInstanceFactoryClass(componentDefinition, component);

        return component;
    }

    /**
     * Injects classloader registry.
     *
     * @param classLoaderRegistry Class loader registry.
     */
    @Reference
    public void setClassLoaderRegistry(ClassLoaderRegistry classLoaderRegistry) {
        this.classLoaderRegistry = classLoaderRegistry;
    }

    /**
     * Injects scope registry.
     *
     * @param scopeRegistry Scope registry.
     */
    @Reference
    public void setScopeRegistry(ScopeRegistry scopeRegistry) {
        this.scopeRegistry = scopeRegistry;
    }

    /*
     * Sets the instance factory class.
     */
    private void setInstanceFactoryClass(JavaPhysicalComponentDefinition componentDefinition, JavaComponent component) {
/*
        // TODO use MPCL to load IF class
        URI classLoaderId = componentDefinition.getClassLoaderId();
        byte[] instanceFactoryByteCode = componentDefinition.getInstanceFactoryByteCode(); //NOPMD
        ClassLoader appCl = classLoaderRegistry.getClassLoader(classLoaderId); //NOPMD
        ClassLoader systemCl = getClass().getClassLoader(); //NOPMD        
        ClassLoader mpcl = null; //NOPMD
        Class<InstanceFactory<?>> instanceFactoryClass = null;
        component.setInstanceFactoryClass(instanceFactoryClass);
*/
    }

    /*
     * Set the scope container.
     */
    private void setScopeContainer(JavaPhysicalComponentDefinition componentDefinition, JavaComponent component) {
        Scope scope = componentDefinition.getScope();
        ScopeContainer scopeContainer = scopeRegistry.getScopeContainer(scope);
        component.setScopeContainer(scopeContainer);
    }

    /**
     * Attaches the source to the component.
     *
     * @param component Component.
     * @param source    Source.
     */
    public void attach(JavaComponent component, JavaPhysicalWireSourceDefinition source) {
    }

    /**
     * Attaches the target to the component.
     *
     * @param component Component.
     * @param target    Target.
     */
    public void attach(JavaComponent component, JavaPhysicalWireTargetDefinition target) {
    }

}
