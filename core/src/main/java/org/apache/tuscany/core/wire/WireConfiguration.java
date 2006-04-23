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
 * Contains configuration for a wire, including its invocation chains. Invocation chains are accessed from the collection of
 * {@link InvocationConfiguration}s keyed by operation on the service specified by the source reference or target service.
 * <code>WireConfiguration</code> subtypes distinguish between source and target sides of a wire and hence return corresponding
 * <code>InvocationChain</code> subtypes.  Operations are represented using JDK reflection, i.e. as a <code>Method</code>
 * corresponding to the Java interface representing the service.
 * <p/>
 * Wire configurations are created from an assembly model by the runtime during the buildSource phase.
 * 
 * @version $Rev$ $Date$
 */
public abstract class WireConfiguration<T extends InvocationConfiguration> {

    protected Map<Method, T> configurations;

    protected ClassLoader proxyClassLoader;

    protected MessageFactory messageFactory;

    protected QualifiedName targetName;

    /**
     * Creates the configuration
     *
     * @param targetName       the qualified name of the target service specified by the wire
     * @param proxyClassLoader the classloader to use when creating a proxy
     * @param messageFactory   the factory used to create wire messages
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
     * Returns the qualified name of the target service specified by the wire
     */
    public QualifiedName getTargetName() {
        return targetName;
    }

    /**
     * Returns the classloader used for creating proxies
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

    /**
     * Returns the invocation configuration for each operation on a service specified by a reference or a target service.
     */
    public Map<Method, T> getInvocationConfigurations() {
        return configurations;
    }


}
