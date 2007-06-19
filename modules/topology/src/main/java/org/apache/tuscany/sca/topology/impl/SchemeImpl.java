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

package org.apache.tuscany.sca.topology.impl;

import org.apache.tuscany.sca.topology.Scheme;

/**
 * Represents the scheme defintion for a domain.
 * 
 * TBD - just a place holder at the moment
 *
 * @version $Rev$ $Date$
 */
public class SchemeImpl implements Scheme {
    
    private String name;
    private String baseURL;
    private String domainName;

    /**
     * Get the scheme name
     * 
     * @return
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Set the scheme name
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the scheme base URL
     * 
     * @return
     */
    public String getBaseURL() {
        return baseURL;
    }
    
    /**
     * Set the scheme base URL
     * 
     * @param name
     */
    public void setBaseURL(String urlString) {
        this.baseURL = urlString;
    }
    
    /**
     * Get the name of the domain that this scheme belongs to
     * 
     * @return
     */
    public String getDomainName() {
        return domainName;
    }
    
    /**
     * Set the name of the domain that this scheme belongs to
     * 
     * @param name
     */
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

}
