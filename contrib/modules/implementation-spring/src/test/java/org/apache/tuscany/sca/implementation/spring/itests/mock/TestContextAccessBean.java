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
 * Spring bean test class for testing the access to the Spring Context from within
 * a Spring Bean running as part of an SCA Component.
 * 
 * This class accesses the Spring Context and only returns non-null data if the 
 * Context is successfully accessed.
 * 
 * The design to receive the application context is as follows:
 * - the Bean implements the ApplicationContextAware interface
 * - this interface provides getter and setter methods for the Spring application
 *   context
 * - when the Bean is created at runtime, the setter method is called, injecting
 *   the context  
 */

import org.apache.tuscany.sca.implementation.spring.itests.helloworld.HelloWorld;

import org.springframework.beans.BeansException;   
import org.springframework.context.ApplicationContext;   
import org.springframework.context.ApplicationContextAware;


public class TestContextAccessBean implements HelloWorld, ApplicationContextAware {

    private static ApplicationContext ctx;
    static String hello = "Hello ";
    
    // Return the hello string only if the application context is successfully accessed
	public String sayHello(String message) {
		System.out.println("TestContextAccessBean - sayHello called");
		ApplicationContext theContext = getApplicationContext();
		
		if( theContext == null ) return null;
		
		// A simple check to see if the context contains this bean, which it should...
		if ( !theContext.containsBean( "testBean" ) ) return null;
		
		return (hello + message);
	} // end sayHello()
	
	/**
	 * Application context setter
	 */
    public void setApplicationContext(ApplicationContext appContext) throws BeansException {
        // Wiring the ApplicationContext into a static method           
        ctx = appContext;      
    }
    
    /**
     * Application context getter
     * @return
     */
    public static ApplicationContext getApplicationContext() {           
        return ctx;       
    }

}
