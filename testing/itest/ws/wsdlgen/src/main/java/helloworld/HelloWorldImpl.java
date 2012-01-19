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

import javax.jws.WebService;

import org.oasisopen.sca.annotation.Service;

import yetanotherpackage.DBean;
import yetanotherpackage.AnotherHelloWorldException;

import anotherpackage.BBean;
import anotherpackage.CBean;

/**
 * This class implements the HelloWorld service.
 */
@WebService
@Service(HelloWorldService.class)
public class HelloWorldImpl implements HelloWorldService {

    public String getGreetings(String name) {
        return "Hello " + name;
    }

    public String getGreetingsBean(ABean bean) {
        return "Hello " + bean.getField1() + " " + bean.getField2()
                + bean.getField3().getField1() + " "
                + bean.getField3().getField2();
    }

    public String getGreetingsBeanArray(ABean[] bean) {
        return "Hello " + bean[0].getField1() + " " + bean[0].getField2();
    }

    /*
     * public String getGreetingsBeanVector(Vector<ABean> bean){ return "Hello "
     * + bean.get(0).getField1() + " " + bean.get(0).getField2(); }
     */

    public String getGreetingsBBean(BBean bean) {
        return "Hello " + bean.getField1() + " " + bean.getField2();
    }

    public String getGreetingsCBean(CBean bean) {
        return "Hello " + bean.getField1() + " " + bean.getField2();
    }
    
    public String getGreetingsDBean(DBean bean) {
        return "Hello " + bean.getField1() + " " + bean.getField2() + " "
                + bean.getField3().getField1() + " "
                + bean.getField3().getField2();
    }   
    
    public String getGreetingsException(String input) throws HelloWorldException {
    	throw new HelloWorldException("Hello " + input);
    }
    
    public String getGreetingsAnotherException(String input) throws AnotherHelloWorldException {
        throw new AnotherHelloWorldException("Hello " + input);
    }    
    
    public byte[] getGreetingsByteArray(byte[] input){
    	System.out.println(String.valueOf(input));
    	return input;
    }
    
    public ABean getGreetingsABeanMultiple(ABean bean1, ABean bean2){
        return bean1;
    }
}
