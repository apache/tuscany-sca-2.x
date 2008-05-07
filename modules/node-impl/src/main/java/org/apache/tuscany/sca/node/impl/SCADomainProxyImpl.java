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

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.core.context.CallableReferenceImpl;
import org.apache.tuscany.sca.domain.DomainException;
import org.apache.tuscany.sca.domain.impl.SCADomainImpl;
import org.apache.tuscany.sca.domain.impl.SCADummyNodeImpl;
import org.apache.tuscany.sca.domain.spi.SCADomainAPIService;
import org.apache.tuscany.sca.domain.spi.SCADomainEventService;
import org.apache.tuscany.sca.host.embedded.impl.ReallySmallRuntime;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletHostExtensionPoint;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.management.SCANodeManagerInitService;
import org.apache.tuscany.sca.node.management.SCANodeManagerService;
import org.apache.tuscany.sca.node.spi.NodeFactoryImpl;
import org.apache.tuscany.sca.node.spi.SCANodeSPI;
import org.apache.tuscany.sca.node.util.SCAContributionUtil;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ServiceReference;


/**
 * A local representation of the SCA Domain running on a single node
 * 
 * @version $Rev$ $Date$
 */
public class SCADomainProxyImpl extends SCADomainImpl {
	
    private final static Logger logger = Logger.getLogger(SCADomainProxyImpl.class.getName());
	    
    // management services
    private SCADomainAPIService domainAPIService; 
    private SCADomainEventService domainEventService;   
    private SCANodeManagerInitService nodeManagerInitService;
    private CallableReferenceImpl<SCANodeManagerService> nodeManagerService;
        
    // the local node implementation
    private SCANode node;
       
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
     * Creates a domain proxy connected to a wider domain.  
     * 
     * @param domainUri - identifies what host and port the domain service is running on, e.g. http://localhost:8081
     * @throws ActivationException
     */
    public SCADomainProxyImpl(String domainURI, ClassLoader cl) throws DomainException {
        super(domainURI);
        domainClassLoader = cl;
    }
    
