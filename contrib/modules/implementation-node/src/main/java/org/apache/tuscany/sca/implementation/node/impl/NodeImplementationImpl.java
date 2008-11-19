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
package org.apache.tuscany.sca.implementation.node.impl;

import java.util.Collections;
import java.util.List;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.implementation.node.NodeImplementation;


/**
 * The model representing a node implementation in an SCA assembly model.
 *
 * @version $Rev$ $Date$
 */
class NodeImplementationImpl implements NodeImplementation {

    private String uri;
    private boolean unresolved;
    private Composite composite;

    /**
     * Constructs a new node implementation.
     */
    NodeImplementationImpl() {
    }

    public ConstrainingType getConstrainingType() {
        // The node implementation does not support constrainingTypes
        return null;
    }

    public List<Property> getProperties() {
        // The node implementation does not support properties
        return Collections.emptyList();
    }

    public List<Service> getServices() {
        // The node implementation does not support services
        return Collections.emptyList();
    }
    
    public List<Reference> getReferences() {
        // The node implementation does not support properties
        return Collections.emptyList();
    }

    public String getURI() {
        return uri;
    }
    
    public Composite getComposite() {
        return composite;
    }
    
    public void setConstrainingType(ConstrainingType constrainingType) {
        // The node implementation does not support constrainingTypes
    }

    public void setURI(String uri) {
        this.uri = uri;
    }
    
    public void setComposite(Composite composite) {
        this.composite = composite;
    }


    public boolean isUnresolved() {
        return unresolved;
    }

    public void setUnresolved(boolean unresolved) {
        this.unresolved = unresolved;
    }
}
