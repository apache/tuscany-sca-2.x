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
package client;

import test.ASM_0002_Client;
import testClient.TestInvocation;

/**
 * Client for ASM_0009_TestCase, which tests that the @name attribute of a 
 * <component/> element in a <composite/> is not the same as the @name 
 * attribute of a second component in the same <composite/>
 */
public class ASM_0009_TestCase extends BaseJAXWSTestCase {

    protected TestConfiguration getTestConfiguration() {
        TestConfiguration config = new TestConfiguration();
        config.testName = "ASM_0009";
        config.input = "request";
        config.output = "exception";
        config.composite = "Test_ASM_0009.composite";
        config.testServiceName = "TestClient";
        config.testClass = ASM_0002_Client.class;
        config.serviceInterface = TestInvocation.class;
        return config;
    }

} // end class Test_ASM_0003
