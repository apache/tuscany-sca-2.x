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


import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Service;

import yetanotherpackage.AnotherHelloWorldException;
import yetanotherpackage.DBean;

import anotherpackage.BBean;
import anotherpackage.CBean;

/**
 * This class implements the HelloWorld service.
 */
@Service(HelloWorldService.class)
public class HelloWorldClientImpl implements HelloWorldService {
    
    @Reference
    protected HelloWorldService hwService;

    public String getGreetings(String name) {
        return "Hello " + hwService.getGreetings(name);
    }
    
    public String getGreetingsBean(ABean bean){
        return "Hello " + hwService.getGreetingsBean(bean);
    }

    public String getGreetingsBeanArray(ABean[] bean){
        return "Hello " + hwService.getGreetingsBeanArray(bean);
    }
   
    /*
    public String getGreetingsBeanVector(Vector<ABean> bean){
        return "Hello " + bean.get(0).getField1() + " " + bean.get(0).getField2();
    }
    */
    
    public String getGreetingsBBean(BBean bean){
        return "Hello " + hwService.getGreetingsBBean(bean);
    }
    
    public String getGreetingsCBean(CBean bean){
        return "Hello " + hwService.getGreetingsCBean(bean);
    }    
    
    public String getGreetingsDBean(DBean bean){
        return "Hello " + hwService.getGreetingsDBean(bean);
    } 
    
    public String getGreetingsException(String input) throws HelloWorldException {
    	return hwService.getGreetingsException(input);
    }
    
    public String getGreetingsAnotherException(String input) throws AnotherHelloWorldException {
        return hwService.getGreetingsAnotherException(input);
    }    
    
    public byte[] getGreetingsByteArray(byte[] input) {
    	return input;
    }
    
    public ABean getGreetingsABeanMultiple(ABean bean1, ABean bean2){
        return hwService.getGreetingsABeanMultiple(bean1, bean2);
    }
}
