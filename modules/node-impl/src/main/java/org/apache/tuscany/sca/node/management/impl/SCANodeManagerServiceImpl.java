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

package org.apache.tuscany.sca.node.management.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.core.assembly.RuntimeComponentImpl;
import org.apache.tuscany.sca.node.NodeException;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.impl.ComponentInfoImpl;
import org.apache.tuscany.sca.node.impl.SCANodeImpl;
import org.apache.tuscany.sca.node.management.SCANodeManagerInitService;
import org.apache.tuscany.sca.node.management.SCANodeManagerService;
import org.apache.tuscany.sca.node.spi.ComponentInfo;
import org.apache.tuscany.sca.node.spi.ComponentManagerService;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * Manages a node implementation
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-11 18:45:36 +0100 (Tue, 11 Sep 2007) $
 */
@Scope("COMPOSITE")
@Service(interfaces = {SCANodeManagerService.class, SCANodeManagerInitService.class, ComponentManagerService.class})
public class SCANodeManagerServiceImpl implements SCANodeManagerService, SCANodeManagerInitService, ComponentManagerService {
    
    private final static Logger logger = Logger.getLogger(SCANodeManagerServiceImpl.class.getName());

    private SCANodeImpl node;


    // NodeManagerInitService
    
    public void setNode(SCANode node) {
        this.node = (SCANodeImpl)node;
    }
    
    // SCANodeManagerService methods
    
    public String getURI() {
        return node.getURI();
    }
   
    public void addContribution(String contributionURI, String contributionURL) throws NodeException {
        try {
            node.addContributionFromDomain(contributionURI, new URL(contributionURL), null);
        } catch (MalformedURLException ex){
            throw new NodeException(ex);
        }            
    }
   
    public void removeContribution(String contributionURI) throws NodeException {
        node.removeContributionFromDomain(contributionURI);
    }

    public void addToDomainLevelComposite(String compositeName) throws NodeException {
        node.addToDomainLevelCompositeFromDomain(QName.valueOf(compositeName));       
    }
    
    public void start() throws NodeException {
        node.startFromDomain();
    }
    
    public void stop() throws NodeException {
        node.stopFromDomain();
    }
    
    public void destroyNode() {
        // do nothing - the domain can't destroy nodes
    }    
    
    public void updateComposite(String compositeQName, String compositeXMLBase64 ) throws NodeException {
        ((SCANodeImpl)node).updateComposite(QName.valueOf(compositeQName), compositeXMLBase64 );
    }

    // ComponentManagerService
    
    public List<ComponentInfo> getComponentInfos() {
        List<ComponentInfo> componentInfos = new ArrayList<ComponentInfo>();
        for (Component component : node.getComponents()) {
            ComponentInfo componentInfo = new ComponentInfoImpl();
            componentInfo.setName(component.getName());
            componentInfo.setStarted(((RuntimeComponentImpl)component).isStarted());
            componentInfos.add(componentInfo);
        }
        return componentInfos;
    }

    public ComponentInfo getComponentInfo(String componentName) {
        Component component = node.getComponent(componentName);
        ComponentInfo componentInfo = new ComponentInfoImpl();
        componentInfo.setName(component.getName());
        componentInfo.setStarted(((RuntimeComponentImpl)component).isStarted());
        return componentInfo;
    }

}
