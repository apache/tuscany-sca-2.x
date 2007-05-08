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
package org.apache.tuscany.implementation.java.invocation;

import org.apache.tuscany.invocation.Invoker;
import org.apache.tuscany.invocation.Message;
import org.osoa.sca.ServiceRuntimeException;


/**
 * Serves as a tail interceptor on a target wire chain. This implementation dispatches to the target invoker passed
 * inside the wire message. Target invokers are passed from the source in order to allow for caching of target
 * instances.
 *
 * @version $Rev$ $Date$
 * @Deprecated
 * @see org.apache.tuscany.implementation.java.invocation.TargetInvoker
 */
public class TargetInvokerInvoker implements Invoker {
    private TargetInvoker invoker;
    
    public TargetInvokerInvoker(TargetInvoker invoker) {
        this.invoker = invoker;
    }

    public Message invoke(Message msg) {
        if (invoker == null) {
            throw new ServiceRuntimeException("No target invoker specified on message");
        }
        return invoker.invoke(msg);
    }

}
