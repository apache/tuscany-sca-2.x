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

package org.apache.tuscany.sca.distributed.node.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.impl.ModelResolverImpl;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.core.context.ServiceReferenceImpl;
import org.apache.tuscany.sca.distributed.domain.Domain;
import org.apache.tuscany.sca.distributed.domain.DomainManagerService;
import org.apache.tuscany.sca.distributed.domain.ServiceDiscoveryService;
import org.apache.tuscany.sca.distributed.node.ComponentManager;
import org.apache.tuscany.sca.distributed.node.ContributionManager;
import org.apache.tuscany.sca.distributed.node.Node;
import org.apache.tuscany.sca.distributed.node.NodeManagerInitService;
import org.apache.tuscany.sca.host.embedded.impl.EmbeddedSCADomain;
import org.apache.tuscany.sca.host.embedded.impl.ReallySmallRuntime;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentContext;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.ServiceRuntimeException;

/**
 * A local representation of the sca domain running on a single node
 * 
 * @version $Rev: 552343 $ $Date$
 */
public class NodeImpl implements Domain, Node {
	
    private final static Logger logger = Logger.getLogger(NodeImpl.class.getName());
	
    final static String LOCAL_DOMAIN_URI = "localdomain";
    final static String LOCAL_NODE_NAME = "localnode";
    
    private boolean isStandalone = false;
    
    // the domain used by the node to talk to the rest of the domain
    private EmbeddedSCADomain managementRuntime;
    
    // class loader used to get the runtime going
    private ClassLoader domainClassLoader;
    
    // representation of the private state of the node that the domain is running on
    private String domainUri;    	
    private String nodeUri;
    private ReallySmallRuntime nodeRuntime;
    private Composite nodeComposite; 
    
    // the managers used to control the domain node
    private ComponentManagerServiceImpl componentManager;
 //   private CompositeManagerImpl compositeManager;
    private ContributionManagerImpl contributionManager;
    
    // the proxies to the domain
    private ServiceDiscoveryService serviceDiscovery;
    private DomainManagerService domainManager;
    private NodeManagerInitService nodeManagerInit;
       
    // methods defined on the implementation only
    
    public NodeImpl()
      throws ActivationException {
        this.domainUri = LOCAL_DOMAIN_URI ; 
        this.nodeUri = LOCAL_NODE_NAME;
        this.isStandalone = true;
        init();
    }
    
    public NodeImpl(String domainUri)
      throws ActivationException {
        this.domainUri = domainUri; 
        this.nodeUri = LOCAL_NODE_NAME;
        this.isStandalone = true;
        init();
    }
    
    public NodeImpl(String domainUri, String nodeUri)
    throws ActivationException {
        this.domainUri = domainUri;
        this.nodeUri = nodeUri;
        this.isStandalone = false;
        init();
    }    
    
    /**
     * Work out if we are representing a domain in memory or can go out to the network to 
     * get domain information. This all depends on whether there is a management
     * composite on the classpath
     */
    private void init()
      throws ActivationException {
        try {
            if (domainClassLoader == null) {
            	domainClassLoader = NodeImpl.class.getClassLoader(); 
            }
            
            // create a node runtime for the domain contributions to run on
            nodeRuntime = new ReallySmallRuntime(domainClassLoader);
            
          
            // Check if node has been given a domain name to connect to
            if (isStandalone) {
            	logger.log(Level.INFO, "Domain node will be started stand-alone as no node name is provided");
            	managementRuntime = null;
            	serviceDiscovery = null;
            } else {
                createManagementNode();
            }
        } catch(ActivationException ex) {
            throw ex;                        
        } catch(Exception ex) {
            throw new ActivationException(ex);
        }
    }
    
