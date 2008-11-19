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

package org.apache.tuscany.sca.core;

import java.util.List;


/**
 * The extension point for the Tuscany module activator extensions.
 *
 * @version $Rev$ $Date$
 */
public interface ModuleActivatorExtensionPoint {

    /**
     * Add a module activator extension to the extension point
     * @param activator The instance of the module activator
     *
     * @throws IllegalArgumentException if activator is null
     */
    void addModuleActivator(ModuleActivator activator);

    /**
     * Returns the module activator extensions.
     * @return The module activator extensions
     */
    List<ModuleActivator> getModuleActivators();

    /**
     * Remove a module activator
     * @param activator The module activator to remove
     *
     * @throws IllegalArgumentException if activator is null
     */
    void removeModuleActivator(Object activator);
}
