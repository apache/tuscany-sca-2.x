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

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.BindlessServiceDefinition;
import org.apache.tuscany.spi.model.ReferenceDefinition;

/**
 * Responsible for processing a service or reference in an assembly configured without any binding. The builder
 * will create and return corresponding {@link org.apache.tuscany.spi.component.Service} or {@link
 * org.apache.tuscany.spi.component.Reference}
 *
 * @version $Rev$ $Date$
 */
public interface BindlessBuilder {

    SCAObject build(CompositeComponent parent,
                    BindlessServiceDefinition serviceDefinition,
                    DeploymentContext deploymentContext);

    SCAObject build(CompositeComponent parent,
                    ReferenceDefinition referenceDefinition,
                    DeploymentContext deploymentContext);
}
