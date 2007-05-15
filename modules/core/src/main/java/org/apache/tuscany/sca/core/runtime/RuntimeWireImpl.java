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

package org.apache.tuscany.sca.core.runtime;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.core.EndpointReference;
import org.apache.tuscany.sca.core.RuntimeComponentReference;
import org.apache.tuscany.sca.core.RuntimeComponentService;
import org.apache.tuscany.sca.core.RuntimeWire;
import org.apache.tuscany.sca.invocation.InvocationChain;

/**
 * @version $Rev$ $Date$
 */
public class RuntimeWireImpl implements RuntimeWire {
    private EndpointReference<RuntimeComponentReference> wireSource;
    private EndpointReference<RuntimeComponentService> wireTarget;

    private final List<InvocationChain> chains = new ArrayList<InvocationChain>();
    private final List<InvocationChain> callbackChains = new ArrayList<InvocationChain>();

    /**
     * @param source
     * @param target
     */
    public RuntimeWireImpl(EndpointReference<RuntimeComponentReference> source,
                           EndpointReference<RuntimeComponentService> target) {
        super();
        this.wireSource = source;
        this.wireTarget = target;
    }

    public List<InvocationChain> getCallbackInvocationChains() {
        return callbackChains;
    }

    public List<InvocationChain> getInvocationChains() {
        return chains;
    }

    public EndpointReference<RuntimeComponentReference> getSource() {
        return wireSource;
    }

    public EndpointReference<RuntimeComponentService>  getTarget() {
        return wireTarget;
    }

}
