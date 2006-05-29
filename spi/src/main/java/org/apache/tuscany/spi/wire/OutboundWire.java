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
package org.apache.tuscany.spi.wire;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Implementations are responsible for managing the reference side of a wire, including the invocation chains
 * associated with each service operation.  A <code>OutboundWire</code> is connected to a {@link InboundWire}
 * through their invocation chains.
 *
 * @version $$Rev$$ $$Date$$
 */
public interface OutboundWire<T> extends RuntimeWire<T> {

    /**
     * Returns the name of the source reference
     */
    String getReferenceName();

    /**
     * Sets the name of the source reference
     */
    void setReferenceName(String name);

    /**
     * Returns the invocation configuration for each operation on a service specified by a reference or a
     * target service.
     */
    Map<Method, OutboundInvocationChain> getInvocationChains();

    /**
     * Adds the collection of invocation chains keyed by operation
     */
    void addInvocationChains(Map<Method, OutboundInvocationChain> chains);

    /**
     * Adds the invocation chain associated with the given operation
     */
    void addInvocationChain(Method method, OutboundInvocationChain chains);

}
