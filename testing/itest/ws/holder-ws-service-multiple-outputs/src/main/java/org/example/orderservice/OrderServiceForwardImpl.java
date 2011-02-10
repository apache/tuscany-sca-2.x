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

@Service(OrderService.class)
public class OrderServiceForwardImpl implements OrderService {

    @Reference
    public OrderService ref;

    public String[] reviewOrder(Holder<Order> myData, Holder<Float> myOutParam) {
        String[] retVal = ref.reviewOrder(myData, myOutParam);
        return retVal;
    }

    @Override
    public String[] reviewOrderTwoInOuts(Holder<Order> myData,
                                         Holder<Float> myOutParam) {
        String[] retVal = ref.reviewOrderTwoInOuts(myData, myOutParam);
        return retVal;
    }

    @Override
    public String[] reviewOrderTwoOutHolders(Holder<Order> myData,
                                             Holder<Float> myOutParam) {
        String[] retVal = ref.reviewOrderTwoOutHolders(myData, myOutParam);
        return retVal;
    }

    @Override
    public String[] reviewOrderTwoInOutsThenIn(Holder<Order> myData,
                                               Holder<Float> myOutParam, Integer myCode) {
        String[] retVal = ref.reviewOrderTwoInOutsThenIn(myData, myOutParam, myCode);
        return retVal;
    }

    @Override
    public void reviewOrderTwoInOutsVoid(Holder<Order> myData, Holder<Float> myOutParam) {
        ref.reviewOrderTwoInOutsVoid(myData, myOutParam);
    }

    @Override
    public String[] reviewOrderOutThenInOut(Holder<Float> myOutParam, Holder<Order> myData) {
        String[] retVal = ref.reviewOrderOutThenInOut(myOutParam, myData);
        return retVal;
    }


}
