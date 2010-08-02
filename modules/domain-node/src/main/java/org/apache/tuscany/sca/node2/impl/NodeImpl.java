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

package org.apache.tuscany.sca.node2.impl;

import java.io.File;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.common.java.io.IOHelper;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionMetadata;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.deployment.Deployer;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.apache.tuscany.sca.node2.Node;
import org.apache.tuscany.sca.node2.NodeFactory;
import org.apache.tuscany.sca.runtime.ActivationException;
import org.apache.tuscany.sca.runtime.CompositeActivator;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;
import org.oasisopen.sca.client.SCAClientFactory;

public class NodeImpl implements Node {

    private String domainName;
    private Deployer deployer;
    private Map<String, InstalledContribution> installedContributions = new HashMap<String, InstalledContribution>();
    private CompositeActivator compositeActivator;
    private EndpointRegistry endpointRegistry;
    private ExtensionPointRegistry extensionPointRegistry;
    private NodeFactory nodeFactory;
    
    public NodeImpl(String domainName, Deployer deployer, CompositeActivator compositeActivator, EndpointRegistry endpointRegistry, ExtensionPointRegistry extensionPointRegistry, NodeFactory nodeFactory) {
        this.domainName = domainName;
        this.deployer = deployer;
        this.compositeActivator = compositeActivator;
        this.endpointRegistry = endpointRegistry;
        this.extensionPointRegistry = extensionPointRegistry;
        this.nodeFactory = nodeFactory;
    }

    public String installContribution(String contributionURL) throws ContributionReadException, ActivationException, ValidationException {
        return installContribution(null, contributionURL, null, null, true);
    }

    public String installContribution(String uri, String contributionURL, String metaDataURL, List<String> dependentContributionURIs, boolean runDeployables) throws ContributionReadException, ActivationException, ValidationException {
        if (uri == null) {
            uri = getDefaultContributionURI(contributionURL);
        }
        Monitor monitor = deployer.createMonitor();
        Contribution contribution = deployer.loadContribution(IOHelper.createURI(uri), IOHelper.getLocationAsURL(contributionURL), monitor);
        monitor.analyzeProblems();
        if (metaDataURL != null) {
            mergeContributionMetaData(metaDataURL, contribution);
        }
        installContribution(contribution, dependentContributionURIs, runDeployables);
        return uri;
    }

    private void mergeContributionMetaData(String metaDataURL, Contribution contribution) throws ValidationException {
        ContributionMetadata metaData;
        Monitor monitor = deployer.createMonitor();
        try {
            metaData = deployer.loadXMLDocument(IOHelper.getLocationAsURL(metaDataURL), monitor);
        } catch (Exception e) {
            throw new ValidationException(e);
        }
        monitor.analyzeProblems();
        contribution.getDeployables().addAll(metaData.getDeployables());
        contribution.getImports().addAll(metaData.getImports());
        contribution.getExports().addAll(metaData.getExports());
    }
    
