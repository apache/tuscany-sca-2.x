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
package echo;

import junit.framework.TestCase;

import org.apache.tuscany.api.SCARuntime;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

/**
 * @version $Rev$ $Date$
 */
public class EchoBindingTestCase extends TestCase {

    private Client client;

    public void testEchoBinding() {
        client.call("foo");
    }

    protected void setUp() throws Exception {
    	SCARuntime.start("EchoBinding.composite");
        CompositeContext context = CurrentCompositeContext.getContext();
        client = context.locateService(Client.class, "Client");
    }
    
    protected void tearDown() throws Exception {
    	SCARuntime.stop();
    }
}
