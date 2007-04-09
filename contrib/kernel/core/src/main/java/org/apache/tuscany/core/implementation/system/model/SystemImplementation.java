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
package org.apache.tuscany.core.implementation.system.model;

import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.model.AtomicImplementation;

/**
 * Represents the system composite implementation
 *
 * @version $Rev$ $Date$
 */
public class SystemImplementation extends AtomicImplementation<PojoComponentType> {
    private Class<?> implementationClass;

    public SystemImplementation(PojoComponentType componentType, Class<?> implementationClass) {
        super(componentType);
        this.implementationClass = implementationClass;
    }

    public SystemImplementation() {
    }

    public SystemImplementation(Class<?> implementationClass) {
        this.implementationClass = implementationClass;
    }

    public Class<?> getImplementationClass() {
        return implementationClass;
    }

    public void setImplementationClass(Class<?> implementationClass) {
        this.implementationClass = implementationClass;
    }
}
