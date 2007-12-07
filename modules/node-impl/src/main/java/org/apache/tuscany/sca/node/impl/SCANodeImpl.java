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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.DeployedArtifact;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.impl.ModelResolverImpl;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.domain.SCADomain;
import org.apache.tuscany.sca.domain.SCADomainEventService;
import org.apache.tuscany.sca.host.embedded.impl.ReallySmallRuntime;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletHostExtensionPoint;
import org.apache.tuscany.sca.node.NodeException;
import org.apache.tuscany.sca.node.NodeFactoryImpl;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeSPI;

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

    // The tuscany runtime that does the hard work
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
    
       
    // methods defined on the implementation only
       
    /** 
     * Creates a node connected to a wider domain.  To find its place in the domain 
     * node and domain identifiers must be provided. 
     * 
     * @param domainUri - identifies what host and port the domain service is running on, e.g. http://localhost:8081
     * @param nodeUri - if this is a url it is assumed that this will be used as root url for management components, e.g. http://localhost:8082
     * @param nodeGroupURI the uri of the node group. This is the enpoint URI of the head of the
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
     * @param domainUri - identifies what host and port the domain service is running on, e.g. http://localhost:8081
     * @param nodeUri - if this is a url it is assumed that this will be used as root url for management components, e.g. http://localhost:8082
     * @param nodeGroupURI the uri of the node group. This is the enpoint URI of the head of the
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
            
            // check whether node uri is an absolute url,  
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
    
    // SCANode API methods 
    
    public void start() throws NodeException {
        if ((!nodeStarted) && (nodeComposite.getIncludes().size() > 0)){
            startComposites();
            nodeStarted = true;
        }
    }
    
    public void stop() throws NodeException {
        if (nodeStarted){
            stopComposites();
            nodeStarted = false;
        } 
    }
    
    public void destroy() throws NodeException {
        try {
            stop();
            
            removeAllContributions(); 
            
            ModelFactoryExtensionPoint factories = nodeRuntime.getExtensionPointRegistry().getExtensionPoint(ModelFactoryExtensionPoint.class);
            factories.removeFactory(nodeFactory); 
            nodeFactory.setNode(null);
            
            scaDomain.removeNode(this);  
            scaDomain.destroy();
            
            // node runtime is stopped by the domain proxy once it has
            // removed the management components
            
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
            ModelResolver modelResolver = null;
            
            // if the contribution is to be resolved using a separate class loader
            // then create a new model resolver
            if (contributionClassLoader != null)  {
                modelResolver = new ModelResolverImpl(contributionClassLoader);
            }
            
            // Add the contribution to the node
            ContributionService contributionService = nodeRuntime.getContributionService();
            Contribution contribution = contributionService.contribute(contributionURI, 
                                                                       contributionURL, 
                                                                       modelResolver, 
                                                                       false);
            
            // remember the contribution
            contributions.put(contributionURI, contribution);
                
            // remember all the composites that have been found
            for (DeployedArtifact artifact : contribution.getArtifacts()) {
                if (artifact.getModel() instanceof Composite) {
                    Composite composite = (Composite)artifact.getModel();
                    composites.put(composite.getName(), composite);
                    compositeFiles.put(composite.getURI(), composite);
                }
            } 
            
            // add the contribution to the domain. It will generally already be there
            // unless the contribution has been added to the node itself. 
            scaDomain.registerContribution(nodeURI, contributionURI, contributionURL.toExternalForm());                  
        
        } catch (Exception ex) {
            throw new NodeException(ex);
        }        
    }

    public void removeContribution(String contributionURI) throws NodeException {
        
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
            for (DeployedArtifact artifact : contribution.getArtifacts()) {
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
                nodeComposite.getIncludes().remove(composite);
            }
            
            // remove the local record of the contribution
            contributions.remove(contributionURI);
            
            // remove the contribution from the domain. It will generally already be removed
            // unless the contribution has been removed from the node itself. 
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
            
            /* being marked as deployable is only an indicator and shouldn;t be enforced             
            if ( !isDeployable(composite)){
                throw new NodeException("Composite " + compositeQName.toString() + " is not deployable");
            }
            */
            
            // if the named composite is not already in the list then deploy it
            if (!nodeComposite.getIncludes().contains(composite)) {
                nodeComposite.getIncludes().add(composite);
                
                try {
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

    /**
     * Configure the default HTTP port for this node.
     * The motivation here is to set the default binding on the servlet container
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
                
                // Configure the default server port for the node
                configureDefaultPort();
                
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
        
        // Create the model for the composite
        nodeRuntime.getCompositeBuilder().build(composite); 
        
        // activate the composite
        nodeRuntime.getCompositeActivator().activate(composite); 
        
        // tell the domain where all the service endpoints are
        scaDomain.registerRemoteServices(nodeURI, composite);
        
        // get up to date with where all services we depend on are
        resolveRemoteReferences(composite);
        
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
        nodeRuntime.getCompositeActivator().deactivate(composite);
    }
        
    
    private void resolveRemoteReferences(Composite composite){
        // Loop through all reference binding URIs. Any that are not resolved
        // should be looked up in the domain
        
        for (Reference reference: composite.getReferences()) {
            for ( ComponentService service : reference.getTargets()){
                for (Binding binding: service.getBindings()) {
                    resolveRemoteReferenceBinding(reference, service, binding);
                }
            } 
        }        
        
        for (Component component: composite.getComponents()) {
            for (ComponentReference reference: component.getReferences()) {
                for ( ComponentService service : reference.getTargets()){
                    for (Binding binding: service.getBindings()) {
                        resolveRemoteReferenceBinding(reference, service, binding);
                    }
                }             
            }
        }         
    }
    
    private void resolveRemoteReferenceBinding(Reference reference, Service service,  Binding binding){
        if (binding.isUnresolved()) {
            // find the right endpoint for this reference/binding. This relies on looking
            // up every binding URI. If a response is returned then it's set back into the
            // binding uri
            String uri = "";
            
            try {
                uri = ((SCADomainEventService)scaDomain).findServiceEndpoint(domainURI, 
                                                                             service.getName(), 
                                                                             binding.getClass().getName());
            } catch(Exception ex) {
                logger.log(Level.WARNING, 
                           "Unable to  find service: "  +
                           domainURI + " " +
                           nodeURI + " " +
                           binding.getURI() + " " +
                           binding.getClass().getName() + " " +
                           uri);
            }
             
            if (!uri.equals(SCADomainEventService.SERVICE_NOT_REGISTERED)){
                binding.setURI(uri);
            }
        }          
    }    

    public void setReferenceEndpoint(String referenceName, String bindingClassName, String serviceURI) throws NodeException {
        // find the named reference and binding and update the uri from this message
        Reference reference = findReference(referenceName);
        
        if (reference != null){
            // find the matching binding and set it's uri
            for( Binding binding : reference.getBindings()){
                if (binding.getClass().getName().equals(bindingClassName)){
                    binding.setURI(serviceURI);
                }
            }
        }
    }
    
    private Reference findReference(String referenceName){

        for (Reference reference: nodeComposite.getReferences()) {
            if (reference.getName().equals(referenceName)){
                return reference; 
            }
        }
        
        for (Component component: nodeComposite.getComponents()) {
            for (ComponentReference reference: component.getReferences()) {
                if (reference.getName().equals(referenceName)){
                    return reference; 
                }
            }
        }
        
        for (Composite composite : nodeComposite.getIncludes()) { 
            for (Component component: composite.getComponents()) {
                for (ComponentReference reference: component.getReferences()) {
                    if (reference.getName().equals(referenceName)){
                        return reference; 
                    }
                }
            }
        }           

        return null;
    }
}
