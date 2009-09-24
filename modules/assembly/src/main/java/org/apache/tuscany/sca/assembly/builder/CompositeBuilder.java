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

package org.apache.tuscany.sca.assembly.builder;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * A builder that handles the configuration of the components inside a
 * composite and the wiring of component references to component services.
 *
 * @version $Rev$ $Date$
 */
public interface CompositeBuilder {

    /**
     * Returns the ID of the builder.
     * 
     * @return An ID that identifies the builder
     */
    String getID();

    /**
     * Build a composite.
     * 
     * @param composite The composite
     * @param definitions SCA definitions
     * @param monitor
     * @return The composite built from the original one. In most cases, it is the same as the orginal one as
     * most builders only change the content of the composite. 
     * 
     * @throws CompositeBuilderException
     */
    Composite build(Composite composite, Definitions definitions, Monitor monitor) throws CompositeBuilderException;

}
