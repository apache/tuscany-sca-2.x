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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;


/**
 * A monitor for the watching for validation problems
 *
 * @version $Rev$ $Date$
 */
public class DefaultLoggingMonitorImpl implements Monitor {
    private final static Logger logger = Logger.getLogger(DefaultLoggingMonitorImpl.class.getName());


    /**
     * Reports a build problem.
     * 
     * @param problem
     */
    public void problem(Problem problem) {
        
        Logger problemLogger = Logger.getLogger(problem.getSourceClassName(), problem.getBundleName());
        
        if (problemLogger == null){
            logger.severe("Can't get logger " + problem.getSourceClassName()+ " with bundle " + problem.getBundleName());
        }
        
        if (problem.getSeverity() == Severity.INFO) {
            problemLogger.log(Level.INFO, problem.getMessageId(), problem.getMessageParams());
        } else if (problem.getSeverity() == Severity.WARNING) {
            problemLogger.log(Level.WARNING, problem.getMessageId(), problem.getMessageParams());
        } else if (problem.getSeverity() == Severity.ERROR) {
            if (problem.getCause() != null) {
                problemLogger.log(Level.SEVERE, problem.getMessageId(), problem.getCause());
            } else {
                problemLogger.log(Level.SEVERE, problem.getMessageId(), problem.getMessageParams());
            }
        }
    }
}
