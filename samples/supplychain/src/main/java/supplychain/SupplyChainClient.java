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
package supplychain;

import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

/**
 * This client program shows how to create an SCA runtime, start it,
 * locate a Customer service component and invoke it.
 */
public class SupplyChainClient {

    public static final void main(String[] args) throws Exception {
        
        // Locate the Customer component and invoke it
        CompositeContext context = CurrentCompositeContext.getContext();
        Customer customer =
                context.locateService(Customer.class, "CustomerComponent");

        System.out.println("Main thread " + Thread.currentThread());
        customer.purchaseGoods();

    }
}
