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
import org.apache.tuscany.sca.domain.DomainManagerInitService;
import org.apache.tuscany.sca.domain.SCADomainSPI;
import org.apache.tuscany.sca.domain.model.Domain;
import org.apache.tuscany.sca.domain.model.DomainModelFactory;
import org.apache.tuscany.sca.domain.model.Node;
import org.apache.tuscany.sca.domain.model.Service;
import org.apache.tuscany.sca.domain.model.impl.DomainModelFactoryImpl;
import org.apache.tuscany.sca.host.embedded.impl.ReallySmallRuntime;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletHostExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.node.NodeFactoryImpl;
import org.apache.tuscany.sca.node.NodeManagerService;
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
public class SCADomainImpl implements SCADomainSPI  {
	
    private final static Logger logger = Logger.getLogger(SCADomainImpl.class.getName());
	     
    // class loader used to get the runtime going
    protected ClassLoader domainClassLoader;
    
    // management runtime
    protected ReallySmallRuntime domainManagementRuntime;
    protected ContributionService domainManagementContributionService;
    protected Composite domainManagementComposite;
    protected DomainManagerNodeImpl domainManagerNode;
          
    // The domain model
    protected DomainModelFactory domainModelFactory = new DomainModelFactoryImpl();
    protected Domain domainModel;
    protected HashMap<String, Contribution> contributions = new HashMap<String, Contribution>();
    
    // management services
    private DomainManagerInitService domainManagerInitService;    
     
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
            
            // Set up the domain so that we can push in the node endpoint before we
            // call a node
            domainManagerNode = new DomainManagerNodeImpl(this);
            ModelFactoryExtensionPoint factories = domainManagementRuntime.getExtensionPointRegistry().getExtensionPoint(ModelFactoryExtensionPoint.class);
            NodeFactoryImpl domainFactory = new NodeFactoryImpl(domainManagerNode);
            factories.addFactory(domainFactory);            
            
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
                    
                    // TODO fix up the domain manager URI to match the provided 
                    //      domain uri

                    domainManagementRuntime.getCompositeActivator().activate(composite); 
                    domainManagementRuntime.getCompositeActivator().start(composite);
                
                    // get the management components out of the domain so that they 
                    // can be configured/used. 
                    domainManagerInitService = getService(DomainManagerInitService.class, 
                                                          "DomainManagerComponent/DomainManagerInitService", 
                                                          domainManagementRuntime, 
                                                          domainManagementComposite);
                    domainManagerInitService.setDomain((SCADomainSPI)this);
                    
                        
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
    
    // SCADomain SPI methods 
    
    public Domain getDomainModel(){        
        return domainModel;
    }    
    
    public String addNode(String nodeURI, String nodeURL){ 
        // try and remove it first just in case it's already registered
        removeNode(nodeURI);
        
        Node node = domainModelFactory.createNode();
        node.setNodeURI(nodeURI);
        node.setNodeURL(nodeURL);
        domainModel.getNodes().put(nodeURI, node);     
        
        logger.log(Level.INFO, "Registered node: " + 
                               nodeURI + 
                               " at endpoint " + 
                               nodeURL);

        return "DummyReturn";
    }
    
    public String removeNode(String nodeURI){ 
        
        domainModel.getNodes().remove(nodeURI);
               
        logger.log(Level.INFO, "Removed node: " + nodeURI);
        
        return "DummyReturn";
    }
    