    public String installContribution(Contribution contribution, List<String> dependentContributionURIs, boolean runDeployables) throws ContributionReadException, ActivationException, ValidationException {
        InstalledContribution ic = new InstalledContribution(contribution.getURI(), contribution.getLocation(), contribution, dependentContributionURIs);
        installedContributions.put(contribution.getURI(), ic);
        if (runDeployables) {
            for (Composite c : ic.getDefaultDeployables()) {
                runComposite(c, ic);
            }
        } else {
            contribution.getDeployables().clear();
            
            List<Contribution> dependentContributions = calculateDependentContributions(ic);

            Monitor monitor = deployer.createMonitor();
            try {
                deployer.resolve(contribution, dependentContributions, monitor);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            monitor.analyzeProblems();
        }
        return ic.getURI();
    }

    protected List<Contribution> calculateDependentContributions(InstalledContribution ic) {
        List<Contribution> dependentContributions = new ArrayList<Contribution>();
        if (ic.getDependentContributionURIs() != null) {
            // if the install specified dependent uris use just those contributions
            for (String uri : ic.getDependentContributionURIs()) {
                InstalledContribution dependee = installedContributions.get(uri);
                if (dependee != null) {
                    dependentContributions.add(dependee.getContribution());
                }
            }
        } else {
            // otherwise use all available contributions for dependents
            for (InstalledContribution ics : installedContributions.values()) {
                dependentContributions.add(ics.getContribution());
            }
        }
        return dependentContributions;
    }

    public String addDeploymentComposite(String contributionURI, Reader compositeXML) throws ContributionReadException, XMLStreamException, ActivationException, ValidationException {
        Monitor monitor = deployer.createMonitor();
        Composite composite = deployer.loadXMLDocument(compositeXML, monitor);
        monitor.analyzeProblems();
        return addDeploymentComposite(contributionURI, composite);
    }

    public String addDeploymentComposite(String contributionURI, Composite composite) throws ActivationException, ValidationException {
        InstalledContribution ic = installedContributions.get(contributionURI);
        if (ic == null) {
            throw new IllegalArgumentException("contribution not installed: " + contributionURI);
        }
        String compositeArtifcatURI = deployer.attachDeploymentComposite(ic.getContribution(), composite, true);
        runComposite(composite, ic);
        return compositeArtifcatURI;
    }

    @Override
    public void addToDomainLevelComposite(String contributionURI, String compositeURI) throws ActivationException, ValidationException {
        InstalledContribution ic = installedContributions.get(contributionURI);
        if (ic == null) {
            throw new IllegalArgumentException("Contribution not installed: " + contributionURI);
        }
        for (Artifact a : ic.getContribution().getArtifacts()) {
            if (a.getURI().equals(compositeURI)) {
                runComposite((Composite) a.getModel(), ic);
                return;
            }
        }
        throw new IllegalArgumentException("composite not found: " + compositeURI);
    }

    @Override
    public void removeFromDomainLevelComposite(String contributionURI, String compositeURI) throws ActivationException {
        InstalledContribution ic = installedContributions.get(contributionURI);
        if (ic == null) {
            throw new IllegalArgumentException("Contribution not installed: " + contributionURI);
        }
        for (DeployedComposite dc : ic.getDeployedComposites()) {
            if (compositeURI.equals(dc.getURI())) {
                ic.getDeployedComposites().remove(dc);
                dc.unDeploy();
                return;
            }
        }
        throw new IllegalStateException("composite not deployed: " + compositeURI);
    }

    public Composite getDomainLevelComposite() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDomainLevelCompositeAsString() {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getQNameDefinition(String contributionURI, QName definition, QName symbolSpace) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<String> removeContribution(String contributionURI) throws ActivationException {
        List<String> removedContributionURIs = new ArrayList<String>();
        InstalledContribution ic = installedContributions.get(contributionURI);
        if (ic != null) {
            removedContributionURIs.add(ic.getURI());
            for (String dependent : getDependentContributions(contributionURI)) {
                removedContributionURIs.addAll(removeContribution(dependent));
            }
            installedContributions.remove(contributionURI);
            for (DeployedComposite dc : ic.getDeployedComposites()) {
                dc.unDeploy();
            }
            ic.getDeployedComposites().clear();
        }
        return removedContributionURIs;
    }

    public void updateContribution(String uri, String contributionURL) {
        // TODO Auto-generated method stub
        // is this just removeContribution/installContribution?
    }
    public void updateContribution(Contribution contribution) {
        // TODO Auto-generated method stub
    }

    public String updateDeploymentComposite(String uri, Reader compositeXML) {
        // TODO Auto-generated method stub
        // is this removeFromDomainLevelComposite/addDeploymentComposite
        return null;
    }
    public String updateDeploymentComposite(String uri, Composite composite) {
        // TODO Auto-generated method stub
        return null;
    }

    public void stop() {
        ArrayList<String> ics = new ArrayList<String>(installedContributions.keySet());
        for (String uri : ics) {
            try {
                removeContribution(uri);
            } catch (Exception e) {
                // TODO: log
                e.printStackTrace();
            }
        }
        if (nodeFactory != null) {
            nodeFactory.stop();
        }
    }

    public <T> T getService(Class<T> interfaze, String serviceURI) throws NoSuchServiceException {
        try {
            return SCAClientFactory.newInstance(URI.create(getDomainName())).getService(interfaze, serviceURI);
        } catch (NoSuchDomainException e) {
            // shouldn't ever happen as we know this is the domain so it must exist
            throw new IllegalStateException(e);
        }
    }
   
    public String getDomainName() {
        return domainName;
    }

    public List<String> getDeployedCompostes(String contributionURI) {
        ArrayList<String> compositeURIs = new ArrayList<String>();
        InstalledContribution ic = installedContributions.get(contributionURI);
        if (ic == null) {
            throw new IllegalArgumentException("no contribution found for: " + contributionURI);
        }
        for (DeployedComposite dc : ic.getDeployedComposites()) {
            compositeURIs.add(dc.getURI());
        }
        return compositeURIs;
    }

    public List<String> getInstalledContributions() {
        return new ArrayList<String>(installedContributions.keySet());
    }

    public Contribution getInstalledContribution(String uri) {
        if (installedContributions.containsKey(uri)) {
            return installedContributions.get(uri).getContribution();
        }
        throw new IllegalArgumentException("no contribution found for: " + uri);
    }

    protected String getContributionUriForArtifact(String artifactURI) {
        String contributionURI = null;
        for (String uri : installedContributions.keySet()) {
            if (artifactURI.startsWith(uri)) {
                contributionURI = uri;
                break;
            }
        }
        if (contributionURI == null) {
            throw new IllegalArgumentException("no contribution found for: " + artifactURI);
        }
        return contributionURI;
    }

    protected void runComposite(Composite c, InstalledContribution ic) throws ActivationException, ValidationException {
        List<Contribution> dependentContributions = calculateDependentContributions(ic);

        DeployedComposite dc = new DeployedComposite(c, ic, dependentContributions, deployer, compositeActivator, endpointRegistry, extensionPointRegistry);
        ic.getDeployedComposites().add(dc);
    }
    
    public Set<String> getDependentContributions(String contributionURI) {
        InstalledContribution ic = installedContributions.get(contributionURI);
        if (ic == null) {
            throw new IllegalArgumentException("Contribution not installed: " + contributionURI);
        }
        Set<String> dependentContributionURIs = new HashSet<String>();
        for (InstalledContribution icx : installedContributions.values()) {
            if (ic != icx) {
                List<Contribution> dependencies = icx.getContribution().getDependencies();
                if (dependencies != null && dependencies.contains(ic.getContribution())) {
                    dependentContributionURIs.addAll(getDependentContributions(icx.getURI()));
                }
            }
        }
        return dependentContributionURIs;
    }

    /**
     * Returns a default URI for a contribution based on the contribution URL
     */
    protected String getDefaultContributionURI(String contributionURL) {
        String uri = null;
        try {
            File f = new File(contributionURL);
            if ("classes".equals(f.getName()) && "target".equals(f.getParentFile().getName())) {
                uri = f.getParentFile().getParentFile().getName();                   
            } else {
                uri = f.getName();
            }
        } catch (Exception e) {
            // ignore
        }
        if (uri == null) {
            uri = contributionURL;
        }
        if (uri.endsWith(".zip") || uri.endsWith(".jar")) {
            uri = uri.substring(0, uri.length()-4);
        }
        return uri;
    }

}
