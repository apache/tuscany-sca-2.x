/*
 * Copyright 2002-2006 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.sca.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.osoa.sca.annotations.ComponentName;
import org.osoa.sca.annotations.OneWay;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Service;

import org.springframework.util.ReflectionUtils;

/**
 * TODO not the way to get this
 *
 * @author Rod Johnson
 */
public class AnnotationServiceMetadata implements ServiceMetadata {

    private final String name;

    private final Class<?> serviceClass;

    public AnnotationServiceMetadata(String name, Class<?> serviceClass) throws NoSuchServiceException {
        if (!serviceClass.isAnnotationPresent(Service.class)) {
            throw new NoSuchServiceException();
        }
        this.name = name;
        this.serviceClass = serviceClass;
    }

    public String getServiceName() {
        return name;
    }

    public Class<?>[] getServiceInterfaces() {
        Service service = serviceClass.getAnnotation(Service.class);
        return service.interfaces();
    }

    public List<Method> getOneWayMethods() {
        List<Method> oneWayMethods = new LinkedList<Method>();
        for (Method m : serviceClass.getMethods()) {
            if (m.isAnnotationPresent(OneWay.class)) {
                oneWayMethods.add(m);
            }
        }

        // TODO fields

        return oneWayMethods;
    }

    public List<Injection> getInjections() {
        final List<Injection> injections = new LinkedList<Injection>();
        for (Method m : serviceClass.getMethods()) {
            if (m.isAnnotationPresent(Property.class)) {
                injections.add(new MethodInjection(m));
            }
        }

        // TODO fields propertly

        ReflectionUtils.doWithFields(serviceClass, new ReflectionUtils.FieldCallback() {
            public void doWith(Field f) throws IllegalArgumentException, IllegalAccessException {
                if (f.isAnnotationPresent(ComponentName.class)) {
                    Injection componentNameInjection = new FieldInjection(f);
                    componentNameInjection.setLiteralValue(name);
                    injections.add(componentNameInjection);
                }
            }
        });

        return injections;
    }

    // TODO reference

}
