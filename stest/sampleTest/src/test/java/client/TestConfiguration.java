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

/** 
 * A class to hold the metadata about the test
 * @author MikeEdwards
 *
 */
class TestConfiguration {

    public String testName;
    public String input;
    public String output;
    public String composite;
    public String testServiceName;
    public Class<?> testClass; //TODO - does the client need this??
    public Class<?> serviceInterface;

    public TestConfiguration() {
    }

    public String getTestName() {
        return testName;
    }

    public String getInput() {
        return input;
    }

    public String getExpectedOutput() {
        return output;
    }

    public String getComposite() {
        return composite;
    }

    public String getTestServiceName() {
        return testServiceName;
    }

    public Class<?> getTestClass() {
        return testClass;
    }

    public Class<?> getServiceInterface() {
        return serviceInterface;
    }
}
