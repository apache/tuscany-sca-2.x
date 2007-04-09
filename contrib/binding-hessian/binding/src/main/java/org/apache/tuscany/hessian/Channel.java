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
package org.apache.tuscany.hessian;

import org.apache.tuscany.spi.wire.Message;

/**
 * Channels dispatch service invocations over a transport
 *
 * @version $Rev$ $Date$
 */
public interface Channel {

    /**
     * Dispatches the service invocation
     *
     * @param operation  the service operation to invoke
     * @param returnType the expected return type or null
     * @param message    the message containing the invocation payload
     * @return the response message
     * @throws InvocationException if an error occurs during the invocation
     */
    Message send(String operation, Class<?> returnType, Message message) throws InvocationException;


}
