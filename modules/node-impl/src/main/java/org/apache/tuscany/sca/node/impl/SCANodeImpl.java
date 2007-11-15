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
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.binding.sca.impl.SCABindingImpl;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.DeployedArtifact;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.impl.ModelResolverImpl;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.domain.SCADomain;
import org.apache.tuscany.sca.domain.SCADomainSPI;
import org.apache.tuscany.sca.host.embedded.impl.ReallySmallRuntime;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletHostExtensionPoint;
import org.apache.tuscany.sca.node.NodeException;
import org.apache.tuscany.sca.node.NodeFactoryImpl;
import org.apache.tuscany.sca.node.SCADomainFinder;
import org.apache.tuscany.sca.node.SCANode;

/**
 * A local representation of the sca domain running on a single node
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-09 23:54:46 +0100 (Sun, 09 Sep 2007) $
 */
public class SCANodeImpl implements SCANode {
	
    private final static Logger logger = Logger.getLogger(SCANodeImpl.class.getName());
	     
    // class loader used to get application resources
    private ClassLoader nodeClassLoader;    
    
    // identity and endpoints for the node and the domain it belongs to
    private String nodeURI;
    private URL nodeURL;
    private String domainURI; 
    private String nodeGroupURI;

    // The tuscany runtime that does the hard work
    private ReallySmallRuntime nodeRuntime;
    
    // the top level components in this node. A subset of the the domain level composite
    private Composite nodeComposite; 
    
    // the domain that the node belongs to. This object acts as a proxy to the domain
    private SCADomain scaDomain;
    
    // collection for managing contributions that have been added to the node 
    private Map<String, Contribution> contributions = new HashMap<String, Contribution>();    
    private Map<QName, Composite> composites = new HashMap<QName, Composite>();
    private Map<String, Composite> compositeFiles = new HashMap<String, Composite>();
    private List<QName> compositesToStart = new ArrayList<QName>();
       
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
    public SCANodeImpl(String nodeURI, String domainURI, String nodeGroupURI) throws NodeException {
        this.domainURI = domainURI;
        this.nodeURI = nodeURI;
        this.nodeGroupURI = nodeGroupURI;
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
    public SCANodeImpl(String nodeURI, String domainURI, String nodeGroupURI, ClassLoader cl) throws NodeException {
        this.domainURI = domainURI;
        this.nodeURI = nodeURI;
        this.nodeGroupURI = nodeGroupURI;
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
// TODO: doing this breaks testcase
//                    servletHost.setContextPath(path);
                }
            }            
            
            // If a non-null domain name is provided make the node available to the model
            // this causes the runtime to start registering binding-sca service endpoints
            // with the domain so only makes sense if we know we have a domain to talk to
            if (domainURI != null) {
                ModelFactoryExtensionPoint factories = nodeRuntime.getExtensionPointRegistry().getExtensionPoint(ModelFactoryExtensionPoint.class);
                NodeFactoryImpl nodeFactory = new NodeFactoryImpl(this);
                factories.addFactory(nodeFactory);    
            }
 
            // Create an in-memory domain level composite
            AssemblyFactory assemblyFactory = nodeRuntime.getAssemblyFactory();
            nodeComposite = assemblyFactory.createComposite();
            nodeComposite.setName(new QName(Constants.SCA10_NS, "node"));
            nodeComposite.setURI(nodeURI);
            
            // add the top level composite into the composite activator
            nodeRuntime.getCompositeActivator().setDomainComposite(nodeComposite);             
            
            // create a link to the domain 
            scaDomain = SCADomainFinder.newInstance().getSCADomain(domainURI);
            
