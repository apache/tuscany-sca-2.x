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

package org.apache.tuscany.sca.itest;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import calculator.CalculatorService;

public class PoliciedCalculatorTestCase {
    private static SCADomain domain;
    private static CalculatorService calculatorService;

    @Test
    public void testPolicies() {
        calculatorService.add(10, 10);
        calculatorService.multiply(10, 10);
        calculatorService.divide(10, 10);
        calculatorService.subtract(10, 10);
    }

    @BeforeClass
    public static void init() throws Exception {
        try {
            domain = SCADomain.newInstance("Calculator.composite");
        } catch (Exception e) {
            e.printStackTrace();
        }
        calculatorService = domain.getService(CalculatorService.class, "CalculatorServiceComponent");
    }

    @AfterClass
    public static void destroy() throws Exception {
        domain.close();
    }
}
