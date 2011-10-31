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

import java.net.URI;

import org.junit.Assert;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.junit.Test;
import org.oasisopen.sca.NoSuchServiceException;
import org.oasisopen.sca.client.SCAClientFactory;

/*
 * A unit test for the Calculator composite that just fires up the
 * composite and expects the EagerInit of the CalculatorClient component
 * to do its thing
 */
public class CalculatorTestCase {

    @Test
    public void testCalculate() throws NoSuchServiceException {

        // Run the SCA composite in a Tuscany runtime
         //TuscanyRuntime.runComposite("Calculator.composite", "target/classes");
        Node node = null;
            
        try {
            TuscanyRuntime tuscanyRuntime = TuscanyRuntime.newInstance();
            node = tuscanyRuntime.createNode();
            node.installContribution("AsyncSample", "target/classes", null, null);
            node.startComposite("AsyncSample", "Calculator.composite");
            node.startComposite("AsyncSample", "CalculatorClient.composite");
            
            SCAClientFactory scaClientFactory = SCAClientFactory.newInstance(URI.create("default"));
            CalculatorClient calculatorClient = scaClientFactory.getService(CalculatorClient.class, "CalculatorClient");
            
            calculatorClient.calculate();
        }catch (Exception e){
            e.printStackTrace();      
        } finally {
            // Stop the Tuscany runtime Node
            node.stop();        
        }
    }
}
