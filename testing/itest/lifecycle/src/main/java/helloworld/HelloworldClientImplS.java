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

package helloworld;

import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;

/* 
 * S = a stateless scoped component that throws exceptions
 */

@Scope("STATELESS")
public class HelloworldClientImplS implements Helloworld {
    
    public static boolean throwTestExceptionOnConstruction = false;
    public static boolean throwTestExceptionOnInit = false;
    public static boolean throwTestExceptionOnDestroy = false;
    

    @Reference
    public Helloworld service;
    
    public HelloworldClientImplS() throws Exception {
        if(throwTestExceptionOnConstruction){
            StatusImpl.appendStatus("Exception on construction", "HelloworldClientImplS");
            throw new Exception("Exception on construction");
        }
    }
    
    @Init
    public void initialize() throws Exception{
        if (throwTestExceptionOnInit) {
            StatusImpl.appendStatus("Exception on init", "HelloworldClientImplS");
            throw new Exception("Exception on init");
        }
        
        StatusImpl.appendStatus("Init", "HelloworldClientImplS");
    	System.out.println(">>>>>> " + sayHello("init"));
    }
    
    @Destroy
    public void destroy() throws Exception{
        if (throwTestExceptionOnDestroy) {
            StatusImpl.appendStatus("Exception on destroy", "HelloworldClientImplS");
            throw new Exception("Exception on destroy");
        }
        
        StatusImpl.appendStatus("Destroy", "HelloworldClientImplS");
    }    
    
    public String sayHello(String name) throws Exception {
        return "Hi " + service.sayHello(name);
    }
    
    public String throwException(String name) throws Exception {
        throw new Exception("test exception");
    }

}
