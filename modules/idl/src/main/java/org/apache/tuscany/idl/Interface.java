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
package org.apache.tuscany.idl;

import java.util.List;

/**
 * Represents a service interface. This interface will typically be extended to
 * support concrete interface type systems, such as Java interfaces, WSDL 1.1
 * portTypes and WSDL 2.0 interfaces.
 */
public interface Interface {

    /**
     * Returns true if the interface is a remotable interface..
     * 
     * @return true if the interface is a remotable interface
     */
    boolean isRemotable();

    /**
     * Sets whether the interface is a remotable or local interface.
     * 
     * @param remotable indicates whether the interface is remotable or local
     */
    void setRemotable(boolean remotable);
    
    /**
     * Test if the interface is conversational
     * @return
     */
    boolean isConversational();
    
    /**
     * Set whether the interface is conversational 
     * @param conversational
     */
    void setConversational(boolean conversational);

    /**
     * Returns the operations defined on this interface.
     * 
     * @return the operations defined on this interface
     */
    List<Operation> getOperations();

    /**
     * Returns true if the model element is unresolved.
     * 
     * @return true if the model element is unresolved.
     */
    boolean isUnresolved();

    /**
     * Sets whether the model element is unresolved.
     * 
     * @param unresolved whether the model element is unresolved
     */
    void setUnresolved(boolean unresolved);

}
