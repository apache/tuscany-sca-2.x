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

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URL;
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
import org.apache.tuscany.sca.contribution.DeployedArtifact;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.core.context.ServiceReferenceImpl;
import org.apache.tuscany.sca.domain.DomainException;
import org.apache.tuscany.sca.domain.DomainManagerInitService;
import org.apache.tuscany.sca.domain.DomainManagerNodeEventService;
import org.apache.tuscany.sca.domain.SCADomainSPI;
import org.apache.tuscany.sca.domain.impl.DomainManagerNodeImpl;
import org.apache.tuscany.sca.domain.impl.SCADomainImpl;
import org.apache.tuscany.sca.domain.model.Domain;
import org.apache.tuscany.sca.domain.model.DomainModelFactory;
import org.apache.tuscany.sca.domain.model.impl.DomainModelFactoryImpl;
import org.apache.tuscany.sca.host.embedded.impl.EmbeddedSCADomain;
import org.apache.tuscany.sca.host.embedded.impl.ReallySmallRuntime;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletHostExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.node.NodeFactoryImpl;
import org.apache.tuscany.sca.node.NodeManagerInitService;
import org.apache.tuscany.sca.node.SCADomainProxySPI;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.util.SCAContributionUtil;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentContext;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.ServiceRuntimeException;

/**
 * A local representation of the sca domain running on a single node
 * 
 * @version $Rev$ $Date$
 */
public class SCADomainProxyImpl extends SCADomainImpl implements SCADomainProxySPI {
	
    private final static Logger logger = Logger.getLogger(SCADomainProxyImpl.class.getName());
	    
    // management services
    private DomainManagerNodeEventService domainManagerService;   
    private NodeManagerInitService nodeManagerInitService;
        
    // the local node implementation
    private String nodeURI;
    private SCANodeImpl nodeImpl;
       
    // methods defined on the implementation only
          
    /** 
     * Creates a domain proxy connected to a wider domain.  
     * 
     * @param domainUri - identifies what host and port the domain service is running on, e.g. http://localhost:8081
     * @throws ActivationException
     */
    public SCADomainProxyImpl(String domainURI) throws DomainException {
        super(domainURI);
    }    
    
    /**
     * Start the composite that connects to the domain manager
     */
    protected void init() throws DomainException {
        try {
            // check where domain uris are urls, they will be used to configure various
            // endpoints if they are
            URI tmpURI;
            try {
                tmpURI = new URI(domainModel.getDomainURI()); 
                domainModel.setDomainURL(tmpURI.toURL().toExternalForm());
            } catch(Exception ex) {
                domainModel.setDomainURL(null);
            }            
          
            // Check if node has been given a valid domain name to connect to
            if (domainModel.getDomainURL() == null) {
            	logger.log(Level.INFO, "Domain will be started stand-alone as domain URL is not provided");
            } 
            
        } catch(Exception ex) {
            throw new DomainException(ex);
        }            
    }
    
    // SPI methods 
    
    public void addNode(SCANode nodeImpl) throws DomainException {
        this.nodeImpl = (SCANodeImpl)nodeImpl; 
        
        // add the node into the local domain model 
        super.addNode(nodeImpl.getURI(), nodeImpl.getURI());
        
        // the registration of the node with the node is delayed until
        // after the runtime has been started
        start();
    }
    
    private void addNode() throws DomainException {

        // pass this object into the node manager service
        nodeManagerInitService.setNode(nodeImpl);   
        
        if (domainModel.getDomainURL() != null){
            // add the 
        
            try {
                // create the node manager endpoint
                // TODO - we really need to pass in a callable reference
                URI nodeURI = new URI(nodeImpl.getURI());
                String nodeHost = nodeURI.getHost();
                
                if (nodeHost.equals("localhost")){
                    nodeHost = InetAddress.getLocalHost().getHostName();
                }
                
                String nodeManagerURL = nodeURI.getScheme()+ "://" +
                                        nodeHost + ":" +
                                        nodeURI.getPort() + "/NodeManagerComponent/NodeManagerService";
                
                // go out and add this node to the wider domain
                domainManagerService.registerNode(nodeImpl.getURI(),nodeManagerURL);

            } catch(Exception ex) {
                logger.log(Level.SEVERE,  
                           "Can't connect to domain manager at: " + 
                           domainModel.getDomainURL());
                throw new DomainException(ex);
            }  
        }      
    }
    
