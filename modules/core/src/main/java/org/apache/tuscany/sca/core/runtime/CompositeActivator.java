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

package org.apache.tuscany.sca.core.runtime;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;

/**
 * Start/stop a composite
 * 
 * @version $Rev$ $Date$
 */
public interface CompositeActivator {

    /**
     * Activate a composite
     * @param composite
     */
    void activate(Composite composite) throws ActivationException;

    /**
     * Stop a composite
     * @param composite
     */
    void deactivate(Composite composite) throws ActivationException;

    /**
     * Start a composite
     * @deprecated
     * @param composite
     */
    void start(Composite composite) throws ActivationException;

    /**
     * Stop a composite
     * @deprecated
     * @param composite
     */
    void stop(Composite composite) throws ActivationException;

    /**
     * Start a component
     * @param component
     */
    void start(Component component) throws ActivationException;

    /**
     * Stop a composite
     * @param composite
     */
    void stop(Component component) throws ActivationException;

}
