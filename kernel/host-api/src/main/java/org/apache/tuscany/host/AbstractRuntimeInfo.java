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
     * Install directory.
     */
    private final File installDirectory;
    
    /**
     * Online indicator.
     */
    private final boolean online;

    /**
     * Initializes the runtime info instance.
     * 
     * @param domain the SCA Domain that this runtime belongs to
     * @param applicationRootDirectory Application root directory.
     * @param baseUrl Base Url.
     * @param installDirectory Install directory.
     * @param online Onlne indicator.
     */
    public AbstractRuntimeInfo(URI domain,
                               File applicationRootDirectory,
                               URL baseUrl,
                               File installDirectory,
                               boolean online) {
        this.domain = domain;
        this.applicationRootDirectory = applicationRootDirectory;
        this.baseUrl = baseUrl;
        this.installDirectory = installDirectory;
        this.online = online;
    }

    public URI getDomain() {
        return domain;
    }

    public final File getApplicationRootDirectory() {
        return applicationRootDirectory;
    }

    public final URL getBaseURL() {
        return baseUrl;
    }

    public final File getInstallDirectory() {
        return installDirectory;
    }

    public final boolean isOnline() {
        return online;
    }

}
