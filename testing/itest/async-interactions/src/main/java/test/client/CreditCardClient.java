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
package test.client;

/**
 * A client interface to invoke the CreditCardPayment using different interaction patterns
 */
public interface CreditCardClient {
    /**
     * Invoke the service synchronously 
     * @param creditCardNumber
     * @param holder
     * @param amount
     * @return
     */
    String authorize(String creditCardNumber, String holder, float amount);

    /**
     * Invoke the service asynchronously and poll for the result 
     * @param creditCardNumber
     * @param holder
     * @param amount
     * @return
     */
    String authorizeAsync(String creditCardNumber, String holder, float amount);

    /**
     * Invoke the service asynchronously and get the result from a callback
     * @param creditCardNumber
     * @param holder
     * @param amount
     * @return
     */
    String authorizeAsyncWithCallback(String creditCardNumber, String holder, float amount);

    /**
     * Invoke the service using oneway (fire-and-forget) and the target component will make a callback upon the request has been processed
     * @param creditCardNumber
     * @param holder
     * @param amount
     * @return
     */
    String authorizeSCAAsyncWithCallback(String creditCardNumber, String holder, float amount);
    
    /**
     * Invoke the service using request/response and the target component will make a callback upon the request has been processed
     * @param creditCardNumber
     * @param holder
     * @param amount
     * @return
     */
    String authorizeSCAWithCallback(String creditCardNumber, String holder, float amount);
}
