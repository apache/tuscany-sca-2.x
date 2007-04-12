/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.core.binding.local;

import java.lang.reflect.InvocationTargetException;

import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.extension.TargetInvokerExtension;

/**
 * Base class for dispatching to a composite reference using the local binding
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractLocalTargetInvoker extends TargetInvokerExtension implements TargetInvoker {
    protected boolean cacheable;

    public boolean isCacheable() {
        return cacheable;
    }

    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    public boolean isOptimizable() {
        return isCacheable(); // we only need to check if the scopes are correct
    }

    public Object invokeTarget(final Object payload, short sequence, WorkContext workContext) throws InvocationTargetException {
        throw new InvocationTargetException(new UnsupportedOperationException());
    }

    protected Message invoke(InvocationChain chain, TargetInvoker invoker, Message msg) throws Throwable {
        Interceptor headInterceptor = chain.getHeadInterceptor();
        if (headInterceptor == null) {
            try {
                // short-circuit the dispatch and invoke the target directly
                if (invoker == null) {
                    String name = chain.getTargetOperation().getName();
                    throw new AssertionError("No target invoker [" + name + "]");
                }
                return invoker.invoke(msg);
            } catch (InvocationRuntimeException e) {
                // the cause was thrown by the target so throw it
                throw e.getCause();
            }
        } else {
            msg.setTargetInvoker(invoker);
            return headInterceptor.invoke(msg);
        }
    }

    @Override
    public AbstractLocalTargetInvoker clone() throws CloneNotSupportedException {
        return (AbstractLocalTargetInvoker) super.clone();
    }
}
