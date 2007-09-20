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

package org.apache.tuscany.sca.node.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
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
import org.apache.tuscany.sca.node.ComponentManager;
import org.apache.tuscany.sca.node.ContributionManager;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.NodeManagerInitService;
import org.apache.tuscany.sca.domain.SCADomain;
import org.apache.tuscany.sca.domain.DomainManagerService;
import org.apache.tuscany.sca.domain.SCADomainService;
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
 * @version $Rev: 552343 $ $Date: 2007-09-09 23:54:46 +0100 (Sun, 09 Sep 2007) $
 */
public class SCANodeImpl extends SCADomain implements SCANode {
	
    private final static Logger logger = Logger.getLogger(SCANodeImpl.class.getName());
	
    public final static String LOCAL_DOMAIN_URI = "standalonedomain";
    public final static String LOCAL_NODE_URI = "standalonenode";
    
    private boolean isStandalone = false;
    
    // the domain used by the node to talk to the rest of the domain
    private EmbeddedSCADomain managementRuntime;
    
    // class loader used to get the runtime going
    private ClassLoader domainClassLoader;
    
    // class loader used to get application resources
    private ClassLoader applicationClassLoader;    
    
    // representation of the private state of the node that the domain is running on
    private String domainUri; 
    private URL domainUrl;
    private String nodeUri;
    private URL nodeUrl;
    private ReallySmallRuntime nodeRuntime;
    private Composite nodeComposite; 
    
    // the managers used to control the domain node
    private ComponentManagerServiceImpl componentManager;
    private ContributionManagerImpl contributionManager;
    
    // the proxies to the domain
    private SCADomainService scaDomain;
    private DomainManagerService domainManager;
    private NodeManagerInitService nodeManagerInit;
       
    // methods defined on the implementation only
    
    /**
     * Default constructor creates a standalone node with no connectivity to a wider
     * domain and no local web page. 
     */
    public SCANodeImpl()
      throws ActivationException {
        this.domainUri = LOCAL_DOMAIN_URI ; 
        this.nodeUri = LOCAL_NODE_URI;
        this.domainClassLoader = SCANodeImpl.class.getClassLoader(); 
        this.applicationClassLoader = Thread.currentThread().getContextClassLoader();                
        this.isStandalone = true;
        init();
    }
       
    /** 
     * Creates a node connected to a wider domain.  To find its place in the domain 
     * node and domain identifiers must be provided. 
     * 
     * @param domainUri - identifies what host and port the domain service is running on, e.g. http://localhost:8081
     * @param nodeUri - if this is a url it is assumed that this will be used as root url for management components, e.g. http://localhost:8082
     * @throws ActivationException
     */
    public SCANodeImpl(String domainUri, String nodeUri)
    throws ActivationException {
        this.domainUri = domainUri;
        this.nodeUri = nodeUri;
        this.domainClassLoader = SCANodeImpl.class.getClassLoader(); 
        this.applicationClassLoader = Thread.currentThread().getContextClassLoader();        
        this.isStandalone = LOCAL_DOMAIN_URI.equals(domainUri);
        init();
    }    
    
    /** 
     * Creates a node connected to a wider domain and allows a classpath to be specified.  
     * To find its place in the domain node and domain identifiers must be provided. 
     * 
     * @param domainUri - identifies what host and port the domain service is running on, e.g. http://localhost:8081
     * @param nodeUri - if this is a url it is assumed that this will be used as root url for management components, e.g. http://localhost:8082
     * @param cl - the ClassLoader to use for loading system resources for the node
     * @throws ActivationException
     */
    public SCANodeImpl(String domainUri, String nodeUri, ClassLoader cl)
    throws ActivationException {
        this.domainUri = domainUri;
        this.nodeUri = nodeUri;
        this.domainClassLoader = cl;
        this.applicationClassLoader = Thread.currentThread().getContextClassLoader();
        this.isStandalone = LOCAL_DOMAIN_URI.equals(domainUri);
        init();
    } 
    
