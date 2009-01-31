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
 *
 * @version $Rev$ $Date$
 */
public interface ResourceImplementation extends Implementation {

    /**
     * The URI of the resource inside its contribution.
     * @return the URI of the resource
     */
    String getLocation();

    /**
     * Sets the URI of the resource inside its contribution.
     * @param location the URI of the resource
     */
    void setLocation(String location);

    /**
     * Returns the URL of the resource.
     * @return the URL of the resource
     */
    URL getLocationURL();

    /**
     * Sets the URL of the resource.
     * @param url the URL of the resource
     */
    void setLocationURL(URL url);

}
