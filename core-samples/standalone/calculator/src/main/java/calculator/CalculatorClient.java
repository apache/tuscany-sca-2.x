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
 * This client program shows how to create an SCA runtime, start it,
 * locate the Calculator service and invoke it.
 */
public class CalculatorClient {
    
    private CalculatorService calculatorService;
    
    public void setCalculatorService(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }
    
    public Object main(Object ... args) throws Exception {
        
        if(args.length != 3) {
            throw new IllegalArgumentException("Usage <add|substract|multiply|divide> <operand1> <operand2>");
        }
        
        if("add".equals(args[0])) {
            return calculatorService.add(Double.parseDouble((String)args[1]), Double.parseDouble((String)args[2]));
        } else if("substract".equals(args[0])) {
            return calculatorService.subtract(Double.parseDouble((String)args[1]), Double.parseDouble((String)args[2]));
        } else if("multiply".equals(args[0])) {
            return calculatorService.multiply(Double.parseDouble((String)args[1]), Double.parseDouble((String)args[2]));
        } else if("divide".equals(args[0])) {
            return calculatorService.divide(Double.parseDouble((String)args[1]), Double.parseDouble((String)args[2]));
        }

        throw new IllegalArgumentException("Usage <add|substract|multiply|divide> <operand1> <operand2>");
    }
    
}
