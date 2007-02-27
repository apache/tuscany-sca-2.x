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
package org.apache.tuscany.spi.model.physical;

import java.net.URI;

import javax.xml.namespace.QName;

/**
 * Model class representing the portable definition of a wire. This class is used to describe the inbound and outbound
 * wires on a physical component definition.
 *
 * @version $Rev$ $Date$
 */
public class PhysicalWireDefinition extends Operations {
    
    // TODO this should be removed
    @Deprecated
    private QName bindingType;
    
    // The resolved source URI of the wire
    private URI sourceUri;
    
    // The resolved source URI of the wire
    private URI targetUri;

    /**
     * Returns the wire binding type.
     * @return the binding type of the wire.
     */
    @Deprecated
    public QName getBindingType() {
        return bindingType;
    }

    /**
     * Sets the Wire source URI.
     * @param sourceUri Wire source URI.
     */
    public void setSourceUri(URI sourceUri) {
        this.sourceUri = sourceUri;
    }

    /**
     * Gets the Wire source URI.
     * @return Wire source URI.
     */
    public URI getSourceUri() {
        return sourceUri;
    }

    /**
     * Sets the Wire target URI.
     * @param targetUri Wire source URI.
     */
    public void setTargetUri(URI targetUri) {
        this.targetUri = targetUri;
    }

    /**
     * Gets the Wire target URI.
     * @return Wire target URI.
     */
    public URI getTargetUri() {
        return targetUri;
    }

}
