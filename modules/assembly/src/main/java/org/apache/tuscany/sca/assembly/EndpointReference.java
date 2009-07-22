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

import java.io.Serializable;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.policy.PolicySubject;

/**
 * Represents an endpoint reference. An SCA reference can reference service endpoints in a
 * number of ways. Target names, autowire, configured bindings. The endpoint reference
 * captures the result of specifying one of these things.
 *
 * @version $Rev$ $Date$
 */
public interface EndpointReference extends Base, PolicySubject, Cloneable, Serializable {

    public static final int NOT_CONFIGURED = 0;
    public static final int RESOLVED_BINDING = 1;
    public static final int WIRED_TARGET_NOT_FOUND = 2;
    public static final int WIRED_TARGET_FOUND_READY_FOR_MATCHING = 3;
    public static final int WIRED_TARGET_FOUND_AND_MATCHED = 4;

    /**
     * Get the structural URI of the reference binding
     * @return The structural URI of the reference/binding
     */
    String getURI();

    /**
     * Set the structural URI of the reference binding
     * @param uri &lt;componentURI&gt;#reference-binding(referenceName/bindingName)
     * or &lt;componentURI&gt;#reference(referenceName) if binding is not present
     *
     */
    void setURI(String uri);

    /**
     * Supports endpoint reference cloning
     *
     * @return endpointReference
     * @throws CloneNotSupportedException
     */
    Object clone() throws CloneNotSupportedException;

    /**
     * Get the component model object
     *
     * @return component
     */
    Component getComponent();

    /**
     * Set the  component model object
     *
     * @param component the component for the endpoint
     */
    void setComponent(Component component);

    /**
     * Get the source component reference model object
     *
     * @return reference the source component reference  for the endpoint
     */
    ComponentReference getReference();

    /**
     * Set the source component reference model object
     *
     * @param reference
     */
    void setReference(ComponentReference reference);

    /**
     * Get the resolved reference binding
     *
     * @return binding the resolved reference binding
     */
    Binding getBinding();

    /**
     * Set the resolved reference binding
     *
     * @param binding the resolved reference binding
     */
    void setBinding(Binding binding);

    /**
     * Get the target endpoint
     *
     * @return endpoint the target endpoint
     */
    Endpoint getTargetEndpoint();

    /**
     * Set the target endpoint model object
     *
     * @param endpoint the target endpoint
     */
    void setTargetEndpoint(Endpoint targetEndpoint);

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
     * Get the reference callback endpoint that
     * represents that target endpoint to which callback
     * messages will be directed
     *
     * @return callbackEndpoint the reference callback endpoint
     */
    Endpoint getCallbackEndpoint();

    /**
     * Set the reference callback endpoint
     *
     * @param callbackEndpoint the reference callback endpoint
     */
    void setCallbackEndpoint(Endpoint callbackEndpoint);

    /**
     * Returns true if this endpoint reference refers to an endpoint that 
     * is not running in this endpoint reference
     * 
     * @return true if the endpoint is remote
     */
    boolean isRemote();

    /**
     * Set true if this endpoint reference refers to an endpoint that 
     * is not running in this endpoint reference
     * 
     * @param remote set to true if the endpoint is remote
     */
    void setRemote(boolean remote);

    /**
     * Set the extension point registry against the endpoint after it is deserialized as
     * the registry needs to be re-attached
     *
     * @param registry
     */
    void setExtensionPointRegistry(ExtensionPointRegistry registry);

    /**
     * Rather than relying on combinations of unresolved flags and 
     * other data we maintain a status enumeration
     * 
     * @return status
     */
    int getStatus();

    /**
     * Rather than relying on combinations of unresolved flags and 
     * other data we maintain a status enumeration
     * 
     * @param status the new status
     */
    void setStatus(int status);
}
