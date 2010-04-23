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

package org.apache.tuscany.sca.monitor;

import java.util.List;
import java.util.logging.Logger;

import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 * A monitor for the watching for validation problems
 *
 * @version $Rev$ $Date$
 * @tuscany.spi.extension.inheritfrom
 */
public abstract class Monitor {
    private static class ContextFinder extends SecurityManager {
        private final static ContextFinder instance = new ContextFinder();
        
        // This is sensitive to the calling stack
        private static Class<?> getContextClass() {
            Class[] classes = instance.getClassContext();
            // 0: ContextFinder (getClassContext)
            // 1: ContextFinder (getContextClass)
            // 2: Monitor (getSourceClassName)
            // 3: Monitor (error/warning)
            return classes[4];
        }
        
    }
    private final static Logger logger = Logger.getLogger(Monitor.class.getName());

    /**
     * A utility function for raising an error. It creates the problem and 
     * adds it to the monitor
     * 
     * @param monitor
     * @param reportingObject
     * @param messageBundle
     * @param messageId
     * @param messageParameters
     */
    public static void error (Monitor monitor, 
                              Object reportingObject,
                              String messageBundle,
                              String messageId, 
                              Object... messageParameters){
        String contextClassName = getSourceClassName(reportingObject);
        if (monitor != null) {
            Problem problem =
                monitor.createProblem(contextClassName,
                                      messageBundle,
                                      Severity.ERROR,
                                      reportingObject,
                                      messageId,
                                      messageParameters);
            monitor.problem(problem);
        } else {
            logNullMonitor(messageId, contextClassName);
        }
    }

    /**
     * A utility function for raising an error. It creates the problem and 
     * adds it to the monitor
     * 
     * @param monitor
     * @param reportingObject
     * @param messageBundle
     * @param messageId
     * @param exception
     */
    public static void error (Monitor monitor, 
                              Object reportingObject,
                              String messageBundle,
                              String messageId, 
                              Throwable cause){
        String contextClassName = getSourceClassName(reportingObject);
        if (monitor != null) {
            Problem problem =
                monitor.createProblem(contextClassName,
                                      messageBundle,
                                      Severity.ERROR,
                                      reportingObject,
                                      messageId,
                                      cause);
            monitor.problem(problem);
        } else {
            logNullMonitor(messageId, contextClassName);
        }
    }

    /**
     * A utility function for raising an error. It creates the problem and 
     * adds it to the monitor
     * 
     * @param monitor
     * @param reportingObject
     * @param messageBundle
     * @param messageId
     * @param exception
     */
    public static void error (Monitor monitor, 
                              Object reportingObject,
                              String messageBundle,
                              String messageId, 
                              Throwable cause,
                              Object... messageParameters) {
        String contextClassName = getSourceClassName(reportingObject);
        if (monitor != null) {
            Problem problem =
                monitor.createProblem(contextClassName,
                                      messageBundle,
                                      Severity.ERROR,
                                      reportingObject,
                                      messageId,
                                      cause,
                                      messageParameters);
            monitor.problem(problem);
        } else {
            logNullMonitor(messageId, contextClassName);
        }
    }

    private static String getSourceClassName(Object reportingObject) {
        String contextClassName = null;
        if (reportingObject != null) {
            contextClassName = reportingObject.getClass().getName();
        } else {
            contextClassName = ContextFinder.getContextClass().getName();
        }
        return contextClassName;
    }

    private static void logNullMonitor(String messageId, String contextClassName) {
        logger.warning("Attempt to report error with id " + 
                messageId + 
                " from class " + 
                contextClassName +
                " but the monitor object was null");
    }

    /**
     * A utility function for raising a warning. It creates the problem and 
     * adds it to the monitor
     * 
     * @param monitor
     * @param reportingObject
     * @param messageBundle
     * @param messageId
     * @param messageParameters
     */
    public static void warning (Monitor monitor, 
                                Object reportingObject,
                                String messageBundle,
                                String messageId, 
                                Object... messageParameters){
        String contextClassName = getSourceClassName(reportingObject);
        if (monitor != null) {
            Problem problem =
                monitor.createProblem(contextClassName,
                                      messageBundle,
                                      Severity.WARNING,
                                      reportingObject,
                                      messageId,
                                      messageParameters);
            monitor.problem(problem);
        } else {
            logNullMonitor(messageId, contextClassName);
        }
    }
    
