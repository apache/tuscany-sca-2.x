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

package org.apache.tuscany.core.runtime;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.assembly.Binding;
import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.SCABinding;
import org.apache.tuscany.core.RuntimeWire;
import org.apache.tuscany.spi.wire.InvocationChain;

/**
 * @version $Rev$ $Date$
 */
public class RuntimeWireImpl implements RuntimeWire {
    private ComponentReference source;
    private Binding sourceBinding;
    private ComponentService target;
    private Binding targetBinding;
    private final List<InvocationChain> chains = new ArrayList<InvocationChain>();
    private final List<InvocationChain> callbackChains = new ArrayList<InvocationChain>();

    /**
     * @param source
     * @param target
     */
    public RuntimeWireImpl(ComponentReference source, ComponentService target) {
        super();
        this.source = source;
        this.target = target;
    }

    /**
     * Create a wire for a promoted reference
     * @param source
     * @param sourceBinding
     */
    public RuntimeWireImpl(ComponentReference source,
                           Binding sourceBinding) {
        this.source = source;
        this.sourceBinding = sourceBinding;
        assert !(sourceBinding instanceof SCABinding);
    }
    
    public RuntimeWireImpl(ComponentReference source,
                           Binding sourceBinding,
                           ComponentService target,
                           Binding targetBinding) {
        super();
        this.source = source;
        this.sourceBinding = sourceBinding;
        this.target = target;
        this.targetBinding = targetBinding;
    }

    public List<InvocationChain> getCallbackInvocationChains() {
        return callbackChains;
    }

    public List<InvocationChain> getInvocationChains() {
        return chains;
    }

    public ComponentReference getSource() {
        return source;
    }

    public ComponentService getTarget() {
        return target;
    }

    public Binding getSourceBinding() {
        return sourceBinding;
    }

    public void setSourceBinding(Binding sourceBinding) {
        this.sourceBinding = sourceBinding;
    }

    public Binding getTargetBinding() {
        return targetBinding;
    }

    public void setTargetBinding(Binding targetBinding) {
        this.targetBinding = targetBinding;
    }

    public boolean isOptimizable() {
        // TODO Auto-generated method stub
        return false;
    }

}
