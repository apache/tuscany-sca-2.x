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

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.domain.SCADomain;
import org.apache.tuscany.sca.node.NodeException;
import org.apache.tuscany.sca.node.SCANode;

/**
 * A dummy representation of and SCA Node used simply to allow
 * CallableReferences in the JVM where the domain is being run
 * to be looked up
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-09 23:54:46 +0100 (Sun, 09 Sep 2007) $
 */
public class SCADummyNodeImpl implements SCANode {
    
    private SCADomain scaDomain = null;

    public SCADummyNodeImpl(SCADomain scaDomain) {
        this.scaDomain = scaDomain;
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
