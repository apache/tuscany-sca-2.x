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

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.xml.Utils;
import org.apache.tuscany.sca.common.java.io.IOHelper;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionMetadata;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.java.JavaImport;
import org.apache.tuscany.sca.contribution.namespace.NamespaceImport;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.deployment.Deployer;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.apache.tuscany.sca.runtime.ActivationException;
import org.apache.tuscany.sca.runtime.CompositeActivator;
import org.apache.tuscany.sca.runtime.ContributionListener;
import org.apache.tuscany.sca.runtime.DomainRegistry;
import org.apache.tuscany.sca.runtime.InstalledContribution;
import org.oasisopen.sca.NoSuchServiceException;

public class NodeImpl implements Node {

    private String domainName;
    private Deployer deployer;
    private CompositeActivator compositeActivator;
    private DomainRegistry domainRegistry;
    private ExtensionPointRegistry extensionPointRegistry;
    private TuscanyRuntime tuscanyRuntime;
    
    private Map<String, Contribution> loadedContributions = new ConcurrentHashMap<String, Contribution>();

    private Map<String, DeployedComposite> startedComposites = new HashMap<String, DeployedComposite>();
    private Map<String, DeployedComposite> stoppedComposites = new HashMap<String, DeployedComposite>();

    public NodeImpl(String domainName,
                     Deployer deployer,
                     CompositeActivator compositeActivator,
                     DomainRegistry domainRegistry,
                     ExtensionPointRegistry extensionPointRegistry,
                     TuscanyRuntime tuscanyRuntime) {
        this.domainName = domainName;
        this.deployer = deployer;
        this.compositeActivator = compositeActivator;
        this.domainRegistry = domainRegistry;
        this.extensionPointRegistry = extensionPointRegistry;
        this.tuscanyRuntime = tuscanyRuntime;
        
        domainRegistry.addContributionListener(new ContributionListener() {
            public void contributionUpdated(String uri) {
                loadedContributions.remove(uri);
            }
            public void contributionRemoved(String uri) {
                loadedContributions.remove(uri);
            }
        });
    }

    public String installContribution(String contributionURL) throws ContributionReadException, ActivationException, ValidationException {
        return installContribution(null, contributionURL, null, null);
    }

    public String installContribution(String uri, String contributionURL) throws ContributionReadException, ActivationException, ValidationException {
        return installContribution(uri, contributionURL, null, null);
    }

    public String installContribution(String uri, String contributionURL, String metaDataURL, List<String> dependentContributionURIs) throws ContributionReadException, ActivationException, ValidationException {
        InstalledContribution ic = new InstalledContribution(uri, contributionURL);

        if (dependentContributionURIs != null) {
            ic.getDependentContributionURIs().addAll(dependentContributionURIs);
        }
        
        if (metaDataURL != null) {
            mergeContributionMetaData(metaDataURL, loadContribution(ic));
        }

        peekIntoContribution(ic);

        domainRegistry.installContribution(ic);

        return ic.getURI();
    }
    
    public void installContribution(Contribution contribution, List<String> dependentContributionURIs) {
        InstalledContribution ic = new InstalledContribution(contribution.getURI(), contribution.getLocation());
        if (dependentContributionURIs != null) {
            ic.getDependentContributionURIs().addAll(dependentContributionURIs);
        }
        ic.configureMetaData(contribution);
        domainRegistry.installContribution(ic);
        loadedContributions.put(ic.getURI(), contribution);
    }
    
    public void uninstallContribution(String contributionURI) {
        domainRegistry.uninstallContribution(contributionURI);
    }
    
    protected void mergeContributionMetaData(String metaDataURL, Contribution contribution) throws ValidationException {
        ContributionMetadata metaData;
        Monitor monitor = deployer.createMonitor();
        try {
            metaData = deployer.loadXMLDocument(IOHelper.getLocationAsURL(metaDataURL), monitor);
        } catch (Exception e) {
            throw new ValidationException(e);
        }
        monitor.analyzeProblems();
        contribution.mergeMetaData(metaData);
    }
    
    /**
     * Peek into the contribution to find its attributes.
     * ASM12032 and ASM12033 say no error checking should be done during install and that should happen later, but 
     * we need to know about deployables and exports so peek into the contribution to try to get those,
     * and just ignore any errors they might happen while doing that. 
     */
    protected void peekIntoContribution(InstalledContribution ic) {
        Contribution contribution = null;
        try {
            contribution = loadContribution(ic);
        } catch (Exception e) {
            // ignore it
        }
        
        if (contribution != null) {
            ic.configureMetaData(contribution);
        }
    }
    
    public List<String> getInstalledContributionURIs() {
        return new ArrayList<String>(domainRegistry.getInstalledContributionURIs());
    }

    public Contribution getContribution(String contributionURI) throws ContributionReadException, ValidationException {
        return loadContribution(getInstalledContribution(contributionURI));
    }

    public List<String> getDeployableCompositeURIs(String contributionURI) {
        InstalledContribution ic = domainRegistry.getInstalledContribution(contributionURI);
        return new ArrayList<String>(ic.getDeployables());
    }
    
    public String addDeploymentComposite(String contributionURI, Reader compositeXML) throws ContributionReadException, XMLStreamException, ValidationException {
        InstalledContribution ic = getInstalledContribution(contributionURI);
        
        // load it to check its valid composite XML
        Composite composite = deployer.loadXMLDocument(compositeXML);
        
        return addDeploymentComposite(ic, composite);
    }

    public String addDeploymentComposite(String contributionURI, Composite composite) {
        InstalledContribution ic = getInstalledContribution(contributionURI);
        return addDeploymentComposite(ic, composite);
    }

