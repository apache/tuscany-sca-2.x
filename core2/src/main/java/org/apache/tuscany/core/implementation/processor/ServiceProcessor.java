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

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Remotable;

import org.apache.tuscany.spi.deployer.DeploymentContext;

import org.apache.tuscany.core.implementation.ImplementationProcessorSupport;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.JavaServiceContract;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.ProcessingException;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;

/**
 * Processes an {@link org.osoa.sca.annotations.Service} annotation and updates the component type with corresponding
 * {@link JavaMappedService}s
 *
 * @version $Rev$ $Date$
 */
public class ServiceProcessor extends ImplementationProcessorSupport {
    public void visitClass(Class<?> clazz, PojoComponentType type, DeploymentContext context)
        throws ProcessingException {
        org.osoa.sca.annotations.Service annotation = clazz.getAnnotation(org.osoa.sca.annotations.Service.class);
        if (annotation == null) {
            // scan intefaces for remotable
            //TODO also service?
            Class<?>[] interfaces = clazz.getInterfaces();
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

    public void visitEnd(Class<?> clazz, PojoComponentType type, DeploymentContext context) throws ProcessingException {
        super.visitEnd(clazz, type, context);
        if (!type.getServices().isEmpty()) {
            return;
        }
        // heuristically determine the service
        Class[] interfaces = clazz.getInterfaces();
        if (interfaces.length == 0) {
            // class is the interface
            throw new UnsupportedOperationException("Classes not yet supported as interfaces");
        } else if (interfaces.length == 1) {
            JavaMappedService service = createService(interfaces[0]);
            type.getServices().put(service.getName(), service);
        }
    }

    private JavaMappedService createService(Class<?> interfaze) {
        JavaMappedService service = new JavaMappedService();
        service.setName(JavaIntrospectionHelper.getBaseName(interfaze));
        service.setRemotable(interfaze.getAnnotation(Remotable.class) != null);
        service.setServiceInterface(interfaze);
        JavaServiceContract contract = new JavaServiceContract();
        contract.setInterfaceClass(interfaze);
        Callback callback = interfaze.getAnnotation(Callback.class);
        if (callback != null && !Void.class.equals(callback.value())) {
            Class<?> callbackClass = callback.value();
            contract.setCallbackClass(callbackClass);
            contract.setCallbackName(JavaIntrospectionHelper.getBaseName(callbackClass));
        }
        service.setServiceContract(contract);
        return service;
    }

}
