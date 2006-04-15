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
package org.apache.tuscany.core.invocation.jdk;

import org.apache.tuscany.core.invocation.ProxyFactory;
import org.apache.tuscany.core.invocation.ProxyFactoryFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Creates JDK Dynamic Proxy-based proxy factories
 * 
 * @version $Rev$ $Date$
 */
public class JDKProxyFactoryFactory implements ProxyFactoryFactory {

    public JDKProxyFactoryFactory() {
    }

    public ProxyFactory createProxyFactory() {
        return new JDKProxyFactory();
    }

    public boolean isProxy(Object object) {
        if (object == null) {
            return false;
        } else {
            return Proxy.isProxyClass(object.getClass());
        }
    }

    public InvocationHandler getHandler(Object proxy) {
        if (proxy == null) {
            return null;
        } else {
            return Proxy.getInvocationHandler(proxy);
        }
    }

}
