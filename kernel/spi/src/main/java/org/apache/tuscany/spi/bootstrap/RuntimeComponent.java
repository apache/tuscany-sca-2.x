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
package org.apache.tuscany.spi.bootstrap;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.Deployer;

/**
 * Interface that represents the Tuscany runtime.
 *
 * @version $Rev$ $Date$
 */
public interface RuntimeComponent extends CompositeComponent {
    /**
     * Returns the component that forms the root of the user component tree. All user components will be managed by
     * composites that are children of this root.
     *
     * @return the root of the user component tree
     */
    CompositeComponent getRootComponent();

    /**
     * Returns the component that forms the root of the system component tree. All system components, components that
     * provide system services needed by the Tuscany runtime itself, will be managed by composites that are children of
     * this root.
     *
     * @return the root of the system component tree
     */
    CompositeComponent getSystemComponent();

    /**
     * Returns the deployer for this runtime. This interface can be used to deploy new SCA bundles to the runtime.
     *
     * @return the deployer for this runtime
     */
    Deployer getDeployer();
}
