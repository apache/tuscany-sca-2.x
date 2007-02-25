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
package org.apache.tuscany.spi.loader;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.Implementation;

/**
 * Loader that will load the ComponentType definition for the supplied implementation. The actual mechanism used to load
 * that definition is determined by the Client and Implementation Specification for the implementaion type. In some
 * cases the definition may be contained in a XML file related to the implementation artifact in some well defined
 * manner; other implementations may obtain this information from introspection of the artifact itself (for example, by
 * examining Java annotations).
 *
 * @version $Rev$ $Date$
 */
public interface ComponentTypeLoader<I extends Implementation> {
    /**
     * Load the component type definition for the supplied implementation.
     *
     * @param implementation the implementation whose component type information should be loaded
     * @param context        the current deployment context
     * @throws LoaderException if there was a problem loading the configuration type
     */
    void load(I implementation, DeploymentContext context) throws LoaderException;
}
