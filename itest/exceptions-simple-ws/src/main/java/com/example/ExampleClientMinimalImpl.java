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
package com.example;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

//@Service(ExampleClient.class)
@Scope("COMPOSITE")
//public class ExampleClientMinimalImpl implements ExampleClient {
public class ExampleClientMinimalImpl {
    @Reference
    protected ExampleServiceMinimal myService;

    public void runTest() {
        try {
            //Object result = myService.hello("John");
            myService.throwException("John");
            //System.out.println("myService returned " + result.getClass().getName());
        } catch (BusinessExceptionMinimal e) {
            System.out.println("caught exception from hello(): " + e.getMessage() );
        }
    }
    
    public String hello(String name) throws BusinessExceptionMinimal{
        throw new BusinessExceptionMinimal("bad news");
    }
    public void throwException(String name) throws BusinessExceptionMinimal{
        throw new BusinessExceptionMinimal("bad news");
    }
    

    public static void main(String[] args) {
        
    }

}
