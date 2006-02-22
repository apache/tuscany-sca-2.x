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

/**
 * Creates proxies for handling invocations using JDK dynamic proxies
 * 
 * @version $Rev$ $Date$
 */
public class JDKProxyFactory implements ProxyFactory {

    private Class[] businessInterfaceArray;

    private Map<Method, InvocationConfiguration> methodToInvocationConfig;

    private ProxyConfiguration configuration;

    public void initialize() throws ProxyInitializationException {
        Map<Method, InvocationConfiguration> invocationConfigs = configuration.getInvocationConfigurations();
        methodToInvocationConfig = new HashMap(invocationConfigs.size());
        for (Map.Entry entry : invocationConfigs.entrySet()) {
//<<<<<<< .mine
//            OperationType operation = (OperationType) entry.getKey();
//            if (operation instanceof JavaOperationType) {
//                JavaOperationType javaOperation = (JavaOperationType) operation;
//                Method method = javaOperation.getJavaMethod();
//                methodToInvocationConfig.put(method, (InvocationConfiguration) entry.getValue());
//            } else {
//                if (businessInterfaceArray == null || businessInterfaceArray.length <1){
//                    throw new ProxyInitializationException("No interface configured for proxy");
//                }
//                Method[] methods = businessInterfaceArray[0].getMethods();
//                for (int i = 0; i < methods.length; i++) {
//                    if (methods[i].getName().equals(operation.getName())) {
//                        methodToInvocationConfig.put(methods[i], (InvocationConfiguration) entry.getValue());
//                        break;
//                    }
//                }
//            }
//
//=======
            Method method = (Method) entry.getKey();
            methodToInvocationConfig.put(method, (InvocationConfiguration) entry.getValue());
//>>>>>>> .r379382
        }
    }

    public Object createProxy() {
        InvocationHandler handler = new JDKInvocationHandler(configuration.getMessageFactory(), methodToInvocationConfig);
        return Proxy.newProxyInstance(configuration.getProxyClassLoader(), businessInterfaceArray, handler);
    }

    public ProxyConfiguration getProxyConfiguration() {
        return configuration;
    }

    public void setProxyConfiguration(ProxyConfiguration config) {
        configuration = config;
    }

    public void setBusinessInterface(Class interfaze) {
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
