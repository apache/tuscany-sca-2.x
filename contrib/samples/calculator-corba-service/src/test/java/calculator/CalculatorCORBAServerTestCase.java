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

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.CORBA.ORB;

import calculator.idl.CalculatorCORBAService;
import calculator.idl.CalculatorCORBAServiceHelper;

/**
 * This shows how to test the Calculator service component.
 */
public class CalculatorCORBAServerTestCase extends TestCase {

    private SCADomain scaDomain;
    private CalculatorCORBAService calculatorService;

    @BeforeClass
    protected void setUp() throws Exception {
        scaDomain = SCADomain.newInstance("CalculatorCORBAServer.composite");
        String[] args = {"-ORBInitialPort", "5080"};
        // retrieve CORBA object which is SCA component
        ORB orb = ORB.init(args, null);
        calculatorService =
            CalculatorCORBAServiceHelper.narrow(orb
                .string_to_object("corbaname::localhost:5080#CalculatorCORBAService"));
    }

    @AfterClass
    protected void tearDown() throws Exception {
        scaDomain.close();
    }

    @Test
    public void testCalculator() throws Exception {
        assertEquals(5.0, calculatorService.add(3, 2));
        assertEquals(1.0, calculatorService.subtract(3, 2));
        assertEquals(6.0, calculatorService.multiply(3, 2));
        assertEquals(1.5, calculatorService.divide(3, 2));
    }
}
