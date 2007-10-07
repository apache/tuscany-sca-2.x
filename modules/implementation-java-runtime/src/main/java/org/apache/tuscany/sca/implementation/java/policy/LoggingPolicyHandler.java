package org.apache.tuscany.sca.implementation.java.policy;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.contribution.util.ServiceConfigurationUtil;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.util.PolicyHandler;

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

/**
 * Policy handler to handle PolicySet related to Logging with the QName
 * {http://tuscany.apache.org/xmlns/sca/1.0/impl/java}LoggingPolicy
 */
public class LoggingPolicyHandler implements PolicyHandler {
    public static final String javaImplPolicyNamespace = "http://tuscany.apache.org/xmlns/sca/1.0/impl/java/policy";
    public static final String logginPolicy = "LoggingPolicy";
    public static final QName policySetQName = new QName(javaImplPolicyNamespace,
                                                         logginPolicy); 
    private Logger logger = null;
    
    private PolicySet handledPolicySet = null;

    public PolicySet getHandledPolicySet() {
        return handledPolicySet;
    }

    public void setHandledPolicySet(PolicySet handledPolicySet) {
        this.handledPolicySet = handledPolicySet;
    }

    public void afterInvoke(Object... context) {
        Operation operation = null;
        Message msg = null; 
        
        for ( Object contextParam : context ) {
            if (contextParam instanceof Operation ) {
                operation = (Operation)contextParam;
            } else if ( contextParam instanceof Message ) {
                msg = (Message)contextParam;
            }
        }
        
        if ( operation != null && msg != null ) {
            System.out.println(" Returning after invoking operation " + operation.getName() + " with result " +
                               msg.getBody());
            
        }
    }

    public void beforeInvoke(Object... context) { 
        Operation operation = null; 
        Message msg = null; 
        
        for ( Object contextParam : context ) {
            if (contextParam instanceof Operation ) {
                operation = (Operation)contextParam;
            } else if ( contextParam instanceof Message ) {
                msg = (Message)contextParam;
            }
        }
        
        if ( operation != null && msg != null ) {
            Object msgBody = msg.getBody();
            if ( msgBody instanceof Object[] ) { 
                System.out.println(" About to invoke operations " + operation.getName() + " with arguments... ");
                for ( Object o : ((Object[])msg.getBody())) {
                    System.out.println(o);
                }
            } else {
                System.out.println(" About to invoke operation " + operation.getName() + " with arguments " +
                                   msg.getBody());
            }
        }
    }

    public void cleanUp(Object... context) {
        logger = null;
    }

    public void setUp(Object... context) {
        for ( Object contextParam : context ) {
            if ( contextParam instanceof JavaImplementation ) {
                JavaImplementation impl = (JavaImplementation)contextParam;
                logger = Logger.getLogger(impl.getJavaClass().getName()); 
                logger.addHandler(new ConsoleHandler());
                logger.setLevel(Level.ALL);
            }
        }
    }
}
