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
package org.apache.tuscany.core.invocation;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.message.MessageFactory;

/**
 * Represents configuration information for creating a service proxy. When a client component implementation is injected
 * with a service proxy representing a wire, source- and target-side proxy configurations are "bridged" together. This
 * concatenated configuration may then be used to generate a proxy implemented a particular business interface required
 * by the client.
 * 
 * @version $Rev$ $Date$
 */
public class ProxyConfiguration {

    private Map<Method, InvocationConfiguration> configurations;

    private ClassLoader proxyClassLoader;

    private MessageFactory messageFactory;

    private QualifiedName serviceName;

    private String referenceName;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public ProxyConfiguration(QualifiedName serviceName, Map<Method, InvocationConfiguration> invocationConfigs,
            ClassLoader proxyClassLoader, MessageFactory messageFactory) {
        this(null, serviceName, invocationConfigs, proxyClassLoader, messageFactory);
    }

    /**
     * Creates a configuration used to generate proxies representing a service.
     * 
     * @param serviceName the qualified name of the service represented by this configuration
     * @param invocationConfigs a collection of operation-to-invocation configuration mappings for the service
     * @param proxyClassLoader the classloader to use when creating a proxy
     * @param messageFactory the factory used to create invocation messages
     */
    public ProxyConfiguration(String referenceName, QualifiedName serviceName,
            Map<Method, InvocationConfiguration> invocationConfigs, ClassLoader proxyClassLoader, MessageFactory messageFactory) {
        assert (invocationConfigs != null) : "No invocation configuration map specified";
        this.referenceName = referenceName;
        this.serviceName = serviceName;
        configurations = invocationConfigs;
        this.messageFactory = messageFactory;
        if (proxyClassLoader == null) {
            this.proxyClassLoader = Thread.currentThread().getContextClassLoader();
        } else {
            this.proxyClassLoader = proxyClassLoader;
        }
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    /**
     * Returns the qualified service name the configuration is associated with
     */
    public QualifiedName getTargetName() {
        return serviceName;
    }

    /**
     * Returns the name of the reference if a source-side configuration
     */
    public String getReferenceName() {
        return referenceName;
    }

    /**
     * Returns a collection of operation types to {@link InvocationConfiguration} mappings that represent the specific
     * proxy configuration information for particular operations
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
