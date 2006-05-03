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
import org.apache.tuscany.spi.wire.SourceWireFactory;
import org.apache.tuscany.spi.wire.TargetWireFactory;
import org.apache.tuscany.spi.builder.BuilderConfigException;

/**
 * Implementations perform the second phase of converting a logical model representing an assembly into a series of
 * runtime or executable artifacts. Specifically, they are responsible for finalizing target-side proxy factories and
 * bridging {@link org.apache.tuscany.core.wire.InvocationConfiguration}s held by source- and target-side proxy
 * factories. <code>WireBuilder</code>s generally operate by target implementation type. In other words, for a wire
 * from a Java source to a JavaScript target, the Javascript <code>WireBuilder</code> will complete the wire. This is
 * necessary as a <code>WireBuilder</code> must set a {@link org.apache.tuscany.spi.wire.TargetInvoker} that is
 * responsible for dispatching to an implementation on the source side of the wire.
 * <p>
 * Runtimes are generally configured with a {@link org.apache.tuscany.core.builder.impl.DefaultWireBuilder} as a
 * top-most wire builder, which delegates to other builders wired to it as part of a system configuration.
 * <p>
 * Wire builders may optimize the wire chains based on certain characteristics of th wire, such as source and
 * target scopes.
 * 
 * @see org.apache.tuscany.core.builder.ContextFactoryBuilder
 * @see org.apache.tuscany.core.builder.impl.DefaultWireBuilder
 * @version $Rev$ $Date$
 */
public interface WireBuilder {

    /**
     * Connects wire configurations of the source proxy factory to corresponding ones in the target proxy to
     * factory
     * 
     * @param sourceFactory the proxy factory used in constructing the source side of the wire chain
     * @param targetFactory the proxy factory used in constructing the target side of the wire chain
     * @param targetType the context type of the target. Used to determine if a paricular wire builder should construct
     *        the wire
     * @param downScope true if the component containing the reference (source side) is of a lesser scope than the
     *        target service
     * @param targetScopeContext the scope context responsible for managing intance contexts of the target component
     *        type
     * @throws BuilderConfigException if an error occurs during the wire buildSource process
     */
    public void connect(SourceWireFactory<?> sourceFactory, TargetWireFactory<?> targetFactory, Class targetType, boolean downScope,
                        ScopeContext targetScopeContext) throws BuilderConfigException;

    /**
     * Finishes processing the target side wire chain. For example, a
     * {@link org.apache.tuscany.spi.wire.TargetInvoker} used by target-side proxies is usually set during this
     * phase.
     * 
     * @param targetFactory the target-side proxy factory
     * @param targetType the target context type
     * @param targetScopeContext the target scope
     * @throws BuilderConfigException if an error occurs during the wire buildSource process
     */
    public void completeTargetChain(TargetWireFactory<?> targetFactory, Class targetType, ScopeContext targetScopeContext)
            throws BuilderConfigException;

}
