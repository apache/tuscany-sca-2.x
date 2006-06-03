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

import java.lang.reflect.Method;

import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.MessageHandler;

/**
 * Contains a target-side invocation chain
 *
 * @version $Rev: 394379 $ $Date: 2006-04-15 15:01:36 -0700 (Sat, 15 Apr 2006) $
 */
public class InboundInvocationChainImpl extends InvocationChainImpl implements InboundInvocationChain {

    /**
     * Creates an new target-side chain for the given operation
     */
    public InboundInvocationChainImpl(Method operation) {
        super(operation);
    }

    public void build() {
        if (requestHandlers != null) {
            if (interceptorChainHead != null) {
                // on target-side, connect existing handlers and interceptors
                MessageHandler messageDispatcher = new MessageDispatcher(interceptorChainHead);
                requestHandlers.add(messageDispatcher);
            }
        }
    }


}
