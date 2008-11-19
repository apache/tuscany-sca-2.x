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

package org.apache.tuscany.sca.assembly.builder;

import java.util.List;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Service;

/**
 * A builder that handles the configuration of the components inside a
 * composite and the wiring of component references to component services.
 *
 * @version $Rev: 563358 $ $Date: 2007-08-07 01:26:27 +0100 (Tue, 07 Aug 2007) $
 */
public interface DomainBuilder {
    
    /**
     * Wire up the references and service in a domain returning a list
     * of the composites that have changed
     * 
     * @param domainLevelCompsite
     * @return a list of change composites
     * @throws CompositeBuilderException
     */
    List<Composite> wireDomain(Composite domainLevelComposite);
    
    /**
     * Locates the referenced service and updates the URI on the identified binding
     * 
     * @param domainLevelComposite
     * @param referenceName
     * @param bindingClassName
     * @param URI
     */
    void updateDomainLevelServiceURI(Composite domainLevelComposite, String referenceName, String bindingClassName, String URI);
    
    /**
     * Get the component name out of the reference name that might look like Component/Service
     * 
     * @param referenceName
     * @return
     */
    String getComponentNameFromReference(String referenceName);
    
    /**
     * Get the service name out of the reference name that might look like Component/Service
     * 
     * @param referenceName
     * @return
     */
    String getServiceNameFromReference(String referenceName);
    
    /**
     * Find the service object given a reference name
     * 
     * @param composite
     * @param referenceName
     * @return
     */
    Service findServiceForReference(Composite composite, String referenceName);
    
}
