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

package org.apache.tuscany.sca.assembly.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Callback;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;

/**
 * Represents a reference.
 * 
 * @version $Rev$ $Date$
 */
public class ReferenceImpl extends AbstractReferenceImpl implements Reference, Cloneable {
    private List<Binding> bindings = new ArrayList<Binding>();
    private boolean wiredByImpl;
    private List<ComponentService> targets = new ArrayList<ComponentService>();
    private Callback callback;
    private boolean promotionOverride;
    private List<EndpointReference> endpointReferences = new ArrayList<EndpointReference>();

    /**
     * Constructs a new reference.
     */
    protected ReferenceImpl() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ReferenceImpl clone = (ReferenceImpl)super.clone();
        clone.bindings = new ArrayList<Binding>(bindings);
        clone.targets = new ArrayList<ComponentService>(targets);
        // clone the endpoint references themselves and set the reference pointer back to 
        // this new refrence
        clone.endpointReferences = new ArrayList<EndpointReference>();
        
        for (EndpointReference epr : endpointReferences){
            EndpointReference eprClone = (EndpointReference)epr.clone();
            eprClone.setReference((ComponentReference)clone);
            clone.endpointReferences.add(eprClone);
        }
        return clone;
    }

    public List<Binding> getBindings() {
        return bindings;
    }

    public <B> B getBinding(Class<B> bindingClass) {
        for (Binding binding : bindings) {
            if (bindingClass.isInstance(binding)) {
                return bindingClass.cast(binding);
            }
        }
        return null;
    }

    public <B> B getCallbackBinding(Class<B> bindingClass) {
        if (callback != null) {
            for (Binding binding : callback.getBindings()) {
                if (bindingClass.isInstance(binding)) {
                    return bindingClass.cast(binding);
                }
            }
        }
        return null;
    }

    public boolean isWiredByImpl() {
        return wiredByImpl;
    }

    public void setWiredByImpl(boolean wiredByImpl) {
        this.wiredByImpl = wiredByImpl;
    }

    public boolean isPromotionOverride() {
        return promotionOverride;
    }

    public void setPromotionOverride(boolean promotionOverride) {
        this.promotionOverride = promotionOverride;
    }

    public List<ComponentService> getTargets() {
        return targets;
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    /**
     * By default return the interface contract for the reference
     */
    public InterfaceContract getInterfaceContract(Binding binding) {
        return getInterfaceContract();
    }

    public List<EndpointReference> getEndpointReferences() {
        return endpointReferences;
    }

    public String toString() {
        return getName();
    }
}
