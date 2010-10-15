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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.xml.ws.Response;

import org.oasisopen.sca.annotation.Reference;

/**
 * An implementation of the Calculator service which just proxies
 * to sync and asyn versions of the calculator service. This proxy
 * exercises the various async interface alternatives
 */
public class CalculatorServiceProxyImpl implements CalculatorServiceSync {
	
	@Reference
	protected CalculateReferenceAsync calculatorServiceRefSync;
	
	@Reference
	protected CalculateReferenceAsync calculatorServiceRefAsync;

	@Override
	public String calculate(Integer n1) {
	    String result = null;
	    
	    // calculate using a sync service
	    System.out.println("Calling sync service");
	    result = calculate(calculatorServiceRefSync, n1);
	    
	    // calculate using an aycn service
	    System.out.println("Calling async service");
	    result += calculate(calculatorServiceRefAsync, n1);
	    
	    return result;
	}    
	    
	// exercise sync and async versions of a service interface method
	private String calculate(CalculateReferenceAsync calculatorRef, Integer n1) {	    
		
		// sync
		String result = calculatorRef.calculate(1);
		System.out.println("Sync client patern: result = " + result);
		
		// async poll
		Response<String> response = calculatorRef.calculateAsync(20);
		
		while (!response.isDone()){
			System.out.println("Waiting for poll");
		}
		
		try {
			result = response.get();
			System.out.println("Async client poll patern: result = " + result);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// async callback 
		CalculatorAsyncHandler handler = new CalculatorAsyncHandler();
		Future<String> future = calculatorRef.calculateAsync(3, handler);
		
		while (!future.isDone()){
			System.out.println("Waiting for callback");
		}

		return result;
	}
}
