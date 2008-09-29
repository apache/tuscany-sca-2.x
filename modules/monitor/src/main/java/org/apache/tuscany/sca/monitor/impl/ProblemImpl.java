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

package org.apache.tuscany.sca.monitor.impl;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.tuscany.sca.monitor.Problem;

/**
 * Reports a composite assembly problem. 
 *
 * @version $Rev$ $Date$
 */
public class ProblemImpl implements Problem {

    private String sourceClassName;
    private String bundleName;
    private Severity severity;
    private Object problemObject;
    private String messageId;
    private Object[] messageParams;
    private Exception cause;

    /**
     * Construct a new problem
     * 
     * @param sourceClassName   the class name reporting the problem
     * @param bundleName        the name of the message bundle to use
     * @param severity          the severity of the problem
     * @param problemObject     the model object for which the problem is being reported
     * @param messageId         the id of the problem message
     * @param messageParams     the parameters of the problem message
     */
    public ProblemImpl(String sourceClassName, String bundleName, Severity severity, Object problemObject, String messageId, Object... messageParams ) {
        this.sourceClassName = sourceClassName;
        this.bundleName = bundleName;
        this.severity = severity;
        this.problemObject = problemObject;
        this.messageId = messageId;
        this.messageParams = messageParams;
    }
    
    /**
     * Construct a new problem
     * 
     * @param sourceClassName   the class name reporting the problem
     * @param bundleName        the name of the message bundle to use
     * @param severity          the severity of the problem
     * @param problemObject     the model object for which the problem is being reported
     * @param messageId         the id of the problem message
     * @param cause             the exception which caused the problem
     */
    public ProblemImpl(String sourceClassName, String bundleName, Severity severity, Object problemObject, String messageId, Exception cause) {
        this.sourceClassName = sourceClassName;
        this.bundleName = bundleName;   
        this.severity = severity;        
        this.problemObject = problemObject;
        this.messageId = messageId;
        this.cause = cause;
    }    

    public String getSourceClassName() {
        return sourceClassName;
    }
    
    public String getBundleName() {
        return bundleName;
    }
    
    public Severity getSeverity() {
        return severity;
    }
    
    public Object getProblemObject() {
        return problemObject;
    }
    
    public String getMessageId() {
        return messageId;
    }

    public Object[] getMessageParams() {
        return messageParams;
    }
    
    public Exception getCause() {
        return cause;
    }    

    @Override
    public String toString() {
        Logger logger = Logger.getLogger(sourceClassName, bundleName);
        
        LogRecord record = new LogRecord(Level.INFO, messageId);
        
        if (cause == null){
            record.setParameters(messageParams);
            
        } else {
            Object[] params = new String[1];
            params[0] = cause.toString();
            record.setParameters(params);
        }
        record.setResourceBundle(logger.getResourceBundle());
        record.setSourceClassName(sourceClassName);
 
        Formatter formatter = new SimpleFormatter();
        
        return formatter.formatMessage(record);
    }
}
