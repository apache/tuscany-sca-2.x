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

package org.apache.tuscany.sca.host.embedded;

import org.osoa.sca.CallableReference;
import org.osoa.sca.ServiceReference;

public class SCADomainBean extends SCADomain {
    
    private SCADomain instance;
    
    private String uri = LOCAL_DOMAIN_URI;
    private String location = "/";
    private String[] composites;

    /**
     * Constructs a new SCA domain
     */
    public SCADomainBean() {
    }

    public String getURI() {
        return uri;
    }
    
    public void setURI(String uri) {
        this.uri = uri;
    }
    
    public void setContributionLocation(String contributionLocation) {
        this.location = contributionLocation;
    }
    
    public String getContributionLocation() {
        return location;
    }

    public void setDeployableComposite(String composite) {
        setDeployableComposites(composite);
    }
    
    public void setDeployableComposites(String... composites) {
        this.composites = composites;
    }
    
    public String[] getDeployableComposites() {
        return composites;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
        if (instance == null) {
            instance = SCADomain.createNewInstance(uri, location, composites);
        }
        Object result = instance.cast(target); 
        return (R) result;
    }

    @Override
    public void close() {
        if (instance == null) {
            instance = SCADomain.createNewInstance(uri, location, composites);
        }
        instance.close();
        instance = null;
    }

    @Override
    public <B> B getService(Class<B> businessInterface, String serviceName) {
        if (instance == null) {
            instance = SCADomain.createNewInstance(uri, location, composites);
        }
        return instance.getService(businessInterface, serviceName);
    }

    @Override
    public <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String referenceName) {
        if (instance == null) {
            instance = SCADomain.createNewInstance(uri, location, composites);
        }
        return instance.getServiceReference(businessInterface, referenceName);
    }

    @Override
    protected void finalize() throws Throwable {
        
        // Make sure that the SCA domain is closed
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }
}