    private void createManagementNode()
      throws ActivationException {
        try {
            // create a runtime for components to run on that will be used for talking to the 
            // rest of the domain
            String contributionString = "_node/";
            URL contributionURL = domainClassLoader.getResource(contributionString);
            
            if ( contributionURL != null ){ 
                logger.log(Level.INFO, 
                           "Domain node will use node management contribution from " + 
                           contributionURL);

                
                // start a local domain in order to talk to the logical domain
                managementRuntime = new EmbeddedSCADomain(domainClassLoader, "node");   
                managementRuntime.start();
            
                // add node composite to the management domain
                ContributionService contributionService = managementRuntime.getContributionService();
            
                Contribution contribution = contributionService.contribute(nodeUri, 
                                                                           contributionURL, 
                                                                           false);
                if (contribution.getDeployables().size() != 0) {
                    Composite composite = contribution.getDeployables().get(0);
                
                    managementRuntime.getDomainComposite().getIncludes().add(composite);
                    managementRuntime.getCompositeBuilder().build(composite);
                    
                    // deal with the special case of registering the node manager service 
                    // in service discovery. It's not on an SCA binding. 
                    // TODO - really want to be able to hand out service references but they
                    //        don't serialize out over web services yet. 
                    String nodeManagerUrl = fixUpManagementServiceUrls();                    
                  
                    managementRuntime.getCompositeActivator().activate(composite); 
                    managementRuntime.getCompositeActivator().start(composite);
                
                    // get the management components out of the domain so that they 
                    // can be configured/used. None are yet but this would be the place to 
                    // get components out of the management domain and give them access to 
                    // useful parts of the node
                    serviceDiscovery =  managementRuntime.getService(ServiceDiscoveryService.class, "ServiceDiscoveryComponent");
                    domainManager = managementRuntime.getService(DomainManagerService.class, "DomainManagerComponent");
                    nodeManagerInit = managementRuntime.getService(NodeManagerInitService.class, "NodeManagerComponent/NodeManagerInitService");
                    
                    if (nodeManagerUrl != null) {
                        if (isStandalone == false){
                            try {
                                
                                serviceDiscovery.registerServiceEndpoint(domainUri, 
                                                                         nodeUri, 
                                                                         nodeUri + "NodeManagerService",
                                                                         "",
                                                                         nodeManagerUrl);
                                
                            } catch(Exception ex) {
                                // not sure what to do here
                                logger.log(Level.WARNING,  "Can't connect to domain manager");
                            }
                        }                        
                    }
                        
                } else {
                    throw new ActivationException("Node management contribution " + 
                                                  contributionURL + 
                                                  " found but could not be loaded");
                }
            } else {
                throw new ActivationException("Node contribution " + 
                                              contributionString + 
                                              " not found on the classpath");
            }
        } catch(ActivationException ex) {
            throw ex;                        
        } catch(Exception ex) {
            throw new ActivationException(ex);
        }
    }
    
    /** 
     * A rather ugly method to find out to fix the url of the service, assuming that there
     * is one. 
     *  
     * we can't get is out of a service reference
     * the component itself doesn't know how to get it  
     * the binding can't to do it automatically as it's not he sca binding
     * 
     * TODO - This would be better done by passing out a serializable reference to service discovery 
     *         but this doesn't work yet     
     * 
     * @return node manager url
     */    
    private String fixUpManagementServiceUrls(){
        String nodeManagerUrl = null;
        
        // First get the NodeManager binding from the model 
        List<Component> components = managementRuntime.getDomainComposite().getIncludes().get(0).getComponents();
        Component nodeManagerComponent = null;
        
        for(Component component : components){
            for (ComponentService service : component.getServices() ){
                for (Binding binding : service.getBindings() ) {
                    fixUpBindingUrl(binding);  
                }
                
                if ( service.getName().equals("NodeManagerService")) {
                    nodeManagerUrl = service.getBindings().get(0).getURI();
                }
            }            
        }
        
        return nodeManagerUrl;
    }
    
    /**
     * For http protocol find a port that isn't in use and make sure the domain name is the real domains name
     * 
     * @param binding
     */
    private void fixUpBindingUrl(Binding binding){

        String urlString = binding.getURI(); 
        
        try {
            
            if( (urlString.startsWith("http") != true ) ||
                (binding instanceof SCABinding)) {
                return;
            }
            
            URL url =  new URL(urlString);
            String protocol = url.getProtocol();
            
            // first find a socket that is available starting with what
            // is in the composite file
            int port = url.getPort();
            int startPort = port;
            boolean portIsBusy = true;
            
            do {
                try {
                    ServerSocket socket = new ServerSocket(port);
                    portIsBusy = false;
                    socket.close();
                    break;
                }
                catch (IOException ex) {
                    // the port is busy
                    port = port + 1;
                }
            } while (portIsBusy || port > 9999); 
            
            urlString = urlString.replace(String.valueOf(startPort), String.valueOf(port));
            
            // now replace localhost, if its there,  with the real host name
            InetAddress address = InetAddress.getLocalHost();
            urlString = urlString.replace("localhost", address.getHostName());
            
            // set the address back into the NodeManager binding.
            binding.setURI(urlString);
        
        } catch (Exception ex) {
            // don't do anything and leave the address as is
            logger.log(Level.WARNING, 
                       "Exception while fixing up binding url in management composite " + 
                       urlString, 
                       ex);
        }
    }    
    
        
    // methods that implement interfaces 
    
