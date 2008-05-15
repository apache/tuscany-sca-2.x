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

import java.util.HashMap;

import org.apache.tuscany.sca.domain.DomainException;
import org.apache.tuscany.sca.domain.SCADomain;
import org.apache.tuscany.sca.domain.impl.SCADummyNodeImpl;
import org.apache.tuscany.sca.node.SCADomainFinder;
import org.apache.tuscany.sca.node.SCANode;

/**
 * A finder for SCA domains.
 * 
 * @version $Rev: 580520 $ $Date: 2007-09-29 00:50:25 +0100 (Sat, 29 Sep 2007) $
 */
public class SCADomainFinderImpl extends SCADomainFinder {
    
    private static HashMap<String, SCADomain> domains = new HashMap<String, SCADomain>();
        
    /**
     * Constructs a new SCA domain finder instance.
     */
    public SCADomainFinderImpl() {

    }

    /**
     * Finds an existing SCA domain.
     * 
     * @param domainURI the URI of the domain, this is the endpoint
     * URI of the domain administration service
     * @return the SCA domain
     */
    public SCADomain getSCADomain(String domainURI) throws DomainException {
        SCADomain scaDomain = domains.get(domainURI);
        
        if (scaDomain == null) {
            scaDomain = new SCADomainProxyImpl(domainURI);
            //domains.put(domainURI, scaDomain);
            // TODO - not caching local domains as currently the local domain can
            //        - only handle one node
            //        - provides the management endpoint for that node
            
            // Add the dummy node as there will be no real node in this case
            SCANode scaNode = new SCADummyNodeImpl(scaDomain);
            ((SCADomainProxyImpl)scaDomain).addNode(scaNode);
        }
        return scaDomain;
    }
    
}
