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
package calculator.dosgi.impl;

import org.osgi.service.component.ComponentContext;

import calculator.dosgi.CalculatorService;
import calculator.dosgi.operations.AddService;
import calculator.dosgi.operations.DivideService;
import calculator.dosgi.operations.MultiplyService;
import calculator.dosgi.operations.SubtractService;

/**
 * An implementation of the Calculator service.
 */
public class CalculatorServiceDSImpl implements CalculatorService {
    private AddService addService;
    private SubtractService subtractService;
    private MultiplyService multiplyService;
    private DivideService divideService;

    public CalculatorServiceDSImpl() {
        super();
        System.out.println("CalculatorServiceDSImpl()");
    }

    protected void activate(ComponentContext context) {
        System.out.println("Activating " + context);
    }

    protected void deactivate(ComponentContext context) {
        System.out.println("Deactivating " + context);
    }

    /*
     * The following setters can be used for DS injection
     */
    public void setAddService(AddService addService) {
        System.out.println("setAddService()");
        this.addService = addService;
    }

    public void setSubtractService(SubtractService subtractService) {
        this.subtractService = subtractService;
    }

    public void setDivideService(DivideService divideService) {
        this.divideService = divideService;
    }

    public void setMultiplyService(MultiplyService multiplyService) {
        this.multiplyService = multiplyService;
    }

    /*
     * The following setters can be used for DS injection
     */
    public void unsetAddService(AddService addService) {
        System.out.println("unsetAddService()");
        this.addService = null;
    }

    public void unsetSubtractService(SubtractService subtractService) {
        this.subtractService = null;
    }

    public void unsetDivideService(DivideService divideService) {
        this.divideService = null;
    }

    public void unsetMultiplyService(MultiplyService multiplyService) {
        this.multiplyService = null;
    }    
    private <T> T getService(Class<T> cls) {
        for (Object s : new Object[] {addService, subtractService, multiplyService, divideService}) {
            if (cls.isInstance(s)) {
                return cls.cast(s);
            }
        }
        throw new IllegalStateException(cls.getSimpleName() + " is not available");
    }

    public double add(double n1, double n2) {
        return getService(AddService.class).add(n1, n2);
    }

    public double subtract(double n1, double n2) {
        return getService(SubtractService.class).subtract(n1, n2);
    }

    public double multiply(double n1, double n2) {
        return getService(MultiplyService.class).multiply(n1, n2);
    }

    public double divide(double n1, double n2) {
        return getService(DivideService.class).divide(n1, n2);
    }
}
