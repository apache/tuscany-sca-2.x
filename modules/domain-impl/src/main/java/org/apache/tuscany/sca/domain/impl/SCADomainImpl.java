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

package org.apache.tuscany.sca.domain.impl; 

import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.DomainBuilder;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.core.context.ServiceReferenceImpl;
import org.apache.tuscany.sca.databinding.impl.XSDDataTypeConverter.Base64Binary;
import org.apache.tuscany.sca.domain.DomainException;
import org.apache.tuscany.sca.domain.SCADomain;
import org.apache.tuscany.sca.domain.SCADomainEventService;
import org.apache.tuscany.sca.domain.SCADomainSPI;
import org.apache.tuscany.sca.domain.management.SCADomainManagerInitService;
import org.apache.tuscany.sca.domain.model.CompositeModel;
import org.apache.tuscany.sca.domain.model.ContributionModel;
import org.apache.tuscany.sca.domain.model.DomainModel;
import org.apache.tuscany.sca.domain.model.DomainModelFactory;
import org.apache.tuscany.sca.domain.model.NodeModel;
import org.apache.tuscany.sca.domain.model.ServiceModel;
import org.apache.tuscany.sca.domain.model.NodeModel.LifecyleState;
import org.apache.tuscany.sca.domain.model.impl.DomainModelFactoryImpl;
import org.apache.tuscany.sca.domain.model.impl.NodeModelImpl;
import org.apache.tuscany.sca.host.embedded.impl.ReallySmallRuntime;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletHostExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.node.NodeException;
import org.apache.tuscany.sca.node.NodeFactoryImpl;
import org.apache.tuscany.sca.node.util.SCAContributionUtil;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentContext;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.ServiceRuntimeException;

/**
 * The SCA domain implementation. In Tuscany we currently have a model of the 
 * SCA Domain that relies on a central domain manager this class provides that 
 * central manager. 
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-09 23:54:46 +0100 (Sun, 09 Sep 2007) $
 */
public class SCADomainImpl implements SCADomain, SCADomainEventService, SCADomainSPI  {
	
    private final static Logger logger = Logger.getLogger(SCADomainImpl.class.getName());
       
    // class loader used to get the runtime going
    protected ClassLoader domainClassLoader;
    
    // domain management application runtime
    protected ReallySmallRuntime domainManagementRuntime;
    protected ContributionService domainManagementContributionService;
    protected Contribution domainManagementContribution;
    protected Composite domainManagementComposite;
    
    // domain application runtime
    protected Composite domainComposite;
    
    // the logic for wiring up references and services at the domain level
    protected DomainBuilder domainBuilder;
    
    // Used to pipe dummy node information into the domain management runtime
    // primarily so that the sca binding can resolve endpoints. 
    protected NodeFactoryImpl nodeFactory;    
          
    // The domain model
    protected DomainModelFactory domainModelFactory = new DomainModelFactoryImpl();
    protected DomainModel domainModel;
    
    // management services
    private SCADomainManagerInitService domainManagerInitService;
    
    
    // Implementation methods
     
    /** 
     * Create a domain giving the URI for the domain. 
     * 
     * @param domainUri - identifies what host and port the domain service is running on, e.g. http://localhost:8081
     * @throws ActivationException
     */
    public SCADomainImpl(String domainURI) throws DomainException {
        this.domainModel = domainModelFactory.createDomain();
        this.domainModel.setDomainURI(domainURI);
        this.domainClassLoader = SCADomainImpl.class.getClassLoader(); 
        init();
    }    
       
