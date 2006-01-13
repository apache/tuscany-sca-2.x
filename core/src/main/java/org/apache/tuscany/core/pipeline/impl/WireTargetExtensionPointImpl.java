/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.pipeline.impl;

import java.util.List;

import org.apache.tuscany.core.addressing.EndpointReference;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.handler.MessageHandler;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ConfiguredPort;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Part;

/**
 * A message handler.
 *
 */
public class WireTargetExtensionPointImpl implements MessageHandler {
    private EndpointReference wire;
    private EndpointReference service;
    private EndpointReference externalService;
    private List<EndpointReference> extensions;

    /**
     * Constructor
     */
    public WireTargetExtensionPointImpl() {
        super();
    }

    public boolean processMessage(Message message) {
        // Get the recipient service endpoint reference
        EndpointReference serviceEndpointReference = message.getEndpointReference();
        ConfiguredPort targetConfiguredPort = serviceEndpointReference.getConfiguredPort();
        Part targetPart = targetConfiguredPort != null ? targetConfiguredPort.getPart() : null;
        if (targetPart instanceof Component) {

            // Route to the service stage
            message.setTo(service);

        } else if (targetPart instanceof ExternalService) {

            // Route to the externalService stage
            message.setTo(externalService);

        } else {
            message.setTo(null);
        }

        // Invoke any extensions
        for (EndpointReference extension : extensions) {
            extension.processMessage(message);
        }

        return false;
    }

    /**
     * @return Returns the extensions.
     */
    public List<EndpointReference> getExtensions() {
        return extensions;
    }

    /**
     * @param extensions The extensions to set.
     */
    public void setExtensions(List<EndpointReference> extensions) {
        this.extensions = extensions;
    }

    /**
     * @return Returns the externalService.
     */
    public EndpointReference getExternalService() {
        return externalService;
    }

    /**
     * @param externalService The externalService to set.
     */
    public void setExternalService(EndpointReference externalService) {
        this.externalService = externalService;
    }

    /**
     * @return Returns the service.
     */
    public EndpointReference getService() {
        return service;
    }

    /**
     * @param service The service to set.
     */
    public void setService(EndpointReference service) {
        this.service = service;
    }

    /**
     * @return Returns the wire.
     */
    public EndpointReference getWire() {
        return wire;
    }

    /**
     * @param wire The wire to set.
     */
    public void setWire(EndpointReference wire) {
        this.wire = wire;
    }


}
