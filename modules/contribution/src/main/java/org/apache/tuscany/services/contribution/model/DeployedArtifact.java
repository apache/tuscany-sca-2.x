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

package org.apache.tuscany.services.contribution.model;

import java.net.URI;
import java.net.URL;

/**
 * Representation of a deployed artifact
 *
 * @version $Rev$ $Date$
 */
public class DeployedArtifact {
    protected Contribution contribution;
    protected URI uri;
    protected URL location;
    protected Object modelObject;

    public DeployedArtifact() {
        super();
    }
    /**
     * @param uri the artifact uri
     */
    public DeployedArtifact(URI uri) {
        super();
        this.uri = uri;
    }
    /**
     * Get the absolute URI as the unique id for the artifact
     * @return
     */
    public URI getUri() {
        return uri;
    }
    
    public void setUri(URI uri) {
        this.uri = uri;
    }
    
    /**
     * @return the location
     */
    public URL getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(URL location) {
        this.location = location;
    }

    public Contribution getContribution() {
        return contribution;
    }

    public void setContribution(Contribution contribution) {
        this.contribution = contribution;
    }
    public Object getModelObject() {
        return modelObject;
    }
    
    public void setModelObject(Object modelObject){
        this.modelObject = modelObject;
    }
}