    /**
     * Create the domain management runtime etc
     */
    protected void init() throws DomainException {
        try {
            // check whether domain URI is a URL
            URI tmpURI;
            try {
                tmpURI = new URI(domainModel.getDomainURI()); 
                domainModel.setDomainURL(tmpURI.toURL().toExternalForm());
            } catch(Exception ex) {
                throw new ActivationException("domain uri " + 
                                              domainModel.getDomainURI() + 
                                              " must be a valid url");
            }
                
            // create a runtime for the domain management services to run on
            domainManagementRuntime = new ReallySmallRuntime(domainClassLoader);
            domainManagementRuntime.start();
            
            // Configure the default server port and path
            int port = URI.create(domainModel.getDomainURI()).getPort();
            String path = URI.create(domainModel.getDomainURI()).getPath();
            if (port != -1) {
                ServletHostExtensionPoint servletHosts = domainManagementRuntime.getExtensionPointRegistry().getExtensionPoint(ServletHostExtensionPoint.class);
                for (ServletHost servletHost: servletHosts.getServletHosts()) {
                    servletHost.setDefaultPort(port);
                    if (path != null && path.length() > 0 && !path.equals("/")) {
                        servletHost.setContextPath(path);
                    }
                }
            }
            
            // Create an in-memory domain level management composite
            AssemblyFactory assemblyFactory = domainManagementRuntime.getAssemblyFactory();
            domainManagementComposite = assemblyFactory.createComposite();
            domainManagementComposite.setName(new QName(Constants.SCA10_NS, "domainManagement"));
            domainManagementComposite.setURI(domainModel.getDomainURI() + "/management");   
            
            // Create an in-memory domain level composite
            domainComposite = assemblyFactory.createComposite();
            domainComposite.setName(new QName(Constants.SCA10_NS, "domain"));
            domainComposite.setURI(domainModel.getDomainURI() + "/domain");  
            
            domainModel.setDomainLevelComposite(domainComposite);
            
            // Set up the domain so that local CallableReferences can find 
            // service out there in the domain
            SCADummyNodeImpl node = new SCADummyNodeImpl(this);
            ModelFactoryExtensionPoint factories = domainManagementRuntime.getExtensionPointRegistry().getExtensionPoint(ModelFactoryExtensionPoint.class);
            nodeFactory = new NodeFactoryImpl(node);
            factories.addFactory(nodeFactory); 
            
            // Find the composite that will configure the domain
            String domainCompositeName = "domain.composite";
            URL contributionURL = SCAContributionUtil.findContributionFromResource(domainClassLoader, domainCompositeName);
            
            if ( contributionURL != null ){ 
                logger.log(Level.INFO, "Domain management configured from " + contributionURL);
                           
                // add node composite to the management domain
                domainManagementContributionService = domainManagementRuntime.getContributionService();
                Contribution contribution = null;
                contribution = domainManagementContributionService.contribute(domainModel.getDomainURI(), 
                                                                              contributionURL, 
                                                                              false);
                
                //get the domain builder
                domainBuilder = domainManagementRuntime.getDomainBuilder();
                
                
                Composite composite = null;
                for (Artifact artifact: contribution.getArtifacts()) {
                    if (domainCompositeName.equals(artifact.getURI())) {
                        composite = (Composite)artifact.getModel();
                    }
                }
                
                if (composite != null) {
                
                    domainManagementComposite.getIncludes().add(composite);
                    domainManagementRuntime.buildComposite(composite);
                    domainManagementRuntime.getCompositeActivator().activate(composite); 
                    domainManagementRuntime.getCompositeActivator().start(composite);
                
                    // get the management components out of the domain so that they 
                    // can be configured/used. 
                    domainManagerInitService = getService(SCADomainManagerInitService.class, 
                                                          "SCADomainManagerComponent/SCADomainManagerInitService", 
                                                          domainManagementRuntime, 
                                                          domainManagementComposite);
                    domainManagerInitService.setDomain(this);
                    domainManagerInitService.setDomainSPI((SCADomainSPI)this);
                    domainManagerInitService.setDomainEventService((SCADomainEventService)this);
                    
                        
                } else {
                    throw new ActivationException("Domain management contribution " + 
                                                  contributionURL + 
                                                  " found but could not be loaded");
                }
            } else {
                throw new ActivationException("Domain management contribution " + 
                                              domainCompositeName + 
                                              " not found on the classpath");
            }  
            
            
        } catch(Exception ex) {
            throw new DomainException(ex);
        }
    }          
    
    private void notifyDomainChange() throws DomainException {
        List<Composite> changedComposites = domainBuilder.wireDomain(domainComposite);
        
        // notify nodes that have composites that the composites have changed
        for (Composite composite : changedComposites){
            String compositeXML = getComposite(composite.getName());
            
            for (NodeModel node : domainModel.getNodes().values()){
                if (node.getDeployedComposites().containsKey(composite.getName())){
                    try {
                        if (((NodeModelImpl)node).getSCANodeManagerService() != null) {
                            
                            logger.log(Level.FINE, "Updating node: " + 
                                                   node.getNodeURI() + 
                                                   " with composite: " + 
                                                   compositeXML);
                            
                            // notify node
                            ((NodeModelImpl)node).getSCANodeManagerService().updateComposite(composite.getName().toString(), 
                                                                                             Base64Binary.encode(compositeXML.getBytes()));
                        }
                    } catch (Exception ex) {
                        throw new DomainException(ex);
                    }                        
                }
            }
        }
    }
    
    private ContributionModel findContributionFromComposite(QName compositeQName){
        ContributionModel returnContributionModel = null;
        
        for(ContributionModel contributionModel : domainModel.getContributions().values()){            
            if (contributionModel.getComposites().containsKey(compositeQName)){
                returnContributionModel = contributionModel;
            }
        }
        
        return returnContributionModel;
    }
    
    private CompositeModel findComposite(QName compositeQName){
        CompositeModel returnCompositeModel = null;
        
        for(ContributionModel contributionModel : domainModel.getContributions().values()){
            returnCompositeModel = contributionModel.getComposites().get(compositeQName);
            
            if (returnCompositeModel != null){
                break;
            }
        }
        
        return returnCompositeModel;
    }
    
    // Recursively look for contributions that contain included artifacts. Deepest dependencies
    // appear first in the list
    // This function should be moved to the contribution package.
    private void findDependentContributions(Contribution contribution, List<Contribution> dependentContributions){
        
        for (Import contribImport : contribution.getImports()) {
            for (ContributionModel tmpContributionModel : domainModel.getContributions().values()) {
                Contribution tmpContribution = tmpContributionModel.getContribution();
                for (Export export : tmpContribution.getExports()) {
                    if (contribImport.match(export)) {
                        if (tmpContribution.getImports().isEmpty()) {
                            dependentContributions.add(tmpContribution);
                        } else {
                            findDependentContributions(tmpContribution, dependentContributions);
                        }
                    }  
                }   
            }
        }
        
        dependentContributions.add(contribution);
    }
    
