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

import org.apache.tuscany.api.SCARuntime;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;

/**
 * @version $Rev$ $Date$
 */
public class CalculatorClient {

    public CalculatorClient() {
        super();
    }

    public static void main(String[] args) {
        // Start the embedded SCA runtime
        SCARuntime.start();
        
        // Look up the ComponentContext by name
        ComponentContext context = SCARuntime.getComponentContext("CalculatorServiceComponent");
        ServiceReference<CalculatorService> self = context.createSelfReference(CalculatorService.class);
        CalculatorService calculatorService = self.getService();
        System.out.println("1.0 x 2.0 = " + calculatorService.multiply(1.0, 2.0));
        AddService addService = context.getService(AddService.class, "addService");
        System.out.println("1.0 + 2.0 = " + addService.add(1.0, 2.0));
        
        // Stop the SCA embedded runtime
        SCARuntime.stop();
    }

}
