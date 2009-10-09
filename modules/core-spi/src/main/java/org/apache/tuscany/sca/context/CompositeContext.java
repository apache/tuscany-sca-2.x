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

package org.apache.tuscany.sca.context;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentContext;

/**
 * The context associated with the Node that provides access to ExtensionPointRegistry and EndpointRegistry
 * 
 * @version $Rev$ $Date$
 */
public abstract class CompositeContext {
    /**
     * @return
     */
    public static RuntimeComponent getCurrentComponent() {
        Message message = ThreadMessageContext.getMessageContext();
        if (message != null) {
            Endpoint to = message.getTo();
            if (to == null) {
                return null;
            }
            RuntimeComponent component = (RuntimeComponent)message.getTo().getComponent();
            return component;
        }
        return null;
    }

    /**
     * @return
     */
    public static CompositeContext getCurrentCompositeContext() {
        RuntimeComponent component = getCurrentComponent();
        if (component != null) {
            RuntimeComponentContext context = component.getComponentContext();
            return context.getCompositeContext();
        }
        return null;
    }

    /**
     * Attach a component context to the component
     * @param runtimeComponent
     */
    public abstract void bindComponent(RuntimeComponent runtimeComponent);
    
    /**
     * 
     * @param endpointReference
     */
    public abstract void bindEndpointReference(EndpointReference endpointReference);

    /**
     * Get the ExtensionPointRegistry for this node
     * @return The ExtensionPointRegistry
     */
    public abstract ExtensionPointRegistry getExtensionPointRegistry();

    /**
     * Get the EndpointRegistry
     * @return The EndpointRegistry for this node
     */
    public abstract EndpointRegistry getEndpointRegistry();
    
    public abstract Composite getDomainComposite();

}
