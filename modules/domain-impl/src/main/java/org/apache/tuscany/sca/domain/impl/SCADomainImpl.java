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

import java.io.Externalizable;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.DeployedArtifact;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.core.context.ServiceReferenceImpl;
import org.apache.tuscany.sca.domain.DomainException;
import org.apache.tuscany.sca.domain.SCADomain;
import org.apache.tuscany.sca.domain.SCADomainSPI;
import org.apache.tuscany.sca.domain.SCADomainEventService;
import org.apache.tuscany.sca.domain.management.SCADomainManagerInitService;
import org.apache.tuscany.sca.domain.model.CompositeModel;
import org.apache.tuscany.sca.domain.model.ContributionModel;
import org.apache.tuscany.sca.domain.model.DomainModel;
import org.apache.tuscany.sca.domain.model.DomainModelFactory;
import org.apache.tuscany.sca.domain.model.NodeModel;
import org.apache.tuscany.sca.domain.model.ServiceModel;
import org.apache.tuscany.sca.domain.model.impl.DomainModelFactoryImpl;
import org.apache.tuscany.sca.host.embedded.impl.ReallySmallRuntime;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletHostExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.node.NodeFactoryImpl;
import org.apache.tuscany.sca.node.management.SCANodeManagerService;
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
    
    // management runtime
    protected ReallySmallRuntime domainManagementRuntime;
    protected ContributionService domainManagementContributionService;
    protected Contribution domainManagementContribution;
    protected Composite domainManagementComposite;
          
    // The domain model
    protected DomainModelFactory domainModelFactory = new DomainModelFactoryImpl();
    protected DomainModel domainModel;
    
    // management services
    private SCADomainManagerInitService domainManagerInitService;    
     
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
            // check whether domain uri is a url
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
            domainManagementComposite.setURI(domainModel.getDomainURI() + "/Management");            
            
            // Set up the domain so that local callable references can find 
            // service out there in the domain
            SCADummyNodeImpl node = new SCADummyNodeImpl(this);
            ModelFactoryExtensionPoint factories = domainManagementRuntime.getExtensionPointRegistry().getExtensionPoint(ModelFactoryExtensionPoint.class);
            NodeFactoryImpl nodeFactory = new NodeFactoryImpl(node);
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
                
                Composite composite = null;
                for (DeployedArtifact artifact: contribution.getArtifacts()) {
                    if (domainCompositeName.equals(artifact.getURI())) {
                        composite = (Composite)artifact.getModel();
                    }
                }
                
                if (composite != null) {
                
                    domainManagementComposite.getIncludes().add(composite);
                    domainManagementRuntime.getCompositeBuilder().build(composite);
                    domainManagementRuntime.getCompositeActivator().activate(composite); 
                    domainManagementRuntime.getCompositeActivator().start(composite);
                
                    // get the management components out of the domain so that they 
                    // can be configured/used. 
                    domainManagerInitService = getService(SCADomainManagerInitService.class, 
                                                          "SCADomainManagerComponent/SCADomainManagerInitService", 
                                                          domainManagementRuntime, 
                                                          domainManagementComposite);
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
    
    public Component getComponent(String componentName) {
        for (Composite composite: domainManagementComposite.getIncludes()) {
            for (Component component: composite.getComponents()) {
                if (component.getName().equals(componentName)) {
                    return component;
                }
            }
        }
        return null;
    }       
    
    // SCADomainSPI methods 
    
    public DomainModel getDomainModel(){        
        return domainModel;
    } 
    
    // SCADomainEventService methods 
    
    public void registerNode(String nodeURI, String nodeURL, Externalizable nodeManagerReference) throws DomainException { 
        // try and remove it first just in case it's already registered
        unregisterNode(nodeURI);
        
        NodeModel node = domainModelFactory.createNode();
        node.setNodeURI(nodeURI);
        node.setNodeURL(nodeURL);
        node.setNodeManagerReference(nodeManagerReference);
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
    

    public void registerContribution(String nodeURI, String contributionURI, String contributionURL) throws DomainException{
        try {
            ContributionModel contributionModel = null;
            
            if ( domainModel.getContributions().containsKey(contributionURI) == false ){
                contributionModel = parseContribution(contributionURI, contributionURL);

                // add contribution to the domain
                domainModel.getContributions().put(contributionURI, contributionModel);

                // assign the contribution to the referenced node
                NodeModel node = domainModel.getNodes().get(nodeURI);
                
                if ((node != null) && (contributionModel != null)) {
                    node.getContributions().put(contributionURI, contributionModel);
                } 
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
                for (QName compositeQName : contributionModel.getDeployableComposites().keySet()){
                    domainModel.getDeployedComposites().remove(compositeQName);
                }
                    
                // remove contribution from the domain
                domainModel.getContributions().remove(contributionURI);

                // remove the contribution from the referenced node
                NodeModel node = domainModel.getNodes().get(nodeURI);
                
                if ((node != null)) {
                    node.getContributions().remove(contributionURI);
                } 
            }            
 
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception when removing contribution " + 
                                     contributionURI + 
                                     ex.toString() );
        }
    }
    
    public void registerServiceEndpoint(String domainURI, String nodeURI, String serviceName, String bindingName, String URL)throws DomainException {
        // if the service name ends in a "/" remove it
        String modifiedServiceName = null;
        if ( serviceName.endsWith("/") ) {
            modifiedServiceName = serviceName.substring(0, serviceName.length() - 1);
        } else {
            modifiedServiceName = serviceName;
        }
        
        // if the service name starts with a "/" remove it
        if ( modifiedServiceName.startsWith("/") ) {
            modifiedServiceName = modifiedServiceName.substring(1, serviceName.length());
        } 
        
        // collect the service info
        ServiceModel service = domainModelFactory.createService();
        service.setServiceURI(modifiedServiceName);
        service.setServiceURL(URL);
        service.setServiceBinding(bindingName);
        
        // find the node
        NodeModel node = domainModel.getNodes().get(nodeURI);
        
        if (node != null){
            //store the service
            node.getServices().put(modifiedServiceName+bindingName, service);
            logger.log(Level.FINE, "Registered service: " + modifiedServiceName + " with URL " + URL);
        } else {
            logger.log(Level.WARNING, "Trying to register service: " + 
                                      modifiedServiceName + 
                                      " for a node " + 
                                      nodeURI + 
                                      "that isn't registered");
        }
    }
     
    public void unregisterServiceEndpoint(String domainUri, String nodeURI, String serviceName, String bindingName) throws DomainException{
        NodeModel node = domainModel.getNodes().get(nodeURI);
        node.getServices().remove(serviceName + bindingName);
        logger.log(Level.FINE, "Removed service: " +  serviceName );     
        
    }
   
    public String findServiceEndpoint(String domainUri, String serviceName, String bindingName) throws DomainException{
        logger.log(Level.FINE, "Finding service: [" + 
                               domainUri + " " +
                               serviceName + " " +
                               bindingName +
                               "]");
        
        String url = "";
        String serviceKey = serviceName + bindingName;
        
        for (NodeModel node : domainModel.getNodes().values()){
            ServiceModel service = node.getServices().get(serviceKey);
            
            if (service != null){
                url = service.getServiceURL();
                // uncomment for debugging
                //url = url.replace("8085", "8086");
                logger.log(Level.FINE, "Found service url: " + url); 
                break;
            }
        }
               
        return url;
    }
            
        
    // SCADomain API methods 
    
    public void start() throws DomainException {
        // call start on all nodes
        
    }
    
    public void stop() throws DomainException {

    }    
    
    public void destroy() throws DomainException {
        try {
            // Stop the node
            domainManagementRuntime.stop();
            
            // TODO - remove all components first
                        
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
        
        // contributions are not assigned to a node until a composite
        // in the contribution is started. 
    }
     
    public void updateContribution(String contributionURI, URL contributionURL) throws DomainException {
        // TODO
    }

    public void removeContribution(String contributionURI) throws DomainException {
        if ( domainModel.getContributions().containsKey(contributionURI) == true ){
            
            // get the contribution model
            ContributionModel contributionModel = domainModel.getContributions().get(contributionURI);
            
            // remove deployed composites
            for (QName compositeQName : contributionModel.getDeployableComposites().keySet()){
                domainModel.getDeployedComposites().remove(compositeQName);
            }
            
            domainModel.getContributions().remove(contributionURI);
            
            for ( NodeModel node : domainModel.getNodes().values()){
                if (node.getContributions().containsKey(contributionURI)) {
                    // TODO remove composite info
                    node.getContributions().remove(contributionURI);
                }
            }
        }
    }
    
    public void addDeploymentComposite(String contributionURI, String compositeXML) throws DomainException {
        // TODO
    }
    
    public void updateDeploymentComposite(String contributionURI, String compositeXML) throws DomainException {
        
    }
    
    public void addToDomainLevelComposite(QName compositeName) throws DomainException {
        // find this composite and add the composite as a deployed composite
        
        for (ContributionModel contribution : domainModel.getContributions().values()){
            CompositeModel composite = contribution.getDeployableComposites().get(compositeName);
            if (composite != null) {
                domainModel.getDeployedComposites().put(compositeName, composite);
            }
        }
    }
      
    public void removeFromDomainLevelComposite(QName compositeName) throws DomainException {
        // TODO
        // remove from node 
        // remove from deployed list
        // if the node has only one composite remove contribution from node
        domainModel.getDeployedComposites().remove(compositeName);
    }
    
    public String getDomainLevelComposite() throws DomainException {
        return null;
    }

    public String getQNameDefinition(QName artifact) throws DomainException {
        return null;
    }
      
    public void startComposite(QName compositeName) throws DomainException {
        
        // find the composite object from the list of deployed composites
        CompositeModel composite = domainModel.getDeployedComposites().get(compositeName);
        
        if (composite == null){
            throw new DomainException("Can't start composite " + compositeName.toString() +
                                      " as it hasn't been added to the domain level composite");
        }
        
        ContributionModel contribution = null;
        
        // find the contribution that has this composite
        for (ContributionModel tmpContribution : domainModel.getContributions().values()){
            if (tmpContribution.getDeployableComposites().containsKey(compositeName)){
                contribution = tmpContribution;
            }
        }
        
        if (contribution == null){
            throw new DomainException("Can't find contribution for composite " + compositeName.toString());
        }
        
        // assign the composite to a node
        NodeModel node = null;
        
        for(NodeModel tmpNode : domainModel.getNodes().values()) {
            if ( tmpNode.getContributions().isEmpty()){
                node = tmpNode;
                node.getContributions().put(contribution.getContributionURI(), contribution);
                break;
            }
        }      
        
        if (node == null){
            throw new DomainException("No free node available for to run composite "  + compositeName.toString());
        }        
        
        // get the node manager for the node in question
        CallableReference<SCANodeManagerService> nodeManagerReference = 
            (CallableReference<SCANodeManagerService>)node.getNodeManagerReference();
        SCANodeManagerService nodeManagerService = nodeManagerReference.getService();
        
        try {
            // add contributions
            for (ContributionModel tmpContribution : node.getContributions().values()){
                nodeManagerService.addContribution(contribution.getContributionURI(),
                                                   contribution.getContributionURL().toString());
            }
    
            // deploy composite
            nodeManagerService.addToDomainLevelComposite(compositeName.toString());
            
            // start node
            nodeManagerService.start();
            
            // TODO
            // somewhere we need to add the deployed composites into the node model 
            
        } catch (Exception ex) {
            throw new DomainException(ex);
        }
            
    }
      
    public void stopComposite(QName qname) throws DomainException {
        // stop the node running the composite. 
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
            for (DeployedArtifact artifact : contribution.getArtifacts()) {
                if (artifact.getModel() instanceof Composite) {
                    Composite composite = (Composite)artifact.getModel();
                    CompositeModel compositeModel = domainModelFactory.createComposite();
                    compositeModel.setCompositeQName(composite.getName());
                    contributionModel.getComposites().put(compositeModel.getCompositeQName(), compositeModel);
                    
                }
            }                        
            
            // add the deployable composite info to the domain model 
            for (Composite composite : contribution.getDeployables()) {
                CompositeModel compositeModel = contributionModel.getComposites().get(composite.getName());
                contributionModel.getDeployableComposites().put(compositeModel.getCompositeQName(), compositeModel);
            }
            
        } catch(Exception ex) {
            throw new DomainException(ex);
        } 
        
        return contributionModel;
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
            // The component is not local in the partition, try to create a remote service ref
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