    /** 
     * Creates a node connected to a wider domain and allows a classpath to be specified.  
     * To find its place in the domain node and domain identifiers must be provided. 
     * 
     * @param domainUri - identifies what host and port the domain service is running on, e.g. http://localhost:8081
     * @param nodeUri - if this is a url it is assumed that this will be used as root url for management components, e.g. http://localhost:8082
     * @param cl - the ClassLoader to use for loading system resources for the node
     * @param applicationCl - the ClassLoader to use for loading application resources for the node
     * @throws ActivationException
     */
    public SCANodeImpl(String domainUri, String nodeUri, ClassLoader cl, ClassLoader applicationCl, String contributionPath, String[] composites)
    throws ActivationException {
        this.domainUri = domainUri;
        this.nodeUri = nodeUri;
        this.domainClassLoader = cl;
        this.applicationClassLoader = applicationCl;        
        this.isStandalone = LOCAL_DOMAIN_URI.equals(domainUri);
        init();
        start();        
        
        try {
            URL contributionURL = SCANodeUtil.findContributionURLFromCompositeNameOrPath(applicationClassLoader, contributionPath, composites);
            
            contributionManager.addContribution(contributionURL);
            
            if (composites.length > 0 ){
                for(int i = 0; i < composites.length; i++) {
                    contributionManager.addComposite(composites[i]);
                    contributionManager.startComposite(composites[i]);
                }
            } else {
                contributionManager.addAllComposites(contributionURL);
                contributionManager.startAllComposites(contributionURL);
            }
             
        } catch(Exception ex) {
            throw new ActivationException(ex);
        }
        
    }    
    
