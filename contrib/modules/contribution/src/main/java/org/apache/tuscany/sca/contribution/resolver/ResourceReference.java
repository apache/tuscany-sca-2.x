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

package org.apache.tuscany.sca.contribution.resolver;

import java.net.URL;

/**
 * A resource URL, which should be used to register resources
 * with an ArtifactResolver and resolve these resources later.
 * 
 * FIXME Don't use as its deprecated, use Artifact instead.
 *
 * @version $Rev$ $Date$
 */
@Deprecated
public class ResourceReference {
    
    private URL resourceURL;
    private String resourceName;

    /**
     * Constructs a new ResourceReference.
     * 
     * @param resourceName Name of resource
     * @param resourceURL  The resource URL
     */
    public ResourceReference(String resourceName, URL resourceURL) {
        this.resourceURL = resourceURL;
        this.resourceName = resourceName;
    }
    
    /**
     * Constructs a new ResourceReference.
     * 
     * @param resourceName Name of resource
     */
    public ResourceReference(String resourceName) {
        this.resourceName = resourceName;
    }
    
    /**
     * Get the resource URL.
     * 
     * @return The resource URL
     */
    public URL getResource() {
        return resourceURL;
    }
    
    /**
     * Get the resource name.
     * 
     * @return The resource name
     */
    public String getResourceName() {
        return resourceName;
    }
    
    /**
     * Returns true if the resource reference is unresolved.
     * 
     * @return Whether or not the resource has been resolved
     */
    public boolean isUnresolved() {
        return resourceURL == null;
    }

    @Override
    public int hashCode() {
        return resourceName.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else {
            if (obj instanceof ResourceReference) {
                return resourceName.equals(((ResourceReference)obj).resourceName);
            } else {
                return false;
            }
        }
    }

}
