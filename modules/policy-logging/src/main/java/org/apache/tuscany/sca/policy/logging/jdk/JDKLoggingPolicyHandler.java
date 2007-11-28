package org.apache.tuscany.sca.policy.logging.jdk;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.xml.Constants;
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
public class JDKLoggingPolicyHandler implements PolicyHandler {
    public static final String loggingPolicy = "JDKLoggingPolicy";
    public static final QName policySetQName = new QName(Constants.SCA10_TUSCANY_NS,
                                                         loggingPolicy); 
    private Logger logger = null;
    
    private PolicySet applicablePolicySet = null;
    
    public void setUp(Object... context) {
        if ( applicablePolicySet != null ) {
            JDKLoggingPolicy policy = (JDKLoggingPolicy)applicablePolicySet.getPolicies().get(0);
            logger = Logger.getLogger(policy.getLoggerName());
            logger.setLevel(policy.getLogLevel());
            logger.setUseParentHandlers(policy.isUseParentHandlers());
            
            boolean found = false;
            for ( Handler handler : logger.getHandlers() ) {
                if ( handler instanceof ConsoleHandler ) {
                    found = true;
                    break;
                }
            }
            
            if ( !found ) {
                ConsoleHandler consoleHandler = new ConsoleHandler();
                consoleHandler.setLevel(Level.ALL);
                logger.addHandler(consoleHandler);
            }
        }
    }
    
    public void cleanUp(Object... context) {
        logger = null;
    }
    
    public void beforeInvoke(Object... context) { 
        Operation operation = null; 
        Message msg = null; 
        JDKLoggingPolicy policy = (JDKLoggingPolicy)applicablePolicySet.getPolicies().get(0);
        
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
                logger.logp(Level.INFO, "", "", " Invoking operation - " + operation.getName());
                
                StringBuffer sb = new StringBuffer();
                for ( Object param : ((Object[])msgBody) ) {
                    sb.append(param);
                    sb.append(", ");
                }
                
                Object[] logParams = new Object[]{operation.getName(), sb.toString()};
                logger.logp(Level.FINER,"","", "Inovoking operation {0} with arguments {1}", logParams);
            }
        }
    }
    
    public void afterInvoke(Object... context) {
        Operation operation = null;
        Message msg = null; 
        JDKLoggingPolicy policy = (JDKLoggingPolicy)applicablePolicySet.getPolicies().get(0);
        
        for ( Object contextParam : context ) {
            if (contextParam instanceof Operation ) {
                operation = (Operation)contextParam;
            } else if ( contextParam instanceof Message ) {
                msg = (Message)contextParam;
            }
        }
        
        if ( operation != null && msg != null ) {
            Object[] logParams = new Object[]{operation.getName(), msg.getBody()};
            
            logger.logp(Level.INFO,"", "", " Returned from operation - " + operation.getName());
            logger.logp(Level.FINER,"","", "Returning from operation {0} with return value {1}", logParams);
        }
    }

    public PolicySet getApplicablePolicySet() {
        return applicablePolicySet;
    }

    public void setApplicablePolicySet(PolicySet applicablePolicySet) {
        this.applicablePolicySet = applicablePolicySet;
    }
}
