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
package org.apache.tuscany.core.wire.jdk;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import org.apache.tuscany.core.util.MethodHashMap;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.MessageHandler;
import org.apache.tuscany.spi.wire.SourceInvocationChain;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetWire;
import org.apache.tuscany.spi.wire.WireInvocationHandler;

/**
 * Creates proxies that are injected on references using JDK dynamic proxy facilities and front a wire. The
 * proxies implement the business interface associated with the service required by reference.
 *
 * @version $Rev: 394431 $ $Date: 2006-04-15 21:27:44 -0700 (Sat, 15 Apr 2006) $
 */
public class JDKSourceWire<T> implements SourceWire<T> {

    private Class<T>[] businessInterfaces;
    private Map<Method, SourceInvocationChain> invocationChains = new MethodHashMap<SourceInvocationChain>();
    private String referenceName;
    private QualifiedName targetName;
    private TargetWire<T> targetWire;

    @SuppressWarnings("unchecked")
    public T getTargetService() throws TargetException {
        if (targetWire != null) {
            // optimized, no interceptors or handlers on either end
            return targetWire.getTargetService();
        }
        WireInvocationHandler handler = new JDKInvocationHandler();
        handler.setChains(invocationChains);
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), businessInterfaces, handler);
    }

    @SuppressWarnings("unchecked")
    public void setBusinessInterface(Class<T> interfaze) {
        businessInterfaces = new Class[]{interfaze};
    }

    public Class<T> getBusinessInterface() {
        return businessInterfaces[0];
    }

    public void addInterface(Class<?> claz) {
        throw new UnsupportedOperationException("Additional proxy interfaces not yet supported");
    }

    public Class[] getImplementedInterfaces() {
        return businessInterfaces;
    }

    public Map<Method, SourceInvocationChain> getInvocationChains() {
        return invocationChains;
    }

    public void addInvocationChains(Map<Method, SourceInvocationChain> chains) {
        invocationChains.putAll(chains);
    }

    public void addInvocationChain(Method method, SourceInvocationChain chain) {
        invocationChains.put(method, chain);
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public QualifiedName getTargetName() {
        return targetName;
    }

    public void setTargetName(QualifiedName targetName) {
        this.targetName = targetName;
    }

    public void setTargetWire(TargetWire<T> wire) {
        targetWire = wire;
    }

    public boolean isOptimizable() {
        for (SourceInvocationChain chain : invocationChains.values()) {
            if (chain.getHeadInterceptor() != null || !chain.getRequestHandlers().isEmpty()
                    || !chain.getResponseHandlers().isEmpty()) {
                Interceptor current = chain.getHeadInterceptor();
                while (current != null && current != chain.getTargetInterceptor()) {
                    if (!current.isOptimizable()) {
                        return false;
                    }
                    current = current.getNext();
                }
                if (chain.getRequestHandlers() != null) {
                    for (MessageHandler handler : chain.getRequestHandlers()) {
                        if (!handler.isOptimizable()) {
                            return false;
                        }
                    }
                }
                if (chain.getResponseHandlers() != null) {
                    for (MessageHandler handler : chain.getResponseHandlers()) {
                        if (!handler.isOptimizable()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
}