    // =====================================================
    // TUSCANY-3132 - new approach to monitoring errors
    //           
    
    /**
     * A utility function for raising an error. It creates the problem and 
     * adds it to the monitor
     * 
     * @param monitor
     * @param reportingObject
     * @param messageBundle
     * @param messageId
     * @param exception
     */
    public static void warning (Monitor monitor, 
                              Object reportingObject,
                              String messageBundle,
                              String messageId, 
                              Throwable cause){
        String contextClassName = getSourceClassName(reportingObject);
        if (monitor != null) {
            Problem problem =
                monitor.createProblem(contextClassName,
                                      messageBundle,
                                      Severity.ERROR,
                                      reportingObject,
                                      messageId,
                                      cause);
            monitor.problem(problem);
        } else {
            logNullMonitor(messageId, contextClassName);
        }
    }
    
    /**
     * A utility function for raising an error. It creates the problem and 
     * adds it to the monitor
     * 
     * @param monitor
     * @param reportingObject
     * @param messageBundle
     * @param messageId
     * @param exception
     */
    public static void warning (Monitor monitor, 
                              Object reportingObject,
                              String messageBundle,
                              String messageId, 
                              Throwable cause,
                              Object... messageParameters) {
        String contextClassName = getSourceClassName(reportingObject);
        if (monitor != null) {
            Problem problem =
                monitor.createProblem(contextClassName,
                                      messageBundle,
                                      Severity.ERROR,
                                      reportingObject,
                                      messageId,
                                      cause,
                                      messageParameters);
            monitor.problem(problem);
        } else {
            logNullMonitor(messageId, contextClassName);
        }
    }
    
    /**
     * Create a new problem.
     *  
     * @param sourceClassName   the class name reporting the problem
     * @param bundleName        the name of the message bundle to use
     * @param severity          the severity of the problem
     * @param problemObject     the model object for which the problem is being reported
     * @param messageId         the id of the problem message
     * @param messageParams     the parameters of the problem message
     * @return
     */
    public abstract Problem createProblem(String sourceClassName,
                                   String bundleName,
                                   Severity severity,
                                   Object problemObject,
                                   String messageId,
                                   Object... messageParams);
    
    /**
     * Create a new problem.
     * 
     * @param sourceClassName   the class name reporting the problem
     * @param bundleName        the name of the message bundle to use
     * @param severity          the severity of the problem
     * @param problemObject     the model object for which the problem is being reported
     * @param messageId         the id of the problem message
     * @param cause             the exception which caused the problem
     * @return
     */
    public abstract Problem createProblem(String sourceClassName,
                                          String bundleName,
                                          Severity severity,
                                          Object problemObject,
                                          String messageId,
                                          Throwable cause);

    /**
     * Get the name of the artifact for which errors are Monitored
     * @return the name of the Artifact or null if no artifact name has been set
     */
    public abstract String getArtifactName();

    /**
     * Returns the last logged problem.
     * 
     * @return
     */
    public abstract Problem getLastProblem();
   
    /** 
     * Returns a list of reported problems. 
     * 
     * @return the list of problems. The list may be empty
     */
    public abstract List<Problem> getProblems();

    /**
     * Remove the most recent context string from the 
     * context stack
     * @return The object popped 
     */
    public abstract Object popContext();

    /**
     * Reports a build problem.
     * 
     * @param problem
     */
    public abstract void problem(Problem problem);
    
    /**
     * Add a context string to the context stack
     * @param context the context string to add
     */
    public abstract void pushContext(Object context);
    
    /**
     * Clear context and problems
     */
    public abstract void reset();    
    /**
     * Set the name of an artifact for which errors are Monitored
     * @param artifactName the artifact name
     */
    public abstract void setArtifactName(String artifactName);
    
    // =====================================================
}
