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

package org.apache.tuscany.sca.contribution;


/**
 * Base Artifact interface to accomodate common properties between Contribution and Deployed Artifact
 * 
 * @version $Rev$ $Date$
 */
public interface Artifact {
    /**
     * Get the URI that unique identifies the artifact
     * 
     * @return The artifact uri
     */
    String getURI();
    
    /**
     * Set the URI that unique identifies the artifact
     * 
     * @param uri The artifact uri
     */
    void setURI(String uri);

    /**
     * Get the URL location for the artifact
     * 
     * @return The artifact location
     */
    String getLocation();
    
    /**
     * Set the URL location for the artifact
     * 
     * @param location The artifact location
     */
    void setLocation(String location);

}
