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

import javax.xml.ws.AsyncHandler;

import org.oasisopen.sca.annotation.Reference;



/**
 * An implementation of the Calculator service.
 */
public class CalculatorServiceImpl implements CalculatorService {
	
	@Reference
	protected CalculateViaAsyncRef calculatorRefSyncService;
	
//	@Reference
//	protected CalculateViaAsyncRef calculatorRefAsyncService;

	@Override
	public String calculate(Integer n1) {
		
		// sync
		String result = calculatorRefSyncService.calculate(1);
		System.out.println("Sync client patern: result = " + result);
		
		// async poll
		Future<String> future = calculatorRefSyncService.calculateAsync(20);
		
		while (!future.isDone()){
			System.out.println("Waiting for poll");
		}
		
		try {
			result = future.get();
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
		future = calculatorRefSyncService.calculateAsync(3, handler);
		
		while (!future.isDone()){
			System.out.println("Waiting for callback");
		}

		return result;
	}







}
