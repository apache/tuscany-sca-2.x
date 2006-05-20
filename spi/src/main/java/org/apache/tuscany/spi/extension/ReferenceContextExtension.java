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
package org.apache.tuscany.spi.extension;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.spi.context.AbstractContext;
import org.apache.tuscany.spi.context.ReferenceContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.wire.ProxyCreationException;
import org.apache.tuscany.spi.wire.TargetInvocationChain;
import org.apache.tuscany.spi.wire.TargetWire;
import org.apache.tuscany.spi.wire.WireInvocationHandler;
import org.apache.tuscany.model.Scope;

/**
 * The default implementation of an external service context
 *
 * @version $Rev$ $Date$
 */
public abstract class ReferenceContextExtension<T> extends AbstractContext<T> implements ReferenceContext<T> {

    protected TargetWire<T> targetWire;
    protected Class<T> referenceInterface;
    protected ObjectFactory<WireInvocationHandler> handlerFactory;

    /**
     * Creates a reference context
     */
    public ReferenceContextExtension() {
    }

    public Scope getScope() {
        return Scope.COMPOSITE;
    }

    public void setTargetWire(TargetWire<T> targetWire) {
        this.targetWire = targetWire;
    }

    public TargetWire<T> getTargetWire() {
        return targetWire;
    }

    public Class<T> getInterface() {
        return referenceInterface;
    }

    public void setInterface(Class<T> referenceInterface) {
        this.referenceInterface = referenceInterface;
    }

    public void setHandlerFactory(ObjectFactory<WireInvocationHandler> handlerFactory) {
        this.handlerFactory = handlerFactory;
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
        WireInvocationHandler handler = handlerFactory.getInstance();
        handler.setConfiguration(configuration);
        return handler;
    }

    public void prepare() {
        for (TargetInvocationChain chain : targetWire.getInvocationChains().values()) {
            chain.setTargetInvoker(createTargetInvoker(targetWire.getServiceName(), chain.getMethod()));
        }
    }

}
