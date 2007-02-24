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

import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.Property;

import org.springframework.core.io.Resource;

/**
 * Represents a composite whose implementation type is a Spring application context.
 *
 * @version $$Rev$$ $$Date$$
 */
public class SpringImplementation extends Implementation<SpringComponentType<Property<?>>> {
    private String location;
    private Resource applicationResource;
    private ClassLoader classLoader;

    public SpringImplementation(ClassLoader classloader) {
        this.classLoader = classloader;
    }

    /**
     * Returns the classloader of the Spring application context
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Returns the path of the Spring application context configuration
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the path of the Spring application context configuration
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Returns the Spring configuration resource for the application context
     */
    public Resource getApplicationResource() {
        return applicationResource;
    }

    /**
     * Sets the Spring configuration resource for the application context
     */
    public void setApplicationResource(Resource applicationXml) {
        this.applicationResource = applicationXml;
    }
}
