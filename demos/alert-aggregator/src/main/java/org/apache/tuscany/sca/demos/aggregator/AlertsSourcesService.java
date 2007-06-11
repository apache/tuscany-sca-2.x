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

package org.apache.tuscany.sca.demos.aggregator;

import org.apache.tuscany.sca.demos.aggregator.types.ConfigType;
import org.apache.tuscany.sca.demos.aggregator.types.SourceType;

import org.osoa.sca.annotations.Remotable;
import org.osoa.sca.annotations.Service;

/**
 * Retrieve and manage alert sources
 *
 * @version $Rev$ $Date$
 */
@Remotable
@Service 
public interface AlertsSourcesService {

    /**
     * Return all of the configured alert sources.
     *
     * @return the list of alert sources
     */
    public ConfigType getAlertSources (String id);
    
    /**
     * Return a single alert source.
     * @param id not currently used
     * @return the alert source
     */
    public SourceType getAlertSource (String id); 
    
    /**
     * Update an alert source.
     *
     * @param updatedSource the alert source to update
     */
    public void updateAlertSource (SourceType updatedSource);    
      
    /**
     * Add an alert source.
     *
     * @param newSource the alert source to add
     */
    public String addAlertSource (SourceType newSource);   

    /**
     * Remove an alert source.
     *
     * @param oldSource the alert source to remove
     */
    public void removeAlertSource (String id); 

}
