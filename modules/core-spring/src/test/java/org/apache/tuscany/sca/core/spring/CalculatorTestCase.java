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
package org.apache.tuscany.sca.core.spring;

import org.apache.tuscany.sca.core.spring.context.ComponentContext;

import junit.framework.TestCase;
import calculator.CalculatorService;

/**
 * This shows how to test the Calculator service component.
 */
public class CalculatorTestCase extends TestCase {

    private ComponentContext context;
    private CalculatorService calculatorService;

    protected void setUp() throws Exception {
        context = new ComponentContext("org/apache/tuscany/core/spring/Calculator.composite");

        calculatorService = context.getService(CalculatorService.class, "CalculatorServiceComponent");
    }

    protected void tearDown() throws Exception {
        context = null;
        calculatorService = null;
    }

    public void testCalculator() throws Exception {
        // Calculate
        assertEquals(calculatorService.add(3, 2), 5.0);
        assertEquals(calculatorService.subtract(3, 2), 1.0);
        assertEquals(calculatorService.multiply(3, 2), 6.0);
        assertEquals(calculatorService.divide(3, 2), 1.5);

    }
}
