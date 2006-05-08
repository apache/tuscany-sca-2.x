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

/**
 * Implementations are responsible for managing the target side of a wire
 *
 * @version $$Rev$$ $$Date$$
 */
public interface TargetWireFactory<T> {

    /**
     * Prepares the factory. This will typically be called at buildSource time, after bridging source- and target-side invocation chains.
     *
     * @throws WireFactoryInitException if an error is encountered during initialization
     */
    public void initialize() throws WireFactoryInitException;

    /**
     * Returns a proxy for a service specified by a reference or target
     */
    public T createProxy() throws ProxyCreationException;

    /**
     * Sets the primary interface type generated proxies implement
     */
    public void setBusinessInterface(Class<T> interfaze);

    /**
     * Returns the primary interface type implemented by generated proxies
     */
    public Class<T> getBusinessInterface();

    /**
     * Adds an interface type generated proxies implement
     */
    public void addInterface(Class<?> claz);

    /**
     * Returns an array of all interfaces implemented by generated proxies
     */
    public Class[] getImplementatedInterfaces();

    /**
     * Returns the configuration information used to create the target-side of a wire, including invocation chains
     */
    public WireTargetConfiguration getConfiguration();

    /**
     * Sets the configuration information used to create the target-side of a wire, including invocation chains
     */
    public void setConfiguration(WireTargetConfiguration config);

}
