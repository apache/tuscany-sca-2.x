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
package org.apache.tuscany.sca.assembly;

import java.util.List;

import org.apache.tuscany.sca.interfacedef.InterfaceContract;

/**
 * Represents an endpoint (primarily a combination of a target service name and a set of
 * candidate bindings)
 * 
 * @version $Rev$ $Date$
 */
public interface Endpoint extends Base, Cloneable {
    
    /**
     * Get the name of the target service that this endpoint refers to
     * 
     * @return target service name
     */
    String getTargetName();
    
    /**
     * Set the name of the target service that this endpoint refers to
     * 
     * @param targetName
     */
    void setTargetName(String targetName);    
    
    /**
     * Get the source component model object
     * 
     * @return source component
     */
    Component getSourceComponent();
    
    /**
     * Set the source component model object
     * 
     * @param component the source component for the endpoint
     */
    void setSourceComponent(Component component);
    
    /**
     * Get the source component reference model object
     * 
     * @return reference the source component reference  for the endpoint
     */
    ComponentReference getSourceComponentReference();
    
    /**
     * Set the source component reference model object
     * 
     * @param reference
     */
    void setSourceComponentReference(ComponentReference reference);   
    
    /**
     * Get the resolved source binding 
     * 
     * @return binding the resolved source binding
     */
    Binding getSourceBinding();
    
    /**
     * Set the resolved source binding 
     * 
     * @param binding the resolved source binding
     */
    void setSourceBinding(Binding binding);

    /**
     * Get the resolved source callback binding 
     * 
     * @return binding the resolved source callback binding
     */
    Binding getSourceCallbackBinding();
    
    /**
     * Set the resolved source callback binding
     * 
     * @param binding the resolved source callback binding
     */
    void setSourceCallbackBinding(Binding binding);  
    
    /**
     * Get the list of candidate bindings that could be used to 
     * communication with the target service
     * 
     * @return list of candidate bindings
     */
    List<Binding> getCandidateBindings();
    
    
    /**
     * Get the target component model object
     * 
     * @return target component
     */
    Component getTargetComponent();
    
    /**
     * Set the target component model object
     * 
     * @param component target component
     */
    void setTargetComponent(Component component);
    
    /**
     * Get the target component service model object
     * 
     * @return target component service
     */
    ComponentService getTargetComponentService();
    
    /**
     * Set the target component service model object
     * 
     * @param service
     */
    void setTargetComponentService(ComponentService service);
    
    /**
     * Get the resolved target binding 
     * 
     * @return target binding
     */
    Binding getTargetBinding();
    
    /**
     * Set the resolved target binding
     * 
     * @param binding target binding
     */
    void setTargetBinding(Binding binding); 
    
    /**
     * Returns the interface contract defining the interface 
     * 
     * @return the interface contract
     */
    InterfaceContract getInterfaceContract();
    
    /**
     * Sets the interface contract defining the interface 
     * 
     * @param interfaceContract the interface contract
     */
    void setInterfaceContract(InterfaceContract interfaceContract);    
}
