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

import java.io.ByteArrayInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.DomainBuilder;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.databinding.impl.XSDDataTypeConverter.Base64Binary;
import org.apache.tuscany.sca.domain.SCADomain;
import org.apache.tuscany.sca.host.embedded.impl.ReallySmallRuntime;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletHostExtensionPoint;
import org.apache.tuscany.sca.node.NodeException;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.spi.NodeFactoryImpl;
import org.apache.tuscany.sca.node.spi.SCANodeSPI;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;

/**
 * A local representation of the sca domain running on a single node
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-09 23:54:46 +0100 (Sun, 09 Sep 2007) $
 */
public class SCANodeImpl implements SCANode, SCANodeSPI {
	
    private final static Logger logger = Logger.getLogger(SCANodeImpl.class.getName());
	     
    // class loader used to get application resources
    private ClassLoader nodeClassLoader;    
    
    // identity and endpoints for the node and the domain it belongs to
    private String nodeURI;
    private URL nodeURL;
    private String logicalNodeURI;
    private String domainURI; 

    // The Tuscany runtime that does the hard work
    private ReallySmallRuntime nodeRuntime;
    
    // the top level components in this node. A subset of the the domain level composite
    private Composite nodeComposite; 
    
    // the domain that the node belongs to. This object acts as a proxy to the domain
    private SCADomainProxyImpl scaDomain;
    
    // the started status of the node
    private boolean nodeStarted = false;
    
    // collection for managing contributions that have been added to the node 
    private Map<String, Contribution> contributions = new HashMap<String, Contribution>();    
    private Map<QName, Composite> composites = new HashMap<QName, Composite>();
    private Map<String, Composite> compositeFiles = new HashMap<String, Composite>();
    
    private QName nodeManagementCompositeName = new QName("http://tuscany.apache.org/xmlns/tuscany/1.0", "node");
    
    // Used to pipe node information into the model
    NodeFactoryImpl nodeFactory;
    
    // domain level wiring 
    DomainBuilder domainBuilder;  
       
    // methods defined on the implementation only
       
    /** 
     * Creates a node connected to a wider domain.  To find its place in the domain 
     * node and domain identifiers must be provided. 
     *  
     * @param physicalNodeUri - if this is a URL it is assumed that this will be used as root URL for management components, e.g. http://localhost:8082
     * @param domainUri - identifies what host and port the domain service is running on, e.g. http://localhost:8081
     * @param logicalNodeURI the URI of the node group. This is the endpoint URI of the head of the
     * group of nodes. For example, in load balancing scenarios this will be the loaded balancer itself 
     * @throws ActivationException
     */
    public SCANodeImpl(String physicalNodeURI, String domainURI, String logicalNodeURI) throws NodeException {
        this.domainURI = domainURI;
        this.nodeURI = physicalNodeURI;
        this.logicalNodeURI = logicalNodeURI;
        this.nodeClassLoader = Thread.currentThread().getContextClassLoader();        
        init();
    }    
    
    /** 
     * Creates a node connected to a wider domain and allows a classpath to be specified.  
     * To find its place in the domain node and domain identifiers must be provided. 
     * 
     * @param physicalNodeUri - if this is a URL it is assumed that this will be used as root URL for management components, e.g. http://localhost:8082
     * @param domainUri - identifies what host and port the domain service is running on, e.g. http://localhost:8081
     * @param logicalNodeURI the URI of the node group. This is the endpoint URI of the head of the
     * group of nodes. For example, in load balancing scenarios this will be the loaded balancer itself 
     * @param cl - the ClassLoader to use for loading system resources for the node
     * @throws ActivationException
     */
    public SCANodeImpl(String physicalNodeURI, String domainURI, String logicalNodeURI, ClassLoader cl) throws NodeException {
        this.domainURI = domainURI;
        this.nodeURI = nodeURI;
        this.logicalNodeURI = logicalNodeURI;
        this.nodeClassLoader = cl;
        init();
    }    
    
