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

package org.apache.tuscany.sca.domain.management;

import org.apache.tuscany.sca.domain.SCADomain;
import org.apache.tuscany.sca.domain.spi.SCADomainEventService;
import org.apache.tuscany.sca.domain.spi.SCADomainSPI;



/**
 * Connects the domain to the domain manager service
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-11 18:45:36 +0100 (Tue, 11 Sep 2007) $
 */
public interface SCADomainManagerInitService {
    
    /**
     * Set the domain  object
     * 
     * @param domain
     */
    void setDomain(SCADomain domain);
     
    /**
     * Set the domain SPI object
     * 
     * @param domain
     */
    void setDomainSPI(SCADomainSPI domainSPI);
    
    /**
     * Set the domain event object
     * 
     * @param domain
     */
    void setDomainEventService(SCADomainEventService domainEventService);    
    
}
