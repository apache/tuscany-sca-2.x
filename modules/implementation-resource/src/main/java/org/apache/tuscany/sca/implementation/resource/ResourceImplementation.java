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
package org.apache.tuscany.sca.implementation.resource;

import java.net.URL;

import org.apache.tuscany.sca.assembly.Implementation;

/**
 * The model representing a resource implementation in an SCA assembly model.
 */
public interface ResourceImplementation extends Implementation {

    /**
     * Returns the location of the directory containing the resources.
     * 
     * @return the location of the directory containing the resources
     */
    public String getLocation();

    /**
     * Sets the location of the directory containing the resources.
     * 
     * @param location the location of the directory containing the resources
     */
    public void setLocation(String location);
    
    /**
     * Returns the resource location URL.
     * 
     * @return the location URL
     */
    public URL getLocationURL();
    
    /**
     * Sets the resource location URL.
     * 
     * @param url the resource location URL
     */
    public void setLocationURL(URL url);

}
