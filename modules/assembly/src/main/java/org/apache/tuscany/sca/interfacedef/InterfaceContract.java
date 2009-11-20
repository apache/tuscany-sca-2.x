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
package org.apache.tuscany.sca.interfacedef;


/**
 * Interface contracts define one or more business functions. These business
 * functions are provided by services and are used by references.
 * 
 * @version $Rev$ $Date$
 */
public interface InterfaceContract extends Cloneable {

    /**
     * Returns the interface definition representing the interface for
     * invocations from the requestor to the provider.
     * 
     * @return the interface definition representing the interface for
     *         invocations from the requestor to the provider
     */
    Interface getInterface();

    /**
     * Sets the interface definition representing the interface for invocations
     * from the requestor to the provider.
     * 
     * @param callInterface the interface definition representing the interface
     *            for invocations from the requestor to the provider
     */
    void setInterface(Interface callInterface);

    /**
     * Returns the interface definition representing the interface for
     * invocations from the provider to the requestor.
     * 
     * @return the interface definition representing the interface for
     *         invocations from the provider to the requestor.
     */
    Interface getCallbackInterface();

    /**
     * Sets the interface definition representing the interface for invocations
     * from the provider to the requestor.
     * 
     * @param callbackInterface the interface definition representing the
     *            interface for invocations from the provider to the requestor.
     */
    void setCallbackInterface(Interface callbackInterface);

    // FIXME: We need a better way to do this
    /**
     * Convert an interface contract to a unidirectional interface contract
     *  
     * @param isCallback true for a callback interface contract, false for
     *        a forward interface contract
     * @return A unidirectional interface contract, cloned if necessary 
     */
    InterfaceContract makeUnidirectional(boolean isCallback);

    /**
     * Implementations must support cloning.
     */
    Object clone() throws CloneNotSupportedException;

}
