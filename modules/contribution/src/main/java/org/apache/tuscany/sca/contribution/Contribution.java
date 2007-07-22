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

import java.util.List;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;

/**
 * The representation of a deployed contribution
 *
 * @version $Rev$ $Date$
 */
public interface Contribution extends Artifact {
    /**
     * Default location of contribution metadata in a contribution package
     */
    String SCA_CONTRIBUTION_META = "META-INF/sca-contribution.xml";
    /**
     * default location of a generated contribution metadata in a contribution package
     */
    String SCA_CONTRIBUTION_GENERATED_META = "META-INF/sca-contribution-generated.xml";
    /**
     * Default location of deployables in a contribution
     */
    String SCA_CONTRIBUTION_DEPLOYABLES = "META-INF/sca-deployables/";

    
    /**
     * Get a list of exports based on the Contribution metadata sidefile
     * 
     * @return The list of exported artifacts from this contribution
     */
    List<Export> getExports();

    /**
     * Get a list of imports based on the Contribution metadata sidefile
     * 
     * @return The list of imported artifacts on this contribution
     */
    List<Import> getImports();
    
    /**
     * Get a list of deployables for the contribution based on the contribution metadata sidefile 
     * 
     * @return The list of deployable composites
     */
    List<Composite> getDeployables();

    /**
     * Get a list of artifacts from the contribution
     * 
     * @return The list of deployed artifacts for the contribution
     */
    List<DeployedArtifact> getArtifacts();

    /**
     * Returns the model resolver for the models representing the artifacts
     * visible in the scope of this contribution.
     * 
     * @return The model resolver
     */
    ModelResolver getModelResolver();
    
    /**
     * Sets the model resolver for the models representing the artifacts
     * visible in the scope of this contribution.
     * 
     * @param modelResolver The model resolver
     */
    void setModelResolver(ModelResolver modelResolver);
    
}