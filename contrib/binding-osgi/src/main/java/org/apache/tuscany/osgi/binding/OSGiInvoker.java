/*
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.osgi.binding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * Invokes the target service of an OSGi reference.
 *
 * @version $Rev$ $Date$
 */
public class OSGiInvoker implements TargetInvoker {
    private final Method remoteMethod;
    private final Object proxy;

    OSGiInvoker(Object proxy, Method remoteMethod) {
        assert remoteMethod.isAccessible();
        this.remoteMethod = remoteMethod;
        this.proxy = proxy;
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            Object resp = invokeTarget(msg.getBody(), TargetInvoker.NONE);
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            msg.setBody(e.getCause());
        }
        return msg;
    }

    public Object invokeTarget(Object payload, final short sequence) throws InvocationTargetException {
        try {
            return remoteMethod.invoke(proxy, (Object[]) payload);
        } catch (IllegalAccessException e) {
            // the method we are passed must be accessible
            throw new AssertionError(e);
        }
    }

    public Object clone() throws CloneNotSupportedException {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public boolean isOptimizable() {
        return false;
    }

    public boolean isCacheable() {
        return false;
    }

    // FIXME I think we can always have this cacheable as OSGi serviceBindings are thread-safe
    public void setCacheable(boolean cacheable) {
    }
}
