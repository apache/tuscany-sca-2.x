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
package org.example.orderservice;

import javax.xml.ws.Holder;

import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Service;

@Service(OrderServiceBare.class)
public class OrderServiceBareForwardImpl implements OrderServiceBare {

    @Reference
    public OrderServiceBare ref;

    @Override
    public Order bareReviewOrder(Order myData) {
        Order retVal = ref.bareReviewOrder(myData);
        return retVal;
    }

    @Override
    public void bareReviewOrderInOutHolder(Holder<Order> myData) {
        ref.bareReviewOrderInOutHolder(myData);
    }

    @Override
    public void bareReviewOrderOutHolder(Holder<Order> myData) {
        ref.bareReviewOrderOutHolder(myData);
    }
}
