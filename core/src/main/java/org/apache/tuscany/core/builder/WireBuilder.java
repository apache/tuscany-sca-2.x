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
 * Responsible for finalizing target-side proxy factories and bridging
 * {@link org.apache.tuscany.core.invocation.InvocationConfiguration}s held by source- and target-side proxy factories.
 * <p>
 * Wire builders may optimize the invocation chains based on certain characteristics of th wire, such as source and
 * target scopes.
 * 
 * @version $Rev$ $Date$
 */
public interface WireBuilder {

    /**
     * Connects invocation configurations of the source proxy factory to corresponding ones in the target proxy to
     * factory
     * 
     * @param sourceFactory the proxy factory used in constructing the source side of the invocation chain
     * @param targetFactory the proxy factory used in constructing the target side of the invocation chain
     * @param targetType the context type of the target. Used to determine if a paricular wire builder should construct
     *        the wire
     * @param downScope true if the component containing the reference (source side) is of a lesser scope than the
     *        target service
     * @param targetScopeContext the scope context responsible for managing intance contexts of the target component
     *        type
     * @throws BuilderConfigException if an error occurs during the wire build process
     */
    public void connect(ProxyFactory sourceFactory, ProxyFactory targetFactory, Class targetType, boolean downScope,
            ScopeContext targetScopeContext) throws BuilderConfigException;

    /**
     * Finishes processing the target side invocation chain. For example, a
     * {@link org.apache.tuscany.core.invocation.TargetInvoker} used by target-side proxies is usually set during this
     * phase.
     * 
     * @param targetFactory the target-side proxy factory
     * @param targetType the target context type
     * @param targetScopeContext the target scope
     * @throws BuilderConfigException if an error occurs during the wire build process
     */
    public void completeTargetChain(ProxyFactory targetFactory, Class targetType, ScopeContext targetScopeContext)
            throws BuilderConfigException;

}
