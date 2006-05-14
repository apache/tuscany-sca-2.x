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
package org.apache.tuscany.core.context;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tuscany.core.wire.jdk.JDKInvocationHandler;
import org.apache.tuscany.spi.context.ReferenceContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.context.AbstractContext;
import org.apache.tuscany.spi.wire.ProxyCreationException;
import org.apache.tuscany.spi.wire.TargetInvocationChain;
import org.apache.tuscany.spi.wire.TargetWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * The default implementation of an external service context
 *
 * @version $Rev$ $Date$
 */
public class ReferenceContextImpl<T> extends AbstractContext<T> implements ReferenceContext<T> {

    private TargetWire<T> targetWire;
    private Class<T> referenceInterface;

    /**
     * Creates a reference context
     *
     * @param name              the name of the reference context
     * @param targetWire the factory which creates proxies implementing the configured service
     *                          interface for the reference context. There is always only one proxy factory as
     *                          an reference context is configured with one service
     */
    public ReferenceContextImpl(String name, Class<T> referenceInterface, TargetWire<T> targetWire) {
        super(name);
        assert (targetWire != null) : "Target proxy factory was null";
        assert (referenceInterface != null) : "Reference interface was null";
        this.targetWire = targetWire;
        this.referenceInterface = referenceInterface;
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
        return null;  // TODO implements
    }

    public T getService() throws TargetException {
        try {
            return targetWire.createProxy();
        } catch (ProxyCreationException e) {
            TargetException te = new TargetException(e);
            te.addContextName(getName());
            throw te;
        }
    }

    public InvocationHandler getHandler() throws TargetException {
        Map<Method, TargetInvocationChain> configuration = targetWire.getInvocationChains();
        assert(configuration != null);
        return new JDKInvocationHandler(configuration);
    }

    public Class<T> getInterface() {
        return referenceInterface;
    }

}
