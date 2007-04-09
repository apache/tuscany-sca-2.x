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
package org.apache.tuscany.host.deployment;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Service interface for managing the logical assembly for a Tuscany runtime.
 *
 * @version $Rev$ $Date$
 */
public interface AssemblyService {
    /**
     * Apply a set of changes to the SCA Domain's logical assembly.
     *
     * @param changeSet the location of a resource containing a set of changes
     * @throws DeploymentException if there was a problem making the changes
     * @throws IOException         if there was a problem accessing the resource
     */
    void applyChanges(URL changeSet) throws DeploymentException, IOException;

    /**
     * Apply a set of changes to the SCA Domain's logical assembly.
     *
     * @param changeSet   a stream for reading a resource containing a set of changes; the stream will not be closed
     *                    but no guarantee is made on the position the stream is left in
     * @param contentType the type of changeSet on the stream; must be a valid Content-Type value
     *                    as specified by <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC2045</a>
     *                    and must not be null
     * @throws DeploymentException if there was a problem making the changes
     * @throws IOException         if there was a problem reading the stream
     */
    void applyChanges(InputStream changeSet, String contentType) throws DeploymentException, IOException;
    
    /**
     * Includes the SCDL in the logical domain.
     * 
     * @param scdl SCDL to be included.
     * @throws DeploymentException If unable to deploy.
     */
    void include(InputStream scdl) throws DeploymentException;
}
