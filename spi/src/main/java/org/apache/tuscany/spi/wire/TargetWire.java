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
 * Implementations are responsible for managing the target side of a wire
 *
 * @version $$Rev$$ $$Date$$
 */
public interface TargetWire<T> {

    /**
     * Returns the name of the target service of the wire
     */
    String getServiceName();

    /**
     * Sets the name of the target service of the wire
     */
    void setServiceName(String name);


    /**
     * Returns a proxy for a service specified by a reference or target
     */
    T createProxy() throws ProxyCreationException;

    /**
     * Sets the primary interface type generated proxies implement
     */
    void setBusinessInterface(Class<T> interfaze);

    /**
     * Returns the primary interface type implemented by generated proxies
     */
    Class<T> getBusinessInterface();

    /**
     * Adds an interface type generated proxies implement
     */
    void addInterface(Class<?> claz);

    /**
     * Returns an array of all interfaces implemented by generated proxies
     */
    Class[] getImplementedInterfaces();

    /**
     * Returns the invocation configuration for each operation on a service specified by a reference or a
     * target service.
     */
    Map<Method, TargetInvocationChain> getInvocationChains();

    void addInvocationChains(Map<Method, TargetInvocationChain> chains);

    void addInvocationChain(Method method, TargetInvocationChain chain);

}
