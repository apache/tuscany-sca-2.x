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
package org.apache.tuscany.spi.implementation.java;

import org.apache.tuscany.spi.model.ComponentType;

/**
 * A specialized component type definition whose services, references and properties can be mapped to the Java
 * programming model.
 *
 * @version $Rev$ $Date$
 */
public class JavaMappedComponentType<
    S extends JavaMappedService,
    R extends JavaMappedReference,
    P extends JavaMappedProperty<?>
    > extends ComponentType<S, R, P> {

    private Class<?> implClass;

    public JavaMappedComponentType() {
    }

    public JavaMappedComponentType(Class<?> implClass) {
        this.implClass = implClass;
    }

    /**
     * Returns the implementation class associated with this component type.
     *
     * @return the implementation class associated with this component type
     */
    public Class<?> getImplClass() {
        return implClass;
    }

    /**
     * Sets the implementation class associated with this component type.
     *
     * @param implClass the implementation class associated with this component type
     */
    public void setImplClass(Class<?> implClass) {
        this.implClass = implClass;
    }
}
