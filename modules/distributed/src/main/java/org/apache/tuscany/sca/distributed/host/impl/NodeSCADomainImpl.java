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

package org.apache.tuscany.sca.distributed.host.impl;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.core.runtime.ActivationException;
import org.apache.tuscany.sca.core.runtime.CompositeActivator;
import org.apache.tuscany.sca.distributed.host.DistributedSCADomain;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.ServiceRuntimeException;

/**
 * An SCA domain that holds components used for managing a node. The components
 * are read from a ".node" file to differentiate them from any other ".composite" 
 * file that might appear in a contribution. Other than that the ".node" file
 * is like any other SCA assembly. 
 * 
 * @version $Rev$ $Date$
 */
public class NodeSCADomainImpl extends DistributedSCADomain {
       
    private String domainURI;
    private String nodeName;
    private NodeRuntime nodeRuntime;
    private Composite nodeComposite;
    private ClassLoader runtimeClassLoader;
    private Map<String, Component> nodeComponents = new HashMap<String, Component>();
    
    
    /**
     * Constructs a new node in the distributed domain
     *
     * @param runtimeClassLoader
     * @param domainURI
     */
    public NodeSCADomainImpl(ClassLoader runtimeClassLoader,
                             String domainURI,
                             String nodeName) {
        this.runtimeClassLoader = runtimeClassLoader;
        this.domainURI = domainURI;
        this.nodeName = nodeName;
        
        // Create a runtime to host the node services
        nodeRuntime = new NodeRuntime(runtimeClassLoader);
        
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
    
    /**
     * Returns the domain that is running the system
     * components for this node. In this case it is this
     * domain
     * 
     * @return the node domain
     */
    public  DistributedSCADomain getNodeDomain(){
        return this;
    }
    
    /** 
     * Starts the domain operation. In this case it involves
     * reading the components from the nodename.node file 
     *  
     * @throws ActivationException
     */    
    public void start()
      throws ActivationException {

        try {
        
            // start up the node services
            
            // Start the node service runtime. A null domain is passed in here
            // to stop the SCABinding in this runtime trying to look 
            // back into this domain for topology information 
            nodeRuntime.start(null);
            
            // get the node service configuration model
                   
            // we expect the node file to be in the same package as the
            // mainline for the node itself. At some point we may want to
            // get smart about where we find this information. 
            // For now we look if there is a file specifically for this
            // node and if not we read the generic file. 
            URL url = runtimeClassLoader.getResource(nodeName + ".node");
            
            if (url == null) {
                url = runtimeClassLoader.getResource("default.node");
            }
            
            if (url == null) {
                throw new ServiceRuntimeException("Node file not found at either  " 
                                                  + nodeName + 
                                                  ".node or node.node");
            }
            
            // load the node file
            nodeComposite = nodeRuntime.getNodeComposite(url);
                       
            // activate the composite
            CompositeActivator compositeActivator = nodeRuntime.getCompositeActivator();
            compositeActivator.activate(nodeComposite);
            
            // start and record the components
            for (Component component : nodeComposite.getComponents()) {
                nodeComponents.put(component.getName(), component);
                compositeActivator.start(component);
            }           
            
        } catch(Exception ex) {
            throw new ActivationException(ex);
        }
    }
    
    /**
     * Stops the runtime and all running components
     * 
     * @throws ActivationException
     */    
    public void stop() throws ActivationException {
                
        // stop the nodes components
        for (Component component : nodeComponents.values()) {
            nodeRuntime.getCompositeActivator().stop(component);
        }
        
        // Stop the runtimes
        nodeRuntime.stop();
    }
    
    @Override
    public void close() {
        super.close();
    }

    @Override
    public String getURI() {
        return domainURI;
    }    
    
    @Override
    public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * Direct copy from getService but dealing in nodeCompinents
     * 
     * @param <B>
     * @param businessInterface
     * @param serviceName
     * @return
     */
    public <B> B getService(Class<B> businessInterface, String serviceName) {       
        ServiceReference<B> serviceReference = getServiceReference(businessInterface, serviceName);
        if (serviceReference == null) {
            throw new ServiceRuntimeException("Service not found: " + serviceName);
        }
        return serviceReference.getService();
    }  
    
    /**
     * Direct copy from getServiceReference but dealing in nodeCompinents
     * 
     * @param <B>
     * @param businessInterface
     * @param name
     * @return
     */
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
        Component component = nodeComponents.get(componentName);
        if (component == null) {
            throw new ServiceRuntimeException("Component not found: " + componentName);
        }
        ComponentContext componentContext = null;

        // If the component is a composite, then we need to find the
        // non-composite
        // component that provides the requested service
        if (component.getImplementation() instanceof Composite) {
            ComponentService promotedService = null;
            for (ComponentService componentService : component.getServices()) {
                if (serviceName == null || serviceName.equals(componentService.getName())) {

                    CompositeService compositeService = (CompositeService)componentService.getService();
                    if (compositeService != null) {
                        promotedService = compositeService.getPromotedService();
                        SCABinding scaBinding = promotedService.getBinding(SCABinding.class);
                        if (scaBinding != null) {
                            Component promotedComponent = scaBinding.getComponent();
                            if (serviceName != null) {
                                serviceName = "$promoted$." + serviceName;
                            }
                            componentContext = (ComponentContext)promotedComponent;
                        }
                    }
                    break;
                }
            }
            if (componentContext == null) {
                throw new ServiceRuntimeException("Composite service not found: " + name);
            }
        } else {
            componentContext = (ComponentContext)component;
        }

        ServiceReference<B> serviceReference;
        if (serviceName != null) {
            serviceReference = componentContext.createSelfReference(businessInterface, serviceName);
        } else {
            serviceReference = componentContext.createSelfReference(businessInterface);
        }
        return serviceReference;

    }    
}
