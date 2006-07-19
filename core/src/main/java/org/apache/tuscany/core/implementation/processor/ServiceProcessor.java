/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.core.implementation.processor;

import java.util.Set;

import org.osoa.sca.annotations.Remotable;

import org.apache.tuscany.core.implementation.ImplementationProcessorSupport;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.ProcessingException;
import static org.apache.tuscany.core.implementation.processor.ProcessorUtils.createService;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;

/**
 * Processes an {@link org.osoa.sca.annotations.Service} annotation and updates the component type with corresponding
 * {@link JavaMappedService}s
 *
 * @version $Rev$ $Date$
 */
public class ServiceProcessor extends ImplementationProcessorSupport {
    public void visitClass(CompositeComponent<?> parent, Class<?> clazz,
                           PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                           DeploymentContext context)
        throws ProcessingException {
        org.osoa.sca.annotations.Service annotation = clazz.getAnnotation(org.osoa.sca.annotations.Service.class);
        if (annotation == null) {
            // scan intefaces for remotable
            //TODO also service?
            Set<Class> interfaces = JavaIntrospectionHelper.getAllInterfaces(clazz);
            for (Class<?> interfaze : interfaces) {
                if (interfaze.getAnnotation(Remotable.class) != null) {
                    JavaMappedService service = createService(interfaze);
                    type.getServices().put(service.getName(), service);
                }
            }
            return;
        }
        Class<?>[] interfaces = annotation.interfaces();
        if (interfaces.length == 0) {
            Class<?> interfaze = annotation.value();
            if (Void.class.equals(interfaze)) {
                throw new IllegalServiceDefinitionException("No interfaces specified");
            } else {
                interfaces = new Class<?>[1];
                interfaces[0] = interfaze;
            }
        }
        for (Class<?> interfaze : interfaces) {
            if (!interfaze.isInterface()) {
                InvalidServiceType e = new InvalidServiceType("Service must be an interface");
                e.setIdentifier(interfaze.getName());
                throw e;
            }
            JavaMappedService service = createService(interfaze);
            type.getServices().put(service.getName(), service);
        }
    }

}
