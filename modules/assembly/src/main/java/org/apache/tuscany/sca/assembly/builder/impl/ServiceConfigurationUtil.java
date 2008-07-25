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
package org.apache.tuscany.sca.assembly.builder.impl;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Service;

/**
 * This class encapsulates utility methods to deal with service definitions.
 *
 * @version $Rev$ $Date$
 */
abstract class ServiceConfigurationUtil {

    /**
     * Follow a service promotion chain down to the inner most (non composite)
     * component service.
     * 
     * @param topCompositeService
     * @return
     */
    static ComponentService getPromotedComponentService(CompositeService compositeService) {
        ComponentService componentService = compositeService.getPromotedService();
        if (componentService != null) {
            Service service = componentService.getService();
            if (componentService.getName() != null && service instanceof CompositeService) {
    
                // Continue to follow the service promotion chain
                return getPromotedComponentService((CompositeService)service);
    
            } else {
    
                // Found a non-composite service
                return componentService;
            }
        } else {
    
            // No promoted service
            return null;
        }
    }

    /**
     * Follow a service promotion chain down to the innermost (non-composite) component.
     * 
     * @param compositeService
     * @return
     */
    static Component getPromotedComponent(CompositeService compositeService) {
        ComponentService componentService = compositeService.getPromotedService();
        if (componentService != null) {
            Service service = componentService.getService();
            if (componentService.getName() != null && service instanceof CompositeService) {

                // Continue to follow the service promotion chain
                return getPromotedComponent((CompositeService)service);

            } else {

                // Found a non-composite service
                return compositeService.getPromotedComponent();
            }
        } else {

            // No promoted service
            return null;
        }
    }

}
