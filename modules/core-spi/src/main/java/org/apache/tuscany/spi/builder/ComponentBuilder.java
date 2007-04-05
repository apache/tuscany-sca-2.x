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
package org.apache.tuscany.spi.builder;

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.deployer.DeploymentContext;

/**
 * Responsible for building a {@link Component} from an component definition
 * 
 * @version $Rev$ $Date$
 */
public interface ComponentBuilder {

    /**
     * Builds the component
     * 
     * @param componentDefinition the component definition
     * @param context the deployment context
     * @return the built component
     * @throws BuilderException
     */
    Component build(org.apache.tuscany.assembly.Component componentDefinition, DeploymentContext context)
        throws BuilderException;
}