    public void removeNode(SCANode nodeImpl) throws DomainException {
        
        // remove this object from the node manager service
        //nodeManagerInitService.removeNode(nodeImpl);
        
        // remove the node from the local domain model
        super.removeNode(nodeImpl.getURI());
        
        if (domainModel.getDomainURL() != null){

            try {
                // go out and remove this node to the wider domain
                domainManagerService.removeNode(nodeImpl.getURI());
            } catch(Exception ex) {
                logger.log(Level.SEVERE,  
                           "Can't connect to domain manager at: " + 
                           domainModel.getDomainURL());
                throw new DomainException(ex);
            }
        }  
        
        this.nodeImpl = null;
    }  
    
    public void registerContribution(String nodeURI, String contributionURI, String contributionURL){
        
        if (domainModel.getDomainURL() != null) {
            domainManagerService.registerContribution(nodeURI, contributionURI, contributionURL);
        }
    }
    
    public void unregisterContribution(String contributionURI){
        if (domainModel.getDomainURL() != null) {
            domainManagerService.unregisterContribution(contributionURI);
        }
    }    
     

    public String registerServiceEndpoint(String domainURI, String nodeURI, String serviceName, String bindingName, String URL){
        
        super.registerServiceEndpoint(domainURI, nodeURI, serviceName, bindingName, URL);
        
        if (domainModel.getDomainURL() != null) {
            return domainManagerService.registerServiceEndpoint(domainURI, nodeURI, serviceName, bindingName, URL);
        } else {
            return null;
        }
    }
   
    public String  removeServiceEndpoint(String domainURI, String nodeURI, String serviceName, String bindingName){
        
        super.removeServiceEndpoint(domainURI, nodeURI, serviceName, bindingName);
        
        if (domainModel.getDomainURL() != null) {
            return domainManagerService.removeServiceEndpoint(domainURI, nodeURI, serviceName, bindingName);
        } else {
            return null;
        }
    }
     
    public String findServiceEndpoint(String domainURI, String serviceName, String bindingName){
        
        String endpoint = super.findServiceEndpoint(domainURI, serviceName, bindingName);
        
        if ( (endpoint.equals("")) && (domainModel.getDomainURL() != null)){
            endpoint = domainManagerService.findServiceEndpoint(domainURI, serviceName, bindingName);
        }
        
        return endpoint;
    }
    
    public Domain getDomainModel(){        
        return domainModel;
    }     
      
