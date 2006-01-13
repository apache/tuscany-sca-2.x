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
package org.apache.tuscany.core.invocation.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.ProxyConfiguration;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.invocation.spi.ProxyInitializationException;
import org.apache.tuscany.model.types.OperationType;
import org.apache.tuscany.model.types.java.JavaOperationType;
import org.apache.tuscany.model.types.wsdl.WSDLOperationType;

/**
 * Creates proxies for handling invocations using JDK dynamic proxies
 *
 * @version $Rev$ $Date$
 */
public class JDKProxyFactory implements ProxyFactory {

    private Class[] businessInterfaceArray;

    private Map<Method, InvocationConfiguration> methodToInvocationConfig;
    private ProxyConfiguration configuration;

    public void initialize(Class businessInterface, ProxyConfiguration config) throws ProxyInitializationException {
        assert (businessInterface != null) : "No business interface specified";
        assert (config != null) : "No proxy configuration specified";
        configuration = config;
        businessInterfaceArray = new Class[]{businessInterface};
        Map<OperationType, InvocationConfiguration> invocationConfigs = config.getInvocationConfigurations();
        methodToInvocationConfig = new HashMap(invocationConfigs.size());
        for (Map.Entry entry : invocationConfigs.entrySet()) {
            OperationType operation = (OperationType) entry.getKey();
            if (operation instanceof JavaOperationType) {
                JavaOperationType javaOperation = (JavaOperationType) operation;
                Method method = javaOperation.getJavaMethod();
                methodToInvocationConfig.put(method, (InvocationConfiguration) entry.getValue());
//				throw new ProxyInitializationException("Operation [" + operation.getName()
//						+ "] not a Java type on interface [" + businessInterface.getName() + "]");
            } else {
                WSDLOperationType wsdlOperation = (WSDLOperationType) operation;
                Method[] methods = businessInterface.getMethods();
                for (int i = 0; i < methods.length; i++) {
                    if (methods[i].getName().equals(wsdlOperation.getName())) {
                        methodToInvocationConfig.put(methods[i], (InvocationConfiguration) entry.getValue());
                        break;
                    }
                }
            }

        }
    }

    public Object createProxy() {
        // TODO pass from part of proxyconfig
        InvocationHandler handler = new JDKInvocationHandler(configuration.getMessageFactory(), methodToInvocationConfig);
        return Proxy.newProxyInstance(configuration.getProxyClassLoader(), businessInterfaceArray, handler);
    }

}
