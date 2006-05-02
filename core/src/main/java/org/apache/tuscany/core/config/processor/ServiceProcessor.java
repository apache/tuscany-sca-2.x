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

import java.util.List;

import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.InvalidMetaDataException;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.ComponentInfo;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.types.java.JavaServiceContract;
import org.osoa.sca.annotations.Callback;

/**
 * Processes the {@link org.osoa.sca.annotations.Service} annotation
 *
 * @version $$Rev$$ $$Date$$
 */
public class ServiceProcessor extends ImplementationProcessorSupport {

    public ServiceProcessor() {
    }

    public ServiceProcessor(AssemblyFactory factory) {
        super(factory);
    }

    @Override
    public void visitClass(Class<?> clazz, ComponentInfo type) throws ConfigurationLoadException {
        if (!clazz.isInterface()) {
            processImplementation(clazz,type);
        } else {
            processInterface(clazz, type);
        }
    }

    private void processImplementation(Class<?> clazz, ComponentInfo type) throws ConfigurationLoadException {
        // visiting the base implementation class
        List<org.apache.tuscany.model.assembly.Service> services = type.getServices();
        Class[] interfaces = clazz.getInterfaces();
        org.osoa.sca.annotations.Service serviceAnnotation = clazz.getAnnotation(org.osoa.sca.annotations.Service.class);
        if (interfaces.length == 0) {
            // no interfaces so the class is the service
            addService(services, clazz);
        } else if (serviceAnnotation == null && interfaces.length == 1) {
            // the impl has one interface, assign it to be the service
            addService(services, interfaces[0]);
        } else {
            // visiting the implementation class
            if (serviceAnnotation == null) {
                return;
            }
            Class<?>[] serviceInterfaces = serviceAnnotation.interfaces();
            Class<?> value = serviceAnnotation.value();
            if (serviceInterfaces.length > 0) {
                if (!Void.class.equals(value)) {
                    InvalidMetaDataException e = new InvalidMetaDataException("Both interfaces and value specified in @Service on ");
                    e.setIdentifier(clazz.getName());
                    throw e;
                }
                for (Class<?> intf : interfaces) {
                    addService(services, intf);
                }
            } else if (!Void.class.equals(value)) {
                addService(services, value);
            }
        }
    }


    @Override
    public void visitEnd(Class<?> clazz, ComponentInfo type) throws ConfigurationLoadException {
        List<Service> services = type.getServices();
        if (services.size() == 0) {
            // no services processed so the class is the service
            addService(services, clazz);
        }
    }

    private void processInterface(Class<?> clazz, ComponentInfo type) {
        List<org.apache.tuscany.model.assembly.Service> services = type.getServices();
        // the interface is a remotable service, add it
        org.osoa.sca.annotations.Remotable remotableAnnotation = clazz.getAnnotation(org.osoa.sca.annotations.Remotable.class);
        if (remotableAnnotation != null) {
            // check to see if service added previously b/c it was specified on @Service
            if (ProcessorHelper.getService(clazz, services) == null) {
                addService(services, clazz);
            }
        }
    }


    private void addService(List<Service> services, Class<?> serviceClass) {
        //FIXME how do we support specifying remotable?
        JavaServiceContract javaInterface = factory.createJavaServiceContract();
        javaInterface.setInterface(serviceClass);
        org.osoa.sca.annotations.Scope scopeAnnotation = serviceClass.getAnnotation(org.osoa.sca.annotations.Scope.class);
        Scope scope;
        if (scopeAnnotation == null) {
            scope = Scope.INSTANCE;
        } else {
            scope = ProcessorHelper.getScope(scopeAnnotation);
        }
        javaInterface.setScope(scope);
        Callback callback = serviceClass.getAnnotation(Callback.class);
        if (callback != null && !Void.class.equals(callback.value())) {
            javaInterface.setCallbackInterface(callback.value());
        }
        String name = JavaIntrospectionHelper.getBaseName(serviceClass);
        Service service = factory.createService();
        service.setName(name);
        service.setServiceContract(javaInterface);
        services.add(service);
    }
}
