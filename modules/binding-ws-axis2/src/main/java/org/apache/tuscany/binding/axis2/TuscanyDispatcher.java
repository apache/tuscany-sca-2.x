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

package org.apache.tuscany.binding.axis2;

import java.net.URI;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.HandlerDescription;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.engine.RequestURIBasedDispatcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Tuscany specific Axis2 Dispatcher that enables using services
 * exposed at the SCA defined service URI instead of /services/<serviceName> 
 */
public class TuscanyDispatcher extends RequestURIBasedDispatcher {

    public static final String NAME = "TuscanyDispatcher";
    private static final Log log = LogFactory.getLog(RequestURIBasedDispatcher.class);
    private static final boolean isDebugEnabled = log.isDebugEnabled();

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.axis2.engine.AbstractDispatcher#findService(org.apache.axis2.context.MessageContext)
     */
    public AxisService findService(MessageContext messageContext) throws AxisFault {
        EndpointReference toEPR = messageContext.getTo();

        if (toEPR != null) {
            if(isDebugEnabled){
                log.debug("Checking for Service using target endpoint address : " + toEPR.getAddress());
            }

            String path = URI.create(toEPR.getAddress()).getPath();
            
            // remove the leading slash as Axis2 doesn't work if the service name starts with one
            if (path != null && path.length() > 1 && path.startsWith("/")) {
                path = path.substring(1);
            }
            
            ConfigurationContext configurationContext = messageContext.getConfigurationContext();
            AxisConfiguration registry = configurationContext.getAxisConfiguration();

            return registry.getService(path);

        } else {
            if(isDebugEnabled){
                log.debug("Attempted to check for Service using null target endpoint URI");
            }
            return null;
        }
    }

    public void initDispatcher() {
        init(new HandlerDescription(NAME));
    }
}
