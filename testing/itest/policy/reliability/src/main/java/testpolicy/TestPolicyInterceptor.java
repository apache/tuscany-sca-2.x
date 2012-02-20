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
package testpolicy;

import helloworld.StatusImpl;

import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.core.invocation.InterceptorAsyncImpl;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.PhasedInterceptor;
import org.apache.tuscany.sca.policy.PolicySubject;


public class TestPolicyInterceptor extends InterceptorAsyncImpl implements PhasedInterceptor {
    
    private Operation operation;
    private List<TestPolicy> policies;
    private PolicySubject subject;
    private String context;
    private String phase;

    public TestPolicyInterceptor(PolicySubject subject,
                                       String context,
                                       Operation operation,
                                       List<TestPolicy> policies,
                                       String phase) {
        super();
        this.operation = operation;
        this.policies = policies;
        this.subject = subject;
        this.phase = phase;
        this.context = getContext();
    }

    private String getContext() {
        if (subject instanceof Endpoint) {
            Endpoint endpoint = (Endpoint)subject;
            return endpoint.getURI();
        } else if (subject instanceof EndpointReference) {
            EndpointReference endpointReference = (EndpointReference)subject;
            return endpointReference.getURI();
        } else if (subject instanceof Component) {
            Component component = (Component)subject;
            return component.getURI();
        }
        return null;
    }

    public Message processRequest(Message msg) {
        String policyStrings = "";
        for (TestPolicy testPolicy : policies){
            policyStrings += testPolicy.getTestString() + " ";
        }
        StatusImpl.appendStatus("TestPolicyInterceptor.processRequest()", context + " @ " + phase + " testPolicyStringes " + policyStrings);
        return msg;
    }
    
    public Message postProcessRequest(Message msg, Throwable e) throws Throwable {
        StatusImpl.appendStatus("TestPolicyInterceptor.postProcessRequest()", context + " @ " + phase);
        return msg;
    } 
    
    public Message processResponse(Message msg) {
        String policyStrings = "";
        for (TestPolicy testPolicy : policies){
            policyStrings += testPolicy.getTestString() + " ";
        }
        StatusImpl.appendStatus("TestPolicyInterceptor.processResponse()", context + " @ " + phase + " testPolicyStringes " + policyStrings);
        return msg;
    }
/* not in interface - should we add it?   
    public Message postProcessResponse(Message msg) {
        System.out.println("TestPolicyInterceptor.postProcessResponse() " + context + " @ " + phase);
        return msg;
    }
*/

    public String getPhase() {
        return phase;
    }

}
