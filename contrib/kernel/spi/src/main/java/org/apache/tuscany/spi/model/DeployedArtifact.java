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

package org.apache.tuscany.spi.model;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Representation of a deployed artifact
 *
 * @version $Rev$ $Date$
 */
public class DeployedArtifact extends ModelObject {
    protected Contribution contribution;
    protected URI uri;
    protected URL location;
    /**
     * The map keeps all the model objects loaded/introspected from this artifact. The objects
     * are keyed by the java type of the model such as javax.wsdl.ModelObject. The value is also
     * a map with namespace as the key and the model object as the value.
     */
    protected Map<Class, Map<String, Object>> modelObjects = new HashMap<Class, Map<String, Object>>();

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
    public Map<Class, Map<String, Object>> getModelObjects() {
        return modelObjects;
    }

    public Map<String, Object> getModelObjects(Class type) {
        return modelObjects.get(type);
    }

    public Object getModelObject(Class type, String namespace) {
        Map<String, Object> map = modelObjects.get(type);
        if (map == null) {
            return null;
        } else {
            return map.get(namespace);
        }
    }

    public void addModelObject(Class type, String namespace, Object modelObject) {
        Map<String, Object> map = modelObjects.get(type);
        if (map == null) {
            map = new HashMap<String, Object>();
            modelObjects.put(type, map);
        }
        map.put(namespace, modelObject);
    }

}