    protected String addDeploymentComposite(InstalledContribution ic, Composite composite) {
        if (composite.getURI() == null || composite.getURI().length() < 1) {
            composite.setURI(composite.getName().getLocalPart() + ".composite");
        }
        ic.getAdditionalDeployables().put(composite.getURI(), Utils.modelToXML(composite, false, extensionPointRegistry));
        domainRegistry.updateInstalledContribution(ic);
        return composite.getURI();
    }

    public void validateContribution(String contributionURI) throws ContributionReadException, ValidationException {
        InstalledContribution ic = getInstalledContribution(contributionURI);
        Contribution contribution = loadContribution(ic);

        Monitor monitor = deployer.createMonitor();
        try {
            deployer.resolve(contribution, calculateDependentContributions(ic), monitor);
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
    
    public Map<String, List<String>> getStartedCompositeURIs() {
        return Collections.unmodifiableMap(domainRegistry.getRunningCompositeURIs());
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
            Composite composite = contribution.getArtifactModel(compositeURI);
            List<Contribution> dependentContributions = calculateDependentContributions(ic);
            dc = new DeployedComposite(composite, contribution, dependentContributions, deployer, compositeActivator, domainRegistry, extensionPointRegistry);
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

    public String getDomainName() {
        return domainName;
    }

    public Composite getDomainComposite() {
        FactoryExtensionPoint factories = extensionPointRegistry.getExtensionPoint(FactoryExtensionPoint.class);
        AssemblyFactory assemblyFactory = factories.getFactory(AssemblyFactory.class);
        Composite domainComposite = assemblyFactory.createComposite();
        domainComposite.setName(new QName(Base.SCA11_TUSCANY_NS, domainName));
        domainComposite.setAutowire(false);
        domainComposite.setLocal(false);
        List<Composite> domainIncludes = domainComposite.getIncludes();
        Map<String, List<String>> runningCompositeURIs = domainRegistry.getRunningCompositeURIs();
        for (String curi : runningCompositeURIs.keySet()) {
            for (String compositeURI : runningCompositeURIs.get(curi)) {
                domainIncludes.add(domainRegistry.getRunningComposite(curi, compositeURI));
            }
        }
        return domainComposite;
    }

    public <T> T getService(Class<T> interfaze, String serviceURI) throws NoSuchServiceException {
        return ServiceHelper.getService(interfaze, serviceURI, domainRegistry, extensionPointRegistry, deployer);
    }

    // TODO: should this be exposed on the interface?
    protected InstalledContribution getInstalledContribution(String contributionURI) {
        InstalledContribution ic = domainRegistry.getInstalledContribution(contributionURI);
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
            if (ic.getAdditionalDeployables().size() > 0) {
                for (String uri : ic.getAdditionalDeployables().keySet()) {
                    String compositeXML = ic.getAdditionalDeployables().get(uri);
                    Composite composite;
                    try {
                        composite = deployer.loadXMLDocument(new StringReader(compositeXML));
                    } catch (XMLStreamException e) {
                        throw new ContributionReadException(e);
                    }
                    composite.setURI(composite.getName().getLocalPart() + ".composite");
                    contribution.addComposite(composite);
                }
            }
            loadedContributions.put(ic.getURI(), contribution);
        }
        return contribution;
    }

    protected List<Contribution> calculateDependentContributions(InstalledContribution ic) throws ContributionReadException, ValidationException {
        List<Contribution> dependentContributions = new ArrayList<Contribution>();
        if (ic.getDependentContributionURIs() != null && ic.getDependentContributionURIs().size() > 0) {
            // if the install specified dependent uris use just those contributions
            for (String uri : ic.getDependentContributionURIs()) {
                InstalledContribution dependee = domainRegistry.getInstalledContribution(uri);
                if (dependee != null) {
                    dependentContributions.add(loadContribution(dependee));
                }
            }
        } else {
            for (Import imprt : loadContribution(ic).getImports()) {
                for (InstalledContribution exportingIC : findExportingContributions(imprt)) {
                    dependentContributions.add(loadContribution(exportingIC));
                }
            }
        }
        // TODO: there is also the location attribute on the import which should be taken into account
        return dependentContributions;
    }

    private List<InstalledContribution> findExportingContributions(Import imprt) {
        List<InstalledContribution> ics = new ArrayList<InstalledContribution>();
        // TODO: Handle Imports in a more extensible way
        for (String curi : domainRegistry.getInstalledContributionURIs()) {
            InstalledContribution ic = domainRegistry.getInstalledContribution(curi);
            if (imprt instanceof JavaImport) {
                for (String s : ic.getJavaExports()) {
                    if (s.startsWith(((JavaImport)imprt).getPackage())) {
                        ics.add(ic);
                    }
                }
            } else if (imprt instanceof NamespaceImport) {
                if (ic.getNamespaceExports().contains(((NamespaceImport)imprt).getNamespace())) {
                    ics.add(ic);
                }
            } 
        }
        return ics;
    }
    
    @Override
    public Object getQNameDefinition(String contributionURI, QName definition, QName symbolSpace) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> startDeployables(String contributionURI) throws ActivationException, ValidationException, ContributionReadException {
        List<String> dcURIs = getDeployableCompositeURIs(contributionURI);
        for (String dcURI : dcURIs) {
            startComposite(contributionURI, dcURI);
        }
        return dcURIs;
    }

    // TODO: this is used by the shell to endpoint detail, should it be on the Node interface?
    public DomainRegistry getEndpointRegistry() {
        return domainRegistry;
    }
    
    public void stop() {
        for (DeployedComposite dc : startedComposites.values()) {
            try {
                dc.stop();
            } catch (ActivationException e) {
            }
        }
        startedComposites.clear();
        startedComposites.clear();
        
    }

}
