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

import org.apache.tuscany.sca.assembly.Base;


/**
 * Represents an artifact in an SCA contribution.
 *
 * @version $Rev$ $Date$
 * @tuscany.spi.extension.asclient
 */
public interface Artifact extends Base {

    /**
     * Returns the URI that unique identifies the artifact inside the contribution.
     *
     * @return The artifact URI
     */
    String getURI();

    /**
     * Sets the URI that uniquely identifies the artifact inside the contribution.
     *
     * @param uri The artifact URI
     */
    void setURI(String uri);

    /**
     * Returns the location of the artifact.
     *
     * @return The artifact location
     */
    String getLocation();

    /**
     * Set the location of the artifact.
     *
     * @param location The artifact location
     */
    void setLocation(String location);


    /**
     * Returns the in-memory model representing the artifact.
     *
     * @return The model object
     */
    <T> T getModel();

    /**
     * Sets the in-memory model representing the artifact.
     *
     * @param model The model object
     */
    void setModel(Object model);
}
