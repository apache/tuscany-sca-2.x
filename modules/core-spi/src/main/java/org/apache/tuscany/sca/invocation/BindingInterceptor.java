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
package org.apache.tuscany.sca.invocation;

/**
 * TODO RRB experiment to allow the request and respose side of the 
 *      invoke to be called independently
 * Synchronous mediation associated with a client- or target- side wire.
 *
 * @version $Rev$ $Date$
 */
public interface BindingInterceptor extends Invoker {

    /**
     * Process a synchronous request
     *
     * @param msg The request Message for the wire
     * @return The response Message from the wire
     */
    Message invokeRequest(Message msg);
    
    /**
     * Process a synchronous response
     *
     * @param msg The request Message for the wire
     * @return The response Message from the wire
     */
    Message invokeResponse(Message msg);    

}
