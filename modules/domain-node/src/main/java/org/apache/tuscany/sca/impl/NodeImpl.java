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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
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
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.deployment.Deployer;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.apache.tuscany.sca.runtime.ActivationException;
import org.apache.tuscany.sca.runtime.ActiveNodes;
import org.apache.tuscany.sca.runtime.CompositeActivator;
import org.apache.tuscany.sca.runtime.ContributionDescription;
import org.apache.tuscany.sca.runtime.ContributionListener;
import org.apache.tuscany.sca.runtime.DomainRegistry;
import org.oasisopen.sca.NoSuchServiceException;

public class NodeImpl implements Node {

    private Deployer deployer;
    private CompositeActivator compositeActivator;
    private DomainRegistry domainRegistry;
    private ExtensionPointRegistry extensionPointRegistry;
    private TuscanyRuntime tuscanyRuntime;
    
    private Map<String, Contribution> loadedContributions = new ConcurrentHashMap<String, Contribution>();

    private Map<String, DeployedComposite> startedComposites = new HashMap<String, DeployedComposite>();
    private Map<String, DeployedComposite> stoppedComposites = new HashMap<String, DeployedComposite>();

    public NodeImpl(Deployer deployer,
                     CompositeActivator compositeActivator,
                     DomainRegistry domainRegistry,
                     ExtensionPointRegistry extensionPointRegistry,
                     TuscanyRuntime tuscanyRuntime) {
        this.deployer = deployer;
        this.compositeActivator = compositeActivator;
        this.domainRegistry = domainRegistry;
        this.extensionPointRegistry = extensionPointRegistry;
        this.tuscanyRuntime = tuscanyRuntime;

        extensionPointRegistry.getExtensionPoint(UtilityExtensionPoint.class).getUtility(ActiveNodes.class).getActiveNodes().add(this);

        domainRegistry.addContributionListener(new ContributionListener() {
            public void contributionUpdated(String uri) {
                loadedContributions.remove(uri);
            }
            public void contributionRemoved(String uri) {
                loadedContributions.remove(uri);
            }
        });
    }

    // TODO: install shouldn't throw ValidationException as it shouldn't do any validation, its
    //      only here from the loadContribution in mergeContributionMetaData so change that approach
    
    public String installContribution(String contributionURL) throws ContributionReadException, ValidationException {
        return installContribution(null, contributionURL, null, null);
    }

    public String installContribution(String uri, String contributionURL) throws ContributionReadException, ValidationException {
        return installContribution(uri, contributionURL, null, null);
    }

    public boolean updateContribution(String uri, String contributionURL, String metaDataURL, List<String> dependentContributionURIs) throws ContributionReadException, ValidationException, ActivationException {
        ContributionDescription ic = domainRegistry.getInstalledContribution(uri);
        if (ic == null) {
            installContribution(uri, contributionURL, metaDataURL, dependentContributionURIs);
            return true;
        }

        // do this if only updating if the contribution has been modified:
        // if url equal and a file and last modified not changed
            // if metadata url equal and a file and laqst modified not changed
                 // if (dependent contributions uris not changed)
                     // return false

        uninstallContribution(uri);

        installContribution(uri, contributionURL, metaDataURL, dependentContributionURIs);
        
        // merge in additional deployables
        if (ic.getAdditionalDeployables().size() > 0) {
            ContributionDescription newIC = getInstalledContribution(uri);
            newIC.getAdditionalDeployables().putAll(ic.getAdditionalDeployables());
            domainRegistry.updateInstalledContribution(newIC);
        }

        // stop/start all started composites using the contribution
        for (DeployedComposite dc : new ArrayList<DeployedComposite>(startedComposites.values())) {
            if (dc.getContributionURIs().contains(uri)) {
                String dcContributionURI = dc.getContributionURIs().get(0);
                String dcCompositeURI = dc.getURI();
                stopComposite(dcContributionURI, dcCompositeURI);
                String key = dcContributionURI + "/" + dcCompositeURI;
                stoppedComposites.remove(key);
                startComposite(dcContributionURI, dcCompositeURI);
            }
        }

        // remove all stopped composites using the contribution
        for (DeployedComposite dc : new ArrayList<DeployedComposite>(stoppedComposites.values())) {
            if (dc.getContributionURIs().contains(uri)) {
                stoppedComposites.remove(uri + "/" + dc.getURI());
            }
        }
        
        return true;
    }
    