    private ContributionModel parseContribution(String contributionURI, String contributionURL) throws DomainException {
        // add the contribution information to the domain model
        ContributionModel contributionModel = domainModelFactory.createContribution();
        contributionModel.setContributionURI(contributionURI);
        contributionModel.setContributionURL(contributionURL);
        domainModel.getContributions().put(contributionURI, contributionModel);
        
        // read the assembly model objects.      
        try {
            // Create a local model from the contribution. Using the contribution
            // processor from the domain management runtime just because we already have it
            Contribution contribution =  domainManagementContributionService.contribute(contributionURI, 
                                                                                        new URL(contributionURL), 
                                                                                        false);
            
            contributionModel.setContribution(contribution);
            
            // add the composites into the domain model 
            for (Artifact artifact : contribution.getArtifacts()) {
                if (artifact.getModel() instanceof Composite) {
                    Composite composite = (Composite)artifact.getModel();
                    CompositeModel compositeModel = domainModelFactory.createComposite();
                    compositeModel.setCompositeQName(composite.getName());
                    compositeModel.setComposite(composite);
                    contributionModel.getComposites().put(compositeModel.getCompositeQName(), compositeModel);      
                }
            }                        
            
            // add the deployable composite info to the domain model 
            for (Composite composite : contribution.getDeployables()) {
                CompositeModel compositeModel = contributionModel.getComposites().get(composite.getName());
                
                if (compositeModel != null){
                    contributionModel.getDeployableComposites().put(compositeModel.getCompositeQName(), compositeModel);
                } else {
                    throw new DomainException("Deployable composite name " + 
                                              composite.getName() + 
                                              " doesn't match a composite in the contribution " +
                                              contributionURI );
                }
            }
        } catch(DomainException ex) {   
            throw ex;
        } catch(Exception ex) {
            throw new DomainException(ex);
        } 
        
        return contributionModel;
    }    
    
    
    // SCADomainSPI methods 
    
    public DomainModel getDomainModel(){        
        return domainModel;
    } 
    
    public String getComposite(QName compositeQName){
        CompositeModel compositeModel = domainModel.getDeployedComposites().get(compositeQName);
        Composite composite = compositeModel.getComposite();
        
        ExtensionPointRegistry registry = domainManagementRuntime.getExtensionPointRegistry();
        
        StAXArtifactProcessorExtensionPoint staxProcessors =
            registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        
        StAXArtifactProcessor<Composite> processor = staxProcessors.getProcessor(Composite.class);
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            //outputFactory.setProperty("javax.xml.stream.isPrefixDefaulting",Boolean.TRUE);
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(bos);
            
            processor.write(composite, writer);
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        
        String compositeString = bos.toString();
        
        return compositeString;
    }
    
    public String getComponent(QName compositeQName, String componentName){
        
        String componentString = null;
        
        return componentString;
    }  
    
    
    // SCADomainEventService methods 
    
    public void registerNode(String nodeURI, String nodeURL, Externalizable nodeManagerReference) throws DomainException { 
        // try and remove it first just in case it's already registered
        unregisterNode(nodeURI);
        
        NodeModel node = domainModelFactory.createNode();
        node.setNodeURI(nodeURI);
        node.setNodeURL(nodeURL);
        node.setNodeManagerReference(nodeManagerReference);
        node.setLifecycleState(LifecyleState.AVAILABLE);
        domainModel.getNodes().put(nodeURI, node);     
        
        logger.log(Level.INFO, "Registered node: " + 
                               nodeURI + 
                               " at endpoint " + 
                               nodeURL);
    }
    
    public void unregisterNode(String nodeURI) throws DomainException{ 
        
        domainModel.getNodes().remove(nodeURI);
               
        logger.log(Level.FINE, "Removed node: " + nodeURI);
    }
    
    public void registerNodeStart(String nodeURI) throws DomainException{
/*  TODO - no longer used     
        NodeModel node = domainModel.getNodes().get(nodeURI);
        
        if (node != null) {
            // if the node was started by the domain we already know it's running
            if (node.getLifecycleState() != LifecyleState.RUNNING){
                node.setLifecycleState(LifecyleState.RUNNING);
                
                // run the update in a separate thread so that the caller doesn't block
                scheduler.execute(domainUpdateProcessor);
            }
        } else {
            logger.log(Level.WARNING, "trying to start node: " + 
                                      nodeURI + 
                                      " that isn't registered " );
        }
*/        
    }
    
    public void registerNodeStop(String nodeURI) throws DomainException{
/* TODO - no longer used        
        NodeModel node = domainModel.getNodes().get(nodeURI);
        
        if (node != null) {
            // if the node was stopped by the domain we already know it's running
            if (node.getLifecycleState() == LifecyleState.RUNNING){
                node.setLifecycleState(LifecyleState.DEPLOYED);
                
                // run the update in a separate thread so that the caller doesn't block
                scheduler.execute(domainUpdateProcessor);
            }                
        } else {
            logger.log(Level.WARNING, "trying to stop node: " + 
                                      nodeURI + 
                                      " that isn't registered " );
        }      
*/
    }    

