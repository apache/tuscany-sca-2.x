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
package org.apache.tuscany.spi.context;

import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetWire;

/**
 * A runtime entity that manages an leaf-type artifact and functions as the runtime analog to
 * atomic components configured in an assembly
 *
 * @version $Rev$ $Date$
 */
public interface AtomicContext<T> extends ComponentContext<T> {

    /**
     * Returns whether the context should be eagerly initialized
     */
    boolean isEagerInit();

    /**
     * Notifies the given instance of an initialization event
     *
     * @throws TargetException
     */
    void init(Object instance) throws TargetException;

    /**
     * Notifies the given instance of a destroy event
     *
     * @throws TargetException
     */
    void destroy(Object instance) throws TargetException;

    /**
     * Creates a new implementation instance, generally used as a callback by a {@link ScopeContext}
     *
     * @throws ObjectCreationException
     */
    InstanceWrapper createInstance() throws ObjectCreationException;

    /**
     * Adds a source-side wire for the given reference. Source-side wires contain the invocation chains for a
     * reference in the implementation associated with the instance wrapper created by this configuration.
     */
    void addSourceWire(SourceWire wire);

    /**
     * Adds a set of source-side multiplicity wires for the given reference. Source-side wires contain the
     * invocation chains for a reference in the implementation associated with the instance wrapper created by
     * this configuration.
     */
    void addSourceWires(Class<?> multiplicityClass, List<SourceWire> wires);

    /**
     * Returns a map of source-side wires for references. There may 1..n wires per reference.
     */
    Map<String,List<SourceWire>> getSourceWires();

    /**
     * Adds a target-side wire. Target-side wire factories contain the invocation chains associated with the
     * destination service of a wire
     */
    void addTargetWire(TargetWire wire);

    /**
     * Returns the target-side wire associated with the given service name
     */
    TargetWire getTargetWire(String serviceName);
    

}
