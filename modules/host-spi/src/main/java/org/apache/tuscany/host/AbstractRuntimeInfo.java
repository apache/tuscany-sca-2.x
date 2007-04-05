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
package org.apache.tuscany.host;

import java.io.File;
import java.net.URL;
import java.net.URI;

/**
 * Abstract runtime info implementation.
 * 
 * @version $Revision$ $Date$
 */
public abstract class AbstractRuntimeInfo implements RuntimeInfo {

    /**
     * This SCA Domain this runtime belongs to.
     */
    private final URI domain;

    /**
     * Application root directory.
     */
    private final File applicationRootDirectory;
    
    /**
     * Base URL.
     */
    private final URL baseUrl;
    
    /**
     * Online indicator.
     */
    private final boolean online;
    
    /**
     * Runtime Id.
     */
    private String runtimeId;

    /**
     * Initializes the runtime info instance.
     * 
     * @param domain the SCA Domain that this runtime belongs to
     * @param applicationRootDirectory Application root directory.
     * @param baseUrl Base Url.
     * @param online Onlne indicator.
     * @param runtimeId Runtime Id.
     */
    public AbstractRuntimeInfo(final URI domain,
                               final File applicationRootDirectory,
                               final URL baseUrl,
                               final boolean online,
                               final String runtimeId) {
        this.domain = domain;
        this.applicationRootDirectory = applicationRootDirectory;
        this.baseUrl = baseUrl;
        this.online = online;
        this.runtimeId = runtimeId;
    }

    /**
     * Returns the SCA domain associated with this runtime.
     * A null domain indicates that this is a standalone runtime with a self-contained assembly.
     *
     * @return the SCA domain associated with this runtime; may be null
     */
    public URI getDomain() {
        return domain;
    }

    /**
     * Returns the unique runtime is in the SCA domain.
     *
     * @return the SCA domain associated with this runtime; may be null
     */
    public String getRuntimeId() {
        return runtimeId;
    }
    
    /**
     * Return the root directory used to resolve application file paths.
     *
     * @return the directory used to resolve application file paths.
     */
    public final File getApplicationRootDirectory() {
        return applicationRootDirectory;
    }

    /**
     * Gets the base URL for the runtime.
     *
     * @return The base URL for the runtime.
     */
    public final URL getBaseURL() {
        return baseUrl;
    }

    /**
     * Returns whether the runtime considers itself "online" or connected to the internet.
     * This can be used by services to enable access to remote resources.
     *
     * @return true if the runtime is online.
     */
    public final boolean isOnline() {
        return online;
    }

}
