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
package org.apache.tuscany.sca.node;

/**
 * Represents an SCA contribution uri + location.
 */
public final class SCAContribution {
    private String uri;
    private String location;
    
    /**
     * Constructs a new SCA contribution.
     * 
     * @param uri The URI that uniquely identifies the contribution in the SCA domain
     * @param location The URL of the contribution archive
     */
    public SCAContribution(String uri, String location) {
        this.uri = uri;
        this.location = location;
    }
    
    /**
     * Get the URI of the contribution
     * @return The URI that uniquely identifies the contribution in the SCA domain
     */
    public String getURI() {
        return uri;
    }
    
    /**
     * The location of the contribution
     * @return The URL of the contribution archive
     */
    public String getLocation() {
        return location;
    }
}