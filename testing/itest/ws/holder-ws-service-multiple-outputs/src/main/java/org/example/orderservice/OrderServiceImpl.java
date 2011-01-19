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
@Service(OrderService.class)
public class OrderServiceImpl implements OrderService {

    /** This dummy implementation approves or rejects orders:
     * < 100 - always approved.
     * 100-1100 - randomly approved. Probability = (1100 - amount)/10
     * >1100 - always rejected.
     */
    // public Order reviewOrder(Order order) {
    //   @WebMethod(action = "http://www.example.org/OrderService/reviewOrder")
    //   @RequestWrapper(localName = "reviewOrder", targetNamespace = "http://www.example.org/OrderService/", className = "org.example.orderservice.ReviewOrder")
    //   @ResponseWrapper(localName = "reviewOrderResponse", targetNamespace = "http://www.example.org/OrderService/", className = "org.example.orderservice.ReviewOrderResponse")
    public String[] reviewOrder(
                                @WebParam(name = "myData", targetNamespace = "", mode = WebParam.Mode.INOUT)
                                Holder<Order> myData,
                                @WebParam(name="myOutParam", targetNamespace = "", mode = WebParam.Mode.OUT)
                                Holder<Float> myOutParam) {
        String[] returnVal = {"retval1", "retval2"};
        Order order = myData.value;
        double total = order.getTotal();
        if ( total < 100.0 ) {
            order.setStatus( Status.APPROVED );
        } else if ( total > 1100.0 ) {
            order.setStatus( Status.REJECTED );
        } else {
            int probability = (int) ((-100.0 + total) / 10.0);
            Random approver = new Random();
            if ( approver.nextInt( 100 ) < probability )
                order.setStatus( Status.APPROVED );
            else
                order.setStatus( Status.REJECTED );
        }        
        System.out.println( ">>> OrderService.reviewOrder return=" + order );

        if ( myOutParam.value == null ) {
            myOutParam.value = new Float("97");
        } else {
            myOutParam.value = new Float("-1000");
        }
        return returnVal;
    }

    @Override
    public String[] reviewOrderTwoInOuts(Holder<Order> myData,
                                         Holder<Float> myOutParam) {
        String[] returnVal = {"retval1", "retval2"};
        Order order = myData.value;
        double total = order.getTotal();
        if ( total < 100.0 ) {
            order.setStatus( Status.APPROVED );
        } else if ( total > 1100.0 ) {
            order.setStatus( Status.REJECTED );
        } else {
            int probability = (int) ((-100.0 + total) / 10.0);
            Random approver = new Random();
            if ( approver.nextInt( 100 ) < probability )
                order.setStatus( Status.APPROVED );
            else
                order.setStatus( Status.REJECTED );
        }        
        System.out.println( ">>> OrderService.reviewOrder return=" + order );

        // Since this is an INOUT, expect "111" to signify test pass
        if ( myOutParam.value.equals(new Float("111"))) {
            myOutParam.value = new Float("97");
        } else {
            myOutParam.value = new Float("-1000");
        }
        return returnVal;
    }

    @Override
    public String[] reviewOrderTwoOutHolders(Holder<Order> myData,
                                             Holder<Float> myOutParam) {			
        String[] returnVal = {"retval1", "retval2"};            	
        if (myData.value == null) {
            myData.value = new Order();
            myData.value.setStatus(Status.REJECTED);
        } else {
            // Do nothing so test logic will fail
        }

        // Use test logic to check it's truly treated as an OUT parm
        if ( myOutParam.value == null ) {
            myOutParam.value = new Float("97");
        } else {
            myOutParam.value = new Float("-1000");
        }
        return returnVal;
    }

    @Override
    public String[] reviewOrderTwoInOutsThenIn(Holder<Order> myData,
                                               Holder<Float> myOutParam, 
                                               Integer myCode) {
        String[] returnVal = {"retval1", "retval2"};
        Order order = myData.value;
        double total = order.getTotal();
        if ( total < 50.1 && myCode.equals(new Integer("23")) ) {
            order.setStatus( Status.APPROVED );
        } else {
            order.setStatus( Status.REJECTED); 
        }

        // Use test logic to check it's truly treated as an INOUT parm
        if ( myOutParam.value != null ) {
            myOutParam.value = new Float("97");
        } else {
            myOutParam.value = new Float("-1000");
        }
        return returnVal;
    }

}
