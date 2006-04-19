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

import org.apache.tuscany.core.wire.impl.MessageChannelImpl;
import org.apache.tuscany.core.wire.impl.MessageDispatcher;
import org.apache.tuscany.core.wire.impl.RequestResponseInterceptor;

import java.lang.reflect.Method;

/**
 * Contains a source-side invocation pipeline for a service operation.
 *
 * @version $Rev: 394379 $ $Date: 2006-04-15 15:01:36 -0700 (Sat, 15 Apr 2006) $
 */
public class SourceInvocationConfiguration extends InvocationConfiguration {

    // the pointer to the bridged target head interceptor or null if the target has no interceptors
    private Interceptor targetInterceptorChainHead;

    // the pointer to bridged target request channel, or null if the target has an interceptor
    private MessageChannel targetRequestChannel;

    // the pointer to bridged target response channel, or null if the target has an interceptor
    private MessageChannel targetResponseChannel;

    /**
     * Creates an new wire configuration for the given service reference operation
     *
     * @param operation the method on the interface representing specified by the reference, where the method corresponds to the
     *                  service operation
     */
    public SourceInvocationConfiguration(Method operation) {
        super(operation);
    }

    /**
     * Sets the head interceptor of the target-side configuration for the wire. Used when the runtime bridges source and target
     * chains.
     *
     * @param interceptor
     */
    public void setTargetInterceptor(Interceptor interceptor) {
        targetInterceptorChainHead = interceptor;
    }

    /**
     * Returns the head target-side interceptor. This will be the head interceptor of the "bridged" target configuration.
     */
    public Interceptor getTargetInterceptor() {
        return targetInterceptorChainHead;
    }

    /**
     * Sets the target-side request channel. Used when the runtime bridges source and target chains.
     */
    public void setTargetRequestChannel(MessageChannel channel) {
        targetRequestChannel = channel;
    }

    /**
     * Sets the target-side response channel. Used when the runtime bridges source and target chains.
     */
    public void setTargetResponseChannel(MessageChannel channel) {
        targetResponseChannel = channel;
    }

    /**
     * Prepares the configuration by linking interceptors and handlers
     */
    @Override
    public void build() {

        if (requestHandlers != null && targetInterceptorChainHead != null) {
            // on target-side, connect existing handlers and interceptors
            MessageHandler messageDispatcher = new MessageDispatcher(targetInterceptorChainHead);
            requestHandlers.add(messageDispatcher);
        }

        if (requestHandlers != null) {
            MessageChannel requestChannel = new MessageChannelImpl(requestHandlers);
            MessageChannel responseChannel = new MessageChannelImpl(responseHandlers);
            Interceptor channelInterceptor = new RequestResponseInterceptor(requestChannel, targetRequestChannel,
                    responseChannel, targetResponseChannel);

            if (interceptorChainHead != null) {
                interceptorChainTail.setNext(channelInterceptor);
            } else {
                interceptorChainHead = channelInterceptor;
            }

        } else {
            // no request handlers
            if (interceptorChainHead != null) {
                if (targetInterceptorChainHead != null) {
                    // Connect source interceptor chain directly to target interceptor chain
                    interceptorChainTail.setNext(targetInterceptorChainHead);
                    // interceptorChainTail = targetInterceptorChainHead;
                } else {
                    // Connect source interceptor chain to the target request channel
                    Interceptor channelInterceptor = new RequestResponseInterceptor(null, targetRequestChannel, null,
                            targetResponseChannel);
                    interceptorChainTail.setNext(channelInterceptor);
                }
            } else {
                // no source interceptor chain or source handlers, conntect to target interceptor chain or channel
                if (targetInterceptorChainHead != null) {
                    interceptorChainHead = targetInterceptorChainHead;
                    interceptorChainTail = targetInterceptorChainHead;
                } else {
                    Interceptor channelInterceptor = new RequestResponseInterceptor(null, targetRequestChannel, null,
                            targetResponseChannel);
                    interceptorChainHead = channelInterceptor;
                    interceptorChainTail = channelInterceptor;
                }
            }
        }
    }

}