    /**
     * Work out if we are representing a domain in memory or can go out to the network to 
     * get domain information. This all depends on whether the domain URI has been specified
     * on construction
     */
    private void init() throws NodeException {
        try {
            
            // Generate a unique node URI
            if (nodeURI == null) {
                
               String host = InetAddress.getLocalHost().getHostName();
               ServerSocket socket = new ServerSocket(0);
               nodeURI = "http://" + host + ":" + socket.getLocalPort();
               socket.close();
            } 
            
            // check whether node URI is an absolute URL,  
            try {
                URI tmpURI = new URI(nodeURI); 
                nodeURL = tmpURI.toURL(); 
            } catch(Exception ex) {
                throw new NodeException("node uri " + 
                                        nodeURI + 
                                        " must be a valid url");
            }
            
            // create a node runtime for the domain contributions to run on
            nodeRuntime = new ReallySmallRuntime(nodeClassLoader);
            nodeRuntime.start();
            
            // get the domain builder
            domainBuilder = nodeRuntime.getDomainBuilder();            
            
            // configure the default port and path for this runtime
            int port = URI.create(nodeURI).getPort();
            String path = nodeURL.getPath();
            ServletHostExtensionPoint servletHosts = nodeRuntime.getExtensionPointRegistry().getExtensionPoint(ServletHostExtensionPoint.class);
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
            //       resolution code that appears in this class
            ModelFactoryExtensionPoint factories = nodeRuntime.getExtensionPointRegistry().getExtensionPoint(ModelFactoryExtensionPoint.class);
            nodeFactory = new NodeFactoryImpl(this);
            factories.addFactory(nodeFactory); 
            
            // Create an in-memory domain level composite
            AssemblyFactory assemblyFactory = nodeRuntime.getAssemblyFactory();
            nodeComposite = assemblyFactory.createComposite();
            nodeComposite.setName(new QName(Constants.SCA10_NS, "node"));
            nodeComposite.setURI(nodeURI);
            
            // add the top level composite into the composite activator
            nodeRuntime.getCompositeActivator().setDomainComposite(nodeComposite);             
            
            // create a link to the domain 
            scaDomain = new SCADomainProxyImpl(domainURI, nodeClassLoader);
            
            // add the node URI to the domain
            scaDomain.addNode(this);  
            
        } catch(NodeException ex) {
            throw ex;
        } catch(Exception ex) {
            throw new NodeException(ex);
        }
    }
    
    // temp methods to help integrate with existing code
   
    public Component getComponent(String componentName) {
        for (Composite composite: nodeComposite.getIncludes()) {
            for (Component component: composite.getComponents()) {
                if (component.getName().equals(componentName)) {
                    return component;
                }
            }
        }
        return null;
    }    
    
    public List<Component> getComponents() {
        List<Component> components = new ArrayList<Component>();
        for (Composite composite: nodeComposite.getIncludes()) {
            components.addAll(composite.getComponents());
        }
        return components;
    }    
    
    /**
     * Stating to think about how a node advertises what it can do. 
     * Maybe need to turn this round and ask the node to decide whether it
     * can process a list of artifacts
     * @return
     */
    public List<String> getFeatures() {
        List<String> featureList = new ArrayList<String>();
        
        ExtensionPointRegistry registry = nodeRuntime.getExtensionPointRegistry();
        
        // TODO - how to get registered features?
        ModelFactoryExtensionPoint factories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
        
        return null;
    } 
    
    // SCANode SPI methods 
    
    public Object getNodeRuntime() {
        return nodeRuntime;
    }
    
    public void startFromDomain() throws NodeException {
        if (!nodeStarted){
            startComposites();
            nodeStarted = true;
        }
    }
    
    public void stopFromDomain() throws NodeException {
        if (nodeStarted){
            stopComposites();
            nodeStarted = false;             
        } 
    }  
    
