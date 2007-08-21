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

package org.apache.tuscany.sca.runtime;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.provider.ImplementationProvider;

/**
 * The runtime component interface. Provides the bridge between the
 * assembly model representation of a component and its runtime 
 * realization.
 * 
 * @version $Rev$ $Date$
 */
public interface RuntimeComponent extends Component {
    /**
     * Set the implementation-specific configuration for this component
     * @param implementationProvider The object that manages the component implementation
     */
    void setImplementationProvider(ImplementationProvider implementationProvider);

    /**
     * Get the implementation-specific configuation for this component
     * @return The implementation provider for this component
     */
    ImplementationProvider getImplementationProvider();

    /**
     * Get the associated component context
     * @return
     */
    RuntimeComponentContext getComponentContext();

    /**
     * Set the associated component context
     * @param context
     */
    void setComponentContext(RuntimeComponentContext context);

    /**
     * Tests if the RuntimeComponent is started
     * @return true if the RuntimeComponent is started otherwise false
     */
    boolean isStarted();

    /**
     * Sets the RuntimeComponent started state
     * @param the state to set
     */
    void setStarted(boolean started);
}
