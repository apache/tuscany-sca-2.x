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
package org.apache.tuscany.sca.binding.ws.axis2.provider;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.host.http.SecurityContext;
import org.apache.tuscany.sca.invocation.MessageFactory;

public class Axis2BaseBindingProvider {

    // Tuscany extensions
    protected ExtensionPointRegistry extensionPoints;
    protected FactoryExtensionPoint modelFactories;
    protected MessageFactory messageFactory;
        
    // derived policy configuration
    protected boolean isSOAP12Required = false;
    protected boolean isRampartRequired = false;
    protected boolean isMTOMRequired = false;
    protected boolean isJMSRequired = false;    
    
    // The Axis2 configuration that the binding creates
    protected ConfigurationContext configContext;
    protected SecurityContext httpSecurityContext;
    
    public Axis2BaseBindingProvider(ExtensionPointRegistry extensionPoints) {

        this.extensionPoints = extensionPoints;
        
        this.modelFactories =  extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        this.messageFactory = modelFactories.getFactory(MessageFactory.class); 
        
        this.httpSecurityContext = new SecurityContext();
    }
    
    public ConfigurationContext getAxisConfigurationContext() {
        return configContext;
    }
    
    public SecurityContext getHttpSecurityContext() {
        return httpSecurityContext;
    }
    
}
