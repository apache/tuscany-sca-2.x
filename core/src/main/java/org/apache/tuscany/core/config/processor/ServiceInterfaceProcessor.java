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

import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.model.assembly.ComponentInfo;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.types.java.JavaServiceContract;
import org.osoa.sca.annotations.Callback;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Processes an SCA <code>@Service</code> annotation and populates the component type
 * @version $$Rev$$ $$Date$$
 */
public class ServiceInterfaceProcessor extends AnnotationProcessorSupport {


    @Override
    public void visitClass(Class clazz, Annotation annotation, ComponentInfo type) {
        if (!(annotation instanceof org.osoa.sca.annotations.Service)) {
            return;
        }
        List<Service> services = type.getServices();
        if (services.isEmpty()) {
            return;
        }

        // add services defined in an @Service annotation
        org.osoa.sca.annotations.Service serviceAnnotation = (org.osoa.sca.annotations.Service) annotation;
        Class<?>[] interfaces = serviceAnnotation.interfaces();
        Class<?> value = serviceAnnotation.value();
        if (interfaces.length > 0) {
            if (!Void.class.equals(value)) {
                throw new IllegalArgumentException("Both interfaces and value specified in @Service on "
                        + clazz.getName());
            }
            for (Class<?> intf : interfaces) {
                addService(services, intf);
            }
            return;
        } else if (!Void.class.equals(value)) {
            addService(services, value);
            return;
        }

    }

    /**
     * Recursively adds supported services to a component type
     */
    private void addService(List<Service> services, Class<?> serviceClass) {
        JavaServiceContract javaInterface = factory.createJavaServiceContract();
        javaInterface.setInterface(serviceClass);
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
