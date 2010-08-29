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

import org.oasisopen.sca.annotation.Reference;

/**
 * An implementation of the Calculator service.
 */
public class CalculatorServiceImpl implements CalculatorService {

    private AddService addService;
    private SubtractService subtractService;
    private MultiplyService multiplyService;
    private DivideService divideService;

    @Reference
    public void setAddService(AddService addService) {
        this.addService = addService;
    }

    @Reference
    public void setSubtractService(SubtractService subtractService) {
        this.subtractService = subtractService;
    }

    @Reference
    public void setDivideService(DivideService divideService) {
        this.divideService = divideService;
    }

    @Reference
    public void setMultiplyService(MultiplyService multiplyService) {
        this.multiplyService = multiplyService;
    }

    public String add(String n1, String n2) {
        //System.out.println("ADD Operation ==> " + n1 + " + " + n2 + " = " + addService.add(Double.parseDouble(n1), Double.parseDouble(n2)));
        return String.valueOf(addService.add(Double.parseDouble(n1), Double.parseDouble(n2)));
    }

    public String subtract(String n1, String n2) {
        //System.out.println("SUBTRACT Operation ==> " + n1 + " + " + n2 + " = " + subtractService.subtract(Double.parseDouble(n1), Double.parseDouble(n2)));
        return String.valueOf(subtractService.subtract(Double.parseDouble(n1), Double.parseDouble(n2)));
    }

    public String multiply(String n1, String n2) {
        //System.out.println("MULTIPLY Operation ==> " + n1 + " + " + n2 + " = " + multiplyService.multiply(Double.parseDouble(n1), Double.parseDouble(n2)));
        return String.valueOf(multiplyService.multiply(Double.parseDouble(n1), Double.parseDouble(n2)));
    }

    public String divide(String n1, String n2) {
        //System.out.println("DIVIDE Operation ==> " + n1 + " + " + n2 + " = " + divideService.divide(Double.parseDouble(n1), Double.parseDouble(n2)));
        return String.valueOf(divideService.divide(Double.parseDouble(n1), Double.parseDouble(n2)));
    }

    public String calculate(String formula) {
        String[] operands = formula.split("[\\+\\-\\*\\:]");
        if(formula.contains("+")) {
            return add( operands[0], operands[1]);
        } else if(formula.contains("-")) {
            return subtract( operands[0], operands[1]);
        } else if(formula.contains("*")) {
            return multiply( operands[0], operands[1]);
        } else if(formula.contains(":")) {
            return divide( operands[0], operands[1]);
        } else {
            throw new IllegalArgumentException("Invalid formula: " + formula );
        }
    }

}
