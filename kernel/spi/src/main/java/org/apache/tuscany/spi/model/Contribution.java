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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * The representation of a deployed contribution
 *
 * @version $Rev$ $Date$
 */
public class Contribution extends DeployedArtifact {
    public static final String SCA_CONTRIBUTION_META = "META-INF/sca-contribution.xml";
    public static final String SCA_CONTRIBUTION_GENERATED_META = "META-INF/sca-contribution-generated.xml";

    protected List<String> exports = new ArrayList<String>();
    protected List<ContributionImport> imports = new ArrayList<ContributionImport>();
    protected List<QName> deployables = new ArrayList<QName>();
    
    /**
     * A list of artifacts in the contribution
     */
    protected Map<URI, DeployedArtifact> artifacts = new HashMap<URI, DeployedArtifact>();

    public Contribution() {
        super();
    }

    /**
     * @param uri
     */
    public Contribution(URI uri) {
        super(uri);
        if (uri != null) {
            artifacts.put(uri, this);
        }
    }
    
    public URI getUri() {
        return uri;
    }

    public void setURI(URI uri) {
        super.setUri(uri);
        if (uri != null) {
            artifacts.put(uri, this);
        }
    }

    public List<String> getExports() {
        return exports;
    }

    public List<ContributionImport> getImports() {
        return imports;
    }

    public List<QName> getDeployables() {
        return deployables;
    }

    public Map<URI, DeployedArtifact> getArtifacts() {
        return Collections.unmodifiableMap(artifacts);
    }
    
    public void addArtifact(DeployedArtifact artifact) {
        artifact.setContribution(this);
        artifacts.put(artifact.getUri(), artifact);
    }
    
    public DeployedArtifact getArtifact(URI uri) {
        return artifacts.get(uri);
    }

    /**
     * @return the location
     */
    public URL getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(URL location) {
        this.location = location;
    }
}
