/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.builder;

import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;

/**
 * Responsible for creating a wire between a reference on a source component to a target service. Wires consist of a set
 * of source invocation configurations held by a proxy factory conntected to corresponding target invocation
 * configurations held by a proxy factory. Wire builders may optimize the invocation chains based on certain characteristics of th wire, such as
 * source and target scopes.
 * 
 * @version $Rev$ $Date$
 */
public interface WireBuilder {

    /**
     * Connects invocation configurations of the source proxy factory to corresponding ones in the target proxy to
     * factory to form a wire
     * 
     * @param sourceFactory the source proxy factory
     * @param targetFactory
     * @param targetType
     * @param downScope
     * @param targetScopeContext
     * @throws BuilderConfigException
     */
    public void wire(ProxyFactory sourceFactory, ProxyFactory targetFactory, Class targetType, boolean downScope,
            ScopeContext targetScopeContext) throws BuilderConfigException;

}
