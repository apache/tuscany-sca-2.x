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

import org.apache.tuscany.sca.distributed.domain.Domain;
import org.apache.tuscany.sca.distributed.domain.DomainFactory;
import org.apache.tuscany.sca.distributed.domain.ServiceDiscoveryService;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ServiceReference;


/**
 * A factory that always returns the same domain object
 * 
 * @version $Rev: 556897 $ $Date$
 */
public class DomainFactoryImpl implements DomainFactory {
	
	Domain domain = null;
	
	public DomainFactoryImpl(Domain domain){
		this.domain = domain;
	}
    
    /**
     * Returns the domain object
     * 
     * @return the domain 
     */
    public Domain getDomain(){
    	return domain;
    }
    
}
