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



/**
 * An instance of a reference associated with a particular component.
 * 
 * @version $Rev$ $Date$
 */
public interface ComponentReference extends Reference {

    /**
     * Returns the reference defined by the implementation for this reference.
     * 
     * @return the implementation reference
     */
    Reference getReference();

    /**
     * Sets the reference defined by the implementation for this reference.
     * 
     * @param reference the implementation reference
     */
    void setReference(Reference reference);
    
    /**
     * Return the Boolean value of autowire
     * @return null/TRUE/FALSE
     */
    Boolean getAutowire();

    /**
     * Sets whether component references should be autowired.
     * 
     * @param autowire whether component references should be autowired
     */
    void setAutowire(Boolean autowire);


    /**
     * Returns the callback service created internally as a target endpoint
     * for callbacks to this reference.
     * 
     * @return the callback service
     */
    ComponentService getCallbackService();

    /**
     * Sets the callback service created internally as a target endpoint
     * for callbacks to this reference.
     * 
     * @param callbackService the callback service
     */
    void setCallbackService(ComponentService callbackService);
    
    /**
     * A boolean value, "false" by default, which indicates whether this component reference 
     * can have its targets overridden by a composite reference which promotes the 
     * component reference.
     * 
     *  If @nonOverridable==false, the target(s) of the promoting composite reference 
     *  replace all the targets explicitly declared on the component reference for any 
     *  value of @multiplicity on the component reference. 
     *  
     *  If the component reference has @nonOverridable==false and @multiplicity 1..1 
     *  and the reference has a target, then any composite reference which promotes 
     *  the component reference has @multiplicity 0..1.by default and MAY have an explicit @multiplicity of either 
     *  0..1 or 1..1. 
     *  
     *  If @nonOverridable==true, and the component reference has @multiplicity 0..1 or 
     *  1..1 and the component reference also declares a target, promotion implies 
     *  that the promoting composite reference has @wiredbyImpl==true and the composite 
     *  reference cannot supply a target, but can influence the policy attached to the 
     *  component reference.
     *  
     *  If @nonOverridable==true, and the component reference @multiplicity is 0..n 
     *  or 1..n, promotion targeting is additive 
     *  
     *  @return
     */
    boolean isNonOverridable();
    
    /**
     * Set the nonOverridable flag
     * @param nonOverridable
     */
    void setNonOverridable(boolean nonOverridable);
    
    /**
     * Sets whether this Component Reference is promoted
     * @param isPromoted - true if the component reference is promoted
     */
    void setPromoted( boolean isPromoted );
    boolean isPromoted();
        
}
