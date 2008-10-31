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
package spring.annotations;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Service;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.ComponentName;

import calculator.AddService;
import calculator.CalculatorService;
import calculator.DivideService;
import calculator.MultiplyService;
import calculator.SubtractService;

/**
 * An implementation of the Calculator service.
 */
@Service(CalculatorService.class)
public class CalculatorServiceImpl implements AddService, SubtractService, MultiplyService, DivideService {
    
    public AddService addService;  // setter injection
    
    @Reference
    public SubtractService subtractService;  // field injection
    
    @Reference(name="multiplyService", required=false)
    public MultiplyService multiply;  // field injection (different reference and field name)
    
    public DivideService divide;  // setter injection (different reference and field name)
    
    public String message;  // setter injection
    
    @Property(name="message", required=false)
    public String message2;  // field injection
    
    public String componentName;
    
    @Init
    public void initMethod () {
        System.out.println("Init method is sucessfully called.....");
        // Property value should be null here.
        System.out.println("Property Value message is...." + message);
    }
    
    @Destroy
    public void destroyMethod () {
        System.out.println("Component Name is...." + componentName);
        System.out.println("Property Value message is...." + message);
        System.out.println("Property Value message2 is...." + message2);
        System.out.println("Destroy method is sucessfully called.....");
    }

    @Reference
    public void setAddService(AddService addService) {
        this.addService = addService;
    }
    
    public AddService getAddService() {
        return addService;
    }

    /*public void setSubtractService(SubtractService subtractService) {
        this.subtractService = subtractService;
    }
    
    public SubtractService getSubtractService() {
        return subtractService;
    }*/    
    
    @Reference(name="divideService", required=false)
    public void setDivideService(DivideService divide) {
        this.divide = divide;
    }
    
    public DivideService getDivideService() {
        return divide;
    }
    
    /*public void setMultiplyService(MultiplyService multiplyService) {
        this.multiplyService = multiplyService;
    }
    
    public MultiplyService getMultiplyService() {
        return multiplyService;
    }*/
    
    @ComponentName
    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }
    
    @Property
    public void setMessage(String message) {
        this.message = message;
    }    

    public double add(double n1, double n2) {
        return addService.add(n1, n2);
    }

    public double subtract(double n1, double n2) {
        return subtractService.subtract(n1, n2);
    }

    public double multiply(double n1, double n2) {
        return multiply.multiply(n1, n2);
    }

    public double divide(double n1, double n2) {
        return divide.divide(n1, n2);
    }
}
