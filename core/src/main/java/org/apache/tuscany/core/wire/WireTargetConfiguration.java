/**
 *
 * Copyright 2005 The Apache Software Foundation
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

import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.context.QualifiedName;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Contains configuration for the target side of a wire
 *
 * @version $$Rev$$ $$Date$$
 */
public class WireTargetConfiguration extends WireConfiguration<TargetInvocationConfiguration> {

    /**
     * Creates the source side of a wire
     *
     * @param targetName        the qualified name of the target service specified by the wire
     * @param invocationConfigs a collection of target service operation-to-invocation chain mappings
     * @param proxyClassLoader  the classloader to use when creating a proxy
     * @param messageFactory    the factory used to create wire messages
     */
    public WireTargetConfiguration(QualifiedName targetName, Map<Method, TargetInvocationConfiguration> invocationConfigs,
                                   ClassLoader proxyClassLoader, MessageFactory messageFactory) {
        super(targetName, proxyClassLoader, messageFactory);
        assert (invocationConfigs != null) : "No wire configuration map specified";
        configurations = invocationConfigs;

    }

}
