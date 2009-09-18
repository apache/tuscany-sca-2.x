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

package org.apache.tuscany.sca.definitions.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.policy.BindingType;
import org.apache.tuscany.sca.policy.ImplementationType;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * Provides a concrete implementation for SCADefinitions
 *
 * @version $Rev$ $Date$
 */
public class DefinitionsImpl implements Definitions {
    private String targetNamespace = null;
    private List<Intent> intents = new CopyOnWriteArrayList<Intent>();
    private List<PolicySet> policySets = new CopyOnWriteArrayList<PolicySet>();
    private List<BindingType> bindingTypes = new CopyOnWriteArrayList<BindingType>();
    private List<ImplementationType> implementationTypes = new CopyOnWriteArrayList<ImplementationType>();
    private List<Binding> bindings = new CopyOnWriteArrayList<Binding>();

   
    public List<BindingType> getBindingTypes() {
        return bindingTypes;
    }

    public List<ImplementationType> getImplementationTypes() {
        return implementationTypes;
    }

    public List<Intent> getIntents() {
        return intents;
    }

    public List<PolicySet> getPolicySets() {
        return policySets;
    }

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public void setTargetNamespace(String ns) {
       this.targetNamespace = ns;
    }

    public List<Binding> getBindings() {
        return bindings;
    }
}
