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
package org.apache.tuscany.sca.implementation.spring.itests.mock;

/**
 * A test Spring bean which provides the HelloWorld service.
 * This bean has a single String property called "hello" which must be set through
 * external configuration to give the correct response message, otherwise an (incorrect)
 * default message is generated
 *
 */

import org.apache.tuscany.sca.implementation.spring.itests.helloworld.HelloWorld;

public class TestSCAPropertyBean implements HelloWorld {

    private String hello = "Go away";

    /**
     * Provides the operation of the "HelloWorld" interface - a simple string response
     * to a string input message, where the response is a greeting followed by the original
     * input message.
     */
    public String sayHello(String message) {
        System.out.println("TestHelloWorldBean - sayHello called");
        return (hello + " " + message);
    }

    /**
     * Public setter for the (unannotated) field "hello" which constitutes an SCA
     * property
     * @param message - the message to use for the response to "sayHello"
     */
    public void setHello(String message) {
        hello = message;
    }

} // end class TestSCAPropertyBean
