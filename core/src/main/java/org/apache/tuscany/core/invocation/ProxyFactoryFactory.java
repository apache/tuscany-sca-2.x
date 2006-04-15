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
package org.apache.tuscany.core.invocation;

import org.apache.tuscany.core.invocation.ProxyFactory;

import java.lang.reflect.InvocationHandler;

/**
 * Creates proxy factories which may be subsequently configured to generate proxies
 * 
 * @version $Rev$ $Date$
 */
public interface ProxyFactoryFactory {

    /**
     * Returns a new proxy factory
     */
    public ProxyFactory createProxyFactory();

    /**
     * Determines whether the given object is a proxy
     */
    public boolean isProxy(Object object);

    /**
     * Returns an invocation handler fronting the invocation chains used by the proxy. Note that should SCA define a
     * DII, this could return such an interface.
     * 
     * @throws IllegalArgumentException if the class is not a proxy
     */
    public InvocationHandler getHandler(Object proxy) throws IllegalArgumentException;
}
