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
package org.apache.tuscany.core.implementation.processor;

import org.apache.tuscany.spi.implementation.java.ImplementationProcessorSupport;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;
import org.apache.tuscany.core.implementation.system.component.SystemCompositeComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.Scope;

/**
 * Processes the {@link Scope} annotation and updates the component type with the corresponding implmentation scope
 *
 * @version $Rev$ $Date$
 */
public class ScopeProcessor extends ImplementationProcessorSupport {

    public void visitClass(CompositeComponent<?> parent, Class<?> clazz,
                           PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                           DeploymentContext context)
        throws ProcessingException {
        org.osoa.sca.annotations.Scope annotation = clazz.getAnnotation(org.osoa.sca.annotations.Scope.class);
        if (annotation == null) {
            // TODO do this with a specialization of a system POJO
            if (SystemCompositeComponent.class.isAssignableFrom(parent.getClass())) {
                type.setImplementationScope(Scope.MODULE);
            } else {
                type.setImplementationScope(Scope.STATELESS);
            }
            return;
        }
        //FIXME deal with eager init
        //FIXME needs to be extensible
        String name = annotation.value();
        Scope scope;
        if ("MODULE".equals(name)) {
            scope = Scope.MODULE;
        } else if ("SESSION".equals(name)) {
            scope = Scope.SESSION;
        } else if ("REQUEST".equals(name)) {
            scope = Scope.REQUEST;
        } else if ("COMPOSITE".equals(name)) {
            scope = Scope.COMPOSITE;
        } else {
            scope = Scope.STATELESS;
        }
        type.setImplementationScope(scope);
    }
}
