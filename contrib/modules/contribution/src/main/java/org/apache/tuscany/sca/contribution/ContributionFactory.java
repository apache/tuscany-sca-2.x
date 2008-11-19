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

package org.apache.tuscany.sca.contribution;


/**
 * A factory for the contribution model.
 * 
 * @version $Rev$ $Date$
 */
public interface ContributionFactory {
    
    /**
     * Create a contribution model object
     * 
     * @return The new contribution model object
     */
    Contribution createContribution();
        
    /**
     * Create a contribution metadata model object
     * 
     * @return The new contribution metadata model object
     */
    ContributionMetadata createContributionMetadata();
        
    /**
     * Create a deployedArtifact model object
     * 
     * @return The new deployedArtifact model object
     */
    @Deprecated
    DeployedArtifact createDeployedArtifact();   

    /**
     * Create an artifact model object
     * 
     * @return The new artifact model object
     */
    Artifact createArtifact();

    /**
     * Create a default import model object.
     * 
     * @return the new default import model object
     */
    DefaultImport createDefaultImport();

    /**
     * Create a default export model object.
     * 
     * @return the new default export model object
     */
    DefaultExport createDefaultExport();
    
}