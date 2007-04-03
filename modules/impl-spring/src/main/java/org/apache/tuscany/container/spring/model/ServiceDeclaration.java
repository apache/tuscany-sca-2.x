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
package org.apache.tuscany.container.spring.model;

/**
 * Represents a <code>sca:service<code> declaration in an application context. Used as component type metadata for a
 * Spring composite
 *
 * @version $Rev$ $Date$
 */
public class ServiceDeclaration {
    private String name;
    private Class<?> serviceType;
    private String target;

    /**
     * Constructor
     *
     * @param name        the service name
     * @param serviceType the service contract type
     * @param target      the name of the target the service is wired to
     */
    public ServiceDeclaration(String name, Class<?> serviceType, String target) {
        this.name = name;
        this.serviceType = serviceType;
        this.target = target;
    }

    public String getName() {
        return name;
    }

    public Class<?> getClazz() {
        return serviceType;
    }

    public String getTarget() {
        return target;
    }

}
