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
package org.apache.tuscany.sca.distributed.assembly;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.SCABinding;


/**
 * Represents an SCA binding used in the distributed runtime.
 * 
 * @version $Rev$ $Date$
 */
public interface DistributedSCABinding extends SCABinding {

    /**
     * Gets the flag which tells you if this SCABinding is distributed across nodes
     * 
     * @return isDistributed flag
     */
    public boolean getIsDistributed(); 

    /**
     * Gets the flag which tells you if this SCABinding is distributed across nodes
     * 
     * @param isDistributed true if this binding is distributed across nodes
     */
    public void setIsDisitributed(boolean isDistributed);
    
    /**
     * Return the remote reference binding that the SCABinding deems is appropriate
     * between the provided service and reference. 
     * 
     * @param reference
     * @param service
     * @return the remote binding
     */
    public Binding getRemoteReferenceBinding(ComponentReference reference,
                                             ComponentService service);
    
    /**
     * Return the remote service binding that the SCABinding deems is appropriate
     * between the provided service and reference. 
     * 
     * @param reference
     * @param service
     * @return
     */
    public Binding getRemoteServiceBinding(ComponentReference reference,
                                           ComponentService service);    
}
