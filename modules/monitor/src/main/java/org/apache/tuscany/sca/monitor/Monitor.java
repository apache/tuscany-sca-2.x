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

import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 * A monitor for the watching for validation problems
 *
 * @version $Rev$ $Date$
 */
public interface Monitor {
    /**
     * Reports a build problem.
     * 
     * @param problem
     */
    void problem(Problem problem);

    /** 
     * Returns a list of reported problems. 
     * 
     * @return the list of problems. The list may be empty
     */
    List<Problem> getProblems();

    /**
     * Returns the last logged problem.
     * 
     * @return
     */
    public Problem getLastProblem();

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
    Problem createProblem(String sourceClassName,
                          String bundleName,
                          Severity severity,
                          Object problemObject,
                          String messageId,
                          Exception cause);

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
    Problem createProblem(String sourceClassName,
                          String bundleName,
                          Severity severity,
                          Object problemObject,
                          String messageId,
                          Object... messageParams);

    /**
     * Set the name of an artifact for which errors are Monitored
     * @param artifactName the artifact name
     */
    void setArtifactName(String artifactName);

    /**
     * Get the name of the artifact for which errors are Monitored
     * @return the name of the Artifact or null if no artifact name has been set
     */
    String getArtifactName();

}
