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
package org.apache.tuscany.core.component;

import java.net.URI;

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentRegistrationException;
import org.apache.tuscany.spi.event.RuntimeEventListener;

/**
 * Responsible for tracking and managing the component tree for a runtime instance
 *
 * @version $Rev$ $Date$
 */
public interface ComponentManager extends RuntimeEventListener {

    /**
     * Registers a component which will be managed by the runtime
     * @param component   the component 
     * @throws ComponentRegistrationException
     */
    void register(Component component) throws ComponentRegistrationException;

    void unregister(Component component) throws ComponentRegistrationException;

    Component getComponent(URI name);

}
