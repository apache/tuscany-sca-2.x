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
package org.apache.tuscany.core.wire;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Wire;

/**
 * Default implementation of a Wire
 *
 * @version $Rev$ $Date$
 */
public class WireImpl implements Wire {
    private URI sourceUri;
    private URI targetUri;
    private QName bindingType;
    private InterfaceContract sourceContract;
    private InterfaceContract targetContract;
    private boolean optimizable;
    private List<InvocationChain> chains = new ArrayList<InvocationChain>();
    private List<InvocationChain> callbackChains = new ArrayList<InvocationChain>();
    private AtomicComponent target;

    /**
     * Creates a wire with a local binding
     */
    public WireImpl() {
    }

    /**
     * Creates a wire with the given binding type
     *
     * @param bindingType the binding type
     */
    public WireImpl(QName bindingType) {
        this.bindingType = bindingType;
    }

    public URI getSourceUri() {
        return sourceUri;
    }

    public void setSourceUri(URI sourceUri) {
        this.sourceUri = sourceUri;
    }

    public URI getTargetUri() {
        return targetUri;
    }

    public void setTargetUri(URI targetUri) {
        this.targetUri = targetUri;
    }

    public QName getBindingType() {
        return bindingType;
    }


    public InterfaceContract getSourceContract() {
        return sourceContract;
    }

    public void setSourceContract(InterfaceContract contract) {
        this.sourceContract = contract;
    }


    public InterfaceContract getTargetContract() {
        return targetContract;
    }

    public void setTargetContract(InterfaceContract contract) {
        this.targetContract = contract;
    }

    public boolean isOptimizable() {
        return optimizable;
    }

    public void setOptimizable(boolean optimizable) {
        this.optimizable = optimizable;
    }

    public Object getTargetInstance() throws TargetResolutionException {
        if (target == null) {
            return null;
        }
        return target.getTargetInstance();
    }

    public void setTarget(AtomicComponent target) {
        this.target = target;
    }

    public List<InvocationChain> getInvocationChains() {
        return Collections.unmodifiableList(chains);
    }

    public void addInvocationChain(InvocationChain chain) {
        chains.add(chain);
    }

    public List<InvocationChain> getCallbackInvocationChains() {
        return Collections.unmodifiableList(callbackChains);
    }

    public void addCallbackInvocationChain(InvocationChain chain) {
        callbackChains.add(chain);
    }

}
