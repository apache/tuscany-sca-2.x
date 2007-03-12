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

package org.apache.tuscany.core.model.physical.instancefactory;

import java.net.URI;

/**
 * Represents an injection site.
 */
public class InjectionSite {
    
    // Type
    private InjectionSiteType type;
    
    // Class
    private String injectionClass;
    
    // URI
    private URI uri;
    
    // Name
    private String name;

    /**
     * Gets the class of the injected reference.
     * @return Class of the injected reference.
     */
    public String getInjectionClass() {
        return injectionClass;
    }

    /**
     * Sets the class of the injected reference.
     * @param injectionClass Class of the injected reference.
     */
    public void setInjectionClass(String injectionClass) {
        this.injectionClass = injectionClass;
    }

    /**
     * Gets the type of the injection site.
     * @return Injection site type.
     */
    public InjectionSiteType getType() {
        return type;
    }

    /**
     * Gets the type of the injection site.
     * @param type Injection site type.
     */
    public void setType(InjectionSiteType type) {
        this.type = type;
    }

    /**
     * Gets njection site URI.
     * @return Injection site URI.
     */
    public URI getUri() {
        return uri;
    }

    /**
     * Gets njection site URI.
     * @param uri Injection site URI.
     */
    public void setUri(URI uri) {
        this.uri = uri;
    }

    /**
     * Gets the name.
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * @param name Name.
     */
    public void setName(String name) {
        this.name = name;
    }

}
