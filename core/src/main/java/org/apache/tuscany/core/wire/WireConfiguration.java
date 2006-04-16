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
package org.apache.tuscany.core.wire;

import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.message.MessageFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Represents configuration information for creating a wire. When a client component implementation is injected with a service
 * proxy representing a wire, source- and target-side proxy configurations are "bridged" together. This concatenated configuration
 * may then be used to generate a proxy implemented a particular business interface required by the client.
 *
 * @version $Rev$ $Date$
 */
public abstract class WireConfiguration {

    protected Map<Method, InvocationConfiguration> configurations;

    protected ClassLoader proxyClassLoader;

    protected MessageFactory messageFactory;

    protected QualifiedName targetName;

    /**
     * Creates a configuration used to generate proxies representing a service.
     *
     * @param targetName        the qualified name of the service represented by this configuration
     * @param invocationConfigs a collection of operation-to-wire configuration mappings for the service
     * @param proxyClassLoader  the classloader to use when creating a proxy
     * @param messageFactory    the factory used to create wire messages
     */
    public WireConfiguration(QualifiedName targetName,
                             Map<Method, InvocationConfiguration> invocationConfigs, ClassLoader proxyClassLoader, MessageFactory messageFactory) {
        assert (invocationConfigs != null) : "No wire configuration map specified";
        this.targetName = targetName;
        configurations = invocationConfigs;
        this.messageFactory = messageFactory;
        if (proxyClassLoader == null) {
            this.proxyClassLoader = Thread.currentThread().getContextClassLoader();
        } else {
            this.proxyClassLoader = proxyClassLoader;
        }
    }

    /**
     * Returns the qualified context/service name the wire targets
     */
    public QualifiedName getTargetName() {
        return targetName;
    }

    /**
     * Returns a collection of {@link InvocationConfiguration}s keyed by their operation type of the service associated with
     * either the wire's source reference or target
     */
    public Map<Method, InvocationConfiguration> getInvocationConfigurations() {
        return configurations;
    }

    /**
     * Returns the classloader to use in creating proxies
     */
    public ClassLoader getProxyClassLoader() {
        return proxyClassLoader;
    }

    /**
     * Returns the factory used to create invocation messages
     */
    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

}