    /**
     * Work out if we are representing a domain in memory or can go out to the network to 
     * get domain information. This all depends on whether there is a management
     * composite on the classpath
     */
    private void init()
      throws ActivationException {
        try {
            
            // create a node runtime for the domain contributions to run on
            nodeRuntime = new ReallySmallRuntime(domainClassLoader);
          
            // Check if node has been given a domain name to connect to
            if (isStandalone) {
            	logger.log(Level.INFO, "Domain node will be started stand-alone as node and domain URIs are not provided");
            	managementRuntime = null;
            	scaDomain = null;
            } else {
                // check where domain and node uris are urls, they will be used to configure various
                // endpoints if they are
                URI tmpURI;
                try {
                    tmpURI = new URI(domainUri); 
                    if (tmpURI.isAbsolute()){
                        domainUrl = tmpURI.toURL();
                    }
                } catch(Exception ex) {
                    throw new ActivationException("domain uri " + 
                                                  domainUri + 
                                                  "must be a valid url");
                }
                
                try {
                    tmpURI = new URI(nodeUri); 
                    if (tmpURI.isAbsolute()){
                        nodeUrl = tmpURI.toURL();
                    }
                } catch(Exception ex) {
                    nodeUrl = null;
                }

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
            // rest of the domain. The components are defined in the node.composite file
            String nodeCompositeName = "node.composite";
            URL contributionURL = SCANodeUtil.findContributionURLFromCompositeNameOrPath(domainClassLoader, null, new String[]{nodeCompositeName} );
            
            if ( contributionURL != null ){ 
                logger.log(Level.INFO, "Node management configured from " + contributionURL);
                
                // start a local domain in order to talk to the logical domain
                managementRuntime = new EmbeddedSCADomain(domainClassLoader, "node");   
                managementRuntime.start();
            
                // add node composite to the management domain
                ContributionService contributionService = managementRuntime.getContributionService();
                Contribution contribution = null;

	            contribution = contributionService.contribute(nodeUri, 
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
                    SCANodeUtil.fixUpNodeServiceUrls(managementRuntime.getDomainComposite().getIncludes().get(0).getComponents(), nodeUrl); 
                    SCANodeUtil.fixUpNodeReferenceUrls(managementRuntime.getDomainComposite().getIncludes().get(0).getComponents(), domainUrl);  
                  
                    managementRuntime.getCompositeActivator().activate(composite); 
                    managementRuntime.getCompositeActivator().start(composite);
                
                    // get the management components out of the domain so that they 
                    // can be configured/used. 
                    scaDomain =  managementRuntime.getService(SCADomainService.class, "SCADomainComponent");
                    domainManager = managementRuntime.getService(DomainManagerService.class, "DomainManagerComponent");
                    nodeManagerInit = managementRuntime.getService(NodeManagerInitService.class, "NodeManagerComponent/NodeManagerInitService");
                    
                    // Now get the uri back out of the component now it has been built and started
                    // TODO - this doesn't pick up the url from external hosting environments
                    String nodeManagerUrl = SCANodeUtil.getNodeManagerServiceUrl(managementRuntime.getDomainComposite().getIncludes().get(0).getComponents());
                    
                    if (nodeManagerUrl != null) {
                        if (isStandalone == false){
                            try {
                                
                                scaDomain.registerServiceEndpoint(domainUri, 
                                                                  nodeUri, 
                                                                  nodeUri + "NodeManagerService",
                                                                  "",
                                                                  nodeManagerUrl);
                                
                            } catch(Exception ex) {
                                logger.log(Level.SEVERE,  
                                           "Can't connect to domain manager at: " + 
                                           domainUrl);
                                throw new ActivationException(ex);
                            }
                        }                        
                    }
                        
                } else {
                    throw new ActivationException("Node management contribution " + 
                                                  contributionURL + 
                                                  " found but could not be loaded");
                }
            } else {
                throw new ActivationException("Node management contribution " + 
                                              nodeCompositeName + 
                                              " not found on the classpath");
            }
        } catch(ActivationException ex) {
            throw ex;                        
        } catch(Exception ex) {
            throw new ActivationException(ex);
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
        //        object. I.e. this node
        ModelFactoryExtensionPoint factories = nodeRuntime.getExtensionPointRegistry().getExtensionPoint(ModelFactoryExtensionPoint.class);
        NodeFactoryImpl domainFactory = new NodeFactoryImpl(this);
        factories.addFactory(domainFactory);
        
        // create the domain node managers
        componentManager = new ComponentManagerServiceImpl(domainUri, nodeUri, nodeComposite, nodeRuntime);
        contributionManager = new ContributionManagerImpl(domainUri, nodeUri, nodeComposite, nodeRuntime, applicationClassLoader, null);
        
        if (isStandalone == false){
            // pass this object into the node manager
            nodeManagerInit.setNode((SCANode)this);
            
            try {
                // go out and add this node to the wider domain
                domainManager.registerNode(domainUri, nodeUri);
            } catch(Exception ex) {
                logger.log(Level.SEVERE,  
                           "Can't connect to domain manager at: " + 
                           domainUrl);
                throw new ActivationException(ex);
            }
        }
    }
    
    @Override
    public void close() {
        try {
            stop();
        } catch (Exception ex) {
            throw new ServiceRuntimeException(ex);
        }
    }

    public void stop() throws ActivationException {
        // stop the components
        
        // remove contributions
        
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
                logger.log(Level.SEVERE,  
                        "Can't connect to domain manager at: " + 
                        domainUrl);
                throw new ActivationException(ex);
            }
        }
    }    
 
    public String getURI(){
        return domainUri;
    }
    
    public String getDomainURI(){
        return domainUri;
    }
    
    public String getNodeURI(){
        return nodeUri;
    }  
    
    public URL getDomainURL(){
        return domainUrl;
    }
    
    public URL getNodeURL(){
        return nodeUrl;
    }     
    
    public ComponentManager getComponentManager() {
        return componentManager;
    } 
    
    public ContributionManager getContributionManager() {    	
        return contributionManager;
    }     
      
    
    /**
     * Return an interface for registering local services and for
     * finding remote services
     * 
     * @return The service discovery interface
     */    
    public SCADomainService getDomainService(){
        return scaDomain;
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
