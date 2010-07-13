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
 * An implementation of the Calculator service.
 */
public class CalculatorServiceImpl implements CalculatorService {

	@Override
	public String calculate(Integer n1) {

		// TODO brute force search for divisors of n1 (http://en.wikipedia.org/wiki/Brute-force_search)
                // which should give a nice example of a method that takes a long time if given a
                // big enough input parameter
		return "1 2";
	}







}