    public void addContributionFromDomain(String contributionURI, URL contributionURL, ClassLoader contributionClassLoader ) throws NodeException {
        
        if (nodeStarted){
            throw new NodeException("Can't add contribution " + contributionURI + " when the node is running. Call stop() on the node first");
        }
       
        if (contributionURI == null){
            throw new NodeException("Contribution URI cannot be null");
        }
        
        if (contributionURL == null){
            throw new NodeException("Contribution URL cannot be null");
        }
        
        if (contributions.containsKey(contributionURI)) {
            throw new NodeException("Contribution " + contributionURI + " has already been added");
        }
        
        try {          

        	//FIXME What to do when a contribution uses a separate class loader ? (e.g contributionClassLoader != null)
            
            // Add the contribution to the node
            ContributionService contributionService = nodeRuntime.getContributionService();
            Contribution contribution = contributionService.contribute(contributionURI, 
                                                                       contributionURL, 
                                                                       false);
            
            // remember the contribution
            contributions.put(contributionURI, contribution);
                
            // remember all the composites that have been found
            for (Artifact artifact : contribution.getArtifacts()) {
                if (artifact.getModel() instanceof Composite) {
                    Composite composite = (Composite)artifact.getModel();
                    composites.put(composite.getName(), composite);
                    compositeFiles.put(composite.getURI(), composite);
                }
            } 
                    
        } catch (Exception ex) {
            throw new NodeException(ex);
        }        
    }   
    
    public void removeContributionFromDomain(String contributionURI) throws NodeException {
        
        if (nodeStarted){
            throw new NodeException("Can't remove contribution " + contributionURI + " when the node is running. Call stop() on the node first");
        }
       
        if (contributionURI == null){
            throw new NodeException("Contribution URI cannot be null");
        }
        
        if (!contributions.containsKey(contributionURI)) {
            throw new NodeException("Contribution " + contributionURI + " has not been added");
        }        
        
        try { 

            Contribution contribution = contributions.get(contributionURI);
            
            // remove the local record of composites associated with this contribution
            for (Artifact artifact : contribution.getArtifacts()) {
                if (artifact.getModel() instanceof Composite) {
                    Composite composite = (Composite)artifact.getModel();
                    composites.remove(composite.getName());
                    compositeFiles.remove(composite.getURI());
                }
            }            
        
            // remove the contribution from the contribution service
            nodeRuntime.getContributionService().remove(contributionURI);
            
            // remove any deployed composites from the node level composite
            for (Composite composite : contribution.getDeployables()) {
                if (nodeComposite.getIncludes().contains(composite)){
                    // deactivate it
                    deactivateComposite(composite);
                    
                    // remove it
                    nodeComposite.getIncludes().remove(composite);
                }
            }
            
            // remove the local record of the contribution
            contributions.remove(contributionURI);                
            
        } catch (Exception ex) {
            throw new NodeException(ex);
        }  
    }   
     
    public void addToDomainLevelCompositeFromDomain(QName compositeQName) throws NodeException {
        
        if (nodeStarted){
            throw new NodeException("Can't add composite " + compositeQName.toString() + " when the node is running. Call stop() on the node first");
        }
        
        Composite composite = composites.get(compositeQName);
        
        if (composite == null) {
            throw new NodeException("Composite " + compositeQName.toString() + " not found" );
        }
        
        // if the named composite is not already in the list then deploy it
        if (!nodeComposite.getIncludes().contains(composite)) {
            nodeComposite.getIncludes().add(composite);  
            
            try {
                // build and activate the model for this composite
                activateComposite(composite); 
            } catch (Exception ex) {
                throw new NodeException(ex);
            }                   
        }
    }

    // SCANode API methods 
    
    public void start() throws NodeException {
        if (domainURI != null){
            throw new NodeException("Node is part of domain " +
                                    domainURI + 
                                    " so must be starterd from there");
        } else {
            startFromDomain();
        }
    }
    
