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

package org.apache.tuscany.sca.distributed.domain.impl;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.binding.sca.impl.SCABindingImpl;
import org.apache.tuscany.sca.distributed.domain.DistributedSCADomain;
import org.apache.tuscany.sca.distributed.management.ServiceDiscovery;
import org.apache.tuscany.sca.host.embedded.impl.EmbeddedSCADomain;


/**
 * A local representation of the sca domain distributed across a number
 * of separate nodes. This provides access to various information relating
 * to the distributed domain
 * 
 * @version $Rev: 552343 $ $Date: 2007-07-01 18:43:40 +0100 (Sun, 01 Jul 2007) $
 */
public class DistributedSCADomainImpl implements DistributedSCADomain {
    
    private String nodeName;
    private String domainName;
    protected EmbeddedSCADomain domain;
    
    public DistributedSCADomainImpl(String domainName){
        this.domainName = domainName;      
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
    
    public void setNodeName(String nodeName){
        this.nodeName = nodeName;
    }    
    
    /**
     * Returns the name of the distributed domain that this node
     * is part of.
     * 
     * @return the domain name
     */
    public String getDomainName(){
        return domainName;
    }
    
    public void setDomainName(String domainName){
        this.domainName = domainName;
    }    
    
    /**
     * Return an interface for registering and looking up remote services
     * 
     * @return The service discovery interface
     */    
    public ServiceDiscovery getServiceDiscovery(){
        return null;
    }
    
    /**
     * Associates this distributed domain representation to all of the 
     * sca binding objects within a composite. The sca binding uses this
     * distributed domain representation for domain level operations like
     * find the enpoints of remote services. 
     * 
     * @param composite the composite that this object will be added to 
     */      
    public void addDistributedDomainToBindings(Composite composite){
        // traverse the composite adding in the distributed domain
        // reference into all sca bindings. 
        for(Component component : composite.getComponents()){
            for (ComponentService service : component.getServices()) {
                for(Binding binding : service.getBindings()){
                    if (binding instanceof SCABinding) {
                        // TODO could do with changing the sca binding SPI 
                        // to carry this piece of information
                        SCABindingImpl scaBinding = (SCABindingImpl)binding;
                        scaBinding.setDistributedDomain(this);
                    }
                }
            }
            
            for (ComponentReference reference : component.getReferences()) {                                      
                for(Binding binding : reference.getBindings()){
                    if (binding instanceof SCABinding) {
                        // TODO could do with changing the sca binding SPI 
                        // to carry this piece of information
                        SCABindingImpl scaBinding = (SCABindingImpl)binding;
                        scaBinding.setDistributedDomain(this);
                    }
                }

                // and reference targets. strange one this but the wiring puts
                // all the bindings into a references target if the target remains
                // unresolved after building
                // This actually the only ones we really need to set as by now
                // the service and reference bindings will already be resolved
                // but it's not doing any hame for the time being
                for ( ComponentService target : reference.getTargets()){
                    for(Binding binding : target.getBindings()){
                        if (binding instanceof SCABinding) {
                            // TODO could do with changing the sca binding SPI 
                            // to carry this piece of information
                            SCABindingImpl scaBinding = (SCABindingImpl)binding;
                            scaBinding.setDistributedDomain(this);
                        }
                    }                      
                }  
            }   
        }        
    }    
    
    public void setManagementDomain(EmbeddedSCADomain localDomain){
        this.domain = localDomain;
    }    
    
}
