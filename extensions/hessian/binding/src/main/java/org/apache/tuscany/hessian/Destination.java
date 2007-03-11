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

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractHessianOutput;

/**
 * Responsible for receiving invocations for a service endpoint from a transport
 *
 * @version $Rev$ $Date$
 */
public interface Destination {

    /**
     * Dispatches to the service endpoint
     *
     * @param in  the input stream containing the invocation payload
     * @param out the output stream to write the response to
     * @throws InvocationException if an error occurs invoking the service
     */
    void invoke(AbstractHessianInput in, AbstractHessianOutput out) throws InvocationException;
}
