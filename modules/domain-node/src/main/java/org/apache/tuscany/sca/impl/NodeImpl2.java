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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.TuscanyRuntime;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.common.java.io.IOHelper;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.java.JavaExport;
import org.apache.tuscany.sca.contribution.java.JavaImport;
import org.apache.tuscany.sca.contribution.namespace.NamespaceExport;
import org.apache.tuscany.sca.contribution.namespace.NamespaceImport;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.deployment.Deployer;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.apache.tuscany.sca.runtime.ActivationException;
import org.apache.tuscany.sca.runtime.CompositeActivator;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.InstalledContribution;

public class NodeImpl2 {

    private String domainName;
    private Deployer deployer;
    private CompositeActivator compositeActivator;
    private EndpointRegistry endpointRegistry;
    private ExtensionPointRegistry extensionPointRegistry;
    private TuscanyRuntime tuscanyRuntime;
    
    private Map<String, Contribution> loadedContributions = new HashMap<String, Contribution>();

    private Map<String, DeployedComposite> startedComposites = new HashMap<String, DeployedComposite>();
    private Map<String, DeployedComposite> stoppedComposites = new HashMap<String, DeployedComposite>();

    public NodeImpl2(String domainName,
                     Deployer deployer,
                     CompositeActivator compositeActivator,
                     EndpointRegistry endpointRegistry,
                     ExtensionPointRegistry extensionPointRegistry,
                     TuscanyRuntime tuscanyRuntime) {
        this.domainName = domainName;
        this.deployer = deployer;
        this.compositeActivator = compositeActivator;
        this.endpointRegistry = endpointRegistry;
        this.extensionPointRegistry = extensionPointRegistry;
        this.tuscanyRuntime = tuscanyRuntime;
    }

    public String installContribution(String contributionURL) throws ContributionReadException, ActivationException, ValidationException {
        return installContribution(null, contributionURL);
    }

    public String installContribution(String uri, String contributionURL) throws ContributionReadException, ActivationException, ValidationException {
        if (uri == null) {
            uri = getDefaultContributionURI(contributionURL);
        }

        InstalledContribution ic = new InstalledContribution();
        ic.setURI(uri);
        ic.setURL(contributionURL);
        
        peekIntoContribution(ic);

        endpointRegistry.installContribution(ic);

        return uri;
    }

    /**
     * Peek into the contribution to find its attributes.
     * ASM12032 and ASM12033 say no error checking should be done during install and that should happen later, but 
     * we would still like to know about deployables and exports so peek into the contribution to try to get those,
     * and just ignore any errors they might happen while doing that. 
     */
    protected void peekIntoContribution(InstalledContribution ic) throws ContributionReadException, ValidationException {
        Contribution contribution = loadContribution(ic);

        // deployables
        for (Composite composite : contribution.getDeployables()) {
            ic.getDeployables().add(composite.getURI());
        }

        // Exports
        for (Export export : contribution.getExports()) {
            if (export instanceof JavaExport) {
                ic.getJavaExports().add(((JavaExport)export).getPackage());
            } else if (export instanceof NamespaceExport) {
                ic.getNamespaceExports().add(((NamespaceExport)export).getNamespace());
            } // TODO: Handle these and others in a more extensible way
        }

    }
    
    public List<String> getDeployableComposites(String contributionURI) {
        InstalledContribution ic = endpointRegistry.getInstalledContribution(contributionURI);
        return new ArrayList<String>(ic.getDeployables());
    }

    public List<String> getInstalledContributionURIs() {
        return new ArrayList<String>(endpointRegistry.getInstalledContributionURIs());
    }

    public Contribution getContribution(String contributionURI) throws ContributionReadException, ValidationException {
        return loadContribution(getInstalledContribution(contributionURI));
    }

    public void validateContribution(String contributionURI) throws ContributionReadException, ValidationException {
        InstalledContribution ic = getInstalledContribution(contributionURI);
        Contribution contribution = loadContribution(ic);

        List<Contribution> dependentContributions = calculateDependentContributions(ic);

        Monitor monitor = deployer.createMonitor();
        try {
            deployer.resolve(contribution, dependentContributions, monitor);
        } catch (Exception e) {
            loadedContributions.remove(ic.getURI());
            throw new RuntimeException(e);
        }
        try {
            monitor.analyzeProblems();
        } catch (ValidationException e) {
            loadedContributions.remove(ic.getURI());
            throw e;
        }
    }
    
    public Map<String, List<QName>> getStartedComposites() {
        return endpointRegistry.getRunningCompositeNames();
    }

