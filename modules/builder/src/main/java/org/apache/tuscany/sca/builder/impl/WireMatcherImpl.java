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

package org.apache.tuscany.sca.builder.impl;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.BuilderExtensionPoint;
import org.apache.tuscany.sca.assembly.builder.PolicyBuilder;
import org.apache.tuscany.sca.assembly.builder.WireBuilder;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;

/**
 * 
 */
public class WireMatcherImpl implements WireBuilder {
    private ExtensionPointRegistry registry;
    private InterfaceContractMapper interfaceContractMapper;
    private BuilderExtensionPoint builderExtensionPoint;

    /**
     * @param registry
     */
    public WireMatcherImpl(ExtensionPointRegistry registry) {
        super();
        this.registry = registry;
        this.builderExtensionPoint = registry.getExtensionPoint(BuilderExtensionPoint.class);
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        this.interfaceContractMapper = utilities.getUtility(InterfaceContractMapper.class);
    }

    public boolean build(EndpointReference endpointReference, Endpoint endpoint, BuilderContext context) {
        InterfaceContract sourceIC = endpointReference.getComponentReferenceInterfaceContract();
        InterfaceContract targetIC = endpoint.getComponentServiceInterfaceContract();
        if (!interfaceContractMapper.isCompatible(sourceIC, targetIC)) {
            return false;
        }
        for (PolicyBuilder policyBuilder : builderExtensionPoint.getPolicyBuilders()) {
            if (!policyBuilder.build(endpointReference, endpoint, context)) {
                return false;
            }
        }
        return true;
    }

}
