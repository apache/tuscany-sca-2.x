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

package org.apache.tuscany.sca.something.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;

public class InstalledContribution {

    private String uri;
    private String url;
    private Contribution contribution;
    private List<Composite> defaultDeployables = new ArrayList<Composite>();
    private List<DeployedComposite> deployedComposites = new ArrayList<DeployedComposite>();
    private List<String> dependentContributionURIs;
    
    public InstalledContribution(String uri, String url, Contribution contribution, List<String> dependentContributionURIs) {
        this.uri = uri;
        this.url = url;
        this.contribution = contribution;
        this.defaultDeployables = contribution.getDeployables();
        this.dependentContributionURIs = dependentContributionURIs;
    }
    public Contribution getContribution() {
        return contribution;
    }
    public void setContribution(Contribution contribution) {
        this.contribution = contribution;
    }
    public String getURI() {
        return uri;
    }
    public String getURL() {
        return url;
    }
    public List<Composite> getDefaultDeployables() {
        return defaultDeployables;
    }
    public List<DeployedComposite> getDeployedComposites() {
        return deployedComposites;
    }
    public List<String> getDependentContributionURIs() {
        return dependentContributionURIs;
    }
}
