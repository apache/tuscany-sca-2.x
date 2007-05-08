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

package org.apache.tuscany.provider;

import org.apache.tuscany.core.RuntimeComponent;


/**
 * A component implementation can optionally implement this interface to control
 * how a component is started ot stopped.
 * 
 * @version $Rev$ $Date$
 */
public interface ImplementationActivator {
    /**
     * This method will be invoked when a component implemented by this
     * implementation is activated.
     * 
     * @param component The component to be started
     */
    void start(RuntimeComponent component);

    /**
     * This method will be invoked when a component implemented by this
     * implementation is deactivated.
     * 
     * @param component The component to be stopped
     */
    void stop(RuntimeComponent component);
}
