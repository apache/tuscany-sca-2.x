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
public class EntryPointExtensionPointImpl implements MessageHandler {
    private EndpointReference binding;
    private EndpointReference reference;
    private List<EndpointReference> extensions;

    /**
     * Constructor
     */
    public EntryPointExtensionPointImpl() {
        super();
    }

    public boolean processMessage(Message message) {
        // Route to the reference stage
        message.setTo(reference);

        // Invoke any extensions
        for (EndpointReference extension : extensions) {
            extension.processMessage(message);
        }

        return false;
    }

    /**
     * @return Returns the binding.
     */
    public EndpointReference getBinding() {
        return binding;
    }

    /**
     * @param binding The binding to set.
     */
    public void setBinding(EndpointReference binding) {
        this.binding = binding;
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
     * @return Returns the reference.
     */
    public EndpointReference getReference() {
        return reference;
    }

    /**
     * @param reference The reference to set.
     */
    public void setReference(EndpointReference reference) {
        this.reference = reference;
    }


}