    public String installContribution(String uri, String contributionURL, String metaDataURL, List<String> dependentContributionURIs) throws ContributionReadException, ValidationException {
        ContributionDescription cd = new ContributionDescription(uri, IOHelper.getLocationAsURL(contributionURL).toString());

        if (dependentContributionURIs != null) {
            cd.getDependentContributionURIs().addAll(dependentContributionURIs);
        }
        
        if (metaDataURL != null) {
            mergeContributionMetaData(metaDataURL, loadContribution(cd));
        }

        peekIntoContribution(cd);

        domainRegistry.installContribution(cd);

        return cd.getURI();
    }
    
    public void installContribution(Contribution contribution, List<String> dependentContributionURIs) {
        ContributionDescription cd = new ContributionDescription(contribution.getURI(), contribution.getLocation());
        if (dependentContributionURIs != null) {
            cd.getDependentContributionURIs().addAll(dependentContributionURIs);
        }
        cd.configureMetaData(contribution);
        domainRegistry.installContribution(cd);
        loadedContributions.put(cd.getURI(), contribution);
    }
    
    public void uninstallContribution(String contributionURI) {
        domainRegistry.uninstallContribution(contributionURI);
        
        // remove any stopped composite that used the contribution
        Iterator<String> i = stoppedComposites.keySet().iterator();
        while (i.hasNext()) {
            DeployedComposite dc = stoppedComposites.get(i.next());
            if (dc.getContributionURIs().contains(contributionURI)) {
                i.remove();
            }
        }
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
    protected void peekIntoContribution(ContributionDescription cd) {
        Contribution contribution = null;
        try {
            contribution = loadContribution(cd);
        } catch (Exception e) {
            // ignore it
        }
        
        if (contribution != null) {
            cd.configureMetaData(contribution);
        }
    }
    
    public List<String> getInstalledContributionURIs() {
        return new ArrayList<String>(domainRegistry.getInstalledContributionURIs());
    }

    public Contribution getContribution(String contributionURI) throws ContributionReadException, ValidationException {
        return loadContribution(getInstalledContribution(contributionURI));
    }

    public List<String> getDeployableCompositeURIs(String contributionURI) {
        ContributionDescription cd = domainRegistry.getInstalledContribution(contributionURI);
        List<String> deployables = new ArrayList<String>(cd.getDeployables());
        deployables.addAll(cd.getAdditionalDeployables().keySet());
        return deployables;
    }
    
    public String addDeploymentComposite(String contributionURI, Reader compositeXML) throws ContributionReadException, XMLStreamException, ValidationException {
        ContributionDescription cd = getInstalledContribution(contributionURI);
        
        // load it to check its valid composite XML
        Composite composite = deployer.loadXMLDocument(compositeXML);
        
        return addDeploymentComposite(cd, composite);
    }

    public String addDeploymentComposite(String contributionURI, Composite composite) {
        ContributionDescription cd = getInstalledContribution(contributionURI);
        return addDeploymentComposite(cd, composite);
    }

    protected String addDeploymentComposite(ContributionDescription cd, Composite composite) {
        if (composite.getURI() == null || composite.getURI().length() < 1) {
            composite.setURI(composite.getName().getLocalPart() + ".composite");
        }
        composite.setContributionURI(cd.getURI());
        cd.getAdditionalDeployables().put(composite.getURI(), Utils.modelToXML(composite, false, extensionPointRegistry));
        domainRegistry.updateInstalledContribution(cd);
        return composite.getURI();
    }

    public void validateContribution(String contributionURI) throws ContributionReadException, ValidationException {
        ContributionDescription cd = getInstalledContribution(contributionURI);
        Contribution contribution = loadContribution(cd);

        Monitor monitor = deployer.createMonitor();
        try {
            deployer.resolve(contribution, calculateDependentContributions(cd), monitor);
        } catch (Exception e) {
            loadedContributions.remove(cd.getURI());
            throw new RuntimeException(e);
        }
        try {
            monitor.analyzeProblems();
        } catch (ValidationException e) {
            loadedContributions.remove(cd.getURI());
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
            ContributionDescription cd = getInstalledContribution(contributionURI);
            Contribution contribution = loadContribution(cd);
            Composite composite = contribution.getArtifactModel(compositeURI);
            List<Contribution> dependentContributions = calculateDependentContributions(cd);
            dc = new DeployedComposite(composite, contribution, dependentContributions, deployer, compositeActivator, domainRegistry, extensionPointRegistry);
            dc.start();
            startedComposites.put(key, dc);
        }
    }

    @Override
    public void startComposite(String contributionURI, String compositeURI, String nodeName) throws ActivationException {
        String response = domainRegistry.remoteCommand(nodeName, new RemoteCommand(domainRegistry.getDomainName(), "start", contributionURI, compositeURI));
        if (!"Started.".equals(response)) {
            throw new ActivationException(response);
        }
    }

    public void stopComposite(String contributionURI, String compositeURI) throws ActivationException {
        String key = contributionURI+"/"+compositeURI;
        DeployedComposite dc = startedComposites.remove(key);
        if (dc != null) {
            dc.stop();
            stoppedComposites.put(key, dc);
        } else {
            String member = domainRegistry.getRunningNodeName(contributionURI, compositeURI);
            if (member == null) {
                throw new IllegalStateException("composite not started: " + compositeURI);
            }
            RemoteCommand command = new RemoteCommand(domainRegistry.getDomainName(), "stop", contributionURI, compositeURI);
            String response = domainRegistry.remoteCommand(member, command);
            if (!"Stopped.".equals(response)) {
                throw new ActivationException(response);
            }
        }
    }

    public void stopCompositeAndUninstallUnused(String contributionURI, String compositeURI) throws ActivationException {
        String key = contributionURI+"/"+compositeURI;
        DeployedComposite dc = startedComposites.remove(key);
        if (dc == null) {
            throw new IllegalArgumentException("No startd composite found: " + key);
        }
        dc.stop();

        loop: for (String curi : dc.getContributionURIs()) {
            for (DeployedComposite started : startedComposites.values()) {
                if (started.getContributionURIs().contains(curi)) {
                    continue loop;
                }
            }
            uninstallContribution(curi);
        }
    }

    public String getDomainName() {
        return domainRegistry.getDomainName();
    }

    public Composite getDomainComposite() {
        return domainRegistry.getDomainComposite();
    }

    public <T> T getService(Class<T> interfaze, String serviceURI) throws NoSuchServiceException {
        return ServiceHelper.getService(interfaze, serviceURI, domainRegistry, extensionPointRegistry, deployer);
    }

    public ContributionDescription getInstalledContribution(String contributionURI) {
        ContributionDescription cd = domainRegistry.getInstalledContribution(contributionURI);
        if (cd == null) {
            throw new IllegalArgumentException("Contribution not installed: " + contributionURI);
        }
        return cd;
    }

    protected Contribution loadContribution(ContributionDescription cd) throws ContributionReadException, ValidationException {
        Contribution contribution = loadedContributions.get(cd.getURI());
        if (contribution == null) {
            Monitor monitor = deployer.createMonitor();
            contribution = deployer.loadContribution(IOHelper.createURI(cd.getURI()), IOHelper.getLocationAsURL(cd.getURL()), monitor);
            
            // TODO: should the monitor be checked? If it is then the peek in to get the metadata doesn't work if there's a problem 
            // monitor.analyzeProblems();
            if (cd.getAdditionalDeployables().size() > 0) {
                for (String uri : cd.getAdditionalDeployables().keySet()) {
                    String compositeXML = cd.getAdditionalDeployables().get(uri);
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
            loadedContributions.put(cd.getURI(), contribution);
        }
        return contribution;
    }

    protected List<Contribution> calculateDependentContributions(ContributionDescription cd) throws ContributionReadException, ValidationException {
        List<Contribution> dependentContributions = new ArrayList<Contribution>();
        if (cd.getDependentContributionURIs() != null && cd.getDependentContributionURIs().size() > 0) {
            // if the install specified dependent uris use just those contributions
            for (String uri : cd.getDependentContributionURIs()) {
                ContributionDescription dependee = domainRegistry.getInstalledContribution(uri);
                if (dependee != null) {
                    dependentContributions.add(loadContribution(dependee));
                }
            }
        } else {
            for (Import imprt : loadContribution(cd).getImports()) {
                for (ContributionDescription exportingIC : findExportingContributions(imprt)) {
                    dependentContributions.add(loadContribution(exportingIC));
                }
            }
        }
        // TODO: there is also the location attribute on the import which should be taken into account
        return dependentContributions;
    }

    private List<ContributionDescription> findExportingContributions(Import imprt) {
        List<ContributionDescription> ics = new ArrayList<ContributionDescription>();
        // TODO: Handle Imports in a more extensible way
        for (String curi : domainRegistry.getInstalledContributionURIs()) {
            ContributionDescription cd = domainRegistry.getInstalledContribution(curi);
            if (imprt instanceof JavaImport) {
                for (String s : cd.getJavaExports()) {
                    if (s.startsWith(((JavaImport)imprt).getPackage())) {
                        ics.add(cd);
                    }
                }
            } else if (imprt instanceof NamespaceImport) {
                if (cd.getNamespaceExports().contains(((NamespaceImport)imprt).getNamespace())) {
                    ics.add(cd);
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

    // TODO: these are used by the shell, should they be on the Node interface?
    public DomainRegistry getEndpointRegistry() {
        return domainRegistry;
    }
    public ExtensionPointRegistry getExtensionPointRegistry() {
        return extensionPointRegistry;
    }
    
    public void stop() {
        for (DeployedComposite dc : startedComposites.values()) {
            try {
                dc.stop();
            } catch (ActivationException e) {
            }
        }
        startedComposites.clear();
        stoppedComposites.clear();
        extensionPointRegistry.getExtensionPoint(UtilityExtensionPoint.class).getUtility(ActiveNodes.class).getActiveNodes().remove(this);
        if (tuscanyRuntime != null) {
            tuscanyRuntime.stop();
        }
    }

    @Override
    public List<String> getNodeNames() {
        return domainRegistry.getNodeNames();
    }

    @Override
    public String getLocalNodeName() {
        return domainRegistry.getLocalNodeName();
    }

    @Override
    public String getRunningNodeName(String contributionURI, String compositeURI) {
        return domainRegistry.getRunningNodeName(contributionURI, compositeURI);
    }

    public List<String> updateUsingComposites(String contributionURI, String compositeURI) throws ActivationException, ContributionReadException, ValidationException {
        List<String> updated = new ArrayList<String>();
        for (DeployedComposite dc : new ArrayList<DeployedComposite>(startedComposites.values())) {
            if (dc.uses(contributionURI, compositeURI)) {
                String dcContributionURI = dc.getContributionURIs().get(0);
                String dcCompositeURI = dc.getURI();
                stopComposite(dcContributionURI, dcCompositeURI);
                String key = dcContributionURI + "/" + dcCompositeURI;
                stoppedComposites.remove(key);
                updated.add(key);
                startComposite(dcContributionURI, dcCompositeURI);
            }
        }
        return updated;
    }

    public void uninstallContribution(String contributionURI, boolean b) throws ActivationException {
        uninstallContribution(contributionURI);
        if (!b) {
            return;
        }

        // stop all started composites using the contribution
        for (DeployedComposite dc : new ArrayList<DeployedComposite>(startedComposites.values())) {
            if (dc.getContributionURIs().contains(contributionURI)) {
                String dcContributionURI = dc.getContributionURIs().get(0);
                String dcCompositeURI = dc.getURI();
                stopComposite(dcContributionURI, dcCompositeURI);
                String key = dcContributionURI + "/" + dcCompositeURI;
                stoppedComposites.remove(key);
            }
        }

        // remove all stopped composites using the contribution
        for (DeployedComposite dc : new ArrayList<DeployedComposite>(stoppedComposites.values())) {
            if (dc.getContributionURIs().contains(contributionURI)) {
                stoppedComposites.remove(contributionURI + "/" + dc.getURI());
            }
        }
    }
}
