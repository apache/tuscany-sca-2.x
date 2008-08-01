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
package org.apache.tuscany.sca.binding.gdata;

import org.apache.tuscany.sca.assembly.Binding;

/**
 * GData binding model.
 * 
 * @version $Rev$ $Date$
 */
public interface GDataBinding extends Binding {

    /**
     * Returns the title of the Atom collection.
     * @return
     */
    String getTitle();

    /**
     * Sets the title of the Atom collection.
     * @param title
     */
    void setTitle(String title);

    /*
     * Returns the username of a Google account
     * @return
     */
    String getUsername();

    /*
     * Sets the username of a Google account
     * @param username
     */
    void setUsername(String username);

    /*
     * Returns the password of a Google account
     * @return
     */
    String getPassword();

    /*
     * Sets the password of a Google account
     * @param password
     */
    void setPassword(String password);

    /*
     * Retruns the name of the Google service to which we are connecting. Sample names of services might include "cl" (Calendar), "mail" (GMail), or "blogger" (Blogger)
     * @return
     */
    String getServiceType();

    /*
     * Sets the name of the Google service to which we are connecting. Sample names of services might include "cl" (Calendar), "mail" (GMail), or "blogger" (Blogger)
     * @param serviceType
     */
    void setServiceType(String serviceType);
}
