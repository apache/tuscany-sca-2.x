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

package org.apache.tuscany.sca.policy.xml;

import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.ProfileIntent;

/**
 * Processor for handling XML models of PolicyIntent definitions that are ProfileIntents
 *
 * @version $Rev$ $Date$
 */
public class ProfileIntentProcessor extends PolicyIntentProcessor<ProfileIntent> {

    public ProfileIntentProcessor(PolicyFactory policyFactory, Monitor monitor) {
        super(policyFactory, monitor);
    }
    
    public ProfileIntentProcessor(ModelFactoryExtensionPoint modelFactories, Monitor monitor) {
        super(modelFactories, monitor);
    }

    public Class<ProfileIntent> getModelType() {
        return ProfileIntent.class;
    }
}
