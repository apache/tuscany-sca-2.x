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

package org.apache.tuscany.sca.implementation.spring.invocation;

import java.util.List;

import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.implementation.java.injection.JavaPropertyValueObjectFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

/**
 * This is the Tuscany side tie for the corresponding Spring runtime side stub class.
 * It enables the Tuscany code to invoke methods on a Spring context without
 * needing to know about any Spring classes. See the PropertyValueStub class
 * in the implementation-spring-runtime module for what the stub does. 
 */
public class PropertyValueTie {

    private RuntimeComponent component;
    private JavaPropertyValueObjectFactory propertyFactory;
    
    public PropertyValueTie(RuntimeComponent component, JavaPropertyValueObjectFactory propertyFactory) {
        this.component = component;
        this.propertyFactory = propertyFactory;
    }

    public Object getPropertyObj(Class<?> type, String name) {
        List<ComponentProperty> props = component.getProperties();
        for (ComponentProperty prop : props) {
            if (prop.getName().equals(name)) {
                return propertyFactory.createValueFactory(prop, prop.getValue(), type).getInstance();
            } 
        }         
        return null; // property name not found
    }
}
