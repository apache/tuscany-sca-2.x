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
package org.apache.tuscany.samples.supplychain;

import junit.framework.TestCase;

import org.apache.tuscany.core.client.TuscanyRuntime;
import org.osoa.sca.CurrentModuleContext;
import org.osoa.sca.ModuleContext;

/**
 * This client program shows how to create an SCA runtime, start it,
 * locate a simple HelloWorld service component and invoke it.
 */
public class SupplyChainClientTestCase extends TestCase {

    public void test() throws Exception {
        
        // Obtain Tuscany runtime
        TuscanyRuntime tuscany = new TuscanyRuntime("hello", null);

        // Associate the application module component with this thread
        tuscany.start();

        // Obtain SCA module context.
        ModuleContext moduleContext = CurrentModuleContext.getContext();

        // Locate the HelloWorld service component and invoke it
        // Locate the CustomerComponent service component and invoke it
        Customer customer = (Customer) moduleContext.locateService("CustomerComponent");
        System.out.println("Main thread " + Thread.currentThread());
        customer.purchaseGoods();

    }
}
