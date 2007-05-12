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

package org.apache.tuscany.sca.contribution.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.contribution.Contribution;
import org.apache.tuscany.contribution.ContributionExport;
import org.apache.tuscany.contribution.ContributionImport;
import org.apache.tuscany.contribution.DeployedArtifact;
import org.apache.tuscany.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.assembly.Composite;

/**
 * The representation of a deployed contribution
 *
 * @version $Rev: 531146 $ $Date: 2007-04-21 22:40:50 -0700 (Sat, 21 Apr 2007) $
 */
public class ContributionImpl extends ArtifactImpl implements Contribution {
    private List<ContributionExport> exports = new ArrayList<ContributionExport>();
    private List<ContributionImport> imports = new ArrayList<ContributionImport>();
    private List<Composite> deployables = new ArrayList<Composite>();
    private ModelResolver modelResolver;
    
    /**
     * A list of artifacts in the contribution
     */
    private List<DeployedArtifact> artifacts = new ArrayList<DeployedArtifact>();

    protected ContributionImpl() {
    }
    
    public List<ContributionExport> getExports() {
        return exports;
    }

    public List<ContributionImport> getImports() {
        return imports;
    }

    public List<Composite> getDeployables() {
        return deployables;
    }

    public List<DeployedArtifact> getArtifacts() {
        return artifacts;
    }

    public ModelResolver getModelResolver() {
        return modelResolver;
    }

    public void setModelResolver(ModelResolver modelResolver) {
        this.modelResolver = modelResolver;
    }
}
