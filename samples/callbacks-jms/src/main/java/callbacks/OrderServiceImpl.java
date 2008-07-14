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

package callbacks;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Service;

@Service(OrderService.class)
public class OrderServiceImpl implements OrderService {

    // A field for the callback reference object
    private OrderCallback callbackReference;

    // The place order operation itself
    public void placeOrder(OrderRequest oRequest) {

        // …do the work to process the order…
        // …which may take some time…

        System.out.println("service received order: " + oRequest);

        // when ready to respond…

        OrderResponse theResponse = new OrderResponse();

        callbackReference.placeOrderResponse(theResponse);
    }

    // A setter method for the callback reference
    @Callback
    public void setCallbackReference(OrderCallback theCallback) {
        callbackReference = theCallback;
    }
}
