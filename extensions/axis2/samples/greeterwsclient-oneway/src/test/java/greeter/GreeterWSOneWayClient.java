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
package greeter;

import org.apache.tuscany.test.SCATestCase;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

public class GreeterWSOneWayClient extends SCATestCase {

    private GreeterLocal greeterLocal;

    @Override
    protected void setUp() throws Exception {
        try {
            setApplicationSCDL(GreeterService.class, "META-INF/sca/default.scdl");
            ClassLoader classLoader = getClass().getClassLoader();
            addExtension("test.extensions", classLoader.getResource("META-INF/tuscany/test-extensions.scdl"));

            super.setUp();
            CompositeContext compositeContext = CurrentCompositeContext.getContext();
            greeterLocal = compositeContext.locateService(GreeterLocal.class, "GreeterServiceComponent");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testWSClient() {
        try {
            greeterLocal.greet("John");

            System.out.println("Sleeping ...");
            Thread.sleep(5000);

            System.out.println("Done ...");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
