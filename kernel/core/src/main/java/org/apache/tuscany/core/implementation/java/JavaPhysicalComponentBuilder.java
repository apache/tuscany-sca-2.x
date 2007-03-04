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

import org.apache.tuscany.core.component.InstanceFactory;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalComponentDefinition;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilder;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.services.classloading.ClassLoaderRegistry;
import org.osoa.sca.annotations.Reference;

/**
 * Java physical component builder.
 * 
 * @version $Rev$ $Date$
 *
 */
public class JavaPhysicalComponentBuilder implements
    PhysicalComponentBuilder<JavaPhysicalComponentDefinition, JavaComponent> {
    
    // Classloader registry
    private ClassLoaderRegistry classLoaderRegistry;
    
    // SCope registry
    private ScopeRegistry scopeRegistry;

    /**
     * Builds a component from its physical component definition.
     * 
     * @param componentDefinition Physical component definition of the component
     *            to be built.
     * @return A component instance that is ready to go live.
     * @throws BuilderException If unable to build the component.
     */
    public JavaComponent build(JavaPhysicalComponentDefinition componentDefinition) throws BuilderException {
        
        JavaComponent component = new JavaComponent();
        
        Scope scope = componentDefinition.getScope();
        URI classLoaderId = componentDefinition.getClassLoaderId();
        ScopeContainer scopeContainer = scopeRegistry.getScopeContainer(scope);
        component.setScopeContainer(scopeContainer);

        // TODO use MPCL to load IF class
        byte[] instanceFactoryByteCode = componentDefinition.getInstanceFactoryByteCode();
        ClassLoader appCl = classLoaderRegistry.getClassLoader(classLoaderId);
        ClassLoader systemCl = getClass().getClassLoader();        
        ClassLoader mpcl = null;
        Class<InstanceFactory<?>> instanceFactoryClass = null;        
        component.setInstanceFactoryClass(instanceFactoryClass);
        
        return component;
    }

    /**
     * Injects classloader registry.
     * @param classLoaderRegistry Class loader registry.
     */
    @Reference
    public void setClassLoaderRegistry(ClassLoaderRegistry classLoaderRegistry) {
        this.classLoaderRegistry = classLoaderRegistry;
    }

    /**
     * Injects scope registry.
     * @param scopeRegistry Scope registry.
     */
    @Reference
    public void setScopeRegistry(ScopeRegistry scopeRegistry) {
        this.scopeRegistry = scopeRegistry;
    }

}