    public void start() throws ActivationException {
        // Start the runtime
        nodeRuntime.start();
        
        // Create an in-memory domain level composite
        AssemblyFactory assemblyFactory = nodeRuntime.getAssemblyFactory();
        nodeComposite = assemblyFactory.createComposite();
        nodeComposite.setName(new QName(Constants.SCA10_NS, "domain"));
        nodeComposite.setURI(domainUri);
        
        // add the top level composite into the composite activator
        nodeRuntime.getCompositeActivator().setDomainComposite(nodeComposite);  
        
        // make the domain available to the model. 
        // TODO - No sure how this should be done properly. As a nod to this though
        //        I have a domain factory which always returns the same domain
        //        object
        ModelFactoryExtensionPoint factories = nodeRuntime.getExtensionPointRegistry().getExtensionPoint(ModelFactoryExtensionPoint.class);
        DomainFactoryImpl domainFactory = new DomainFactoryImpl(this);
        factories.addFactory(domainFactory);
        
        // create the domain node managers
        componentManager = new ComponentManagerServiceImpl(domainUri, nodeUri, nodeComposite, nodeRuntime);
        contributionManager = new ContributionManagerImpl(domainUri, nodeUri, nodeComposite, nodeRuntime, domainClassLoader, null);
        
        
        if (isStandalone == false){
            // pass this object into the node manager
            nodeManagerInit.setNode((Node)this);
            
            try {
                // go out an add this node to the wider domain
                domainManager.registerNode(domainUri, nodeUri);
            } catch(Exception ex) {
                // not sure what to do here
                logger.log(Level.WARNING,  "Can't connect to domain manager");
            }
        }
    }

    public void stop() throws ActivationException {
        // Stop the node
    	nodeRuntime.stop();
        
        // Cleanup the top level composite
        nodeComposite = null;
        
        // remove the manager objects
        
        // go out and remove this node from the wider domain
        if (isStandalone == false){
            try {
                domainManager.removeNode(domainUri, nodeUri);
            } catch(Exception ex) {
                // not sure what to do here
                logger.log(Level.WARNING,  "Can't connect to domain manager");
            }
        }
    }    
 
    public String getDomainUri(){
        return domainUri;
    }
    
    public String getNodeUri(){
        return nodeUri;
    }    
    
    public ComponentManager getComponentManager() {
        return componentManager;
    } 

/*
    public CompositeManager getCompositeManager() {
        return compositeManager;
    }
*/
    
    public ContributionManager getContributionManager() {    	
        return contributionManager;
    }     
    
    public ContributionManager getContributionManager(ClassLoader classLoader) {
        
    	return new ContributionManagerImpl(domainUri, nodeUri, nodeComposite, nodeRuntime, classLoader, new ModelResolverImpl(classLoader));
    }      

    
    /**
     * Return an interface for registering local services and for
     * finding remote services
     * 
     * @return The service discovery interface
     */    
    public ServiceDiscoveryService getServiceDiscovery(){
        return serviceDiscovery;
    }
       
    public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
        return (R)nodeRuntime.getProxyFactory().cast(target);
    }

    public <B> B getService(Class<B> businessInterface, String serviceName) {
        ServiceReference<B> serviceReference = getServiceReference(businessInterface, serviceName);
        if (serviceReference == null) {
            throw new ServiceRuntimeException("Service not found: " + serviceName);
        }
        return serviceReference.getService();
    }

    private <B> ServiceReference<B> createServiceReference(Class<B> businessInterface, String targetURI) {
        try {
            AssemblyFactory assemblyFactory = nodeRuntime.getAssemblyFactory();
            Composite composite = assemblyFactory.createComposite();
            composite.setName(new QName(Constants.SCA10_TUSCANY_NS, "default"));
            RuntimeComponent component = (RuntimeComponent)assemblyFactory.createComponent();
            component.setName("default");
            component.setURI("default");
            nodeRuntime.getCompositeActivator().configureComponentContext(component);
            composite.getComponents().add(component);
            RuntimeComponentReference reference = (RuntimeComponentReference)assemblyFactory.createComponentReference();
            reference.setName("default");
            ModelFactoryExtensionPoint factories =
            	nodeRuntime.getExtensionPointRegistry().getExtensionPoint(ModelFactoryExtensionPoint.class);
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
            return new ServiceReferenceImpl<B>(businessInterface, component, reference, binding, nodeRuntime
                .getProxyFactory(), nodeRuntime.getCompositeActivator());
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String name) {

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

        // Lookup the component in the domain
        Component component = componentManager.getComponent(componentName);
        if (component == null) {
            // The component is not local in the partition, try to create a remote service ref
            return createServiceReference(businessInterface, name);
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
