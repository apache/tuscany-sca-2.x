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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.contribution.DefaultModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.DefaultURLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.core.runtime.ActivationException;
import org.apache.tuscany.sca.distributed.host.DistributedSCADomain;
import org.apache.tuscany.sca.distributed.host.impl.DistributedSCADomainImpl;
import org.apache.tuscany.sca.distributed.host.impl.NodeSCADomainImpl;
import org.apache.tuscany.sca.distributed.node.ComponentRegistry;
import org.apache.tuscany.sca.topology.Component;
import org.apache.tuscany.sca.topology.DefaultTopologyFactory;
import org.apache.tuscany.sca.topology.Node;
import org.apache.tuscany.sca.topology.Runtime;
import org.apache.tuscany.sca.topology.TopologyFactory;
import org.apache.tuscany.sca.topology.xml.TopologyDocumentProcessor;
import org.apache.tuscany.sca.topology.xml.TopologyProcessor;

/**
 * A stand alone runtime node that holds a NodeSCADomain to run managment components and one 
 * or more DistributedSCADomains for application components 
 * 
 * @version $Rev$ $Date$
 */
public class NodeImpl {
  
    private ClassLoader runtimeClassLoader;    
    private String runtimeName;
    private String nodeName;
    private DistributedSCADomain nodeDomain;
    private Map<String, DistributedSCADomain> appDomains = new HashMap<String, DistributedSCADomain>();
    private ComponentRegistry componentRegistry;

    public NodeImpl(String runtimeName,
                              String nodeName,
                              ClassLoader classLoader){
        this.runtimeName = runtimeName;
        this.nodeName = nodeName;
        this.runtimeClassLoader = classLoader;
        this.nodeDomain = new NodeSCADomainImpl(runtimeClassLoader,
                                                runtimeName,
                                                nodeName);     
    }
    
    public void start() 
      throws ActivationException, ContributionReadException {
        //Start the node domain
        nodeDomain.start();  
        
        // load the node composite
        
        // get the component registry 
        componentRegistry = nodeDomain.getService(ComponentRegistry.class, "ComponentRegistry");        
        
        // load the topology from file
        
        // create the factories
        AssemblyFactory factory = new DefaultAssemblyFactory();
        TopologyFactory topologyFactory = new DefaultTopologyFactory();
        
        URLArtifactProcessorExtensionPoint documentProcessors = new DefaultURLArtifactProcessorExtensionPoint(new DefaultModelFactoryExtensionPoint());
        ExtensibleURLArtifactProcessor documentProcessor = new ExtensibleURLArtifactProcessor(documentProcessors); 
        
        // Create Stax processors
        DefaultStAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint(new DefaultModelFactoryExtensionPoint());
        ExtensibleStAXArtifactProcessor staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, XMLInputFactory.newInstance(), XMLOutputFactory.newInstance());
        staxProcessors.addArtifactProcessor(new TopologyProcessor(topologyFactory, factory, staxProcessor));        
        
        // Create document processors
        XMLInputFactory inputFactory = XMLInputFactory.newInstance(); 
        documentProcessors.addArtifactProcessor(new TopologyDocumentProcessor(staxProcessor, inputFactory));

        URL url = runtimeClassLoader.getResource("runtime.topology");
        
        if ( url == null ){
            throw new ActivationException("Unable to find file runtime.topology on classpath");            
        }
        
        Runtime runtime = (Runtime)documentProcessor.read(null, null, url);
        
        // get the node model for this node
        Node thisNode = runtime.getNode(nodeName);
        
        if ( thisNode == null ){
            throw new ActivationException("Node " +
                                          nodeName +
                                          " not defined in runtime topology"); 
        }
        
        // get all the domains that are in the node
        for(String domainName : thisNode.getDomainNames()){
            // create a domain. this will also add it to the
            // appDomains property
            DistributedSCADomain domain = createDistributedDomain(domainName);
                      
            // start the domain
            startDomain(domainName);
        }     
        
        // get all the node models and load the component registry
        // we might expect this to be done across the network
        for(Node node : runtime.getNodes()){
            
            // get all the domains that are in the node
            for(String domainName : node.getDomainNames()){ 
                
                // set node mappings into the component registry  
                for (Component component : node.getComponents(domainName)){
                     componentRegistry.setComponentNode(domainName, component.getName(), node.getName());
                }
                
                // set the scheme base urls into the URI registry
                // TODO 
            }
        }
    }
    
    public void stop()
      throws ActivationException {
        
        for(DistributedSCADomain domain : appDomains.values()){
            domain.close();            
            domain.stop();
        }
        
        nodeDomain.close();        
        nodeDomain.stop();

    }
    
    /**
     * Returns the name of the node that this part of the
     * distributed domain is running on
     * 
     * @return the node name
     */
    public String getNodeName(){
        return nodeName;
    }
    
    public DistributedSCADomain createDistributedDomain(String domainURI){
        DistributedSCADomain domain = new DistributedSCADomainImpl(runtimeClassLoader,
                                                                   domainURI,
                                                                   nodeDomain);
        addDomain(domain);
        
        return domain;
    }
    
    public void addDomain(DistributedSCADomain distributedDomain) {
        appDomains.put(distributedDomain.getURI(), distributedDomain);
    }
    
    public DistributedSCADomain getDomain(String domainURI){
        return appDomains.get(domainURI);
    }
    
    public void startDomain(String domainURI) throws ActivationException {
        DistributedSCADomain appDomain = appDomains.get(domainURI);
        appDomain.start();
    }
    
    public void stopDomain(String domainURI)throws ActivationException {
        DistributedSCADomain appDomain = appDomains.get(domainURI);
        appDomain.stop();
    }
}