    public void stop() throws NodeException {
        if (domainURI != null){
            throw new NodeException("Node is part of domain " +
                                    domainURI + 
                                    " so must be stopped from there");
        } else {
            stopFromDomain();           
        } 
    }
    
    public void destroy() throws NodeException {
        try {
            stopFromDomain();
            
            removeAllContributions();
                       
            // remove the node factory
            ModelFactoryExtensionPoint factories = nodeRuntime.getExtensionPointRegistry().getExtensionPoint(ModelFactoryExtensionPoint.class);
            factories.removeFactory(nodeFactory); 
            nodeFactory.setNode(null);
            
            // unregister the node
            scaDomain.removeNode(this);
            
            // node runtime is stopped by the domain proxy once it has
            // removed the management components
            scaDomain.destroy();
                        
            scaDomain = null;            
            nodeRuntime = null;
            contributions = null;
            composites = null;
            compositeFiles = null;            
        } catch(NodeException ex) {
            throw ex;            
        } catch (Exception ex) {
            throw new NodeException(ex);
        }  
    }
 
    public String getURI(){
        return nodeURI;
    }
    
    public SCADomain getDomain(){
        return scaDomain;
    }   
    
    public void addContribution(String contributionURI, URL contributionURL) throws NodeException {
        addContribution(contributionURI, contributionURL, null);
    }
    
    public void addContribution(String contributionURI, URL contributionURL, ClassLoader contributionClassLoader ) throws NodeException {
        
        try {          
            addContributionFromDomain(contributionURI, contributionURL, contributionClassLoader);
            
            // add the contribution to the domain. 
            scaDomain.registerContribution(nodeURI, contributionURI, contributionURL.toExternalForm());                  
        
        } catch (Exception ex) {
            throw new NodeException(ex);
        }        
    }

    public void removeContribution(String contributionURI) throws NodeException {       
        
        try { 
            removeContributionFromDomain(contributionURI);
            
            // remove the contribution from the domain. 
            scaDomain.unregisterContribution(nodeURI, contributionURI);                  
            
        } catch (Exception ex) {
            throw new NodeException(ex);
        }  
    }

    private void removeAllContributions() throws NodeException {
        try {     
            // copy the keys so we don't get a concurrency error
            List<String> keys = new ArrayList<String>();
            
            for (String contributionURI : contributions.keySet()){
                keys.add(contributionURI);
            }
            
            // Remove all contributions
            for (String contributionURI : keys){
                removeContribution(contributionURI);
            }
        } catch (Exception ex) {
            throw new NodeException(ex);
        }              
    }
    
    private boolean isDeployable(Composite composite){
        boolean deployable = false;
        
        for (Contribution contribution : contributions.values()){
            if (contribution.getDeployables().contains(composite)) {
                deployable = true;
                break;
            }
        }
        
        return deployable;
    }
    
    public void addToDomainLevelComposite(QName compositeQName) throws NodeException {
        
        if (nodeStarted){
            throw new NodeException("Can't add composite " + compositeQName.toString() + " when the node is running. Call stop() on the node first");
        }
       
        // if no composite name is specified add all deployable composites
        // to the domain
        if (compositeQName == null){
            for (Composite composite : composites.values()) {
                if (!nodeComposite.getIncludes().contains(composite)) {
                    nodeComposite.getIncludes().add(composite);
                    
                    try {
                        // build and activate the model for this composite
                        activateComposite(composite);
                        
                        // register the composite with the domain
                        scaDomain.registerDomainLevelComposite(nodeURI, composite.getName().toString());                  
                    
                    } catch (Exception ex) {
                        throw new NodeException(ex);
                    }   
                    
                }
            } 
        } else {          
            Composite composite = composites.get(compositeQName);
            
            if (composite == null) {
                throw new NodeException("Composite " + compositeQName.toString() + " not found" );
            }
                        
            // if the named composite is not already in the list then deploy it
            if (!nodeComposite.getIncludes().contains(composite)) {
                nodeComposite.getIncludes().add(composite);
                
                try {
                    // build and activate the model for this composite
                    activateComposite(composite);
                    
                    // register the composite with the domain
                    scaDomain.registerDomainLevelComposite(nodeURI, composite.getName().toString());                  
                
                } catch (Exception ex) {
                    throw new NodeException(ex);
                }                 
            }
        }  
        
    }
    
