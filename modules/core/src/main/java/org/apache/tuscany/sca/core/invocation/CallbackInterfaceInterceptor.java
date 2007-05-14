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
package org.apache.tuscany.sca.core.invocation;

import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.osoa.sca.NoRegisteredCallbackException;

/**
 * An interceptor applied to the forward direction of a wire that ensures the callback target implements the required
 * service contract. This is required as callback targets may be set dynamically by service implementations.
 *
 * @version $Rev$ $Date$
 */
public class CallbackInterfaceInterceptor implements Interceptor {
    private boolean invokingServiceImplements;
    private Invoker next;

    public CallbackInterfaceInterceptor(boolean invokingServiceImplements) {
        this.invokingServiceImplements = invokingServiceImplements;
    }

    public Message invoke(Message msg) {
        // TODO check in the context if a callback object is set, if so invoke next since the setCallback will
        // perform the check
        if (!invokingServiceImplements) {
            throw new NoRegisteredCallbackException("Callback target does not implement the callback interface");
        }
        return next.invoke(msg);
    }

    public void setNext(Invoker next) {
        this.next = next;
    }

    public Invoker getNext() {
        return next;
    }

}
