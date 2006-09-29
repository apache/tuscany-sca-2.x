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
 *
 */
package org.apache.tuscany.container.spring.impl;

import org.apache.tuscany.container.spring.model.SpringComponentType;
import org.springframework.sca.ScaAdapter;

/**
 * @author Andy Piper
 * @since 2.1
 */
public class SpringScaAdapter implements ScaAdapter {
    //private final SpringComponentType componentType;

    public SpringScaAdapter(SpringComponentType componentType) {
        //this.componentType = componentType;
    }

    public Object getServiceReference(String referenceName, Class referenceType, String moduleName,
                                      String defaultServiceName) {
        /*
        ReferenceDefinition reference = null;
        componentType.getReferences().put(referenceName, reference);
        */
        return null;
    }

    public Object getPropertyReference(String propertyName, Class propertyType, String moduleName) {
        return null;
    }

    public void publishAsService(Object serviceImplementation, Class serviceInterface, String serviceName,
                                 String moduleName) {
        /*
        componentType.addServiceType(serviceName, serviceInterface);
        ServiceDefinition service = null;
        componentType.getServices().put(serviceName, service);
        */
    }
}