    public void addToDomainLevelComposite(String compositePath) throws NodeException {
       
        if (compositePath == null){
            addToDomainLevelComposite((QName)null);
        } else {          
            Composite composite = compositeFiles.get(compositePath);
            
            if (composite != null){
                addToDomainLevelComposite(composite.getName());
            } else {
                throw new NodeException("Composite " + compositePath + " not found" );
            }  
        }
    }
    public ContributionService getContributionService()
    {
    	return nodeRuntime.getContributionService();
    }
    /* TODO - giorgio:  These are just little steps to support contribution-updater,
     * 
     * in order to have more fine grain on updating stuffs.
    public void addComponentToComposite(MetaComponent mc, String contributionURI, String compositeURI)
    {
    }
    public void removeComponentFromComposite(String nodeName, String compositeURI,String componentName)
    {
    }
    */
    private void activateComposite(Composite composite) throws CompositeBuilderException, ActivationException {
        logger.log(Level.INFO, "Building composite: " + composite.getName());
        
        // Create the model for the composite
        nodeRuntime.getCompositeBuilder().build(composite); 
        
        // activate the composite
        nodeRuntime.getCompositeActivator().activate(composite); 
        
        // tell the domain where all the service endpoints are
        registerRemoteServices(nodeURI, composite);        
    }  
    
    private void deactivateComposite(Composite composite) throws CompositeBuilderException, ActivationException {
        nodeRuntime.getCompositeActivator().deactivate(composite);
       
        // no deregistering of endpoints as endpoint handling is going to have to change
    }
    

    /**
     * Configure the default HTTP port for this node.
     * The motivation here is to set the default binding on the Servlet container
     * based on whatever information is available. In particular if no Node URL is 
     * provided then one of the ports from the first composite is used so that 
     * some recognizable default is provided for any bindings that are specified
     * without URIs 
     */    
    private void configureDefaultPort() {
        if (composites.size() == 0){
            return;
        }
        
        Composite composite = nodeComposite.getIncludes().get(1);
        
        if (composite == null) {
            return;
        }
        
        int port = -1;
        for (Service service: composite.getServices()) {
            for (Binding binding: service.getBindings()) {
                String uri = binding.getURI();
                if (uri != null) {
                    port = URI.create(uri).getPort();
                    if (port != -1) {
                        break;
                    }
                }
            }
            if (port != -1) {
                break;
            }
        }
        for (Component component: composite.getComponents()) {
            for (ComponentService service: component.getServices()) {
                for (Binding binding: service.getBindings()) {
                    String uri = binding.getURI();
                    if (uri != null) {
                        port = URI.create(uri).getPort();
                        if (port != -1) {
                            break;
                        }
                    }
                }
                if (port != -1) {
                    break;
                }
            }
            if (port != -1) {
                break;
            }
        }

        // Then get the port from the node URI 
        if (port == -1) {
            port = URI.create(nodeURI).getPort();
        }
        
        // Configure the default port
        if (port != -1) {
            ServletHostExtensionPoint servletHosts = nodeRuntime.getExtensionPointRegistry().getExtensionPoint(ServletHostExtensionPoint.class);
            for (ServletHost servletHost: servletHosts.getServletHosts()) {
                servletHost.setDefaultPort(port);
            }
        }
    }

