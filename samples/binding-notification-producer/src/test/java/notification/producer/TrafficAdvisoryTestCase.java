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
package notification.producer;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import notification.producer.TestCaseProducer;

import org.apache.tuscany.sca.host.embedded.SCADomain;
//import org.apache.tuscany.sca.notification.remoteProducer.TestCaseProducer;

import junit.framework.TestCase;

public class TrafficAdvisoryTestCase extends TestCase {

    private SCADomain domain;
    
    public void testTrafficAdvisoryNotification() throws Exception {
        System.out.println("Only instantiating and closing domain ...");
        TestCaseProducer testCaseProducer = domain.getService(TestCaseProducer.class, "TrafficAdvisoryProducer");

        /* Uncomment to test with consumer
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String value = "foo";
        do {
            try {
                System.out.println("Send a report value, ^C or <end> to end");
                value = reader.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(value == null || value.equals("end")) {
                break;
            }
            testCaseProducer.produceTrafficNotification("Report value [" + value + "]");
            value = "end";
        }
        while(true);
        */
    }

    @Override
    protected void setUp() throws Exception {
        try {
            domain = SCADomain.newInstance("TrafficAdvisoryNotification.composite");
        } catch(Throwable e) {
            e.printStackTrace();
            if (e instanceof Exception) {
                throw (Exception)e;
            }
            else {
                throw new Exception(e);
            }
        }
    }
    
    @Override
    protected void tearDown() throws Exception {
        domain.close();
    }
}
