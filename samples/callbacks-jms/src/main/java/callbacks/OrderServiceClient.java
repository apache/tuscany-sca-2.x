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

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

@Service(OrderServiceClient.class)
public class OrderServiceClient implements OrderCallback {

    // A field to hold the reference to the order service

    private OrderService orderService;
    public static OrderResponse oResponse;

    public void doSomeOrdering() {

        OrderRequest oRequest = new OrderRequest();

        // … fill in the details of the order …

        System.out.println("client placing order: " + oRequest);

        orderService.placeOrder(oRequest);

        // …the client code can continue to do processing
    }

    public void placeOrderResponse(OrderResponse oResponse) {

        // …handle the response as needed

        System.out.println("client callback received order response: " + oResponse);
        OrderServiceClient.oResponse = oResponse;
    }

    // A setter method for the order service reference
    @Reference
    public void setOrderService(OrderService theService) {
        orderService = theService;
    }
}
