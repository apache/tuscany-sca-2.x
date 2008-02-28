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
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.DomainBuilder;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.core.context.ServiceReferenceImpl;
import org.apache.tuscany.sca.databinding.impl.XSDDataTypeConverter.Base64Binary;
import org.apache.tuscany.sca.domain.DomainException;
import org.apache.tuscany.sca.domain.SCADomain;
import org.apache.tuscany.sca.host.embedded.impl.ReallySmallRuntime;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletHostExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.node.NodeException;
import org.apache.tuscany.sca.node.NodeFactoryImpl;
import org.apache.tuscany.sca.node.SCADomainAccess;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeSPI;
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
public class SCANodeImpl implements SCANode, SCADomainAccess  {
	
    private final static Logger logger = Logger.getLogger(SCANodeImpl.class.getName());
	     
    // class loader used to get application resources
    private ClassLoader nodeClassLoader;    
    
    // store and endpoints for the node and the domain it belongs to
    private String nodeURI;
    private String domainURI; 
    
    // the URL of the node 
    private URL nodeURL;

    // The tuscany runtime that does the hard work
    private ReallySmallRuntime nodeRuntime;
    
    // the top level components in this node. A subset of the the domain level composite
    private Composite nodeComposite; 
    
    // the started status of the node
    private boolean nodeStarted = false;
    
    // collection for managing contributions that have been added to the node 
    private Map<String, Contribution> contributions = new HashMap<String, Contribution>();    
    private Map<QName, Composite> composites = new HashMap<QName, Composite>();
    private Map<String, Composite> compositeFiles = new HashMap<String, Composite>();
    
    private QName nodeManagementCompositeName = new QName("http://tuscany.apache.org/xmlns/tuscany/1.0", "node");
       
    // methods defined on the implementation only
       
    /** 
     * Creates a node connected to a wider domain.  To find its place in the domain 
     * node and domain identifiers must be provided. 
     *  
     * @param physicalNodeUri - if this is a url it is assumed that this will be used as root url for management components, e.g. http://localhost:8082
     * @param domainUri - identifies what host and port the domain service is running on, e.g. http://localhost:8081
     * @throws ActivationException
     */
    public SCANodeImpl(String physicalNodeURI, String domainURI) throws NodeException {
        this.domainURI = domainURI;
        this.nodeURI = physicalNodeURI;
        this.nodeClassLoader = Thread.currentThread().getContextClassLoader();        
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
            
            // check that the node uri is an absolute url,  
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
            
            // Create an in-memory domain level composite
            AssemblyFactory assemblyFactory = nodeRuntime.getAssemblyFactory();
            nodeComposite = assemblyFactory.createComposite();
            nodeComposite.setName(new QName(Constants.SCA10_NS, "node"));
            nodeComposite.setURI(nodeURI);
            
            // add the top level composite into the composite activator
            nodeRuntime.getCompositeActivator().setDomainComposite(nodeComposite);              
            
        } catch(NodeException ex) {
            throw ex;
        } catch(Exception ex) {
            throw new NodeException(ex);
        }
    }

    // SCANode API methods 
    
    public void start() throws NodeException {
        if (!nodeStarted){
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
    
    @Deprecated
    public SCADomain getDomain() {
        return null;
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
    
    public void startComposite(QName compositeQName) throws NodeException {
        addToDomainLevelComposite(compositeQName);
        start();
    }
    
    @Deprecated
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
                                    
                } catch (Exception ex) {
                    throw new NodeException(ex);
                }                 
            }
        }  
        
    }
    
    @Deprecated
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
    
    private void activateComposite(Composite composite) throws CompositeBuilderException, ActivationException {
        logger.log(Level.INFO, "Building composite: " + composite.getName());
        
        // Create the model for the composite
        nodeRuntime.getCompositeBuilder().build(composite); 
        
        // activate the composite
        nodeRuntime.getCompositeActivator().activate(composite); 
            
    }  
    
    private void deactivateComposite(Composite composite) throws CompositeBuilderException, ActivationException {
        nodeRuntime.getCompositeActivator().deactivate(composite);
    }

    private void startComposites() throws NodeException {
        try {
            if (nodeComposite.getIncludes().size() == 0 ){
                logger.log(Level.INFO, nodeURI + 
                                       " has no composites to start" );
            } else {
                
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
    
    // SCADomainAccess methods
    
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

    public <B> ServiceReference<B> createServiceReference(Class<B> businessInterface, String targetURI) {
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

        // Lookup the component 
        Component component = null;
         
        if ( nodeComposite != null ) {
            for (Composite composite: nodeComposite.getIncludes()) {
                for (Component compositeComponent: composite.getComponents()) {
                    if (compositeComponent.getName().equals(componentName)) {
                        component = compositeComponent;
                    }
                }
            }    
        }
       
        if (component == null) {
            throw new ServiceRuntimeException("The service " + name + " has not been contributed to the domain");
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
