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

// import org.osoa.sca.annotations.Service;
import java.util.Random;

import javax.jws.WebParam;
import javax.xml.ws.Holder;

import org.oasisopen.sca.annotation.Service;

/**
 * This class implements the OrderService service.
 */
@Service(OrderServiceBare.class)
public class OrderServiceBareImpl implements OrderServiceBare {

    @Override
    public Order bareReviewOrder(Order order) {
        double total = order.getTotal();
        Order retVal = new Order();
        if ( total < 100.0 ) {
            retVal.setStatus( Status.APPROVED );
        } else if ( total > 1100.0 ) {
            retVal.setStatus( Status.REJECTED );
        }
        return retVal;
    }

    @Override
    public void bareReviewOrderInOutHolder(Holder<Order> myData) {
        double total = myData.value.getTotal();
        myData.value = new Order();
        if ( total < 100.0 ) {
            myData.value.setStatus( Status.APPROVED );
        } else if ( total > 1100.0 ) {
            myData.value.setStatus( Status.REJECTED );
        }    
    }

    @Override
    public void bareReviewOrderOutHolder(Holder<Order> myData) {
        boolean holderEmpty = (myData.value == null ? true : false);
        myData.value = new Order();
        if (holderEmpty) {
            myData.value.setStatus( Status.APPROVED );
            myData.value.setCustomerId("approved.1234");
        } else {
            myData.value.setStatus( Status.REJECTED );
        }
    }
}
