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
package org.apache.tuscany.sca.policy.logging.jdk;

import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.PhasedInterceptor;
import org.apache.tuscany.sca.policy.PolicySubject;

/**
 * Policy handler to handle PolicySet related to Logging with the QName
 * {http://tuscany.apache.org/xmlns/sca/1.0/impl/java}LoggingPolicy
 *
 * @version $Rev$ $Date$
 */
public class JDKLoggingPolicyInterceptor implements PhasedInterceptor {
    public static final String loggingPolicy = "JDKLoggingPolicy";
    public static final QName policySetQName = new QName(JDKLoggingPolicy.SCA11_TUSCANY_NS, loggingPolicy);
    private Logger logger = null;

    private Invoker next;
    private Operation operation;
    private List<JDKLoggingPolicy> policies;
    private PolicySubject subject;
    private String context;
    private String phase;

    public JDKLoggingPolicyInterceptor(PolicySubject subject,
                                       String context,
                                       Operation operation,
                                       List<JDKLoggingPolicy> policies,
                                       String phase) {
        super();
        this.operation = operation;
        this.policies = policies;
        this.subject = subject;
        this.phase = phase;
        this.context = getContext();
        init();
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

    private void init() {
        JDKLoggingPolicy policy = policies.get(0);
        logger = Logger.getLogger(policy.getLoggerName());
        logger.setLevel(policy.getLogLevel());
        logger.setUseParentHandlers(policy.isUseParentHandlers());

        boolean found = false;
        for (Handler handler : logger.getHandlers()) {
            if (handler instanceof ConsoleHandler) {
                found = true;
                break;
            }
        }

        if (!found) {
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            logger.addHandler(consoleHandler);
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, context, "", "Intents: {0}", subject.getRequiredIntents());
            logger.logp(Level.FINE, context, "", "PolicySets: {0}", subject.getPolicySets());
        }
    }

    public Message invoke(Message msg) {
        if (logger == null) {
            return getNext().invoke(msg);
        }
        logger.logp(Level.INFO, context, "", "Invoking operation - " + operation.getName());
        Object msgBody = msg.getBody();
        if (msgBody instanceof Object[]) {

            if (logger.isLoggable(Level.FINE)) {
                StringBuffer sb = new StringBuffer();
                if (msgBody == null) {
                    sb.append("");
                } else {
                    Object[] args = (Object[])msgBody;
                    for (int i = 0; i < args.length; i++) {
                        sb.append(args[i]);
                        if (i != args.length - 1) {
                            sb.append(", ");
                        }
                    }
                }

                Object[] logParams = new Object[] {operation.getName(), sb.toString()};
                logger.logp(Level.FINE, context, "", "Invoking operation {0} with arguments {1}", logParams);
            }
        }

        Message responseMsg = null;
        try {
            responseMsg = getNext().invoke(msg);
            return responseMsg;
        } catch (RuntimeException e) {
            logger.logp(Level.SEVERE, context, "", "Exception thrown from operation - " + operation.getName(), e);
            throw e;
        } finally {
            if (responseMsg != null) {
                logger.logp(Level.INFO, context, "", "Returned from operation - " + operation.getName());
                if (logger.isLoggable(Level.FINE)) {
                    Object[] logParams = new Object[] {operation.getName(), responseMsg.getBody()};
                    logger.logp(Level.FINE,
                                context,
                                "",
                                "Returning from operation {0} with return value {1}",
                                logParams);
                }
            }
        }
    }

    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }

    public String getPhase() {
        return phase;
    }

}
