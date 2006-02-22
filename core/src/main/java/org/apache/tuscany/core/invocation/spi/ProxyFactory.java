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
package org.apache.tuscany.core.invocation.spi;

import org.apache.tuscany.core.invocation.ProxyConfiguration;

/**
 * Implementations are responsible for creating service proxies using a particular proxy strategy. Service proxies may
 * represent a wire between two components or a reference to a service resolved through a locate operation. When
 * representing a wire, a proxy is injected on reference in a component implementation. In this case the proxy will
 * implement the interface required by the reference and pass invocation messages down source- and target-side
 * invocation chains for processing. These source- and target-side invocation chains will be derived from metadata
 * decorating the source reference and target service definition and implementation respectively.
 * <p>
 * The second type of proxy will be generated when non-component client code (such as a JSP) locates a service. In this
 * case, the proxy will implement the requested service interface but will only contain a target-side invocation chain.
 * 
 * @version $Rev$ $Date$
 */
public interface ProxyFactory<T> {

    /**
     * Prepares the factory for generating the proxy of a particular reference type. This will typically be called when
     * construction of the proxy configuration is complete, including linking of source and target invocation chains.
     * 
     * @throws ProxyInitializationException if an error is encountered during initialization
     */
    public void initialize() throws ProxyInitializationException;

    /**
     * Returns a proxy for a service reference
     */
    public T createProxy() throws ProxyCreationException;

    /**
     * Returns the configuration information used to create a proxy
     */
    public ProxyConfiguration getProxyConfiguration();

    /**
     * Sets the configuration information used to create a proxy
     */
    public void setProxyConfiguration(ProxyConfiguration config);

    /**
     * Sets the primary interface type generated proxies should implement
     */
    public void setBusinessInterface(Class interfaze);

    /**
     * Returns the primary interface type implemented by generated proxies
     */
    public Class getBusinessInterface();

    /**
     * Adds an interface type generated proxies should implement
     */
    public void addInterface(Class claz);

    /**
     * Returns an array of all interfaces implemented by generated proxies
     */
    public Class[] getImplementatedInterfaces();

}
