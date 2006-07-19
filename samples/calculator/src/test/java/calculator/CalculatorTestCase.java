/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package calculator;

import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

import org.apache.tuscany.test.SCATestCase;

/**
 * This shows how to test the Calculator service component.
 */
public class CalculatorTestCase extends SCATestCase {

    private CalculatorService calculatorService;

    protected void setUp() throws Exception {
        super.setUp();

        CompositeContext context = CurrentCompositeContext.getContext();
        calculatorService = context.locateService(CalculatorService.class, "CalculatorServiceComponent");
    }

    public void testCalculator() throws Exception {
        // Calculate
        assertEquals(calculatorService.add(3, 2), 5.0);
        assertEquals(calculatorService.subtract(3, 2), 1.0);
        assertEquals(calculatorService.multiply(3, 2), 6.0);
        assertEquals(calculatorService.divide(3, 2), 1.5);

    }
}
