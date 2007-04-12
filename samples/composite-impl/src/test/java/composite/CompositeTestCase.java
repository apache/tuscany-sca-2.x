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
package composite;

import junit.framework.TestCase;

import org.apache.tuscany.api.SCARuntime;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;

public class CompositeTestCase extends TestCase {

    private Source source;

    protected void setUp() throws Exception {
        SCARuntime.start("OuterComposite.composite");
        ComponentContext context = SCARuntime.getComponentContext("SourceComponent/InnerSourceService");
        ServiceReference<Source> service = context.createSelfReference(Source.class);
        source = service.getService();   
      	
    }
    
    protected void tearDown() throws Exception {
    	SCARuntime.stop();
    }

    public void test() throws Exception {
        try {
            System.out.println("Main thread " + Thread.currentThread());
            source.clientMethod("Client.main");
            System.out.println("Sleeping ...");
            Thread.sleep(1000);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
