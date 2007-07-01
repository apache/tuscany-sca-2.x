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

package org.apache.tuscany.sca.distributed.host;

import org.apache.tuscany.sca.core.runtime.ActivationException;
import org.apache.tuscany.sca.distributed.core.DistributedSCADomainExtensionPoint;
import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * An SCA Domain for the system components that allow a node
 * in a distributed domain to be controlled. The distributed domain also
 * holds information about the node that it is running in so that the 
 * component activation process can make decisions about the appropriate
 * bindings to create between components
 * 
 * @version $Rev$ $Date$
 */
public abstract class DistributedSCADomain extends SCADomain implements DistributedSCADomainExtensionPoint{
    
    /**
     * Returns the name of the node that this part of the
     * distributed domain is running on
     * 
     * @return the node name
     */
    public abstract String getNodeName();
    
    /**
     * Returns the domain that is running the system
     * components for this node
     * 
     * @return the node domain
     */
    public abstract DistributedSCADomain getNodeDomain();
    
    /** Starts the domain operation. Usually involves starting the
     *  runtime and creating the top level composite ready for 
     *  new contributions
     *  
     * @throws ActivationException
     */
    public abstract void start() throws ActivationException;
    
    /**
     * Stops the runtime and all running components
     * 
     * @throws ActivationException
     */
    public abstract void stop() throws ActivationException;
    
}
