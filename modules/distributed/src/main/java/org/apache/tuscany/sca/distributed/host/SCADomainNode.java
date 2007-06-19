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

package org.apache.tuscany.sca.distributed.host;

import org.osoa.sca.ServiceReference;

/**
 * A handle to an SCA domain.
 * 
 * @version $Rev$ $Date$
 */
public interface SCADomainNode {
    
    /**
     * Returns the name of the node that this part of the
     * distributed domain is running on
     * 
     * @return the node name
     */
    public String getNodeName();    
    
    /**
     * Returns a proxy for a node service provided by a component in the SCA domain node.
     * 
     * @param businessInterface the interface that will be used to invoke the
     *            service
     * @param serviceName the name of the service
     * @param <B> the Java type of the business interface for the service
     * @return an object that implements the business interface
     */
    public abstract <B> B getNodeService(Class<B> businessInterface, String serviceName);

    /**
     * Returns a ServiceReference for a node service provided by a component in the
     * SCA domain node.
     * 
     * @param businessInterface the interface that will be used to invoke the
     *            service
     * @param serviceName the name of the service
     * @param <B> the Java type of the business interface for the service
     * @return a ServiceReference for the designated service
     */
    public abstract <B> ServiceReference<B> getNodeServiceReference(Class<B> businessInterface, String referenceName);

}
