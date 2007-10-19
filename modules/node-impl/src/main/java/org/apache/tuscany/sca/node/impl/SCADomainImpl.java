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

import java.net.URI;
import java.net.URL;
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
import org.apache.tuscany.sca.domain.DomainManagerNodeEventService;
import org.apache.tuscany.sca.domain.model.Domain;
import org.apache.tuscany.sca.host.embedded.impl.EmbeddedSCADomain;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.node.NodeManagerInitService;
import org.apache.tuscany.sca.node.SCADomainProxySPI;
import org.apache.tuscany.sca.node.SCANode;
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
public class SCADomainImpl implements SCADomainProxySPI {
	
    private final static Logger logger = Logger.getLogger(SCADomainImpl.class.getName());
	    
    // the domain used to talk to the rest of the domain
    private EmbeddedSCADomain domainManagementRuntime;
    
    // class loader used to get the runtime going
    private ClassLoader domainClassLoader;  
    
    // representation of the private state of the node that the domain is running on
    private String domainURI; 
    private URL domainURL; 
    
    // proxy to the domain
    private DomainManagerNodeEventService domainManager;
    
    // proxy to the node manager
    private NodeManagerInitService nodeManagerInit;
    private String nodeManagerUrl;
    
    // the local node implementation
    private SCANodeImpl nodeImpl;
       
    // methods defined on the implementation only
          
    /** 
     * Creates a domain proxy connected to a wider domain.  
     * 
     * @param domainUri - identifies what host and port the domain service is running on, e.g. http://localhost:8081
     * @throws ActivationException
     */
    public SCADomainImpl(String domainURI) throws DomainException {
        this.domainURI = domainURI;
        this.domainClassLoader = SCADomainImpl.class.getClassLoader(); 
        init();
    }    
    
    /**
     * Start the composite that connects to the domain manager
     */
    private void init()
      throws DomainException {
        try {
            // check where domain and node uris are urls, they will be used to configure various
            // endpoints if they are
            URI tmpURI;
            try {
                tmpURI = new URI(domainURI); 
                if (tmpURI.isAbsolute() == true){
                    domainURL = tmpURI.toURL();
                }
            } catch(Exception ex) {
                domainURL = null;
            }            
          
            // Check if node has been given a valid domain name to connect to
            if (domainURL == null) {
            	logger.log(Level.INFO, "Domain will be started stand-alone as domain URL is not provided");
            } else {
                // load the composite that allows this domain representation to 
                // connect to the rest of the domain
                String domainCompositeName = "node.composite";
                URL contributionURL = SCANodeUtil.findContributionURLFromCompositeNameOrPath(domainClassLoader, null, new String[]{domainCompositeName} );
                
                if ( contributionURL != null ){ 
                    logger.log(Level.INFO, "Domain management configured from " + contributionURL);
                    
                    // start a local domain in order to talk to the logical domain
                    domainManagementRuntime = new EmbeddedSCADomain(domainClassLoader, domainURI);   
                    domainManagementRuntime.start();
                
                    // add node composite to the management domain
                    ContributionService contributionService = domainManagementRuntime.getContributionService();
                    Contribution contribution = null;

                    contribution = contributionService.contribute(domainURI, 
                                                                  contributionURL, 
                                                                  false);
                    
                    if (contribution.getDeployables().size() != 0) {
                        Composite composite = contribution.getDeployables().get(0);
                    
                        domainManagementRuntime.getDomainComposite().getIncludes().add(composite);
                        domainManagementRuntime.getCompositeBuilder().build(composite);
                        
                        // deal with the special case of registering the node manager service 
                        // in service discovery. It's not on an SCA binding. 
                        // TODO - really want to be able to hand out service references but they
                        //        don't serialize out over web services yet. 
                        SCANodeUtil.fixUpNodeServiceUrls(domainManagementRuntime.getDomainComposite().getIncludes().get(0).getComponents(), null); 
                        SCANodeUtil.fixUpNodeReferenceUrls(domainManagementRuntime.getDomainComposite().getIncludes().get(0).getComponents(), domainURL);  
                      
                        domainManagementRuntime.getCompositeActivator().activate(composite); 
                        domainManagementRuntime.getCompositeActivator().start(composite);
                    
                        // get the management components out of the domain so that they 
                        // can be configured/used. 
                        domainManager = domainManagementRuntime.getService(DomainManagerNodeEventService.class, "DomainManagerComponent");
                        nodeManagerInit = domainManagementRuntime.getService(NodeManagerInitService.class, "NodeManagerComponent/NodeManagerInitService");
                        
                        // Now get the uri back out of the component now it has been built and started
                        // TODO - this doesn't pick up the url from external hosting environments
                        nodeManagerUrl = SCANodeUtil.getNodeManagerServiceUrl(domainManagementRuntime.getDomainComposite().getIncludes().get(0).getComponents());                       
                             
                    } else {
                        throw new ActivationException("Domain management contribution " + 
                                                      contributionURL + 
                                                      " found but could not be loaded");
                    }
                } else {
                    throw new ActivationException("Doamin management contribution " + 
                                                  domainCompositeName + 
                                                  " not found on the classpath");
                }
            }                   
        } catch(Exception ex) {
            throw new DomainException(ex);
        }
    }
    
