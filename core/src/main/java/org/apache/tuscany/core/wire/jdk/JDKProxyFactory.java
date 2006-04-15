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
package org.apache.tuscany.core.wire.jdk;

import org.apache.tuscany.core.wire.InvocationConfiguration;
import org.apache.tuscany.core.wire.MethodHashMap;
import org.apache.tuscany.core.wire.WireConfiguration;
import org.apache.tuscany.core.wire.ProxyFactory;
import org.apache.tuscany.core.wire.ProxyInitializationException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Creates proxies for handling invocations using JDK dynamic proxies
 *
 * @version $Rev$ $Date$
 */
public class JDKProxyFactory implements ProxyFactory {

    private static final int UNINITIALIZED = 0;

    private static final int INITIALIZED = 1;

    private int state = UNINITIALIZED;

    private Class[] businessInterfaceArray;

    private Map<Method, InvocationConfiguration> methodToInvocationConfig;

    private WireConfiguration configuration;

    public void initialize() throws ProxyInitializationException {
        if (state != UNINITIALIZED) {
            throw new IllegalStateException("Proxy factory in wrong state [" + state + "]");
        }
        Map<Method, InvocationConfiguration> invocationConfigs = configuration.getInvocationConfigurations();
        methodToInvocationConfig = new MethodHashMap(invocationConfigs.size());
        for (Map.Entry entry : invocationConfigs.entrySet()) {
            Method method = (Method) entry.getKey();
            methodToInvocationConfig.put(method, (InvocationConfiguration) entry.getValue());
        }
        state = INITIALIZED;
    }

    public Object createProxy() {
        if (state != INITIALIZED) {
            throw new IllegalStateException("Proxy factory not INITIALIZED [" + state + "]");
        }
        InvocationHandler handler = new JDKInvocationHandler(configuration.getMessageFactory(), methodToInvocationConfig);
        return Proxy.newProxyInstance(configuration.getProxyClassLoader(), businessInterfaceArray, handler);
    }

    public WireConfiguration getProxyConfiguration() {
        return configuration;
    }

    public void setProxyConfiguration(WireConfiguration config) {
        configuration = config;
    }

    public void setBusinessInterface(Class interfaze) {
  //      if(!interfaze.isInterface()){
  //          throw new IllegalArgumentException("Class is not an interface ["+interfaze+"]");
  //      }
        businessInterfaceArray = new Class[] { interfaze };
    }

    public Class getBusinessInterface() {
        return businessInterfaceArray[0];
    }

    public void addInterface(Class claz) {
        throw new UnsupportedOperationException("Additional proxy interfaces not yet supported");
    }

    public Class[] getImplementatedInterfaces() {
        return businessInterfaceArray;
    }

}
