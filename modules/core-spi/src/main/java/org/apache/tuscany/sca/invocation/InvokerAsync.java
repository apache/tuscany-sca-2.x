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
 * TUSCANY-3786
 * 
 * Interface to describe an invoation where the request processing
 * can be performed independently of the response processing. This 
 * has been instigated to allow async responses to be processed
 * independently of the requests that instigated them. Due to the need
 * to run the reponse processing interceptors effectively backwards the 
 * methods defined here are not responsible for finding the next invoker 
 * in the chain. 
 *
 */
public interface InvokerAsync {
    
    /**
     * Process the forward message and pass it down the chain
     *
     * @param msg The request Message
     * @return the processed message
     * 
     */
    void invokeAsyncRequest(Message msg);
    
    /**
     * Process response message and pass it back up the chain.
     * This returns the message that is processed by the chain
     * so that it can be passes onto the appropriate invoker by the caller
     * the response path doesn't have an invoker. 
     *
     * @param msg The request Message
     * @return the processed message
     * 
     */
    Message invokeAsyncResponse(Message msg);    

    /**
     * Process a request message
     *
     * @param msg The request Message
     * @return the processed message
     * 
     */
    Message processRequest(Message msg);
    
    /**
     * Process a response message
     *
     * @param msg The request Message
     * @return the processed message
     * 
     */
    Message processResponse(Message msg);    

}
