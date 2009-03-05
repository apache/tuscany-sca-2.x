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
 * Client for ASM_0011_TestCase, which tests that the the multiplicity 
 * of a <reference/> element of a <component/> can be 1..1 where the 
 * multiplicity of the equivalent <reference/> element in the 
 * <componentType/> of the <implementation/> of the <component/> is 1..n
 */
public class ASM_0011_TestCase extends BaseJAXWSTestCase {

    protected TestConfiguration getTestConfiguration() {
        TestConfiguration config = new TestConfiguration();
        config.testName = "ASM_0011";
        config.input = "request";
        config.output = "ASM_0011 request service1 operation1 invoked service2 operation1 invoked";
        config.composite = "Test_ASM_0011.composite";
        config.testServiceName = "TestClient";
        config.testClass = ASM_0002_Client.class;
        config.serviceInterface = TestInvocation.class;
        return config;
    }

} // end class Test_ASM_0003
