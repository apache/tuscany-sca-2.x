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

/**
 * A message handler.
 *
 */
public class ReferenceExtensionPointImpl implements MessageHandler {
    private EndpointReference entryPoint;
    private EndpointReference wire;
    private List<EndpointReference> extensions;

    /**
     * Constructor
     */
    public ReferenceExtensionPointImpl() {
        super();
    }

    public boolean processMessage(Message message) {
        message.setTo(wire);

        // Invoke any extensions
        for (EndpointReference extension : extensions) {
            extension.processMessage(message);
        }

        return false;
    }

    /**
     * @return Returns the entryPoint.
     */
    public EndpointReference getEntryPoint() {
        return entryPoint;
    }

    /**
     * @param entryPoint The entryPoint to set.
     */
    public void setEntryPoint(EndpointReference entryPoint) {
        this.entryPoint = entryPoint;
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
