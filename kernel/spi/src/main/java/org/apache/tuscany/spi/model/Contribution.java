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

package org.apache.tuscany.spi.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * The representation of a deployed contribution
 */
public class Contribution extends ModelObject {
    public static final String SCA_CONTRIBUTION_META = "META-INF/sca-contribution.xml";
    public static final String SCA_CONTRIBUTION_GENERATED_META = "META-INF/sca-contribution-generated.xml";

    protected URI uri;
    protected List<String> exports = new ArrayList<String>();
    protected List<ContributionImport> imports = new ArrayList<ContributionImport>();
    protected List<QName> runnables = new ArrayList<QName>();
    
    /**
     * A list of artifacts in the contribution
     */
    protected List<DeployedArtifact> artifacts = new ArrayList<DeployedArtifact>();


    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public List<String> getExports() {
        return exports;
    }

    public List<ContributionImport> getImports() {
        return imports;
    }

    public List<QName> getRunnables() {
        return runnables;
    }

    public List<DeployedArtifact> getArtifacts() {
        return artifacts;
    }
    
    public void addArtifact(DeployedArtifact artifact) {
        artifact.setContribution(this);
        artifacts.add(artifact);
    }
}
