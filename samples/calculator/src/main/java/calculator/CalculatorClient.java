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

import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * This client program shows how to create an SCA runtime, start it,
 * and locate and invoke a SCA component
 */
public class CalculatorClient {
    public static void main(String[] args) throws Exception {

        SCADomain scaDomain = SCADomain.newInstance("Calculator.composite");
        
        CalculatorService calculatorService = 
            scaDomain.getService(CalculatorService.class, "CalculatorServiceComponent");

        // Calculate
        System.out.println("3 + 2=" + calculatorService.add(3, 2));
        System.out.println("3 - 2=" + calculatorService.subtract(3, 2));
        System.out.println("3 * 2=" + calculatorService.multiply(3, 2));
        System.out.println("3 / 2=" + calculatorService.divide(3, 2));

        double r;
        for (int i=0; i < 100; i++) {
            r = calculatorService.add(3, 2);
            r = calculatorService.subtract(3, 2);
            r = calculatorService.multiply(3, 2);
            r = calculatorService.divide(3, 2);
        }

        long begin = System.currentTimeMillis();
        long n = 10000;
        for (int i=0; i < n; i++) {
            r = calculatorService.add(3, 2);
            r = calculatorService.subtract(3, 2);
            r = calculatorService.multiply(3, 2);
            r = calculatorService.divide(3, 2);
        }
        long end = System.currentTimeMillis();
        double time = ((double)(end - begin)) / ((double)n);
        System.out.println("Time: " + time);
        
        scaDomain.close();
    }

}