    private void startComposites() throws NodeException {
        try {
            if (nodeComposite.getIncludes().size() == 0 ){
                logger.log(Level.INFO, nodeURI + 
                                       " has no composites to start" );
            } else {
/* TODO - moved build/activate back to the point where the 
 *        composite is added. What should I do about this default port business.
 *        I think that needs to be consumed by the domain model anyhow            
                // Configure the default server port for the node
                configureDefaultPort();
*/                 
               
                // do cross composite wiring. This is here just in case
                // the node has more than one composite and is stand alone
                // If the node is not stand alone the domain will do this
                if (domainURI == null){
                    domainBuilder.wireDomain(nodeComposite);
                }
                
                for (Composite composite : nodeComposite.getIncludes()) {
                    // don't try and restart the management composite
                    // they will already have been started by the domain proxy
                    if (!composite.getName().equals(nodeManagementCompositeName)){
                        startComposite(composite);
                    }
                }
            }

        } catch (Exception ex) {
            throw new NodeException(ex);
        }  
    }

    private void startComposite(Composite composite) throws CompositeBuilderException, ActivationException {
        logger.log(Level.INFO, "Starting composite: " + composite.getName());               
        
        //start the composite
        nodeRuntime.getCompositeActivator().start(composite);
    }    

    private void stopComposites() throws NodeException {
        
        try {

            for (Composite composite : nodeComposite.getIncludes()) { 
                // don't try and stop the management composite
                // if we do that we can't manage the node
                if (!composite.getName().equals(nodeManagementCompositeName)){
                    stopComposite(composite);
                }
            }
                            
        } catch (Exception ex) {
            throw new NodeException(ex);
        }              
    }
    
    private void stopComposite(Composite composite) 
      throws ActivationException {
        logger.log(Level.INFO, "Stopping composite: " + composite.getName());
        nodeRuntime.getCompositeActivator().stop(composite);
    }
    
    private void registerRemoteServices(String nodeURI, Composite composite){
        // Loop through all service binding URIs registering them with the domain 
        for (Service service: composite.getServices()) {
            for (Binding binding: service.getBindings()) {
                registerRemoteServiceBinding(nodeURI, null, service, binding);
            }
        }
        
        for (Component component: composite.getComponents()) {
            for (ComponentService service: component.getServices()) {
                for (Binding binding: service.getBindings()) {
                    registerRemoteServiceBinding(nodeURI, component, service, binding);
                }
            }
        }
    }
    
    private void registerRemoteServiceBinding(String nodeURI, Component component, Service service, Binding binding ){
        if (service.getInterfaceContract().getInterface().isRemotable()) {
            String uriString = binding.getURI();
            if (uriString != null) {
                 
                
                String serviceName = service.getName();
                
                if (component != null){
                    serviceName = component.getName() + '/' + serviceName;
                }
                   
                try {
                    scaDomain.registerServiceEndpoint(domainURI, 
                                                      nodeURI, 
                                                      serviceName, 
                                                      binding.getClass().getName(), 
                                                      uriString);
                } catch(Exception ex) {
                    logger.log(Level.WARNING, 
                               "Unable to  register service: "  +
                               domainURI + " " +
                               nodeURI + " " +
                               service.getName()+ " " +
                               binding.getClass().getName() + " " +
                               uriString);
                }
            }
        }
    }   
    
