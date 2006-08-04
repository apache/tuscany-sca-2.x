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
 * Implementations are responsible for managing the service side of a wire, including the invocation chains associated
 * with each service operation. A <Code>InboundWire</code> can be connected to another <code>InboundWire</code> when
 * connecting a {@link org.apache.tuscany.spi.component.Service} to an
 * {@link org.apache.tuscany.spi.component.AtomicComponent}.
 *
 * @version $$Rev$$ $$Date$$
 */
public interface InboundWire<T> extends RuntimeWire<T> {

    /**
     * Returns the name of the target service of the wire
     */
    String getServiceName();

    /**
     * Sets the name of the target service of the wire
     */
    void setServiceName(String name);

    /**
     * Returns the invocation configuration for each operation on a service specified by a reference or a target
     * service.
     */
    Map<Method, InboundInvocationChain> getInvocationChains();

    /**
     * Adds the collection of invocation chains keyed by operation
     */
    void addInvocationChains(Map<Method, InboundInvocationChain> chains);

    /**
     * Adds the invocation chain associated with the given operation
     */
    void addInvocationChain(Method method, InboundInvocationChain chain);

    /**
     * Returns the name of the callback associated with the service of the wire
     */
    String getCallbackReferenceName();

    /**
     * Sets the name of the callback associated with the service of the wire
     */
    void setCallbackReferenceName(String callbackReferenceName);
    
    /**
     * Set when a wire can be optimized; that is when no handlers or interceptors exist on either end
     */
    void setTargetWire(OutboundWire<T> wire);


}
