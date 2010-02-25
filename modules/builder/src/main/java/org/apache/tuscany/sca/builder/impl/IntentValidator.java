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

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.Messages;
import org.apache.tuscany.sca.assembly.builder.PolicyBuilder;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySubject;

/**
 * Intent Validator
 */
public class IntentValidator implements PolicyBuilder {

    /**
     * Defaut constructor
     * @param registry Extension Registry
     */
    public IntentValidator(ExtensionPointRegistry registry) {
        super();
    }

    public boolean build(Endpoint endpoint, BuilderContext context) {
        return checkMutualExclusion(endpoint, context);
    }

    public boolean build(EndpointReference endpointReference, BuilderContext context) {
        boolean ok = checkMutualExclusion(endpointReference, context);
        if(!ok) {
            return false;
        }

        Endpoint endpoint = endpointReference.getTargetEndpoint();
        if (endpoint == null) {
            return true;
        }
        ok = checkMutualExclusion(endpointReference, endpoint, context);
        List<Intent> intentList1 = endpointReference.getRequiredIntents();
        List<Intent> intentList2 = endpoint.getRequiredIntents();
        return ok;
    }

    public boolean build(Component component, Implementation implementation, BuilderContext context) {
        return true;
    }
    
    public boolean build(EndpointReference endpointReference, Endpoint endpoint, BuilderContext context) {
        return true;
    }    

    public QName getPolicyType() {
        return null;
    }
    
    public List<QName> getSupportedBindings() {
        return null;
    }    

    /**
     * Check if two policy subjects requires multually exclusive intents
     * @param subject1
     * @param subject2
     * @param monitor 
     * @return
     */
    private boolean checkMutualExclusion(PolicySubject subject1, PolicySubject subject2, BuilderContext context) {
        if (subject1 == subject2 || subject1 == null || subject2 == null) {
            return false;
        }
        for (Intent i1 : subject1.getRequiredIntents()) {
            for (Intent i2 : subject2.getRequiredIntents()) {
                if (i1.getExcludedIntents().contains(i2) || i2.getExcludedIntents().contains(i1)) {
                    Monitor.error(context.getMonitor(),
                                  this,
                                  Messages.ASSEMBLY_VALIDATION,
                                  "MutuallyExclusiveIntents",
                                  new Object[] {subject1, subject2},
                                  i1,
                                  i2);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkMutualExclusion(PolicySubject subject, BuilderContext context) {
        if (subject == null) {
            return false;
        }
        List<Intent> intents = subject.getRequiredIntents();
        int size = intents.size();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                Intent i1 = intents.get(i);
                Intent i2 = intents.get(j);
                if (i1 != i2 && i1.getExcludedIntents().contains(i2) || i2.getExcludedIntents().contains(i1)) {
                    Monitor.error(context.getMonitor(),
                                  this,
                                  Messages.ASSEMBLY_VALIDATION,
                                  "MutuallyExclusiveIntents",
                                  subject,
                                  i1,
                                  i2);
                    return true;
                }
            }
        }
        return false;
    }
}
