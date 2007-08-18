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
package sample;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;

//FIXME Fix this test case
public class RecursiveCompositeTestCaseFIXME extends TestCase {

    private SCADomain domain;
    private Service1 tracker, tracker2;

    protected void setUp() throws Exception {
        domain = SCADomain.newInstance("http://localhost", "/", "Composite1.composite", "Composite2.composite");
        tracker = domain.getService(Service1.class, "ComponentC");
        tracker2 = domain.getService(Service1.class, "ComponentB");

    }

    protected void tearDown() throws Exception {
        domain.close();
    }

    public void test() throws Exception {
        try {
            System.out.println("Main thread " + Thread.currentThread());
            System.out.println(tracker.track("Client"));
            System.out.println(tracker2.track("Client"));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