    public void updateComposite(QName compositeQName, String compositeXMLBase64 ) throws NodeException {
        logger.log(Level.INFO, "Updating composite " +  compositeQName.toString() + 
                               " at node " + nodeURI);
        
        ByteArrayInputStream bais = new ByteArrayInputStream(Base64Binary.decode(compositeXMLBase64));

        // find the composite that will be updated
        Composite composite = composites.get(compositeQName);
        
        if (composite == null) {
            throw new NodeException("trying to update composite " + compositeQName.toString() + 
                                    " which can't be found in node " + nodeURI);
        }
        
        // parse the XML into an composite object
        Composite newComposite = null;
        
        ExtensionPointRegistry registry = nodeRuntime.getExtensionPointRegistry();
        StAXArtifactProcessorExtensionPoint staxProcessors =
            registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        
        StAXArtifactProcessor<Composite> processor = staxProcessors.getProcessor(Composite.class);
              
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader reader = inputFactory.createXMLStreamReader(bais);
            newComposite = processor.read(reader);
            reader.close();
        } catch (Exception ex) {
            throw new NodeException(ex);
        }       

        
        // for each component in the composite compare it against the live component
        for (Component newComponent : newComposite.getComponents()){
            for (Component component : composite.getComponents()){         
                if (component.getName().equals(newComponent.getName())){
                    // compare the component references
                    for (Reference newReference : newComponent.getReferences()){
                        for (Reference reference : component.getReferences()) {           
                            if (reference.getName().equals(newReference.getName())) {
                                boolean referenceChanged = false;
                                List<Binding> removeCandidates = new ArrayList<Binding>();
                                List<Binding> addCandidates = new ArrayList<Binding>();
                                
                                removeCandidates.addAll(reference.getBindings());
                                
                                for (Binding newBinding : newReference.getBindings()){
                                    boolean bindingFound = false;
                                    for (Binding binding : reference.getBindings()){ 
                                        // find the matching target service binding       
                                        if (binding.getName().equals(newBinding.getName())){
                                            if ((binding.getURI() != null) && 
                                                (newBinding.getURI() != null) &&
                                                !binding.getURI().equals(newBinding.getURI())){
                                                binding.setURI(newBinding.getURI());
                                                referenceChanged = true;
                                                
                                                logger.log(Level.INFO, "Updating binding " + 
                                                                       component.getName() + 
                                                                       " reference " + 
                                                                       reference.getName() +
                                                                       " binding " + 
                                                                       binding.getClass().getName() + 
                                                                       " URI " + 
                                                                       binding.getURI());
                                            }
                                            bindingFound = true;
                                            removeCandidates.remove(binding);
                                        } 
                                    }
                                    
                                    if (bindingFound == false){
                                        addCandidates.add(newBinding);
                                    }

                                }
                                
                                for (Binding addBinding : addCandidates){
                                    reference.getBindings().add(addBinding);
                                    referenceChanged = true;
                                    logger.log(Level.INFO, "Adding binding " + 
                                            component.getName() + 
                                            " reference " + 
                                            reference.getName() +
                                            " binding " + 
                                            addBinding.getClass().getName() + 
                                            " URI " + 
                                            addBinding.getURI());                                        
                                }
                                
                                // remove all of the old bindings
                                for (Binding removeBinding : removeCandidates){
                                    reference.getBindings().remove(removeBinding);
                                    referenceChanged = true;
                                    logger.log(Level.INFO, "Removing binding " + 
                                            component.getName() + 
                                            " reference " + 
                                            reference.getName() +
                                            " binding " + 
                                            removeBinding.getClass().getName() + 
                                            " URI " + 
                                            removeBinding.getURI());
                                }
                                
                                // if the node is running restart the reference and the component that holds it
                                if (referenceChanged && nodeStarted){
                                    try {
                                        nodeRuntime.getCompositeActivator().stop((RuntimeComponent)component);
                                        nodeRuntime.getCompositeActivator().deactivate((RuntimeComponent)component, 
                                                (RuntimeComponentReference)reference);
                                        nodeRuntime.getCompositeActivator().start((RuntimeComponent)component, 
                                                (RuntimeComponentReference)reference);
                                        nodeRuntime.getCompositeActivator().start((RuntimeComponent)component);
                                       
                                    } catch (Exception ex) {
                                        throw new NodeException(ex);
                                    }
                                    
                                }                                
                            }
                        }
                    }
                    
                    // TODO - compare other parts of the component
                }
            }
        }
        
        // TODO - Compare other parts of the composite?
    }

}
