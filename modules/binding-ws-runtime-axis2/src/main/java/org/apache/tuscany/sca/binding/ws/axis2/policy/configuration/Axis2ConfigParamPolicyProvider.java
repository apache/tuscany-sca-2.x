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
import org.apache.tuscany.sca.policy.PolicySubject;
import org.apache.tuscany.sca.provider.BasePolicyProvider;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * @version $Rev$ $Date$
 */
public class Axis2ConfigParamPolicyProvider extends BasePolicyProvider<Axis2ConfigParamPolicy> {

    public Axis2ConfigParamPolicyProvider(PolicySubject subject) {
        super(Axis2ConfigParamPolicy.class, subject);
    }

    public void configureBinding(ConfigurationContext configurationContext) {
        Axis2ConfigParamPolicy axis2ConfigParamPolicy = null;
        Parameter configParam = null;
        for (Object policy : findPolicies()) {
            if (policy instanceof Axis2ConfigParamPolicy) {
                axis2ConfigParamPolicy = (Axis2ConfigParamPolicy)policy;
                for (String paramName : axis2ConfigParamPolicy.getParamElements().keySet()) {
                    configParam =
                        new Parameter(paramName, axis2ConfigParamPolicy.getParamElements().get(paramName)
                            .getFirstElement());
                    configParam.setParameterElement(axis2ConfigParamPolicy.getParamElements().get(paramName));
                    try {
                        configurationContext.getAxisConfiguration().addParameter(configParam);
                    } catch (AxisFault e) {
                        throw new ServiceRuntimeException(e);
                    }
                }
            }
        }        
    }
}
