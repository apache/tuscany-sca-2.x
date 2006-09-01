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
package org.apache.tuscany.spi.wire;

/**
 * A set of interceptors and handlers (contained in request and response message channels) associated with the outbound
 * side of a wire for a service operation. Outbound invocation chains always start with an {@link Interceptor} and may
 * contain 0..n {@link MessageHandler}s. <code>MessageHandlers</code> are part of a request or response chainnel, which
 * are invoked prior to and after dispatching to a target instance respectively.
 *
 * @version $$Rev$$ $$Date$$
 */
public interface OutboundInvocationChain extends InvocationChain {

}
