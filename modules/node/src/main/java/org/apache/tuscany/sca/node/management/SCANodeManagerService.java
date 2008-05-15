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

package org.apache.tuscany.sca.node.management;

import org.apache.tuscany.sca.node.NodeException;
import org.osoa.sca.annotations.OneWay;
import org.osoa.sca.annotations.Remotable;


/**
 * The management interface for a node
 *
 * @version $Rev: 552343 $ $Date: 2007-09-07 12:41:52 +0100 (Fri, 07 Sep 2007) $
 */
@Remotable
public interface SCANodeManagerService {

    /**
     * Returns the URI of the SCA node. That URI is the endpoint of the
     * SCA node administration service.
     *
     * @return the URI of the SCA node
     */
    String getURI();

    /**
     * Add an SCA contribution into the node.
     *
     * @param contributionURI the URI of the contribution
     * @param contributionURL the URL of the contribution
     */
    void addContribution(String contributionURI, String contributionURL) throws NodeException;

    /**
     * Remove an SCA contribution from the node.
     *
     * @param contributionURI the URI of the contribution
     */
    void removeContribution(String contributionURI) throws NodeException;

    /**
     * Add the named deployable composite to the domain level composite
     *
     * @param compositeName the name of the composite
     */
    void addToDomainLevelComposite(String compositeName) throws NodeException;

    /**
     * Start the SCA node service.
     */
    void start() throws NodeException;    

    /**
     * Stop the SCA node service.
     */
    void stop() throws NodeException; 

    /**
     * Destroy the SCA node service. destroyNode rather than just destroy
     * as the WSDL processing struggles with methods called destroy
     */
    @OneWay
    void destroyNode();

    /**
     * Pass in an updated version of a composite in base64 encoded XML form. The node will compare
     * this against the version of the composite it has and apply and changes found
     * @param compositeQName
     * @param compositeXMLBase64
     * @throws NodeException
     */
    void updateComposite(String compositeQName, String compositeXMLBase64 ) throws NodeException;
}