    /**
     * Start the composite that connects to the domain manager
     */
    protected void init() throws DomainException {
        try {
            // check where domain URIs are URLs, they will be used to configure various
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
    
    private void createRuntime() throws DomainException {
        try {
            // check we don't try to do this twice
            if (domainManagementRuntime != null){
                return;
            }
            
            // if there is no node create a runtime otherwise use the runtime from the node
            if ((node == null) || 
                ( (node != null) && (node.getClass().equals(SCADummyNodeImpl.class)))){
                // create a runtime for the domain management services to run on
                domainManagementRuntime = new ReallySmallRuntime(domainClassLoader);
                domainManagementRuntime.start();
                
                String path = URI.create(domainModel.getDomainURI()).getPath();

                // invent a default URL for the runtime
                String host = InetAddress.getLocalHost().getHostName();
                ServerSocket socket = new ServerSocket(0);
                int port = socket.getLocalPort();
                socket.close();
                
                ServletHostExtensionPoint servletHosts = domainManagementRuntime.getExtensionPointRegistry().getExtensionPoint(ServletHostExtensionPoint.class);
                for (ServletHost servletHost: servletHosts.getServletHosts()) {
                    servletHost.setDefaultPort(port);
                    if (path != null && path.length() > 0 && !path.equals("/")) {
                        servletHost.setContextPath(path);
                    }
                }
                
                // make the node available to the model
                // this causes the runtime to start registering binding-sca service endpoints
                // with the domain proxy
                // TODO - This code is due to be pulled out and combined with the register and 
                //        resolution code that appears in this class
                ModelFactoryExtensionPoint factories = domainManagementRuntime.getExtensionPointRegistry().getExtensionPoint(ModelFactoryExtensionPoint.class);
                nodeFactory = new NodeFactoryImpl(node);
                factories.addFactory(nodeFactory);                

                // Create an in-memory domain level management composite
                AssemblyFactory assemblyFactory = domainManagementRuntime.getAssemblyFactory();
                domainManagementComposite = assemblyFactory.createComposite();
                domainManagementComposite.setName(new QName(Constants.SCA10_NS, "domainManagement"));
                domainManagementComposite.setURI(domainModel.getDomainURI() + "/Management"); 
                
                
            } else {
                domainManagementRuntime = (ReallySmallRuntime)((SCANodeSPI)node).getNodeRuntime();
                domainManagementComposite = domainManagementRuntime.getCompositeActivator().getDomainComposite();

                // set the context path for the node
                String path = URI.create(node.getURI()).getPath();
                if (path != null && path.length() > 0 && !path.equals("/")) {
                    ServletHostExtensionPoint servletHosts = domainManagementRuntime.getExtensionPointRegistry().getExtensionPoint(ServletHostExtensionPoint.class);
                    for (ServletHost servletHost: servletHosts.getServletHosts()) {
                        servletHost.setContextPath(path);
                    }
                }
            }
          
            // Find the composite that will configure the domain
            String domainCompositeName = "node.composite";
            URL contributionURL = SCAContributionUtil.findContributionFromResource(domainClassLoader, domainCompositeName);
            
            if ( contributionURL != null ){ 
                logger.log(Level.INFO, "Domain management configured from " + contributionURL);
                           
                // add node composite to the management domain
                domainManagementContributionService = domainManagementRuntime.getContributionService();
                domainManagementContribution = domainManagementContributionService.contribute("nodedomain", 
                                                                                              contributionURL, 
                                                                                              false);
                
                Composite composite = null;
                
                for (Artifact artifact: domainManagementContribution.getArtifacts()) {
                    if (domainCompositeName.equals(artifact.getURI())) {
                        composite = (Composite)artifact.getModel();
                    }
                }
                
                if (composite != null) {
                
                    domainManagementComposite.getIncludes().add(composite);
                    domainManagementRuntime.buildComposite(composite);
                    
                    if (domainModel.getDomainURL() != null) {
                        URI domainURI = URI.create(domainModel.getDomainURI());
                        String domainHost = domainURI.getHost();
                        int domainPort = domainURI.getPort();
                        
                        // override any domain URLs in node.composite and replace with the
                        // domain URL provided on start up
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
                    domainAPIService = getService(SCADomainAPIService.class, 
                                                  "SCADomainAPIServiceProxyComponent", 
                                                  domainManagementRuntime, 
                                                  domainManagementComposite);
                    domainEventService = getService(SCADomainEventService.class, 
                                                      "SCADomainEventServiceProxyComponent", 
                                                      domainManagementRuntime, 
                                                      domainManagementComposite); 
                    
                    nodeManagerInitService = getService(SCANodeManagerInitService.class, 
                                                        "SCANodeManagerComponent/SCANodeManagerInitService", 
                                                        domainManagementRuntime, 
                                                        domainManagementComposite); 
                    
                    nodeManagerService = (CallableReferenceImpl<SCANodeManagerService>)
                                         getServiceReference(SCANodeManagerService.class, 
                                                             "SCANodeManagerComponent/SCANodeManagerService", 
                                                             domainManagementRuntime, 
                                                             domainManagementComposite);
                    
                    // add the registered node now that the runtime is started
                    if ((node != null) && (!node.getClass().equals(SCADummyNodeImpl.class))){
                        addNode();
                    }

                                                
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
    
    
    public String getComposite(QName compositeQName){
        
        Composite composite = null;
        for(Composite tmpComposite : domainManagementComposite.getIncludes()){
            if (tmpComposite.getName().equals(compositeQName)){
                composite = tmpComposite;
            }
        }
        
        String compositeString = null;
            
        if (composite != null){     
            ExtensionPointRegistry registry = domainManagementRuntime.getExtensionPointRegistry();
            
            StAXArtifactProcessorExtensionPoint staxProcessors =
                registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
            
            StAXArtifactProcessor<Composite> processor = staxProcessors.getProcessor(Composite.class);
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
                XMLStreamWriter writer = outputFactory.createXMLStreamWriter(bos);
                processor.write(composite, writer);
                writer.flush();
                writer.close();
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
            
            compositeString = bos.toString();
        }
        
        return compositeString;
    }    
    
    // SCADomainEventService methods 
    
    public void addNode(SCANode node) throws DomainException {
        this.node = node; 
        
        // add the node into the local domain model 
        super.registerNode(node.getURI(), node.getURI(), null);
        
        // the registration of the node with the domain is delayed until
        // after the runtime has been started
        createRuntime();
    }
    
    private void addNode() throws DomainException {

        // pass this object into the node manager service
        nodeManagerInitService.setNode(node);   
        
        if (domainModel.getDomainURL() != null){
            // add the node to the domain
        
            try {
                // create the node manager endpoint
                // TODO - we really need to pass in a CallableReference
                URI nodeURI = new URI(node.getURI());
                String nodeHost = nodeURI.getHost();
                
                if (nodeHost.equals("localhost")){
                    nodeHost = InetAddress.getLocalHost().getHostName();
                }
                
                String nodeManagerURL = nodeURI.getScheme()+ "://" +
                                        nodeHost + ":" +
                                        nodeURI.getPort() + nodeURI.getPath() + "/SCANodeManagerComponent/SCANodeManagerService";
                
                // go out and add this node to the wider domain
                domainEventService.registerNode(node.getURI(), nodeManagerURL, nodeManagerService);

            } catch(Exception ex) {
                logger.log(Level.SEVERE,  
                           "Can't connect to domain manager at: " + 
                           domainModel.getDomainURL());
                throw new DomainException(ex);
            }  
        }      
    }
    
    public void removeNode(SCANode node) throws DomainException {
                
        // remove the node from the local domain model
        super.unregisterNode(node.getURI());
        
        if (domainModel.getDomainURL() != null){

            try {
                // go out and remove this node to the wider domain
                domainEventService.unregisterNode(node.getURI());
            } catch(Exception ex) {
                logger.log(Level.SEVERE,  
                           "Can't connect to domain manager at: " + 
                           domainModel.getDomainURL());
                throw new DomainException(ex);
            }
        }  
        
        // remove this object from the node manager service
        nodeManagerInitService.setNode(null);        
        
        this.node = null;
    }  
    
    public void registerNodeStart(String nodeURI) throws DomainException {
        if ((domainModel.getDomainURL() != null) && (domainEventService != null)){
            domainEventService.registerNodeStart(nodeURI);
        }
    }
    
    public void registerNodeStop(String nodeURI) throws DomainException {
        if ((domainModel.getDomainURL() != null) && (domainEventService != null)){
            domainEventService.registerNodeStop(nodeURI);
        }
    }
    
    public void registerContribution(String nodeURI, String contributionURI, String contributionURL) throws DomainException {
        
        if ((domainModel.getDomainURL() != null) && (domainEventService != null)){
            domainEventService.registerContribution(nodeURI, contributionURI, contributionURL);
        }
    }
    
    public void unregisterContribution(String nodeURI, String contributionURI) throws DomainException {
        if ((domainModel.getDomainURL() != null) && (domainEventService != null)) {
            domainEventService.unregisterContribution(nodeURI, contributionURI);
        }
    } 

    public void registerDomainLevelComposite(String nodeURI, String compositeQNameString) throws DomainException{
        if ((domainModel.getDomainURL() != null) && (domainEventService != null)) {
            domainEventService.registerDomainLevelComposite(nodeURI, compositeQNameString);
        }
    }
     

    public void registerServiceEndpoint(String domainURI, String nodeURI, String serviceName, String bindingName, String URL) throws DomainException {
        
        //super.registerServiceEndpoint(domainURI, nodeURI, serviceName, bindingName, URL);
        
        if ((domainModel.getDomainURL() != null) && (domainEventService != null)) {
            domainEventService.registerServiceEndpoint(domainURI, nodeURI, serviceName, bindingName, URL);
        } 
    }
   
    public void unregisterServiceEndpoint(String domainURI, String nodeURI, String serviceName, String bindingName) throws DomainException {
        
        //super.unregisterServiceEndpoint(domainURI, nodeURI, serviceName, bindingName);
        
        if ((domainModel.getDomainURL() != null) && (domainEventService != null)) {
            domainEventService.unregisterServiceEndpoint(domainURI, nodeURI, serviceName, bindingName);
        } 
    }
     
    public String findServiceEndpoint(String domainURI, String serviceName, String bindingName) throws DomainException {
        
        String endpoint = super.findServiceEndpoint(domainURI, serviceName, bindingName);
        
        if ( (endpoint.equals(SERVICE_NOT_REGISTERED)) && (domainModel.getDomainURL() != null) && (domainEventService != null)){
            endpoint = domainEventService.findServiceEndpoint(domainURI, serviceName, bindingName);
        }
        
        return endpoint;
    }
    
    public String findServiceNode(String domainURI, String serviceName, String bindingName) throws DomainException {
        
        String nodeName = super.findServiceEndpoint(domainURI, serviceName, bindingName);
        
        if ( (nodeName.equals(SERVICE_NOT_KNOWN)) && (domainModel.getDomainURL() != null) && (domainEventService != null)){
            nodeName = domainEventService.findServiceNode(domainURI, serviceName, bindingName);
        }
        
        return nodeName;
    }
    
      
    // SCADomain API methods 
    public void start() throws DomainException {
                  
        if ((domainModel.getDomainURL() != null) && (domainAPIService != null)){
            domainAPIService.start();
        } else {
            logger.log(Level.INFO,"Not connected to domain");
        }
    }
    
    public void stop() throws DomainException {
        if ((domainModel.getDomainURL() != null) && (domainAPIService != null)){
            domainAPIService.stop();
        } else {
            logger.log(Level.INFO,"Not connected to domain");
        }
    }
    
    public void destroy() throws DomainException {

        try {

            
            if (domainManagementRuntime != null) {
                Composite composite = domainManagementComposite.getIncludes().get(0);
                
                domainManagementRuntime.getCompositeActivator().stop(composite);
                domainManagementRuntime.getCompositeActivator().deactivate(composite);
                
                domainManagementComposite.getIncludes().clear();
                domainManagementRuntime.getContributionService().remove(domainManagementContribution.getURI());
                
                domainManagementRuntime.stop(); 
                
                domainManagementRuntime = null;
                domainManagementComposite = null;
                
                domainAPIService = null;
                domainEventService = null;
                nodeManagerInitService = null;
            }
          
 
        } catch (Exception ex) {
            throw new DomainException(ex);
        }
    }
 

    public void addContribution(String contributionURI, URL contributionURL) throws DomainException {
        if ((domainModel.getDomainURL() != null) && (domainAPIService != null)){
            domainAPIService.addContribution(contributionURI, contributionURL.toString());
        } else {
            throw new DomainException("Not connected to domain");
        }
    }
    
    public void updateContribution(String contributionURI, URL contributionURL) throws DomainException {
        if ((domainModel.getDomainURL() != null) && (domainAPIService != null)){
            domainAPIService.updateContribution(contributionURI, contributionURL.toString());
        } else {
            throw new DomainException("Not connected to domain");
        }
    }

    public void removeContribution(String contributionURI) throws DomainException {
        if ((domainModel.getDomainURL() != null) && (domainAPIService != null)){
            domainAPIService.removeContribution(contributionURI);
        } else {
            throw new DomainException("Not connected to domain");
        }
    }
    
    public void addDeploymentComposite(String contributionURI, String compositeXML) throws DomainException {
        if ((domainModel.getDomainURL() != null) && (domainAPIService != null)){
            domainAPIService.addDeploymentComposite(contributionURI, compositeXML);
        } else {
            throw new DomainException("Not connected to domain");
        }
    }
    
    public void updateDeploymentComposite(String contributionURI, String compositeXML) throws DomainException {
        if ((domainModel.getDomainURL() != null) && (domainAPIService != null)){
            domainAPIService.updateDeploymentComposite(contributionURI, compositeXML);
        } else {
            throw new DomainException("Not connected to domain");
        }       
    }
    
    public void addToDomainLevelComposite(QName compositeQName) throws DomainException {
        if ((domainModel.getDomainURL() != null) && (domainAPIService != null)){
            domainAPIService.addToDomainLevelComposite(compositeQName.toString());
        } else {
            throw new DomainException("Not connected to domain");
        }
    }
      
    public void removeFromDomainLevelComposite(QName compositeQName) throws DomainException {
        if ((domainModel.getDomainURL() != null) && (domainAPIService != null)){
            domainAPIService.removeFromDomainLevelComposite(compositeQName.toString());
        } else {
            throw new DomainException("Not connected to domain");
        }        
    }    
    
    public String getDomainLevelComposite() throws DomainException {
        if ((domainModel.getDomainURL() != null) && (domainAPIService != null)){
            return domainAPIService.getDomainLevelComposite();
        } else {
            throw new DomainException("Not connected to domain");
        }
    }
    
    public String getQNameDefinition(QName artifact) throws DomainException {
        if ((domainModel.getDomainURL() != null) && (domainAPIService != null)){
            return domainAPIService.getQNameDefinition(artifact.toString());
        } else {
            throw new DomainException("Not connected to domain");
        }
    }
      
    public void startComposite(QName compositeQName) throws DomainException {
        if ((domainModel.getDomainURL() != null) && (domainAPIService != null)){
            domainAPIService.startComposite(compositeQName.toString());
        } else {
            logger.log(Level.INFO,"Not connected to domain");
        }       
    }
      
    public void stopComposite(QName compositeQName) throws DomainException {
        if ((domainModel.getDomainURL() != null) && (domainAPIService != null)){
            domainAPIService.stopComposite(compositeQName.toString());
        } else {
            logger.log(Level.INFO,"Not connected to domain");
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