    public void startComposite(String contributionURI, String compositeURI) throws ActivationException, ValidationException, ContributionReadException {
        String key = contributionURI+"/"+compositeURI;
        if (startedComposites.containsKey(key)) {
            throw new IllegalStateException("composite already started: " + compositeURI);
        }
        DeployedComposite dc = stoppedComposites.remove(key);
        if (dc != null) {
            dc.start();
            startedComposites.put(key, dc);
        } else {
            InstalledContribution ic = getInstalledContribution(contributionURI);
            Contribution contribution = loadContribution(ic);
            Composite composite = getComposite(contribution, compositeURI);
            List<Contribution> dependentContributions = calculateDependentContributions(ic);
            dc = new DeployedComposite(composite, contribution, dependentContributions, deployer, compositeActivator, endpointRegistry, extensionPointRegistry);
            dc.start();
            startedComposites.put(key, dc);
        }
    }
    
    public void stopComposite(String contributionURI, String compositeURI) throws ActivationException {
        String key = contributionURI+"/"+compositeURI;
        DeployedComposite dc = startedComposites.remove(key);
        if (dc == null) {
            throw new IllegalStateException("composite not started: " + compositeURI);
        }
        dc.stop();
        stoppedComposites.put(key, dc);
    }

    protected InstalledContribution getInstalledContribution(String contributionURI) {
        InstalledContribution ic = endpointRegistry.getInstalledContribution(contributionURI);
        if (ic == null) {
            throw new IllegalArgumentException("Contribution not installed: " + contributionURI);
        }
        return ic;
    }

    protected Contribution loadContribution(InstalledContribution ic) throws ContributionReadException, ValidationException {
        Contribution contribution = loadedContributions.get(ic.getURI());
        if (contribution == null) {
            Monitor monitor = deployer.createMonitor();
            contribution = deployer.loadContribution(IOHelper.createURI(ic.getURI()), IOHelper.getLocationAsURL(ic.getURL()), monitor);
            monitor.analyzeProblems();
            loadedContributions.put(ic.getURI(), contribution);
            fixDeployedCompositeURIs(contribution);
        }
        return contribution;
    }

    protected List<Contribution> calculateDependentContributions(InstalledContribution ic) throws ContributionReadException, ValidationException {
        List<Contribution> dependentContributions = new ArrayList<Contribution>();
        Contribution c = loadContribution(ic);
        if (ic.getDependentContributionURIs() != null && ic.getDependentContributionURIs().size() > 0) {
            // if the install specified dependent uris use just those contributions
            for (String uri : ic.getDependentContributionURIs()) {
                InstalledContribution dependee = endpointRegistry.getInstalledContribution(uri);
                if (dependee != null) {
                    dependentContributions.add(loadContribution(dependee));
                }
            }
        } else {
            for (Import imprt : c.getImports()) {
                InstalledContribution exportingIC = findExportingContribution(imprt);
                if (exportingIC != null) {
                    dependentContributions.add(loadContribution(exportingIC));
                }
            }
        }
        // TODO: there is also the location attribute on the import which should be taken into account
        return dependentContributions;
    }

    private InstalledContribution findExportingContribution(Import imprt) {
        // TODO: Handle Imports in a more extensible way
        for (String curi : endpointRegistry.getInstalledContributionURIs()) {
            InstalledContribution ic = endpointRegistry.getInstalledContribution(curi);
            if (imprt instanceof JavaImport) {
                for (String s : ic.getJavaExports()) {
                    if (s.startsWith(((JavaImport)imprt).getPackage())) {
                        return ic;
                    }
                }
            } else if (imprt instanceof NamespaceImport) {
                if (ic.getNamespaceExports().contains(((NamespaceImport)imprt).getNamespace())) {
                        return ic;
                }
            } 
        }
        return null;
    }

    protected Composite getComposite(Contribution contribution, String compositeURI) {
        for (Artifact a : contribution.getArtifacts()) {
            if (a.getURI().equals(compositeURI)) {
                return (Composite) a.getModel();
            }
        }
        throw new IllegalArgumentException("composite not found: " + compositeURI);
    }
    
    /**
     * Deployable composites don't have the uri set so get it from the artifact in the contribution
     * // TODO: fix the Tuscany code so this uri is correctly set and this method isn't needed
     */
    private void fixDeployedCompositeURIs(Contribution contribution) {
        int i = contribution.getDependencies().size();
        for (Artifact a : contribution.getArtifacts()) {
            if (a.getModel() != null) {
                if (a.getModel() instanceof Composite) {
                    Composite cm = a.getModel();
                    for (Composite c : contribution.getDeployables()) {
                        if (c.getName().equals(cm.getName())) {
                            c.setURI(cm.getURI());
                            i = i-1;
                            if (i < 1) {
                                return;
                            }
                        }
                    }
                }
            }
        }
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
            uri = uri.substring(0, uri.length() - 4);
        }
        if (uri.endsWith("SNAPSHOT")) {
            uri = uri.substring(0, uri.lastIndexOf('-'));
            uri = uri.substring(0, uri.lastIndexOf('-'));
        }
        return uri;
    }
}
