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

package org.apache.tuscany.sca.binding.ws.axis2.policy.configuration;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.Parameter;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.provider.PolicyProviderFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.osoa.sca.ServiceRuntimeException;

/**
 * @version $Rev$ $Date$
 */
public class Axis2ConfigParamPolicyProviderFactory implements PolicyProviderFactory<Axis2ConfigParamPolicy> {

    public Axis2ConfigParamPolicyProviderFactory(ExtensionPointRegistry registry) {
        super();
    }

    /**
     * @see org.apache.tuscany.sca.provider.PolicyProviderFactory#createImplementationPolicyProvider(org.apache.tuscany.sca.runtime.RuntimeComponent, org.apache.tuscany.sca.assembly.Implementation)
     */
    public PolicyProvider createImplementationPolicyProvider(RuntimeComponent component, Implementation implementation) {
        return null;
    }

    /**
     * @see org.apache.tuscany.sca.provider.PolicyProviderFactory#createReferencePolicyProvider(org.apache.tuscany.sca.runtime.RuntimeComponent, org.apache.tuscany.sca.runtime.RuntimeComponentReference, org.apache.tuscany.sca.assembly.Binding)
     */
    public PolicyProvider createReferencePolicyProvider(RuntimeComponent component,
                                                        RuntimeComponentReference reference,
                                                        Binding binding) {
        return null;
    }

    /**
     * @see org.apache.tuscany.sca.provider.PolicyProviderFactory#createServicePolicyProvider(org.apache.tuscany.sca.runtime.RuntimeComponent, org.apache.tuscany.sca.runtime.RuntimeComponentService, org.apache.tuscany.sca.assembly.Binding)
     */
    public PolicyProvider createServicePolicyProvider(RuntimeComponent component,
                                                      RuntimeComponentService service,
                                                      Binding binding) {
        return null;
    }

    /**
     * @see org.apache.tuscany.sca.provider.ProviderFactory#getModelType()
     */
    public Class<Axis2ConfigParamPolicy> getModelType() {
        return Axis2ConfigParamPolicy.class;
    }

    // FIXME: [rfeng] I think this should be refactored into the binding.ws axis2 code
    public void setUp(ConfigurationContext configContext, PolicySet ps) {
        Axis2ConfigParamPolicy axis2ConfigParamPolicy = null;
        Parameter configParam = null;
        for (Object policy : ps.getPolicies()) {
            if (policy instanceof Axis2ConfigParamPolicy) {
                axis2ConfigParamPolicy = (Axis2ConfigParamPolicy)policy;
                for (String paramName : axis2ConfigParamPolicy.getParamElements().keySet()) {
                    configParam =
                        new Parameter(paramName, axis2ConfigParamPolicy.getParamElements().get(paramName)
                            .getFirstElement());
                    configParam.setParameterElement(axis2ConfigParamPolicy.getParamElements().get(paramName));
                    try {
                        configContext.getAxisConfiguration().addParameter(configParam);
                    } catch (AxisFault e) {
                        throw new ServiceRuntimeException(e);
                    }
                }
            }
        }
    }

}
