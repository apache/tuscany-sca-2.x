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
package org.apache.tuscany.spi.deployer;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tuscany.host.deployment.DeploymentException;

/**
 * Interface implemented by services that process assembly change sets.
 *
 * @version $Rev$ $Date$
 */
public interface ChangeSetHandler {
    /**
     * Returns the content type that this implementation can handle.
     *
     * @return the content type that this implementation can handle
     */
    String getContentType();

    /**
     * Apply the changes in the supplied changeSet stream to an Assembly.
     * The content on the stream must match the content type this implementation can handle.
     *
     * @param changeSet the set of changes to apply represented as the supported content type
     * @throws DeploymentException if there was a problem applying the changes
     * @throws IOException         if there was a problem reading the stream
     */
    void applyChanges(InputStream changeSet) throws DeploymentException, IOException;
}
