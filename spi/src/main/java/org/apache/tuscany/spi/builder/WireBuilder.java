/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.spi.builder;

import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetWire;
import org.apache.tuscany.spi.context.Context;

/**
 * Implementations perform the second phase of converting a logical model representing an assembly into a series of
 * runtime or executable artifacts. Specifically, they are responsible for finalizing target-side proxy factories and
 * bridging {@link org.apache.tuscany.spi.wire.InvocationChain}s held by source- and target-side proxy
 * factories. <code>WireBuilder</code>s generally operate by target implementation type. In other words, for a wire
 * from a Java source to a JavaScript target, the Javascript <code>WireBuilder</code> will complete the wire. This is
 * necessary as a <code>WireBuilder</code> must set a {@link org.apache.tuscany.spi.wire.TargetInvoker} that is
 * responsible for dispatching to an implementation on the source side of the wire.
 * <p/>
 * Wire builders may optimize the wire chains based on certain characteristics of th wire, such as source and
 * target scopes.
 *
 * @version $Rev$ $Date$
 */
public interface WireBuilder<T extends Context<?>> {

    /**
     * Connects wire configurations of the source proxy factory to corresponding ones in the target proxy to
     * factory
     *
     * @param sourceWire the proxy factory used in constructing the source side of the wire chain
     * @param targetWire the proxy factory used in constructing the target side of the wire chain
     * @throws BuilderConfigException if an error occurs during the wire buildSource process
     */
    public void connect(SourceWire<?> sourceWire, TargetWire<?> targetWire,T context) throws BuilderConfigException;

    /**
     * Finishes processing the target side wire chain.
     *
     * @param targetWire the target-side proxy factory
     * @throws BuilderConfigException if an error occurs during the wire buildSource process
     */
    public void completeTargetChain(TargetWire<?> targetWire, T context) throws BuilderConfigException;

}