    public void registerContribution(String nodeURI, String contributionURI, String contributionURL){
        try {
            if ( domainModel.getContributions().containsKey(contributionURI) == false ){
                // add the contribution information to the domain model
                org.apache.tuscany.sca.domain.model.Contribution contributionModel = 
                    parseContribution(contributionURI, contributionURL);
            }

        
            // assign the contribution to the referenced node
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception when registering contribution " + 
                                     contributionURI + 
                                     ex.toString() );
        }
        
    }
    

    public void unregisterContribution(String contributionURI){
        // TODO
        
    }
    
    public String  registerServiceEndpoint(String domainURI, String nodeURI, String serviceName, String bindingName, String URL){
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
        Service service = domainModelFactory.createService();
        service.setServiceURI(modifiedServiceName);
        service.setServiceURL(URL);
        service.setServiceBinding(bindingName);
        
        // find the node
        Node node = domainModel.getNodes().get(nodeURI);
        
        if (node != null){
            //store the service
            node.getServices().put(modifiedServiceName+bindingName, service);
            logger.log(Level.INFO, "Registered service: " + modifiedServiceName);
        } else {
            logger.log(Level.WARNING, "Trying to register service: " + 
                                      modifiedServiceName + 
                                      " for a node " + 
                                      nodeURI + 
                                      "that isn't registered");
        }
        
        return "";
    }
     
    public String  removeServiceEndpoint(String domainUri, String nodeURI, String serviceName, String bindingName){
        Node node = domainModel.getNodes().get(nodeURI);
        node.getServices().remove(serviceName + bindingName);
        logger.log(Level.INFO, "Removed service: " +  serviceName );     
        
        return "";
    }
   
    public String findServiceEndpoint(String domainUri, String serviceName, String bindingName){
        logger.log(Level.INFO, "Finding service: [" + 
                               domainUri + " " +
                               serviceName + " " +
                               bindingName +
                               "]");
        
        String url = "";
        String serviceKey = serviceName + bindingName;
        
        for (Node node : domainModel.getNodes().values()){
            Service service = node.getServices().get(serviceKey);
            
            if (service != null){
                url = service.getServiceURL();
                //url = replacePort(url, "8085", "8086");
                logger.log(Level.INFO, "Found service url: " + url); 
                break;
            }
        }
               
        return url;
    }
    
    /**
     * Converts a port number to something else to allow for debugging using a
     * HTTP sniffer
     * @param url
     * @param fromPort the port to look for
     * @param toPort the port to replace it with
     * @return the new url
     */
    private String replacePort(String url, String fromPort, String toPort) {
        return url.replace(fromPort, toPort);
    }
        
        
    // SCADomain API methods 
    
    public void start() throws DomainException {
        // Does nothing in the domain implementation but 
        // is used in the proxy
    }
    
    public void destroy() throws DomainException {
        try {
            // Stop the node
            domainManagementRuntime.stop();
                        
        } catch(ActivationException ex) {
            throw new DomainException(ex); 
        }         
    }
 
    public String getURI(){
        return domainModel.getDomainURI();
    }
    
    private org.apache.tuscany.sca.domain.model.Contribution parseContribution(String contributionURI, String contributionURL) throws DomainException {
        // add the contribution information to the domain model
        org.apache.tuscany.sca.domain.model.Contribution contributionModel = domainModelFactory.createContribution();
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
            
            // store the contribution
            contributions.put(contributionURI, contribution);
            
            // add the composite info to the domain model 
            for (DeployedArtifact artifact : contribution.getArtifacts()) {
                if (artifact.getModel() instanceof Composite) {
                    Composite composite = (Composite)artifact.getModel();
                    org.apache.tuscany.sca.domain.model.Composite compositeModel = 
                        domainModelFactory.createComposite();
                    compositeModel.setCompositeQName(composite.getName());
                    contributionModel.getComposites().put(compositeModel.getCompositeQName(), compositeModel);
                    
                }
            }            
            
            // add all composites into the domain model
            for (Composite composite : contribution.getDeployables()) {
                org.apache.tuscany.sca.domain.model.Composite compositeModel = 
                    domainModelFactory.createComposite();
                compositeModel.setCompositeQName(composite.getName());
                contributionModel.getComposites().put(compositeModel.getCompositeQName(), compositeModel);
            }            
            
            // add the deployable composite info to the domain model 
            for (Composite composite : contribution.getDeployables()) {
                org.apache.tuscany.sca.domain.model.Composite compositeModel = 
                    contributionModel.getComposites().get(composite.getName());
                contributionModel.getDeployableComposites().put(compositeModel.getCompositeQName(), compositeModel);
                domainModel.getDeployedComposites().put(compositeModel.getCompositeQName(), compositeModel);
            }
            
        } catch(Exception ex) {
            throw new DomainException(ex);
        } 
        
        return contributionModel;
    }
    
    private void assignContributionToNode(org.apache.tuscany.sca.domain.model.Contribution contributionModel) throws DomainException {
        // Find a node to run the contribution. 
        // TODO - add some more sophisticated algorithm here
        // find a node without a contribution and add it to it. There is no deployment
        // step here we just assume the contribution is available. 
        
        boolean foundFreeNode = false;
        
        for(Node node : domainModel.getNodes().values()) {
            if ( node.getContributions().isEmpty()){
                foundFreeNode = true;
                node.getContributions().put(contributionModel.getContributionURI(), contributionModel);
                break;
            }
        }      
        
        if (foundFreeNode == false){
            throw new DomainException("No free node available for contribution " + 
                                      contributionModel.getContributionURI());
        }
    }
    
    public void addContribution(String contributionURI, URL contributionURL) throws DomainException {
        // add the contribution information to the domain model
        org.apache.tuscany.sca.domain.model.Contribution contributionModel = 
            parseContribution(contributionURI, contributionURL.toExternalForm());

        assignContributionToNode(contributionModel);
    }

    public void removeContribution(String uri) throws DomainException {
        
        // TODO
    }
    
    public void addDeploymentComposite(String contributionURI, String compositeXML) throws DomainException {
        // TODO
    }
    
    public void addToDomainLevelComposite(QName compositeName) throws DomainException {
        // find the nodes with this composite and add the composite as a deployable composite
        for ( Node node : domainModel.getNodes().values()) {
            for (org.apache.tuscany.sca.domain.model.Contribution contribution : node.getContributions().values()){
                org.apache.tuscany.sca.domain.model.Composite composite = 
                    contribution.getComposites().get(compositeName);
                if (composite != null) {
                    contribution.getDeployableComposites().put(compositeName, composite);
                    domainModel.getDeployedComposites().put(compositeName, composite);
                }
            }
        }
    }
      
    public void removeFromDomainLevelComposite(QName qname) throws DomainException {
        // TODO
    }
      
    public void startComposite(QName compositeName) throws DomainException {
        for (Node node : domainModel.getNodes().values()){
            boolean startNode = false;
            
            for (org.apache.tuscany.sca.domain.model.Contribution contribution : node.getContributions().values()){
                org.apache.tuscany.sca.domain.model.Composite composite = 
                    contribution.getDeployableComposites().get(compositeName);
                if (composite != null) {
                    startNode = true;
                    break;
                }
            }
            
            if (startNode == true){
                // get the endpoint of the node in question and set it into the
                // domain manager node in order to flip the node reference to 
                // the correct endpoint
                String nodeURL = node.getNodeURL();
                domainManagerNode.setNodeEndpoint(nodeURL);
                
                
                // get a node manager service reference. This will have to have its
                // physical endpoint set by the domain node manage we have just 
                // configured
                NodeManagerService nodeManagerService = getService(NodeManagerService.class, 
                                                        "NodeManagerComponent/NodeManagerService",
                                                        domainManagementRuntime, 
                                                        domainManagementComposite);                
                
                // add contributions
                for (org.apache.tuscany.sca.domain.model.Contribution contribution : node.getContributions().values()){
                    nodeManagerService.addContribution(contribution.getContributionURI(),
                                                       contribution.getContributionURL().toString());
                }

                // deploy composite
                nodeManagerService.deployComposite(compositeName.toString());
                
                // start node
                nodeManagerService.start();
                
                // TODO
                // somewhere we need to add the deployed composites into the node model 
                
                // reset the endpoint setting function
                domainManagerNode.setNodeEndpoint(null);
            }        
        }    
    }
      
    public void stopComposite(QName qname) throws DomainException {
        // TODO
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
