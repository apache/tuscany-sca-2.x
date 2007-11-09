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

package org.apache.tuscany.sca.domain.impl;

import java.net.URL;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.domain.DomainException;
import org.apache.tuscany.sca.domain.SCADomain;
import org.apache.tuscany.sca.domain.SCADomainSPI;
import org.apache.tuscany.sca.domain.model.Domain;
import org.apache.tuscany.sca.node.NodeException;
import org.apache.tuscany.sca.node.SCANode;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ServiceReference;

/**
 * A dummy representation of and SCA Node used simply to control the 
 * endpoint of a callable reference
 * We can remove this if we change the runtime over to referencing the 
 * domain directly rather than going via the node. 
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-09 23:54:46 +0100 (Sun, 09 Sep 2007) $
 */
public class DomainManagerNodeImpl implements SCANode {
	
    private final static Logger logger = Logger.getLogger(DomainManagerNodeImpl.class.getName());
    
    private String nodeEndpoint;
    private SCADomain scaDomain = new DomainManagerDomainImpl();
    private SCADomainSPI realSCADomain = null;
	
    class DomainManagerDomainImpl implements SCADomainSPI {
        public String addNode(String nodeURI, String nodeURL){ 
            return null;
        }
        
        public String removeNode(String nodeURI){ 
            return null;
        }
        
        public void registerContribution(String nodeURI, String contributionURI, String contributionURL) {
        }
        
        public void unregisterContribution(String contributionURI){
        }        
        
        public String  registerServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName, String URL){
            return null;
        }
         
        public String  removeServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName){
            return null;
        }
       
        public String findServiceEndpoint(String domainUri, String serviceName, String bindingName){
            if (nodeEndpoint != null){
                return nodeEndpoint;
            } else {
                return realSCADomain.findServiceEndpoint(domainUri, serviceName, bindingName);
            }
            
        }

        public Domain getDomainModel(){
            return null;
        }
            
        // SCADomain API methods 
        public void start() throws DomainException {
        }        
        
        public void destroy() throws DomainException {
        }
     
        public String getURI(){
            return null;
        }
        
        public void addContribution(String contributionURI, URL contributionURL) throws DomainException {
        }

        public void removeContribution(String uri) throws DomainException {
        }
        
        public void addDeploymentComposite(String contributionURI, String compositeXML) throws DomainException {
        }

        public void addToDomainLevelComposite(QName compositeQName) throws DomainException {
        }
     
        public void removeFromDomainLevelComposite(QName compositeQName) throws DomainException {
        }
          
        public void startComposite(QName compositeName) throws DomainException {            
        }
          
        public void stopComposite(QName qname) throws DomainException {
        }
                 
        public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {

            return null;
        }

        public <B> B getService(Class<B> businessInterface, String serviceName) {
            return null;
        }
        
        public <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String name) {
            return null;
        }        
        
    }

    public DomainManagerNodeImpl(SCADomainSPI scaDomain) {
        this.realSCADomain = scaDomain;
    }    
    
    public void setNodeEndpoint(String nodeEndpoint) {
        this.nodeEndpoint = nodeEndpoint;
    }
    
    // API methods 
    
    public void start() throws NodeException {
    }
    
    public void stop() throws NodeException {

    }    
    
    public void destroy() throws NodeException {
    }
 
    public String getURI(){
        return null;
    }
    
    public SCADomain getDomain(){
        return scaDomain;
    }   
    
    public void addContribution(String contributionURI, URL contributionURL) throws NodeException {
    }
    
    public void removeContribution(String contributionURI) throws NodeException {
    }
    
    public void addToDomainLevelComposite(QName compositeName) throws NodeException {
    }  

    public void addToDomainLevelComposite(String compositePath) throws NodeException {
    }

    public void startContribution(String contributionURI) throws NodeException {
    }
      
}
