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

package org.apache.tuscany.sca.binding.sca.axis2;

import java.io.Externalizable;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.domain.DomainException;
import org.apache.tuscany.sca.domain.SCADomain;
import org.apache.tuscany.sca.domain.SCADomainEventService;
import org.apache.tuscany.sca.domain.model.DomainModel;
import org.apache.tuscany.sca.host.embedded.impl.ReallySmallRuntime;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ServiceReference;

/**
 * The very minimum domain implementation to get these tests going without creating a dependency on 
 * domain-impl
 * 
 * @version $Rev: 552343 $ $Date$
 */
public class TestDomain implements SCADomain, SCADomainEventService {
    
    private String domainURI;
    
    private ReallySmallRuntime domainRuntime;
    private Composite domainComposite = null;
    
    private ClassLoader cl = TestDomain.class.getClassLoader();
        
    
    public TestDomain(String domainURI) {
        try {
            this.domainURI = domainURI; 
            domainRuntime = new ReallySmallRuntime(cl);
            domainRuntime.start();
            
            AssemblyFactory assemblyFactory = domainRuntime.getAssemblyFactory();
            domainComposite = assemblyFactory.createComposite();
            domainComposite.setName(new QName(Constants.SCA10_NS, "domain"));
            domainComposite.setURI(domainURI);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
    
    public void start() throws DomainException {
        domainRuntime.getDomainBuilder().wireDomain(domainComposite);
    }    
    
    public void stop() throws DomainException {
    }       
    
    public void destroy() throws DomainException {
    }
        
    public String getURI(){
        return domainURI;
    }
     
    
    public DomainModel getDomainModel() {
        return null;
    }
     

    public void registerServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName, String URL){
    }
    
    public void unregisterServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName){  
    }
    
    public void registerNodeStart(String nodeURI) throws DomainException{
        
    }
    
    public void registerNodeStop(String nodeURI) throws DomainException {
        
    }
    

    public String findServiceEndpoint(String domainUri, String serviceName, String bindingName){
        return null;
    }
    
    public String findServiceNode(String domainUri, String serviceName, String bindingName){
        return null;
    }
    
    public SCADomain getDomain(){
        return null;
    }
    
    public void registerNode(String nodeURI, String nodeURL, Externalizable nodeManagerRefence){
    }
    

    public void unregisterNode(String nodeURI){
    }
    
    public void registerContribution(String nodeURI, String contributionURI, String contributionURL){
    }
    
    public void updateContribution(String contributionURI, URL contributionURL) throws DomainException {
        
    }
    
    public void registerDomainLevelComposite(String nodeURI, String compositeQNameString) throws DomainException{

    }    
    
    public void unregisterContribution(String nodeURI, String contributionURI){
    }    
    
    public void addContribution(String contributionURI, URL contributionURL) throws DomainException {
    }
        

    public void removeContribution(String contributionURI) throws DomainException {
    }
    
    public void addDeploymentComposite(String contributionURI, String compositeXML) throws DomainException {
    }
    
    public void updateDeploymentComposite(String contributionURI, String compositeXML) throws DomainException {
        
    }

    public void addToDomainLevelComposite(QName compositeQName) throws DomainException {
    }
  
    public void removeFromDomainLevelComposite(QName compositeQName) throws DomainException {
    }
    
    public String getDomainLevelComposite(){
        return null;
    }

    public String getQNameDefinition(QName artifact){
        return null;
    }
    
    public void startComposite(QName compositeName) throws DomainException {
       
    }

    public void stopComposite(QName compositeName) throws DomainException {             
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
    
    public void addComposite(Composite composite){
        domainComposite.getIncludes().add(composite);
    }
}