    public void registerContribution(String nodeURI, String contributionURI, String contributionURL) throws DomainException{
        try {
            ContributionModel contributionModel = null;
            
            if ( domainModel.getContributions().containsKey(contributionURI) == false ){
                contributionModel = parseContribution(contributionURI, contributionURL);

                // assign the contribution to the referenced node
                NodeModel node = domainModel.getNodes().get(nodeURI);
                
                if ((node != null) && (contributionModel != null)) {
                    node.getContributions().put(contributionURI, contributionModel);
                } 
            } else {
                // TODO - throw an exception here ?
            }
            
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception when registering contribution " + 
                                     contributionURI + 
                                     ex.toString() );
        }   
    }

    public void unregisterContribution(String nodeURI, String contributionURI) throws DomainException {
        try {
            
            if ( domainModel.getContributions().containsKey(contributionURI) == true ){
                // get the contribution model
                ContributionModel contributionModel = domainModel.getContributions().get(contributionURI);
                
                // remove deployed composites
                for (QName compositeQName : contributionModel.getDeployedComposites().keySet()){
                    domainModel.getDomainLevelComposite().getIncludes().remove(contributionModel.getDeployedComposites().get(compositeQName));
                    domainModel.getDeployedComposites().remove(compositeQName);
                }
                    
                // remove contribution from the domain
                domainModel.getContributions().remove(contributionURI);

                // remove the contribution from the referenced node
                NodeModel nodeModel = domainModel.getNodes().get(nodeURI);
                
                if ((nodeModel != null)) {
                    nodeModel.getContributions().remove(contributionURI);
                    
                    // remove deployed composites
                    for (QName compositeQName : contributionModel.getDeployedComposites().keySet()){
                        nodeModel.getDeployedComposites().remove(compositeQName);
                    }
                } 
            }            
 
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception when removing contribution " + 
                                     contributionURI + 
                                     ex.toString() );
        }
    }
    
    public void registerDomainLevelComposite(String nodeURI, String compositeQNameString) throws DomainException{
        try {
            QName compositeQName = QName.valueOf(compositeQNameString);
            
            if (!domainModel.getDeployedComposites().containsKey(compositeQName)){
                // get the composite from the node
                NodeModel node = domainModel.getNodes().get(nodeURI);
                
                if (node != null) {
                    if (node.getLifecycleState() != LifecyleState.AVAILABLE){
                        throw new DomainException("Node " + nodeURI + " is already marked as active");
                    } else {
                        for (ContributionModel contributionModel : node.getContributions().values()){
                            CompositeModel compositeModel = contributionModel.getComposites().get(compositeQName);
                            
                            if (compositeModel != null){
                                contributionModel.getDeployedComposites().put(compositeQName, compositeModel);
                                node.getDeployedComposites().put(compositeQName, compositeModel);
                                domainModel.getDeployedComposites().put(compositeQName, compositeModel);
                                domainManagementRuntime.getCompositeBuilder().build(compositeModel.getComposite()); 
                                domainModel.getDomainLevelComposite().getIncludes().add(compositeModel.getComposite());
                                

                            }
                        }
                    }
                }   
            } else {
                throw new DomainException("Composite " + compositeQNameString + 
                                          " is already marked as deployed on node " + nodeURI);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception when registering domain level composite " + 
                                     nodeURI +  " " +
                                     compositeQNameString +
                                     ex.toString() );
        }   
    }    
    
    public void registerServiceEndpoint(String domainURI, String nodeURI, String targetServiceName, String bindingClassName, String URL)throws DomainException {
       
        // the target service name is the component/service name combination
        String componentName = null;
        String serviceName = null;
        
        // if the service name ends in a "/" remove it
        if (targetServiceName.endsWith("/")) {
            targetServiceName = targetServiceName.substring(0, targetServiceName.length() - 1);
        } 
        
        // if the service name starts with a "/" remove it
        if (targetServiceName.startsWith("/")) {
            targetServiceName = targetServiceName.substring(1, targetServiceName.length());
        } 
        
        // TODO - only interested if multiplicity is <= 1
        componentName = domainBuilder.getComponentNameFromReference(targetServiceName);
        serviceName = domainBuilder.getServiceNameFromReference(targetServiceName);
        
        domainBuilder.updateDomainLevelServiceURI(domainComposite, targetServiceName, bindingClassName, URL);        
        
        // find the node with the service
        NodeModel node = domainModel.getNodes().get(nodeURI);     
        
        if (node != null){
            
            // collect the service info
            ServiceModel serviceModel = domainModelFactory.createService();
            serviceModel.setServiceURI(targetServiceName);
            serviceModel.setServiceURL(URL);
            serviceModel.setServiceBinding(bindingClassName);
            
            //store the service
            node.getServices().put(targetServiceName+bindingClassName, serviceModel);
            
            if (componentName != null){
                node.getServices().put(componentName+bindingClassName, serviceModel);
            }
            logger.log(Level.INFO, "Registering service: [" + 
                                   domainURI + " " +
                                   targetServiceName + " " +
                                   URL + " " +
                                   bindingClassName + "]");         
        } else {
            logger.log(Level.WARNING, "Trying to register service: " + 
                                      targetServiceName + 
                                      " for a node " + 
                                      nodeURI + 
                                      "that isn't registered");
        }

    }    
     
    public void unregisterServiceEndpoint(String domainURI, String nodeURI, String serviceName, String bindingClassName) throws DomainException{
        NodeModel node = domainModel.getNodes().get(nodeURI);
        node.getServices().remove(serviceName + bindingClassName);
        
        // extract the short name from the long name 
        String shortServiceName = null;
        if (serviceName.indexOf("/") >= 0 ) {
            shortServiceName = serviceName.substring(0, serviceName.indexOf("/"));
        } 
        
        node.getServices().remove(shortServiceName + bindingClassName);
        
        logger.log(Level.FINE, "Removed service: " +  serviceName );   

    }
       
    public String findServiceEndpoint(String domainURI, String serviceName, String bindingName) throws DomainException{
        logger.log(Level.INFO, "Finding service: [" + 
                               domainURI + " " +
                               serviceName + " " +
                               bindingName +
                               "]");
        
        String url = SERVICE_NOT_REGISTERED;
        if (serviceName.startsWith("/")) {
            serviceName = serviceName.substring(1);
        }
        String serviceKey = serviceName + bindingName;
        
        for (NodeModel node : domainModel.getNodes().values()){
            ServiceModel service = node.getServices().get(serviceKey);
            
            if (service != null){
                url = service.getServiceURL();
                // uncomment for debugging
                //url = url.replace("8085", "8086");
                logger.log(Level.INFO, "Found service " + serviceName + " url: " + url); 
                break;
            } 
        }
               
        return url;
    }
    
    public String findServiceNode(String domainURI, String serviceName, String bindingName) throws DomainException{
        logger.log(Level.FINE, "Finding service node: [" + 
                               domainURI + " " +
                               serviceName + " " +
                               bindingName +
                               "]");
        
        String nodeURI = SERVICE_NOT_KNOWN;
        
        for (NodeModel node : domainModel.getNodes().values()){
            Service service = null;
            for (CompositeModel compositeModel : node.getDeployedComposites().values()){
                service = domainBuilder.findServiceForReference(compositeModel.getComposite(), serviceName);
                if (service != null) {
                    nodeURI = node.getNodeURI();
                    break;
                }
            }
        }
               
        return nodeURI;
    }
    
            
        
    // SCADomain API methods 
    
    public void start() throws DomainException {
        
        // notify any top level domain changes before composites are started
        notifyDomainChange();
        
        // call start on all nodes with deployed composites  
        for(NodeModel node : domainModel.getNodes().values()) {
            if ( !node.getDeployedComposites().isEmpty()){
                try {
                    if (node.getLifecycleState() != LifecyleState.RUNNING) {
                        ((NodeModelImpl)node).getSCANodeManagerService().start();
                        node.setLifecycleState(LifecyleState.RUNNING);
                    }
                } catch (Exception ex) {
                    // TODO - collate errors and report
                    ex.printStackTrace();
                }
            }
        }             
    }
    
    public void stop() throws DomainException {
        // call stop on all nodes
        for(NodeModel node : domainModel.getNodes().values()) {
            try {
                if (node.getLifecycleState() == LifecyleState.RUNNING) {
                    ((NodeModelImpl)node).getSCANodeManagerService().stop();
                    node.setLifecycleState(LifecyleState.DEPLOYED);
                }
            } catch (Exception ex) {
                // TODO - collate errors and report
                ex.printStackTrace();
            }
        }         
    }    
    
    public void destroy() throws DomainException {
        try {
           
            // unregister all nodes
            domainModel.getNodes().clear();

            // remove all management components
            Composite composite = domainManagementComposite.getIncludes().get(0);
            
            domainManagementRuntime.getCompositeActivator().stop(composite);
            domainManagementRuntime.getCompositeActivator().deactivate(composite);
            
            // remove the node factory
            ModelFactoryExtensionPoint factories = domainManagementRuntime.getExtensionPointRegistry().getExtensionPoint(ModelFactoryExtensionPoint.class);
            factories.removeFactory(nodeFactory); 
            nodeFactory.setNode(null);
            
            // Stop the SCA runtime that the domain is using
            domainManagementRuntime.stop();

                        
        } catch(ActivationException ex) {
            throw new DomainException(ex); 
        }         
    }
 
    public String getURI(){
        return domainModel.getDomainURI();
    }   
    
    public void addContribution(String contributionURI, URL contributionURL) throws DomainException {
        // add the contribution information to the domain model
        org.apache.tuscany.sca.domain.model.ContributionModel contributionModel = 
            parseContribution(contributionURI, contributionURL.toExternalForm());
    }
     
    public void updateContribution(String contributionURI, URL contributionURL) throws DomainException {
        if ( domainModel.getContributions().containsKey(contributionURI) == true ){

            List<QName> deployedCompositeNames = new ArrayList<QName>();
            
            // record the names of composites that must be restarted after the 
            // contribution has been removed
            for ( NodeModel node : domainModel.getNodes().values()){
                if ((node.getLifecycleState() == LifecyleState.RUNNING) && (node.getContributions().containsKey(contributionURI))) {
                    for (CompositeModel tmpCompositeModel : node.getDeployedComposites().values()){
                        deployedCompositeNames.add(tmpCompositeModel.getCompositeQName());
                    }
                }
            }
            
            // remove the old version of the contribution 
            removeContribution(contributionURI);
            
            // Add the updated contribution back into the domain model
            // TODO - there is a problem here with dependent contributions
            //        as it doesn't look like the contribution listeners
            //        are working quite right
            addContribution(contributionURI, contributionURL);
            
            // add the deployed composites back into the domain if they still exist
            // if they don't then the user will have to add and start any new composites manually
            for (QName compositeQName : deployedCompositeNames) {
                // make sure the composite still exists
                CompositeModel compositeModel = findComposite(compositeQName);
           
                if (compositeModel != null){
                    addToDomainLevelComposite(compositeModel.getCompositeQName());
                } else {
                    // the composite has been removed from the contribution 
                    // by the update
                }
            }
            
            // automatically start all the composites
            for (QName compositeName : deployedCompositeNames) {
                startComposite(compositeName);
            }
        } else {
            throw new DomainException("Contribution " + contributionURI + " not found in domain contributions");
        }        
    }

    public void removeContribution(String contributionURI) throws DomainException {
        if ( domainModel.getContributions().containsKey(contributionURI) == true ){
            
            // get the contribution model
            ContributionModel contributionModel = domainModel.getContributions().get(contributionURI);
            
            // remove potentially deployed composites
            for (QName compositeQName : contributionModel.getDeployableComposites().keySet()){
                domainModel.getDeployedComposites().remove(compositeQName);
                domainModel.getDomainLevelComposite().getIncludes().remove(contributionModel.getDeployableComposites().get(compositeQName));
            }
            
            // remove contribution from the domain model
            domainModel.getContributions().remove(contributionURI);
            
            // remove contribution from the contribution processor
            try {
                domainManagementContributionService.remove(contributionURI);
            } catch (Exception ex){
                throw new DomainException(ex);
            }
            
            // stop and tidy any nodes running this contribution
            for ( NodeModel node : domainModel.getNodes().values()){
                if (node.getContributions().containsKey(contributionURI)) {
                    try {                
                        if (node.getLifecycleState() == LifecyleState.RUNNING) {
                            ((NodeModelImpl)node).getSCANodeManagerService().stop();
                            node.setLifecycleState(LifecyleState.DEPLOYED);
                        }
                        
                        // remove all contributions from this node including the
                        // one that is specifically being removed.
                        for (ContributionModel tmpContributionModel :  node.getContributions().values()){
                            ((NodeModelImpl)node).getSCANodeManagerService().removeContribution(tmpContributionModel.getContributionURI());
                        }
                        
                        node.getContributions().clear();
                        node.getDeployedComposites().clear();
                        node.setLifecycleState(LifecyleState.AVAILABLE);
                        node.getServices().clear();
                    } catch (Exception ex) {
                        // TODO - collate errors and report
                        ex.printStackTrace();
                    }
                }
            }
        } else {
            throw new DomainException("Contribution " + contributionURI + " not found in domain contributions");
        }
    }
    
    public void addDeploymentComposite(String contributionURI, String compositeXML) throws DomainException {
        // TODO
        throw new DomainException("Not yet implemented");        
    }
    
    public void updateDeploymentComposite(String contributionURI, String compositeXML) throws DomainException {
        // TODO 
        throw new DomainException("Not yet implemented");         
    }

    public void addToDomainLevelComposite(QName compositeQName) throws DomainException {
        addToDomainLevelComposite(compositeQName, null);
    }
      
    public void addToDomainLevelComposite(QName compositeQName, String nodeURI) throws DomainException {
        
        try {
            // check to see if this composite has already been added 
            if (domainModel.getDeployedComposites().containsKey(compositeQName) ){
                throw new DomainException("Composite " + compositeQName.toString() + 
                                          " had already been added to the domain level composite");
            }
            
            // find the contribution that has this composite
            ContributionModel contributionModel = findContributionFromComposite(compositeQName);
            
            if (contributionModel == null){
                throw new DomainException("Can't find contribution for composite " + compositeQName.toString());
            }
            
            // find the composite object from the contribution
            CompositeModel compositeModel = contributionModel.getComposites().get(compositeQName);
            
            if (compositeModel == null){
                throw new DomainException("Can't find composite model " + compositeQName.toString() +
                                          " in contribution " + contributionModel.getContributionURI());
            }  
            
            // build the contribution to create the services and references
            domainModel.getDeployedComposites().put(compositeQName, compositeModel);
            domainManagementRuntime.getCompositeBuilder().build(compositeModel.getComposite());                
            domainModel.getDomainLevelComposite().getIncludes().add(compositeModel.getComposite()); 
            
            NodeModel node = null;
            
            // find the node for the composite to run on
            if (nodeURI != null) {
                // find the named node
                node = domainModel.getNodes().get(nodeURI);
                
                if (node == null){
                    throw new DomainException("Node " + nodeURI + " not found in domain");
                }
            } else {
                // noddy algorithm to find a free node
                // TODO - do something better
                for(NodeModel tmpNode : domainModel.getNodes().values()) {
                    if (tmpNode.getLifecycleState() == LifecyleState.AVAILABLE){
                        node = tmpNode;
                    }
                }
                
                if (node == null){
                    throw new DomainException("No free node available to run composite "  + compositeQName.toString());
                }                
            }

            // find all the composites that the node must know about
            List<Contribution> dependentContributions = new ArrayList<Contribution>();
            findDependentContributions(contributionModel.getContribution(), dependentContributions);
             
            // assign the set of contributions to the node model
            for (Contribution tmpContribution : dependentContributions){
                node.getContributions().put(tmpContribution.getURI(), 
                                            domainModel.getContributions().get(tmpContribution.getURI()));
            }
            
            // assign the composite to the node model
            node.getDeployedComposites().put(compositeQName, compositeModel);
            
            node.setLifecycleState(LifecyleState.DEPLOYED);
    
            // now pass this information over to the real node
       
            
            // add contributions. Use the dependent contribution list here rather than the 
            // one built up in the node model to ensure that contributions are added in the correct order
            // I.e. the top most in the dependency tree last. 
            for (Contribution tmpContribution : dependentContributions){
                ((NodeModelImpl)node).getSCANodeManagerService().addContribution(tmpContribution.getURI(),
                         domainModel.getContributions().get(tmpContribution.getURI()).getContributionURL());
            }
    
            // deploy composite
            ((NodeModelImpl)node).getSCANodeManagerService().addToDomainLevelComposite(compositeQName.toString());
                        
            // spray all of the service endpoints from this composite out to interested nodes
            notifyDomainChange(); 
        } catch (DomainException ex) {            
            throw ex;  
        } catch (Exception ex) {
            throw new DomainException(ex);
        }
            
    }    
      
    public void removeFromDomainLevelComposite(QName compositeQName) throws DomainException {

        domainModel.getDomainLevelComposite().getIncludes().remove(domainModel.getDeployedComposites().get(compositeQName).getComposite());
        domainModel.getDeployedComposites().remove(compositeQName);
        
        ContributionModel contributionModel = findContributionFromComposite(compositeQName);
        
        if (contributionModel != null){
            contributionModel.getDeployedComposites().remove(compositeQName);
            
            for(NodeModel node : domainModel.getNodes().values()) {
                if ( node.getDeployedComposites().containsKey(compositeQName)){
                    try {
                        if (node.getLifecycleState() == LifecyleState.RUNNING) {
                            ((NodeModelImpl)node).getSCANodeManagerService().stop();
                            node.setLifecycleState(LifecyleState.DEPLOYED);
                        }
                        // TODO - how to remove it from the node. Remove all contributions???
                        
                        node.getDeployedComposites().remove(compositeQName);
                    } catch (Exception ex) {
                        // TODO - collate errors and report
                        ex.printStackTrace();
                    }                
                }
            } 
        } else {
            throw new DomainException("Composite " + compositeQName.toString() + " not found in domain contributions");
        }
    }
    
    public String getDomainLevelComposite() throws DomainException {
        
        String domainLevelComposite = "<composite xmlns=\"http://www.osoa.org/xmlns/sca/1.0\"" + 
                                      " targetNamespace=\"http://tuscany.apache.org/domain\"" + 
                                      " xmlns:domain=\"http://tuscany.apache.org/domain\"";
        
        int includeCount = 0;
        for (CompositeModel compositeModel : domainModel.getDeployedComposites().values()){
            domainLevelComposite = domainLevelComposite + " xmlns:include" +
                                                          includeCount +
                                                          "=\"" + compositeModel.getCompositeQName().getNamespaceURI() + "\"";
            includeCount++;
        }
        
        domainLevelComposite = domainLevelComposite + " name=\"DomainLevelComposite\">";
           
        includeCount = 0;
        for (CompositeModel compositeModel : domainModel.getDeployedComposites().values()){
            domainLevelComposite = domainLevelComposite + "<include name=\"include" +
                                                           includeCount + 
                                                           ":" + 
                                                           compositeModel.getCompositeQName().getLocalPart() +
                                                           "\"/>";
            includeCount++;
        }
        
        domainLevelComposite = domainLevelComposite + "</composite>";
     
        return domainLevelComposite;
    }

    public String getQNameDefinition(QName artifact) throws DomainException {
        // TODO - no absolutely sure what is intended here as I don't have 
        //        an explicit scenario but here is some code to get me thinking about it
        String artifactString = null;
        
        // find the composite that matches and return its XML
        CompositeModel compositeModel = domainModel.getDeployedComposites().get(artifact);
        
        if (compositeModel != null){
            // convert the composite to XML
        }
        
        return artifactString;
    }
    
    public void startComposite(QName compositeQName) throws DomainException {
        try {
            // find the composite object from the list of deployed composites
            CompositeModel compositeModel = domainModel.getDeployedComposites().get(compositeQName);
            
            if (compositeModel == null){
                throw new DomainException("Can't start composite " + compositeQName.toString() +
                                          " as it hasn't been added to the domain level composite");
            }
            
            // find the contribution that has this composite
            ContributionModel contributionModel = findContributionFromComposite(compositeQName);
            
            if (contributionModel == null){
                throw new DomainException("Can't find contribution for composite " + compositeQName.toString());
            }
            
            NodeModel node = null;
            
            // find the node that has this composite 
            for(NodeModel tmpNode : domainModel.getNodes().values()) {
                if (tmpNode.getDeployedComposites().containsKey(compositeQName)){
                    node = tmpNode;
                    break;
                }
            }
            
            if (node != null){
                if (node.getLifecycleState() == LifecyleState.DEPLOYED){
                 // start the node
                    ((NodeModelImpl)node).getSCANodeManagerService().start();
                    node.setLifecycleState(LifecyleState.RUNNING);
                }
                
            } else {
                // composite not assigned to node for some reason
                throw new DomainException("Composite "  + compositeQName.toString() + 
                                          " is not associated with a node and cannot be started");
            }
        } catch (NodeException ex){
            throw new DomainException(ex);
        }
         
    }
      
    public void stopComposite(QName compositeName) throws DomainException {
        // find the composite object from the list of deployed composites
        CompositeModel composite = domainModel.getDeployedComposites().get(compositeName);
        
        if (composite == null){
            throw new DomainException("Can't stop composite " + compositeName.toString() +
                                      " as it hasn't been added to the domain level composite");
        }
                
        // stop all the nodes running this composite
        for(NodeModel node : domainModel.getNodes().values()) {
            if ( node.getDeployedComposites().containsKey(compositeName)){
                try {
                    if (node.getLifecycleState() == LifecyleState.RUNNING) {
                        node.setLifecycleState(LifecyleState.DEPLOYED);
                        ((NodeModelImpl)node).getSCANodeManagerService().stop();
                    }
                } catch (Exception ex) {
                    // TODO - how to report this?
                }                
            }
        }     
    }
                 
    public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
        return (R)cast(target, domainManagementRuntime);
    }
    
    protected <B, R extends CallableReference<B>> R cast(B target, ReallySmallRuntime runtime) throws IllegalArgumentException {
        return (R)runtime.getProxyFactory().cast(target);
    }

    public <B> B getService(Class<B> businessInterface, String serviceName) {
        return getService( businessInterface, serviceName, domainManagementRuntime, null);
    }
    
    protected <B> B getService(Class<B> businessInterface, String serviceName, ReallySmallRuntime runtime, Composite domainComposite) {
        
        ServiceReference<B> serviceReference = getServiceReference(businessInterface, serviceName, runtime, domainComposite);
        if (serviceReference == null) {
            throw new ServiceRuntimeException("Service not found: " + serviceName);
        }
        return serviceReference.getService();
    }

    protected <B> ServiceReference<B> createServiceReference(Class<B> businessInterface, String targetURI) {
        return createServiceReference(businessInterface, targetURI, domainManagementRuntime, null);
    }

    
    protected <B> ServiceReference<B> createServiceReference(Class<B> businessInterface, String targetURI, ReallySmallRuntime runtime, Composite domainComposite) {
        try {
          
            AssemblyFactory assemblyFactory = runtime.getAssemblyFactory();
            Composite composite = assemblyFactory.createComposite();
            composite.setName(new QName(Constants.SCA10_TUSCANY_NS, "default"));
            RuntimeComponent component = (RuntimeComponent)assemblyFactory.createComponent();
            component.setName("default");
            component.setURI("default");
            runtime.getCompositeActivator().configureComponentContext(component);
            composite.getComponents().add(component);
            RuntimeComponentReference reference = (RuntimeComponentReference)assemblyFactory.createComponentReference();
            reference.setName("default");
            ModelFactoryExtensionPoint factories =
                runtime.getExtensionPointRegistry().getExtensionPoint(ModelFactoryExtensionPoint.class);
            JavaInterfaceFactory javaInterfaceFactory = factories.getFactory(JavaInterfaceFactory.class);
            InterfaceContract interfaceContract = javaInterfaceFactory.createJavaInterfaceContract();
            interfaceContract.setInterface(javaInterfaceFactory.createJavaInterface(businessInterface));
            reference.setInterfaceContract(interfaceContract);
            component.getReferences().add(reference);
            reference.setComponent(component);
            SCABindingFactory scaBindingFactory = factories.getFactory(SCABindingFactory.class);
            SCABinding binding = scaBindingFactory.createSCABinding();
            
            // find the service endpoint somewhere else in the domain
            try {
                String endpointURL = findServiceEndpoint(domainModel.getDomainURI(), 
                                                         targetURI, 
                                                         binding.getClass().getName());
                
                if (endpointURL.equals(SERVICE_NOT_REGISTERED)){
                    logger.log(Level.WARNING, "Created a sevice reference for service that is not yet started: Service " + targetURI); 
                } else {
                    targetURI = endpointURL;
                }
            } catch (DomainException ex){
                throw new ServiceRuntimeException(ex);
            }
            
            binding.setURI(targetURI);
            reference.getBindings().add(binding);       
            return new ServiceReferenceImpl<B>(businessInterface, component, reference, binding, runtime
                .getProxyFactory(), runtime.getCompositeActivator());
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String name) {
        return getServiceReference(businessInterface, name, domainManagementRuntime, null);
    }

        
    protected <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String name, ReallySmallRuntime runtime, Composite domainComposite) {
        
        // Extract the component name
        String componentName;
        String serviceName;
        int i = name.indexOf('/');
        if (i != -1) {
            componentName = name.substring(0, i);
            serviceName = name.substring(i + 1);

        } else {
            componentName = name;
            serviceName = null;
        }

        // Lookup the component 
        Component component = null;
         
        if ( domainComposite != null ) {
            for (Composite composite: domainComposite.getIncludes()) {
                for (Component compositeComponent: composite.getComponents()) {
                    if (compositeComponent.getName().equals(componentName)) {
                        component = compositeComponent;
                    }
                }
            }    
        }
       
        if (component == null) {
            // look to see of the service exists somewhere else in the domain
            try {
                String nodeName = findServiceNode(domainModel.getDomainURI(), 
                                                  name, 
                                                  "org.apache.tuscany.sca.binding.sca.impl.SCABindingImpl");
                
                if (nodeName.equals(SERVICE_NOT_KNOWN)){
                    throw new ServiceRuntimeException("The service " + name + " has not been contributed to the domain");
                }
            } catch (DomainException ex){
                throw new ServiceRuntimeException(ex);
            }
            
            // now create a service reference
            return createServiceReference(businessInterface, name, runtime, domainComposite);
        }
        RuntimeComponentContext componentContext = null;

        // If the component is a composite, then we need to find the
        // non-composite component that provides the requested service
        if (component.getImplementation() instanceof Composite) {
            for (ComponentService componentService : component.getServices()) {
                if (serviceName == null || serviceName.equals(componentService.getName())) {
                    CompositeService compositeService = (CompositeService)componentService.getService();
                    if (compositeService != null) {
                        if (serviceName != null) {
                            serviceName = "$promoted$." + serviceName;
                        }
                        componentContext =
                            ((RuntimeComponent)compositeService.getPromotedComponent()).getComponentContext();
                        return componentContext.createSelfReference(businessInterface, compositeService
                            .getPromotedService());
                    }
                    break;
                }
            }
            // No matching service is found
            throw new ServiceRuntimeException("Composite service not found: " + name);
        } else {
            componentContext = ((RuntimeComponent)component).getComponentContext();
            if (serviceName != null) {
                return componentContext.createSelfReference(businessInterface, serviceName);
            } else {
                return componentContext.createSelfReference(businessInterface);
            }
        }
    } 

}
