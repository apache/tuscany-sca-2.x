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
package org.apache.tuscany.core.runtime;

import org.apache.tuscany.contribution.Contribution;
import org.apache.tuscany.host.RuntimeInfo;
import org.apache.tuscany.host.runtime.ShutdownException;
import org.osoa.sca.ComponentContext;

/**
 * @version $Rev$ $Date$
 */
public interface RuntimeActivator<I extends RuntimeInfo> {
    /**
     * Initialize a runtime.
     *
     * @throws ActivationException if there is an error initializing the runtime
     */
    void start() throws ActivationException;

    /**
     * Destroy the runtime. Any further invocations should result in an error.
     *
     * @throws ShutdownException if there is an error destroying the runtime
     */
    void stop() throws ActivationException;
    
    void start(Contribution contribution, String deployable) throws ActivationException;
    void stop(Contribution contribution) throws ActivationException;

    /**
     * Returns the ComponentContext for the designated component.
     *
     * @param componentName The name of the top-level component in the SCA domain
     * @return the ComponentContext for the designated component
     */
    ComponentContext getComponentContext(String componentName);
}