    // API methods 
    public void start() throws DomainException {
        try {
            
            // if there is no node create a runtime otherwise use the runtime from the node
            if (nodeImpl == null){
                // create a runtime for the domain management services to run on
                domainManagementRuntime = new ReallySmallRuntime(domainClassLoader);
                domainManagementRuntime.start();
                
                String path = URI.create(domainModel.getDomainURI()).getPath();

                // invent a default URL for the runtime
                String host = InetAddress.getLocalHost().getHostName();
                ServerSocket socket = new ServerSocket(0);
                int port = socket.getLocalPort();
                nodeURI = "http://" + host + ":" + port + path;
                socket.close();
                
                ServletHostExtensionPoint servletHosts = domainManagementRuntime.getExtensionPointRegistry().getExtensionPoint(ServletHostExtensionPoint.class);
                for (ServletHost servletHost: servletHosts.getServletHosts()) {
                    servletHost.setDefaultPort(port);
                    if (path != null && path.length() > 0 && !path.equals("/")) {
                        servletHost.setContextPath(path);
                    }
                }

                // Create an in-memory domain level management composite
                AssemblyFactory assemblyFactory = domainManagementRuntime.getAssemblyFactory();
                domainManagementComposite = assemblyFactory.createComposite();
                domainManagementComposite.setName(new QName(Constants.SCA10_NS, "domainManagement"));
                domainManagementComposite.setURI(domainModel.getDomainURI() + "/Management"); 
            } else {
                domainManagementRuntime = nodeImpl.getNodeRuntime();
                domainManagementComposite = domainManagementRuntime.getCompositeActivator().getDomainComposite();

// TODO: doing this breaks testcase
//                // set the context path for the node
//                String path = URI.create(nodeImpl.getURI()).getPath();
//                if (path != null && path.length() > 0 && !path.equals("/")) {
//                    ServletHostExtensionPoint servletHosts = domainManagementRuntime.getExtensionPointRegistry().getExtensionPoint(ServletHostExtensionPoint.class);
//                    for (ServletHost servletHost: servletHosts.getServletHosts()) {
//                        servletHost.setContextPath(path);
//                    }
//                }
            }
          
            // Find the composite that will configure the domain
            String domainCompositeName = "node.composite";
            URL contributionURL = SCAContributionUtil.findContributionFromResource(domainClassLoader, domainCompositeName);
            
            if ( contributionURL != null ){ 
                logger.log(Level.INFO, "Domain management configured from " + contributionURL);
                           
                // add node composite to the management domain
                domainManagementContributionService = domainManagementRuntime.getContributionService();
                Contribution contribution = null;

                contribution = domainManagementContributionService.contribute("nodedomain", 
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
                    
                    if (domainModel.getDomainURL() != null) {
                        URI domainURI = URI.create(domainModel.getDomainURI());
                        String domainHost = domainURI.getHost();
                        int domainPort = domainURI.getPort();
                        
                        // override any domain URLs in node.composite and replace with the
                        // domain url provided on start up
                        for ( Component component : composite.getComponents()){
                            for (ComponentReference reference : component.getReferences() ){
                                for (Binding binding : reference.getBindings() ) {
                                    String bindingURIString = binding.getURI();
                                    if (bindingURIString != null) {
                                        URI bindingURI = URI.create(bindingURIString);
                                        String bindingHost = bindingURI.getHost();
                                        int bindingPort = bindingURI.getPort();
                                        
                                        if ( bindingPort == 9999){
                                            // replace the old with the new
                                            bindingURIString = domainURI + bindingURI.getPath() ;
                                            
                                            // set the address back into the NodeManager binding.
                                            binding.setURI(bindingURIString);                                               
                                        }
                                    }
                                }
                            } 
                        }
                    }
                                        
                    domainManagementRuntime.getCompositeActivator().activate(composite); 
                    domainManagementRuntime.getCompositeActivator().start(composite);
                    
                    // get the management components out of the domain so that they 
                    // can be configured/used. 
                    domainManagerService = getService(DomainManagerNodeEventService.class, 
                                                      "DomainManagerComponent", 
                                                      domainManagementRuntime, 
                                                      domainManagementComposite); 
                    
                    nodeManagerInitService = getService(NodeManagerInitService.class, 
                                                        "NodeManagerComponent/NodeManagerInitService", 
                                                        domainManagementRuntime, 
                                                        domainManagementComposite); 
                    
                    // add the registered node now that the runtime is started
                    addNode();

                                                
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
    
    public void destroy() throws DomainException {
        try {
          // Stop the domain
          domainManagementRuntime.stop();
        } catch (Exception ex) {
            throw new DomainException(ex);
        }
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
            nodeImpl.addToDomainLevelComposite(qname);
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
            nodeImpl.addToDomainLevelComposite(qname);
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
        return (R)cast(target, domainManagementRuntime);
    }
    
    public <B> B getService(Class<B> businessInterface, String serviceName) {
        return getService( businessInterface, serviceName, domainManagementRuntime, domainManagementComposite);
    }
    
    public <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String name) {
        return getServiceReference(businessInterface, name, domainManagementRuntime, domainManagementComposite);
    }
}
