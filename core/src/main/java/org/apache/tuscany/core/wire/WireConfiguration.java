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
public abstract class WireConfiguration <T extends InvocationConfiguration> {

    protected Map<Method, T> configurations;

    protected ClassLoader proxyClassLoader;

    protected MessageFactory messageFactory;

    protected QualifiedName targetName;

    /**
     * Creates a configuration used to generate proxies representing a service.
     *
     * @param targetName        the qualified name of the service represented by this configuration
     * @param proxyClassLoader  the classloader to use when creating a proxy
     * @param messageFactory    the factory used to create wire messages
     */
    public WireConfiguration(QualifiedName targetName, ClassLoader proxyClassLoader, MessageFactory messageFactory) {
        this.targetName = targetName;
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

    public Map<Method, T> getInvocationConfigurations(){
         return configurations;
     }


}
