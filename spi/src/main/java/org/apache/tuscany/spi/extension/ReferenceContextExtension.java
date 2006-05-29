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
import org.apache.tuscany.spi.wire.ReferenceInvocationChain;
import org.apache.tuscany.spi.wire.ReferenceInvocationHandler;
import org.apache.tuscany.spi.wire.ReferenceWire;
import org.apache.tuscany.spi.wire.WireInvocationHandler;

/**
 * The default implementation of an external service context
 *
 * @version $Rev$ $Date$
 */
public abstract class ReferenceContextExtension<T> extends AbstractContext<T> implements ReferenceContext<T> {

    protected ReferenceWire<T> referenceWire;
    protected Class<T> referenceInterface;

    protected ReferenceContextExtension(String name, CompositeContext<?> parent) {
        super(name, parent);
    }

    public Scope getScope() {
        return Scope.COMPOSITE;
    }

    public void setTargetWire(ReferenceWire<T> wire) {
        this.referenceWire = wire;
    }

    public ReferenceWire<T> getWire() {
        return referenceWire;
    }

    public Class<T> getInterface() {
        return referenceInterface;
    }

    public void setInterface(Class<T> referenceInterface) {
        this.referenceInterface = referenceInterface;
    }

    public T getService() throws TargetException {
        return referenceWire.getTargetService();
    }

    public WireInvocationHandler getHandler() throws TargetException {
        Map<Method, ReferenceInvocationChain> configuration = referenceWire.getInvocationChains();
        assert(configuration != null);
        return new ReferenceInvocationHandler(configuration);
    }

    public void prepare() {
        for (ReferenceInvocationChain chain : referenceWire.getInvocationChains().values()) {
            chain.setTargetInvoker(createTargetInvoker(referenceWire.getTargetName().getQualifiedName(), chain.getMethod()));
            chain.build();
        }
    }

}
