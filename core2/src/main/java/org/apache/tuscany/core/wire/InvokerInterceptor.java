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
package org.apache.tuscany.core.wire;

import java.lang.reflect.InvocationTargetException;

import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * Serves as a tail interceptor on a target wire chain. This implementation dispatches to the target invoker
 * passed inside the wire message. Target invokers are passed from the source in order to allow for caching of
 * target instances.
 *
 * @version $Rev$ $Date$
 * @see org.apache.tuscany.spi.wire.TargetInvoker
 */
public class InvokerInterceptor implements Interceptor {

    public InvokerInterceptor() {
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        TargetInvoker invoker = msg.getTargetInvoker();
        if (invoker == null) {
            throw new InvocationRuntimeException("No target invoker specified on message");
        }
        try {
            Object resp = invoker.invokeTarget(msg.getBody());
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            msg.setBody(e.getCause());
        } catch (Throwable e) {
            msg.setBody(e);
        }
        return msg;
    }

    public void setNext(Interceptor next) {
        throw new IllegalStateException("This interceptor must be the last one in an target interceptor chain");
    }

}
