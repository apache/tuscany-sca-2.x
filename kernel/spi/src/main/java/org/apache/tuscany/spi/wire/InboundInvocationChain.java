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
 * A set of interceptors and handlers (contained in request and response message channels) associated with the inbound
 * side of a wire for an operation. Inbound chains may start with request {@link MessageHandler}s and contain at least
 * one {@link Interceptor} processed after the handlers prior to dipatching to the target instance. Inbound invocation
 * chains may also contain a set of response <code>MessageHandler</code>s which are processed after dispatching to the
 * target instance.
 * <p/>
 *
 * @version $$Rev$$ $$Date$$
 */
public interface InboundInvocationChain extends InvocationChain {


}