            // add the node URI to the domain
            ((SCADomainProxyImpl)scaDomain).addNode(this);  
           
            
        } catch(NodeException ex) {
            throw ex;
        } catch(Exception ex) {
            throw new NodeException(ex);
        }
    }
    
    // temp methods to help integrate with existing code
    
    public ReallySmallRuntime getNodeRuntime() {
        return nodeRuntime;
    }
    
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
    
    
    // API methods 
    
    public void start() throws NodeException {
        startComposites();
    }
    
    public void stop() throws NodeException {
        stopComposites();
    }
    
    public void destroy() throws NodeException {
        try {
            if (compositesToStart.size() != 0) {
                stopComposites();
            }
            removeAllContributions();           
            nodeRuntime.stop();
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
            if (contributionURL != null) {
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
                
                // remember all the deployable composites ready to be started
                for (Composite composite : contribution.getDeployables()) {
                    compositesToStart.add(composite.getName());
                }  
                
                // add the contribution to the domain. It will generally already be there
                // unless the contribution has been added to the node itself. 
                ((SCADomainProxyImpl)scaDomain).registerContribution(nodeURI, contributionURI, contributionURL.toExternalForm());                  
                
            } else {
                    throw new ActivationException("Contribution " + contributionURL + " not found");
            }  
        } catch (Exception ex) {
            throw new NodeException(ex);
        }        
    }

    public void startContribution(String contributionURI) throws NodeException {
        try {

            Contribution contribution = contributions.get(contributionURI);
            for (Composite composite : contribution.getDeployables()) {
                    startComposite(composite);
            }  

        } catch (ActivationException e) {
            throw new NodeException(e);
        } catch (CompositeBuilderException e) {
            throw new NodeException(e);
        }
    }

    public void removeContribution(String contributionURI) throws NodeException {
        try { 
            Contribution contribution = contributions.get(contributionURI);
            
            // remove the local record of composites associated with this contribution
            for (DeployedArtifact artifact : contribution.getArtifacts()) {
                if (artifact.getModel() instanceof Composite) {
                    Composite composite = (Composite)artifact.getModel();
                    composites.remove(composite.getName());
                    compositeFiles.remove(composite.getURI());
                    compositesToStart.remove(composite.getName());
                }
            }            
        
            // remove the contribution from the runtime
            nodeRuntime.getContributionService().remove(contributionURI);
            
            // remove the local record of the contribution
            contributions.remove(contributionURI);
        } catch (Exception ex) {
            throw new NodeException(ex);
        }   

    }

    private void removeAllContributions() throws NodeException {
        try {     
            // Remove all contributions
            for (String contributionURI : contributions.keySet()){
                nodeRuntime.getContributionService().remove(contributionURI);
            }
            
            // remove local records
            contributions.clear();
            composites.clear();
            compositeFiles.clear();
            compositesToStart.clear();
            
        } catch (Exception ex) {
            throw new NodeException(ex);
        }   
    }
    
    public void addToDomainLevelComposite(QName compositeName) throws NodeException {
        // if the named composite is not already in the list then add it
        Composite composite = composites.get(compositeName);
        if (composite == null) {
            throw new NodeException("Composite not found: " + compositeName);
        }
        if (compositesToStart.indexOf(compositeName) == -1 ){
            compositesToStart.add(compositeName);  
        }
    }
    
    public void addToDomainLevelComposite(String compositePath) throws NodeException {
        // if the composite is not already in the list then add it
        Composite composite = compositeFiles.get(compositePath);
        if (composite == null) {
            throw new NodeException("Composite file not found: " + compositePath);
        }
        QName compositeName = composite.getName();
        if (compositesToStart.indexOf(compositeName) == -1 ){
            compositesToStart.add(compositeName);  
        }
    }

    /**
     * Configure the default HTTP port for this node.
     */
    private void configureDefaultPort() {
        Composite composite = composites.get(compositesToStart.get(0));
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
            if (compositesToStart.size() == 0 ){
                logger.log(Level.INFO, nodeURI + 
                                       " has no composites to start" );
            } else {
                
                // Configure the default server port for the node
                configureDefaultPort();
                
                for (QName compositeName : compositesToStart) {
                    Composite composite = composites.get(compositeName);
                    if (composite == null) {
                        logger.log(Level.INFO, "Composite not found during start: " + compositeName);
                    } else {
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
        
        // Add the composite to the top level domain
        nodeComposite.getIncludes().add(composite);
        nodeRuntime.getCompositeBuilder().build(composite); 
        
        // activate the composite
        nodeRuntime.getCompositeActivator().activate(composite); 
        
        registerRemoteServices(composite);
        
        resolveRemoteReferences(composite);
        
        //start the composite
        nodeRuntime.getCompositeActivator().start(composite);
    }    

    private void stopComposites() throws NodeException {
        
        try {
            if (compositesToStart.size() == 0 ){
                throw new NodeException("Stopping node " + 
                                        nodeURI + 
                                        " with no composite started");
            }
            for (QName compositeName : compositesToStart) {
                Composite composite = composites.get(compositeName);   

                stopComposite(composite);
            }
            
        } catch (NodeException ex) {
            throw ex;                  
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
    
    private void registerRemoteServices(Composite composite){
        // Loop through all service binding URIs registering them with the domain 
        // TODO - the sca binding does this for itself at the moment
        //        so that function should move here to make it
        //        applicable to other bindings
        for (Service service: composite.getServices()) {
            for (Binding binding: service.getBindings()) {
                String uri = binding.getURI();
                if (uri != null) {
                    try {
                        ((SCADomainSPI)scaDomain).registerServiceEndpoint(domainURI, 
                                                                          nodeURI, 
                                                                          service.getName(), 
                                                                          binding.getClass().getName(), 
                                                                          uri);
                    } catch(Exception ex) {
                        logger.log(Level.WARNING, 
                                   "Unable to  register service: "  +
                                   domainURI + " " +
                                   nodeURI + " " +
                                   service.getName()+ " " +
                                   binding.getClass().getName() + " " +
                                   uri);
                    }
                }
            }
        }
        
        for (Component component: composite.getComponents()) {
            for (ComponentService service: component.getServices()) {
                for (Binding binding: service.getBindings()) {
                    if (binding instanceof SCABindingImpl){
                        // TODO - only register it its in remote node
                    } else {
                        String uriString = binding.getURI();
                        if (uriString != null) {
                            try {
                                URI uri = new URI(uriString);
                                ((SCADomainSPI)scaDomain).registerServiceEndpoint(domainURI, 
                                                                                  nodeURI, 
                                                                                  uri.getPath(),
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
            }
        }
    }
    
    private void resolveRemoteReferences(Composite composite){
        // Loop through all reference binding URIs. Any that are not resolved
        // should be looked up in the domain
        // TODO - the sca binding does this for itself at the moment
        //        so that function should move here to make it
        //        applicable to other bindings
        for (Reference reference: composite.getReferences()) {
            for (Binding binding: reference.getBindings()) {
                if (binding.isUnresolved()) {
                    if (binding instanceof SCABindingImpl){
                        // TODO - only find uri if its in a remote node
                    } else {
                        // find the right endpoint for this reference/binding. This relies on looking
                        // up every binding URI. If a response is returned then it's set back into the
                        // binding uri
                        String uri = "";
                        try {
                            uri = ((SCADomainSPI)scaDomain).findServiceEndpoint(domainURI, 
                                                                                binding.getURI(), 
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
                         
                        if (uri.equals("") == false){
                            binding.setURI(uri);
                        }
                    }
                }
            }
        }
        
        for (Component component: composite.getComponents()) {
            for (ComponentReference reference: component.getReferences()) {
                for (Binding binding: reference.getBindings()) {
                    if (binding.isUnresolved()) {
                        if (binding instanceof SCABindingImpl){
                            // TODO - only find uri if its in a remote node
                        } else {
                            // find the right endpoint for this reference/binding. This relies on looking
                            // up every binding URI. If a response is returned then it's set back into the
                            // binding uri
                            String uri = "";
                            try {
                                uri = ((SCADomainSPI)scaDomain).findServiceEndpoint(domainURI, 
                                                                                    binding.getURI(), 
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
                             
                            if (uri.equals("") == false){
                                binding.setURI(uri);
                            }
                        }
                    }
                }
            }
        }        
    }
}
