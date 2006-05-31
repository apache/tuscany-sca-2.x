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

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tuscany.spi.context.AbstractContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.ReferenceContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.JDKInboundInvocationHandler;
import org.apache.tuscany.spi.wire.WireInvocationHandler;

/**
 * The default implementation of an external service context
 *
 * @version $Rev$ $Date$
 */
public abstract class ReferenceContextExtension<T> extends AbstractContext<T> implements ReferenceContext<T> {

    protected InboundWire<T> inboundWire;
    protected OutboundWire<T> outboundWire;
    protected Class<T> referenceInterface;

    protected ReferenceContextExtension(String name, CompositeContext<?> parent) {
        super(name, parent);
    }

    public Scope getScope() {
        return Scope.COMPOSITE;
    }

    public void setInboundWire(InboundWire<T> wire) {
        this.inboundWire = wire;
    }

    public InboundWire<T> getInboundWire() {
        return inboundWire;
    }

    public OutboundWire<T> getOutboundWire() {
        return outboundWire;
    }

    public void setOutboundWire(OutboundWire<T> outboundWire) {
        this.outboundWire = outboundWire;
    }

    public Class<T> getInterface() {
        return referenceInterface;
    }

    public void setInterface(Class<T> referenceInterface) {
        this.referenceInterface = referenceInterface;
    }

    public T getService() throws TargetException {
        return outboundWire.getTargetService();
    }

    public WireInvocationHandler getHandler() throws TargetException {
        Map<Method, InboundInvocationChain> configuration = inboundWire.getInvocationChains();
        assert(configuration != null);
        return new JDKInboundInvocationHandler(configuration);
    }

    public void prepare() {
        for (InboundInvocationChain chain : inboundWire.getInvocationChains().values()) {
            chain.setTargetInvoker(createTargetInvoker(outboundWire.getTargetName().getQualifiedName(), chain.getMethod()));
            chain.build();
        }
    }

}
