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
package calculator;

/**
 * A dummy domain registry component
 */
public class TestRegistryImpl {

    public String locateService(String targetServiceName){
        String targetURL = null;
        
        // In reality of course a registry would have to be initialized
        // with the real target service endpoints but in this test
        // we know what they are going to be ahead of time
        if (targetServiceName.equals("AddServiceComponentB")){
            targetURL = "http://localhost:8085/" + targetServiceName;
        } else if (targetServiceName.equals("SubtractServiceComponentC")){
            targetURL = "http://localhost:8086/" + targetServiceName;
        }
        
        return targetURL;
    }
}
