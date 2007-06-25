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
package org.apache.tuscany.sca.implementation.resource.impl;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.binding.resource.HTTPResourceBindingFactory;
import org.apache.tuscany.sca.implementation.resource.Resource;
import org.apache.tuscany.sca.implementation.resource.ResourceImplementation;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceIntrospector;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;


/**
 * The model representing a resource implementation in an SCA assembly model.
 */
public class ResourceImplementationImpl implements ResourceImplementation {

    private Service resourceService;
    
    private String location;
    private URL url;
    private boolean unresolved;

    /**
     * Constructs a new resource implementation.
     */
    public ResourceImplementationImpl(AssemblyFactory assemblyFactory,
                                  JavaInterfaceFactory javaFactory,
                                  JavaInterfaceIntrospector introspector,
                                  HTTPResourceBindingFactory bindingFactory) {

        // Resource implementation always provide a single service exposing
        // the Resource interface, and have no references and properties
        resourceService = assemblyFactory.createService();
        resourceService.setName("Resource");
        
        // Create the Java interface contract for the Resource service
        JavaInterface javaInterface;
        try {
            javaInterface = introspector.introspect(Resource.class);
        } catch (InvalidInterfaceException e) {
            throw new IllegalArgumentException(e);
        }
        JavaInterfaceContract interfaceContract = javaFactory.createJavaInterfaceContract();
        interfaceContract.setInterface(javaInterface);
        resourceService.setInterfaceContract(interfaceContract);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public URL getLocationURL() {
        return url;
    }
    
    public void setLocationURL(URL url) {
        this.url = url;
    }
    
    public ConstrainingType getConstrainingType() {
        // The resource implementation does not support constrainingTypes
        return null;
    }

    public List<Property> getProperties() {
        // The resource implementation does not support properties
        return Collections.emptyList();
    }

    public List<Service> getServices() {
        // The resource implementation does not support services
        return Collections.singletonList(resourceService);
    }
    
    public List<Reference> getReferences() {
        // The resource implementation does not support properties
        return Collections.emptyList();
    }

    public String getURI() {
        return location;
    }

    public void setConstrainingType(ConstrainingType constrainingType) {
        // The resource implementation does not support constrainingTypes
    }

    public void setURI(String uri) {
        this.location = uri;
    }

    public List<PolicySet> getPolicySets() {
        // The resource implementation does not support policy sets
        return Collections.emptyList();
    }

    public List<Intent> getRequiredIntents() {
        // The resource implementation does not support intents
        return Collections.emptyList();
    }

    public List<Object> getExtensions() {
        // The resource implementation does not support extensions
        return Collections.emptyList();
    }

    public boolean isUnresolved() {
        return unresolved;
    }

    public void setUnresolved(boolean unresolved) {
        this.unresolved = unresolved;
    }

}
