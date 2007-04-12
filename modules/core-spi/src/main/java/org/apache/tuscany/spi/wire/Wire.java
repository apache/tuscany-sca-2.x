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
package org.apache.tuscany.spi.wire;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.TargetResolutionException;

/**
 * The base wire type used to connect references and serviceBindings
 * 
 * @version $$Rev$$ $$Date: 2007-04-11 16:50:20 -0700 (Wed, 11 Apr
 *          2007) $$
 */
public interface Wire {
    QName LOCAL_BINDING = new QName("http://tuscany.apache.org/xmlns/sca/binding/1.0", "binding.local");

    /**
     * Returns the URI of the wire source
     * 
     * @return the wire source URI
     */
    URI getSourceUri();

    /**
     * Sets the URI of the wire source
     * 
     * @param uri the source uri
     */
    void setSourceUri(URI uri);

    /**
     * Returns the URI of the wire target
     * 
     * @return the URI of the wire target
     */
    URI getTargetUri();

    /**
     * Sets the URI of the wire target
     * 
     * @param uri the URI of the wire target
     */
    void setTargetUri(URI uri);

    /**
     * Returns the wire binding type
     * 
     * @return the wire binding type
     */
    QName getBindingType();

    /**
     * Returns the service contract associated with the the source side of the
     * wire
     * 
     * @return the service contract associated with the wire
     */
    InterfaceContract getSourceContract();

    /**
     * Sets the contract associated with the source side of the wire
     * 
     * @param contract the contract associated with the wire
     */
    void setSourceContract(InterfaceContract contract);

    /**
     * Returns the service contract associated with the the target side of the
     * wire
     * 
     * @return the service contract associated with the wire
     */
    InterfaceContract getTargetContract();

    /**
     * Sets the contract associated with the the target side of the wire
     * 
     * @param contract the contract associated with the wire
     */
    void setTargetContract(InterfaceContract contract);

    /**
     * Returns true if the wire is optimizable and its invocation chains may be
     * bypassed
     * 
     * @return true if the wire is optimizable and its invocation chains may be
     *         bypassed
     */
    boolean isOptimizable();

    /**
     * Determines if the wire may be optimized
     * 
     * @param optimizable true if the wire is optimizable
     */
    void setOptimizable(boolean optimizable);

    /**
     * Returns the non-proxied target instance for this wire
     * 
     * @return the non-proxied target instance for this wire
     */
    Object getTargetInstance() throws TargetResolutionException;

    /**
     * Sets the target for the wire for optimizations
     * 
     * @param target the target for the wire
     */
    void setTarget(AtomicComponent target);

    /**
     * Returns the invocation chains for service operations associated with the
     * wire
     * 
     * @return the invocation chains for service operations associated with the
     *         wire
     */
    List<InvocationChain> getInvocationChains();

    /**
     * Adds the invocation chain associated with the given operation
     * 
     * @param chain the invocation chain
     */
    void addInvocationChain(InvocationChain chain);

    /**
     * Returns the invocation chains for callback service operations associated
     * with the wire
     * 
     * @return the invocation chains for callback service operations associated
     *         with the wire
     */
    List<InvocationChain> getCallbackInvocationChains();

    /**
     * Adds the callback invocation chain associated with the given operation
     * 
     * @param chain the invocation chain
     */
    void addCallbackInvocationChain(InvocationChain chain);

}
