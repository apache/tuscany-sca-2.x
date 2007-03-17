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
package org.apache.tuscany.spi.generator;

import java.net.URI;
import java.util.List;

import org.apache.tuscany.spi.model.physical.PhysicalComponentDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalWireDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalChangeSet;

/**
 * A context used during generation of physical definitions
 *
 * @version $Rev$ $Date$
 */
public interface GeneratorContext {
    /**
     * Returns the runtime id for the resource.
     *
     * @param resourceURI the resource id
     * @return the runtime id for the resource
     */
    URI getRuntimeId(URI resourceURI);

    /**
     * TODO should be changed to map runtime id by physical change set
     */
    PhysicalChangeSet getPhysicalChangeSet();

}
