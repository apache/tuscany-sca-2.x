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

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.invocation.InvocationChain;

/**
 * The runtime wire that connects a component reference to a component service
 * (or an external service) over the selected binding
 * 
 * @version $Rev$ $Date$
 */
public interface RuntimeWire {

    /**
     * Get the source of the wire
     * @return
     */
    Source getSource();

    /**
     * Get the target of the wire
     * @return
     */
    Target getTarget();

    /**
     * Returns the invocation chains for service operations associated with the
     * wire
     * 
     * @return the invocation chains for service operations associated with the
     *         wire
     */
    List<InvocationChain> getInvocationChains();

    /**
     * Returns the invocation chains for callback service operations associated
     * with the wire
     * 
     * @return the invocation chains for callback service operations associated
     *         with the wire
     */
    List<InvocationChain> getCallbackInvocationChains();
    
    /**
     * The source of a runtime wire
     */
    public interface Source {
        /**
         * Get the source component
         * @return The source component, null of the source is external
         */
        RuntimeComponent getComponent();

        /**
         * Get the source component reference
         * @return The source component reference, null if the source is external
         */
        RuntimeComponentReference getComponentReference();

        /**
         * Get the source binding
         * @return The source binding
         */
        Binding getBinding();

        /**
         * Get the source interface contract
         * @return The source interface contract
         */
        InterfaceContract getInterfaceContract();
    }
    
    /**
     * The target of a runtime wire
     */
    public interface Target {
        /**
         * Get the target component
         * @return The target component, null if the target is an external service
         */
        RuntimeComponent getComponent();

        /**
         * Get the target component service
         * @return The target component service, null if the target is an external service
         */
        RuntimeComponentService getComponentService();

        /**
         * Get the target binding
         * @return The target binding
         */
        Binding getBinding();

        /**
         * Get the target interface contract
         * @return The target interface contract
         */
        InterfaceContract getInterfaceContract();
    }    

}
