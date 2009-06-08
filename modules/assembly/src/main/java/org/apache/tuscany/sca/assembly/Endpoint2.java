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
import org.apache.tuscany.sca.policy.PolicySubject;

/**
 * Represents a service endpoint which is what results from having a configured 
 * binding applied to a component service. In a deployed application an endpoint
 * will relate directly to a physical endpoint, for example, a HTTP URL or a
 * JMS destination. 
 * 
 * @version $Rev$ $Date$
 */
public interface Endpoint2 extends Base, PolicySubject, Cloneable {
    
    /**
     * Supports endpoint cloning
     * 
     * @return endpoint
     * @throws CloneNotSupportedException
     */
    Object clone() throws CloneNotSupportedException;
    
    /**
     * Get the name of the component to which this endpoint refers
     * 
     * @return componentName
     */
    String getComponentName();
    
    /**
     * Set the name of the component to which this endpoint refers
     * 
     * @Param componentName
     */    
    void setComponentName(String componentName);
    
    /**
     * Get the name of the service to which this endpoint refers
     * 
     * @return serviceName
     */
    String getServiceName();
    
    /**
     * Set the name of the component to which this endpoint refers
     * 
     * @Param serviceName
     */
    void setServiceName(String serviceName);
    
    /**
     * Get the name of the binding to which this endpoint refers
     * 
     * @return bindingName
     */
    String getBindingName();
    
    /**
     * Set the name of the component to which this endpoint refers
     * 
     * @param bindingName
     */
    void setBindingName(String bindingName);
    
    /**
     * Get the component model object
     * 
     * @return component
     */
    Component getComponent();
    
    /**
     * Set the component model object
     * 
     * @param component 
     */
    void setComponent(Component component);
    
    /**
     * Get the service model object
     * 
     * @return service
     */
    ComponentService getService();
    
    /**
     * Set the service model object
     * 
     * @param service
     */
    void setService(ComponentService service);
    
    /**
     * Get the resolved target binding 
     * 
     * @return target binding
     */
    Binding getBinding();
    
    /**
     * Set the binding
     * 
     * @param  binding
     */
    void setBinding(Binding binding); 

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
    
    /**
     * Get the service's callback endpoint references that 
     * represent endpoint references from which callbacks
     * originate
     * 
     * @return callbackEndpoint the reference callback endpoint
     */
    List<EndpointReference2> getCallbackEndpointReferences();

}
