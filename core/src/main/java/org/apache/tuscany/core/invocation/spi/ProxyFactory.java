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
 * Implementations are responsible for creating service reference proxies using
 * a proxying strategy.
 *
 * @version $Rev$ $Date$
 */
public interface ProxyFactory<T> {

    /**
     * Prepares the factory with information required for generating the proxy of
     * a particular reference type
     *
     * @param businessInterface the business interface of the service reference
     *                          the proxy must implement
     * @param config            the configuration information for the proxy
     * @throws ProxyInitializationException if an error generating a proxy is
     *                                      encountered
     */
    public void initialize() throws ProxyInitializationException;

    /**
     * Returns a proxy for a service reference
     */
    public T createProxy() throws ProxyCreationException;

    public ProxyConfiguration getProxyConfiguration();
    
    public void setProxyConfiguration(ProxyConfiguration config);

    public void setBusinessInterface(Class interfaze);
    
    public Class getBusinessInterface();
    
    public void addInterface(Class claz);
    
    public Class[] getImplementatedInterfaces();
    
}
