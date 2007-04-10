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
package org.apache.tuscany.assembly;

import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.policy.IntentAttachPoint;

/**
 * Interface contracts define one or more business functions. These business
 * functions are provided by services and are used by references.
 * 
 * @version $Rev$ $Date$
 */
public interface AbstractContract extends Base, IntentAttachPoint {

    /**
     * Returns the name of the contract.
     * 
     * @return the name of the contract
     */
    String getName();

    /**
     * Sets the name of the contract.
     * 
     * @param name the name of the contract
     */
    void setName(String name);

    /**
     * Returns the interface contract defining the interface and callback
     * interface for the contract.
     * 
     * @return the interface contract
     */
    InterfaceContract getInterfaceContract();
    

    /**
     * Returns the interface contract defining the interface and callback
     * interface for the contract.
     * 
     * @param interfaceContract the interface contract
     */
    void setInterfaceContract(InterfaceContract interfaceContract);
}
