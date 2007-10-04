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
import java.util.ArrayList;
import java.util.List;
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
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.core.context.ServiceReferenceImpl;
import org.apache.tuscany.sca.domain.DomainException;
import org.apache.tuscany.sca.domain.DomainManagerInitService;
import org.apache.tuscany.sca.domain.NodeInfo;
import org.apache.tuscany.sca.domain.SCADomainSPI;
import org.apache.tuscany.sca.domain.ServiceInfo;
import org.apache.tuscany.sca.host.embedded.impl.ReallySmallRuntime;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.node.NodeManagerService;
import org.apache.tuscany.sca.node.NodeFactoryImpl;
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
    private ClassLoader domainClassLoader;
    
    // management runtime
    private ReallySmallRuntime domainManagementRuntime;
    private Composite domainManagementComposite;
    private DomainManagerNodeImpl domainManagerNode = new DomainManagerNodeImpl();
    
    // management services
    private DomainManagerInitService domainManagerInitService;
       
    // dummy runtime to give access to sca references that are part of the domain.
    private ReallySmallRuntime domainRuntime;
    
    // the state of the domain
    private String domainURI; 
    private URL domainURL;
    
    private List<NodeInfo> nodes = new ArrayList<NodeInfo>();
    private List<ServiceInfo> services = new ArrayList<ServiceInfo>();
     
    /** 
     * Create a domain giving the URI for the domain. 
     * 
     * @param domainUri - identifies what host and port the domain service is running on, e.g. http://localhost:8081
     * @param nodeUri - if this is a url it is assumed that this will be used as root url for management components, e.g. http://localhost:8082
     * @throws ActivationException
     */
    public SCADomainImpl(String domainURI) throws DomainException {
        this.domainURI = domainURI;
        this.domainClassLoader = SCADomainImpl.class.getClassLoader(); 
        init();
    }    
       
    /**
     * Create the domain management runtime etc
     */
    private void init() throws DomainException {
        try {
            // check whether domain uri is a url
            URI tmpURI;
            try {
                tmpURI = new URI(domainURI); 
                if (tmpURI.isAbsolute()){
                    domainURL = tmpURI.toURL();
                }
            } catch(Exception ex) {
                throw new ActivationException("domain uri " + 
                                              domainURI + 
                                              "must be a valid url");
            }
                
            // create a runtime for the domain management services to run on
            domainManagementRuntime = new ReallySmallRuntime(domainClassLoader);
            domainManagementRuntime.start();
            
            // Create an in-memory domain level composite
            AssemblyFactory assemblyFactory = domainManagementRuntime.getAssemblyFactory();
            domainManagementComposite = assemblyFactory.createComposite();
            domainManagementComposite.setName(new QName(Constants.SCA10_NS, "domainManagement"));
            domainManagementComposite.setURI(domainURI); 
            
            // Set up the domain so that we can push in the node endpoint before we
            // call a node
            ModelFactoryExtensionPoint factories = domainManagementRuntime.getExtensionPointRegistry().getExtensionPoint(ModelFactoryExtensionPoint.class);
            NodeFactoryImpl domainFactory = new NodeFactoryImpl(domainManagerNode);
            factories.addFactory(domainFactory);            
            
            // Find the composite that will configure the domain
            String domainCompositeName = "domain.composite";
            URL contributionURL = SCADomainUtil.findContributionFromComposite(domainClassLoader, domainCompositeName);
            
            if ( contributionURL != null ){ 
                logger.log(Level.INFO, "Domain management configured from " + contributionURL);
                           
                // add node composite to the management domain
                ContributionService contributionService = domainManagementRuntime.getContributionService();
                Contribution contribution = null;

                contribution = contributionService.contribute(domainURI, 
                                                              contributionURL, 
                                                              false);
                
                if (contribution.getDeployables().size() != 0) {
                    Composite composite = contribution.getDeployables().get(0);
                
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
    
    public String addNode(String nodeURI, String nodeURL){ 
        // try and remove it first just in case it's already registered
        removeNode(nodeURI);
        
        NodeInfo nodeInfo = new NodeInfoImpl(nodeURI);
        nodeInfo.setNodeURL(nodeURL);
        nodes.add(nodeInfo);
        logger.log(Level.INFO, "Registered node: " + 
                               nodeURI + 
                               " at endpoint " + 
                               nodeURL);

        return "DummyReturn";
    }
    
    public String removeNode(String nodeURI){ 
        
        List<NodeInfo> nodesToRemove = new ArrayList<NodeInfo>();
        
        for(NodeInfo node : nodes){
            if ( node.match(nodeURI)){
                nodesToRemove.add(node);
            }
        }

        for(NodeInfo nodeToRemove : nodesToRemove){
            nodes.remove(nodeToRemove);
            logger.log(Level.INFO, "Removed node: " + nodeURI);
        }
        
        return "DummyReturn";
    }
    
    public List<NodeInfo> getNodeInfo(){        
        return nodes;
    }
    
    public String  registerServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName, String URL){
        // if the service name ends in a "/" remove it
        String modifiedServiceName = null;
        if ( serviceName.endsWith("/") ) {
            modifiedServiceName = serviceName.substring(0, serviceName.length() - 1);
        } else {
            modifiedServiceName = serviceName;
        }
        
        
        ServiceInfoImpl serviceEndpoint = new ServiceInfoImpl (domainUri, nodeUri, modifiedServiceName, bindingName, URL);
        services.add(serviceEndpoint);
        logger.log(Level.INFO, "Registered service: " + serviceEndpoint.toString());
        return "";
    }
     
    public String  removeServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName){
        
        List<ServiceInfo> serviceEndpointsToRemove = new ArrayList<ServiceInfo>();
        
        for(ServiceInfo serviceEndpoint : services){
            if ( serviceEndpoint.match(domainUri, serviceName, bindingName)){
                serviceEndpointsToRemove.add(serviceEndpoint);
            }
        }
        
        for(ServiceInfo serviceEndpointToRemove : serviceEndpointsToRemove){
            services.remove(serviceEndpointToRemove);
            logger.log(Level.INFO, "Removed service: " +  serviceName );
        }
        
        return "";
    }
   
    public String findServiceEndpoint(String domainUri, String serviceName, String bindingName){
        logger.log(Level.INFO, "Finding service: [" + 
                               domainUri + " " +
                               serviceName + " " +
                               bindingName +
                               "]");
        
        String url = "";
        
        for(ServiceInfo serviceEndpoint : services){
            if ( serviceEndpoint.match(domainUri, serviceName, bindingName)){
                url = serviceEndpoint.getUrl();
                // if you want to temporarily modify the registered port 
                // numbers for debugging uncomment this line
                //url = replacePort(url, "8085", "8086");
                logger.log(Level.INFO, "Found service url: " + url); 
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
    
    public ServiceInfo getServiceInfo(){     
        return services.get(0);
    }
        
        
    // SCADomain API methods 
    
    public void start() throws DomainException {
        // TODO     
    }


    public void stop() throws DomainException {
        try {
            // Stop the node
            domainManagementRuntime.stop();
                        
        } catch(ActivationException ex) {
            throw new DomainException(ex); 
        }         
        
    }    
 
    public String getURI(){
        return domainURI;
    }
    
    public void addContribution(String contributionURI, URL contributionURL) throws DomainException {
        // find a node without a contribution and add it to it
        
        boolean foundFreeNode = false;
        
        for (NodeInfo nodeInfo : nodes) {
            if (nodeInfo.getContributionURI() == null) {
                foundFreeNode = true;
                nodeInfo.setContributionURI(contributionURI);
                nodeInfo.setContributionURL(contributionURL);
                break;
            }
        }
        
        if (foundFreeNode == false){
            throw new DomainException("No free node available for contribution " + 
                                      contributionURI);
        }
    }

    public void removeContribution(String uri) throws DomainException {
          // TODO
    }
    
    public void addComposite(QName compositeName) throws DomainException {
        // find the nodes with this composite and add it. Currently we add it to 
        // all nodes and let the node worry about whether it has the composite
        for (NodeInfo nodeInfo : nodes) {
             nodeInfo.addCompositeName(compositeName);
        }
    }
      
    public void removeComposite(QName qname) throws DomainException {
        // TODO
    }
      
    public void startComposite(QName compositeName) throws DomainException {
        // Start all nodes with this composite
        for (NodeInfo nodeInfo : getNodeInfo()) {
            
            boolean startNode = false;
            
            for (QName nodeCompositeName : nodeInfo.getCompositeNames()){
                if (compositeName.equals(nodeCompositeName) ) {
                    startNode = true;
                }
            }
            
            if (startNode = true){
                // get the endpoint of the node in question and set it into the
                // domain manager node in order to flip the node reference to 
                // the correct endpoint
                String nodeURL = nodeInfo.getNodeURL();
                domainManagerNode.setNodeEndpoint(nodeURL);
                
                
                // get a ode manager service reference. This will have to have its
                // physical enpoint set by the domain node manage we have just 
                // configured
                NodeManagerService nodeManagerService = getService(NodeManagerService.class, 
                                                        "NodeManagerComponent/NodeManagerService",
                                                        domainManagementRuntime, 
                                                        domainManagementComposite);                
                
                // add contribution
                nodeManagerService.addContribution(nodeInfo.getContributionURI(),
                                                   nodeInfo.getContributionURL().toString());
                
                // start composite
                nodeManagerService.startComposite(compositeName.toString());
                
                // start node
                nodeManagerService.start();
            }
            nodeInfo.addCompositeName(compositeName);
       }
         
    }
      
    public void stopComposite(QName qname) throws DomainException {
        // TODO
    }
             
    public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
        /*
        return (R)nodeRuntime.getProxyFactory().cast(target);
        */
        return null;
    }

    public <B> B getService(Class<B> businessInterface, String serviceName) {
        return null;
    }
    
    private <B> B getService(Class<B> businessInterface, String serviceName, ReallySmallRuntime runtime, Composite domainComposite) {
        
        ServiceReference<B> serviceReference = getServiceReference(businessInterface, serviceName, runtime, domainComposite);
        if (serviceReference == null) {
            throw new ServiceRuntimeException("Service not found: " + serviceName);
        }
        return serviceReference.getService();
    }

    private <B> ServiceReference<B> createServiceReference(Class<B> businessInterface, String targetURI) {
        return null;
    }

    
    private <B> ServiceReference<B> createServiceReference(Class<B> businessInterface, String targetURI, ReallySmallRuntime runtime, Composite domainComposite) {
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
        return null;
    }

        
    private <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String name, ReallySmallRuntime runtime, Composite domainComposite) {
        
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
            
        for (Composite composite: domainComposite.getIncludes()) {
            for (Component compositeComponent: composite.getComponents()) {
                if (compositeComponent.getName().equals(componentName)) {
                    component = compositeComponent;
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
