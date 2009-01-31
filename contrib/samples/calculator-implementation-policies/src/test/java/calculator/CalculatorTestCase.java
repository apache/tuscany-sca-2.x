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

import javax.security.auth.login.Configuration;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * This shows how to test the Calculator service component.
 */
public class CalculatorTestCase extends TestCase {

    private CalculatorService calculatorService;
    private CalculatorService anotherCalculatorService;
    private SCADomain scaDomain;

    @Override
    protected void setUp() throws Exception {
        try {
            Configuration secConf = Configuration.getConfiguration();
        } catch ( java.lang.SecurityException e ) {
        	//FIXME: We should not compare exception strings as they are localized in various languages
            //if ( e.getMessage().equals("Unable to locate a login configuration") ) {
                System.setProperty("java.security.auth.login.config", 
                       this.getClass().getClassLoader().getResource("CalculatorJass.config").toString());
            //} else {
            //    throw e;
            //}
        }
        scaDomain = SCADomain.newInstance("Calculator.composite");
        calculatorService = scaDomain.getService(CalculatorService.class, "CalculatorServiceComponent");
        anotherCalculatorService = scaDomain.getService(CalculatorService.class, "AnotherCalculatorServiceComponent");
    }

    @Override
    protected void tearDown() throws Exception {
        scaDomain.close();
    }

    public void testCalculator() throws Exception {
        // Calculate
        assertEquals(calculatorService.add(3, 2), 5.0);
        assertEquals(calculatorService.subtract(3, 2), 1.0);
        assertEquals(calculatorService.multiply(3, 2), 6.0);
        assertEquals(calculatorService.divide(3, 2), 1.5);
    }
    
    public void testAnotherCalculator() throws Exception {
        // Calculate
        assertEquals(anotherCalculatorService.add(3, 2), 5.0);
        assertEquals(anotherCalculatorService.subtract(3, 2), 1.0);
        assertEquals(anotherCalculatorService.multiply(3, 2), 6.0);
        assertEquals(anotherCalculatorService.divide(3, 2), 1.5);
    }
}
