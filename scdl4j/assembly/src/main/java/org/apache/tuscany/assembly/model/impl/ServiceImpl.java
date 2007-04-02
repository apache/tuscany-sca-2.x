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

package org.apache.tuscany.assembly.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.assembly.model.Binding;
import org.apache.tuscany.assembly.model.Callback;
import org.apache.tuscany.assembly.model.Service;
import org.apache.tuscany.policy.model.PolicySet;

/**
 * Represents a reference.
 * 
 * @version $Rev$ $Date$
 */
public class ServiceImpl extends AbstractServiceImpl implements Service {
    private List<Binding> bindings = new ArrayList<Binding>();
    private List<PolicySet> policySets = new ArrayList<PolicySet>();
    private Callback callback;

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

    public List<PolicySet> getPolicySets() {
        return policySets;
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

}
