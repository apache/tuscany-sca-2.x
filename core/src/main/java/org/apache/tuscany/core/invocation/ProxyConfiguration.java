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

import java.util.Map;

import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.model.types.OperationType;

/**
 * Represents configuration information for creating a service reference proxy
 * 
 * @version $Rev$ $Date$
 */
public class ProxyConfiguration {

    private Map<OperationType, InvocationConfiguration> configurations;

    private ClassLoader proxyClassLoader;

    private MessageFactory messageFactory;

    private Map<Integer, ScopeContext> scopeContainers;

    private QualifiedName targetName;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    // TODO add "from"
    public ProxyConfiguration(QualifiedName targetName, Map<OperationType, InvocationConfiguration> invocationConfigs,
            ClassLoader proxyClassLoader, Map<Integer, ScopeContext> scopeContainers, MessageFactory messageFactory) {
        assert (invocationConfigs != null) : "No invocation configuration map specified";
        assert (targetName != null) : "No target name specified";
        this.targetName = targetName;
        configurations = invocationConfigs;
        this.scopeContainers = scopeContainers;
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

    public QualifiedName getTargetName() {
        return targetName;
    }

    /**
     * Returns a collection of operation types to {@link InvocationConfiguration} mappings that represent the specific
     * proxy configuration information for particular operations
     */
    public Map<OperationType, InvocationConfiguration> getInvocationConfigurations() {
        return configurations;
    }

    public ClassLoader getProxyClassLoader() {
        return proxyClassLoader;
    }

    /**
     * @return Returns the messageFactory.
     */
    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

    /**
     * @return Returns the scopeContainers.
     */
    public Map<Integer, ScopeContext> getScopeContainers() {
        return scopeContainers;
    }

}
