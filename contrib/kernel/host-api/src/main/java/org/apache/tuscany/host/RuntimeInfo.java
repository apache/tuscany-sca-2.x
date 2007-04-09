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
import java.net.URI;
import java.net.URL;

/**
 * Interface that provides information on the runtime environment.
 *
 * @version $Rev$ $Date$
 */
public interface RuntimeInfo {

    /**
     * Returns the SCA domain associated with this runtime.
     * A null domain indicates that this is a standalone runtime with a self-contained assembly.
     *
     * @return the SCA domain associated with this runtime; may be null
     */
    URI getDomain();

    /**
     * Returns the unique runtime is in the SCA domain.
     *
     * @return the SCA domain associated with this runtime; may be null
     */
    String getRuntimeId();
    
    /**
     * Return the root directory used to resolve application file paths.
     *
     * @return the directory used to resolve application file paths.
     */
    File getApplicationRootDirectory();

    /**
     * Gets the base URL for the runtime.
     *
     * @return The base URL for the runtime.
     */
    URL getBaseURL();

    /**
     * Returns whether the runtime considers itself "online" or connected to the internet.
     * This can be used by services to enable access to remote resources.
     *
     * @return true if the runtime is online.
     */
    boolean isOnline();
}
