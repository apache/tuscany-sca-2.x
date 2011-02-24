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
package sample;

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

/**
 * Policy interceptor that tests the locations where interceptors can be
 * added
 *
 * @version $Rev: 881959 $ $Date: 2009-11-18 22:07:09 +0000 (Wed, 18 Nov 2009) $
 */
public class TestPolicyInterceptor  extends InterceptorAsyncImpl implements PhasedInterceptor{
    //public static final QName policySetQName = new QName(JDKLoggingPolicy.SCA11_TUSCANY_NS, loggingPolicy);

    private Operation operation;
    private List<?> policies;
    private PolicySubject subject;
    private String context;
    private String phase;

    public TestPolicyInterceptor(PolicySubject subject,
                                 String context,
                                 Operation operation,
                                 List<?> policies,
                                 String phase) {
        super();
        this.operation = operation;
        this.policies = policies;
        this.subject = subject;
        this.phase = phase;
        this.context = context;
    }

    public Message processRequest(Message msg) {
        System.out.println("TestPolicyInterceptor.processRequest() " + context + " @ " + phase);
        return msg;
    }
    
    public Message processResponse(Message msg) {
        System.out.println("TestPolicyInterceptor.processResponse() " + context + " @ " + phase);
        return msg;
    }

    public String getPhase() {
        return phase;
    }
}
