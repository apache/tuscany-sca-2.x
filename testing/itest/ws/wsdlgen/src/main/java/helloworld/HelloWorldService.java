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

import org.oasisopen.sca.annotation.Remotable;

import yetanotherpackage.AnotherHelloWorldException;
import yetanotherpackage.DBean;

import anotherpackage.BBean;
import anotherpackage.CBean;

/**
 * This is the business interface of the HelloWorld greetings service.
 */
@WebService
@Remotable
public interface HelloWorldService {
	
    // primitives
	public String getGreetings(String name);
    byte[] getGreetingsByteArray(byte[] input);
    String getGreetingsException(String input) throws HelloWorldException;
    String getGreetingsAnotherException(String input) throws AnotherHelloWorldException;

	// simple bean configs
    public String getGreetingsBean(ABean bean);
    public String getGreetingsBeanArray(ABean[] bean);
    public String getGreetingsBBean(BBean bean);
    public String getGreetingsCBean(CBean bean);
    public String getGreetingsDBean(DBean bean);
    
    // more complex bean configs
    public ABean getGreetingsABeanMultiple(ABean bean1, ABean bean2);
    
    // collections
    //public String getGreetingsBeanVector(Vector<ABean> bean);
}

