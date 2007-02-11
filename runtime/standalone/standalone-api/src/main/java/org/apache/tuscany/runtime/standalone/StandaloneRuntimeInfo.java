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
package org.apache.tuscany.runtime.standalone;

import java.io.File;
import java.net.URI;

import org.apache.tuscany.host.RuntimeInfo;

/**
 * @version $Rev$ $Date$
 */
public interface StandaloneRuntimeInfo extends RuntimeInfo {

    URI STANDALONE_COMPONENT_URI = URI.create("sca://StandaloneRuntimeInfo");

    /**
     * Return the directory where the standalone distribution was installed.
     *
     * @return the directory where the standalone distribution was installed
     */
    File getInstallDirectory();

    /**
     * Returns the name of this runtime's profile.
     *
     * @return the name of this runtime's profile
     */
    String getProfileName();

    /**
     * Returns the directory containing this runtime's profile.
     *
     * @return the directory containing this runtime's profile
     */
    File getProfileDirectory();

    /**
     * Return the value of the named property.
     *
     * @param name         the name of the property
     * @param defaultValue default value to return if the property is not defined
     * @return the value of the named property
     */
    String getProperty(String name, String defaultValue);
}
