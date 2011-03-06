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

package org.apache.tuscany.sca.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.runtime.ActivationException;

public class InstalledContribution {

    private String uri;
    private String url;
    private Contribution contribution;
    private List<Composite> defaultDeployables = new ArrayList<Composite>();
    private Map<String, DeployedComposite> startedComposites = new HashMap<String, DeployedComposite>();
    private Map<String, DeployedComposite> stoppedComposites = new HashMap<String, DeployedComposite>();
    private List<String> dependentContributionURIs;
    
    public InstalledContribution(String uri, String url, Contribution contribution, List<String> dependentContributionURIs) {
        this.uri = uri;
        this.url = url;
        this.contribution = contribution;
        this.defaultDeployables = new ArrayList<Composite>(contribution.getDeployables());
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
    public List<String> getStartedCompositeURIs() {
        return new ArrayList<String>(startedComposites.keySet());
    }
    public List<String> getDependentContributionURIs() {
        return dependentContributionURIs;
    }

    public void start(DeployedComposite composite) throws ActivationException {
        composite.start();
        startedComposites.put(composite.getURI(), composite);
    }
    
    public void stop(String compositeURI) throws ActivationException {
        DeployedComposite dc = startedComposites.remove(compositeURI);
        if (dc == null) {
            throw new IllegalStateException("composite not deployed: " + compositeURI);
        }
        dc.stop();
        stoppedComposites.put(compositeURI, dc);
    }
    
    public boolean restart(String compositeURI) throws ActivationException {
        DeployedComposite dc = stoppedComposites.remove(compositeURI);
        if (dc != null) {
            dc.start();
            startedComposites.put(compositeURI, dc);
        }
        return dc != null;
    }
}
