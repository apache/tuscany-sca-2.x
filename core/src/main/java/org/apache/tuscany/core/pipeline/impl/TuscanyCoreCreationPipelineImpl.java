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

import org.apache.tuscany.core.addressing.EndpointReference;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.handler.MessageHandler;
import org.apache.tuscany.model.assembly.ConfiguredPort;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Part;

/**
 * The message processing pipeline.
 *
 */
public class TuscanyCoreCreationPipelineImpl implements MessageHandler {
    private EndpointReference entryPointBindingExtensionPoint;
    private EndpointReference referenceExtensionPoint;
    private EndpointReference serviceExtensionPoint;
    private EndpointReference externalServiceExtensionPoint;
    private EndpointReference externalServiceBindingExtensionPoint;

    /**
     * Constructor
     */
    public TuscanyCoreCreationPipelineImpl() {
        super();
    }

    /**
     * @return Returns the entryPointBindingStage.
     */
    public EndpointReference getEntryPointBindingExtensionPoint() {
        return entryPointBindingExtensionPoint;
    }

    /**
     * @param entryPointBindingStage The entryPointBindingStage to set.
     */
    public void setEntryPointBindingExtensionPoint(EndpointReference entryPointBindingStage) {
        this.entryPointBindingExtensionPoint = entryPointBindingStage;
    }

    /**
     * @return Returns the externalServiceBindingStage.
     */
    public EndpointReference getExternalServiceBindingExtensionPoint() {
        return externalServiceBindingExtensionPoint;
    }

    /**
     * @param externalServiceBindingStage The externalServiceBindingStage to set.
     */
    public void setExternalServiceBindingExtensionPoint(EndpointReference externalServiceBindingStage) {
        this.externalServiceBindingExtensionPoint = externalServiceBindingStage;
    }

    /**
     * @return Returns the externalServiceStage.
     */
    public EndpointReference getExternalServiceExtensionPoint() {
        return externalServiceExtensionPoint;
    }

    /**
     * @param externalServiceStage The externalServiceStage to set.
     */
    public void setExternalServiceExtensionPoint(EndpointReference externalServiceStage) {
        this.externalServiceExtensionPoint = externalServiceStage;
    }

    /**
     * @return Returns the referenceStage.
     */
    public EndpointReference getReferenceExtensionPoint() {
        return referenceExtensionPoint;
    }

    /**
     * @param referenceStage The referenceStage to set.
     */
    public void setReferenceExtensionPoint(EndpointReference referenceStage) {
        this.referenceExtensionPoint = referenceStage;
    }

    /**
     * @return Returns the serviceStage.
     */
    public EndpointReference getServiceExtensionPoint() {
        return serviceExtensionPoint;
    }

    /**
     * @param serviceStage The serviceStage to set.
     */
    public void setServiceExtensionPoint(EndpointReference serviceStage) {
        this.serviceExtensionPoint = serviceStage;
    }

    public boolean processMessage(Message message) {
        // Determine the starting point in the pipeline
        EndpointReference startingPoint;
        EndpointReference from = message.getFrom();
        if (from != null) {
            ConfiguredPort fromConfiguredPort = from.getConfiguredPort();
            Part fromPart = fromConfiguredPort != null ? fromConfiguredPort.getPart() : null;
            if (fromPart instanceof EntryPoint) {

                // The message is sent by an entry point, start from the entry point binding extension point
                startingPoint = entryPointBindingExtensionPoint;
            } else {

                // The message is sent by a component, start from the reference extension point
                startingPoint = referenceExtensionPoint;
            }
        } else {

            // The message is sent by a non-SCA client, start directly from the target service extension point
            EndpointReference target = message.getEndpointReference();
            ConfiguredPort targetConfiguredPort = target.getConfiguredPort();
            Part targetPart = targetConfiguredPort != null ? targetConfiguredPort.getPart() : null;
            if (targetPart instanceof ExternalService) {
                startingPoint = externalServiceExtensionPoint;
            } else {
                startingPoint = serviceExtensionPoint;
            }
        }

        // Dispatch the message to the starting point
        message.setTo(startingPoint);

        // Dispatch the message to the stages / extensionPoints until we reach the end of the pipeline
        for (; ;) {

            // Debug
            //XMLHelper.INSTANCE.print((DataObject)message, System.out);
            //System.out.println();

            // Get the To stage endpoint reference
            EndpointReference stage = message.getTo();
            if (stage != null) {

                // Dispatch to the To stage
                stage.processMessage(message);

            } else {

                // To is null, end of the pipeline
                break;
            }
        }

        // Debug
        //XMLHelper.INSTANCE.print((DataObject)message, System.out);
        //System.out.println();

        return true;
    }

}