   // SPI methods 
    
    public String addNode(String nodeURI, String nodeURL){
        // Does nothing in the proxy
        return null;
    }
    
    public String removeNode(String nodeURI){
        // Does nothing in the proxy
        return null;
    }  
    
    public void addNode(SCANode nodeImpl) throws DomainException {
        this.nodeImpl = (SCANodeImpl)nodeImpl;
        
        // register node with wider domain
        if (domainURL != null){
            // pass this object into the node manager service
            nodeManagerInit.setNode(nodeImpl);
            
            try {
                // go out and add this node to the wider domain
                domainManager.registerNode(nodeImpl.getURI(),nodeManagerUrl);

            } catch(Exception ex) {
                logger.log(Level.SEVERE,  
                           "Can't connect to domain manager at: " + 
                           domainURL);
                throw new DomainException(ex);
            }              
        }
    }
    
    public void removeNode(SCANode nodeImpl) throws DomainException {
        
        if (domainURL != null){
            // remove the node from node manager service
            //nodeManagerInit.removeNode(nodeImpl);
            
            try {
                // go out and add this node to the wider domain
                domainManager.removeNode(nodeImpl.getURI());
            } catch(Exception ex) {
                logger.log(Level.SEVERE,  
                           "Can't connect to domain manager at: " + 
                           domainURL);
                throw new DomainException(ex);
            }
        }  
        
        this.nodeImpl = null;
    }    
     

    public String registerServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName, String URL){
        return domainManager.registerServiceEndpoint(domainUri, nodeUri, serviceName, bindingName, URL);
    }
   
    public String  removeServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName){
        return domainManager.removeServiceEndpoint(domainUri, nodeUri, serviceName, bindingName);
    }
     
    public String findServiceEndpoint(String domainUri, String serviceName, String bindingName){
        return domainManager.findServiceEndpoint(domainUri, serviceName, bindingName);
    }
    
    public Domain getDomainModel(){        
        return null;
    }     
       
      
    // API methods 
    
    public void start() throws DomainException {
        // TODO - what happens here?
    }
    
    public void stop() throws DomainException {
        try {
          // Stop the domain
          domainManagementRuntime.stop();
        } catch (Exception ex) {
            throw new DomainException(ex);
        }

    }    
 
    public String getURI(){
        return domainURI;
    }

    public void addContribution(String contributionURI, URL contributionURL) throws DomainException {
        try {
            nodeImpl.addContribution(contributionURI, contributionURL);
        } catch(Exception ex) {
            new DomainException(ex);
        }
    }

    public void removeContribution(String uri) throws DomainException {
        try {
            //nodeImpl.removeContributions();
        } catch(Exception ex) {
            new DomainException(ex);
        }
    }
    
    public void addDeploymentComposite(String contributionURI, String compositeXML) throws DomainException {
        // TODO 
    }
    
    public void addToDomainLevelComposite(QName qname) throws DomainException {
        try {
            nodeImpl.deployComposite(qname);
        } catch(Exception ex) {
            new DomainException(ex);
        }
    }
      
    public void removeFromDomainLevelComposite(QName qname) throws DomainException {
        try {
            //nodeImpl.stopComposite();
        } catch(Exception ex) {
            new DomainException(ex);
        }        
    }     
      
    public void startComposite(QName qname) throws DomainException {
        try {
            nodeImpl.deployComposite(qname);
        } catch(Exception ex) {
            new DomainException(ex);
        }        
    }
      
    public void stopComposite(QName qname) throws DomainException {
        try {
            //nodeImpl.stopComposite();
        } catch(Exception ex) {
            new DomainException(ex);
        }         
    }    
           
    public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
        return (R)nodeImpl.getNodeRuntime().getProxyFactory().cast(target);
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
            AssemblyFactory assemblyFactory = nodeImpl.getNodeRuntime().getAssemblyFactory();
            Composite composite = assemblyFactory.createComposite();
            composite.setName(new QName(Constants.SCA10_TUSCANY_NS, "default"));
            RuntimeComponent component = (RuntimeComponent)assemblyFactory.createComponent();
            component.setName("default");
            component.setURI("default");
            nodeImpl.getNodeRuntime().getCompositeActivator().configureComponentContext(component);
            composite.getComponents().add(component);
            RuntimeComponentReference reference = (RuntimeComponentReference)assemblyFactory.createComponentReference();
            reference.setName("default");
            ModelFactoryExtensionPoint factories =
                nodeImpl.getNodeRuntime().getExtensionPointRegistry().getExtensionPoint(ModelFactoryExtensionPoint.class);
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
            return new ServiceReferenceImpl<B>(businessInterface, component, reference, binding, nodeImpl.getNodeRuntime()
                .getProxyFactory(), nodeImpl.getNodeRuntime().getCompositeActivator());
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
        Component component = null;
        
        if (nodeImpl != null) {
            component = nodeImpl.getComponent(componentName);
       
            if (component == null) {
                // The component is not local in the partition, try to create a remote service ref
                return createServiceReference(businessInterface, name);
            }
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
