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
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.tuscany.sca.host.embedded.SCADomain;

public class TrafficAdvisoryServer {

    public static void main(String[] args) {
        try {
            SCADomain domain = SCADomain.newInstance("TrafficAdvisoryNotification.composite");
            TestCaseProducer testCaseProducer = domain.getService(TestCaseProducer.class, "TrafficAdvisoryProducer");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String value = null;
            do {
                try {
                    System.out.println("Send a report value, ^C or <end> to end");
                    value = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(value == null || value.equals("end")) {
                    break;
                }
                testCaseProducer.produceTrafficNotification("Report value [" + value + "]");
            }
            while(true);
                                
            domain.close();
        } catch(Throwable e) {
            e.printStackTrace();
        }
    }
}
