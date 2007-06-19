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

package org.apache.tuscany.sca.topology;


/**
 * Represents an SCA scheme. This holds the base URL that a runtime node
 * will use by default to expose services
 *
 * @version $Rev$ $Date$
 */
public interface Scheme {
    
    /**
     * Get the scheme name
     * 
     * @return
     */
    public String getName();
    
    /**
     * Set the scheme name
     * 
     * @param name
     */
    public void setName(String name);

    /**
     * Get the scheme base URL
     * 
     * @return
     */
    public String getBaseURL();
    
    /**
     * Set the scheme base URL
     * 
     * @param name
     */
    public void setBaseURL(String urlString);
    
    /**
     * Get the name of the domain that this scheme belongs to
     * 
     * @return
     */
    public String getDomainName();
    
    /**
     * Set the name of the domain that this scheme belongs to
     * 
     * @param name
     */
    public void setDomainName(String domainName);
}
