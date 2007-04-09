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

import org.osoa.sca.annotations.Reference;

/**
 * This client program shows how to create an SCA runtime, start it,
 * locate the Calculator service and invoke it.
 */
public class CalculatorClient {
    
    private CalculatorService calculatorService;

    @Reference
    public void setCalculatorService(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }
    
    public int main(String[] args) throws Exception {
        
        if(args.length != 3) {
            System.err.println("usage: <add|substract|multiply|divide> <operand1> <operand2>");
            return 1;
        }

        double result;
        if("add".equals(args[0])) {
            result = calculatorService.add(Double.parseDouble(args[1]), Double.parseDouble(args[2]));
        } else if("subtract".equals(args[0])) {
            result = calculatorService.subtract(Double.parseDouble(args[1]), Double.parseDouble(args[2]));
        } else if("multiply".equals(args[0])) {
            result = calculatorService.multiply(Double.parseDouble(args[1]), Double.parseDouble(args[2]));
        } else if("divide".equals(args[0])) {
            result = calculatorService.divide(Double.parseDouble(args[1]), Double.parseDouble(args[2]));
        } else {
            throw new IllegalArgumentException("Usage <add|substract|multiply|divide> <operand1> <operand2>");
        }
        System.out.println("result = " + result);
        return 0;
    }
    
}
