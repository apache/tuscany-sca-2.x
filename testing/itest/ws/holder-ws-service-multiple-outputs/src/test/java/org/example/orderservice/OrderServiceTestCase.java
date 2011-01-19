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

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.xml.ws.Holder;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests that the order server responds.
 */
public class OrderServiceTestCase {

    private Node node;

    @Before
    public void startServer() throws Exception {
        try {

            NodeFactory factory = NodeFactory.newInstance();
            String contribution = ContributionLocationHelper.getContributionLocation(OrderService.class);
            node = factory.createNode("ordersca.composite", new Contribution("order", contribution)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
     
    }

    @Test
    public void testOrderReviewApprovedSCA() throws IOException {
        OrderService orderService =
            node.getService(OrderService.class, "OrderServiceComponent/OrderService");
        assertNotNull(orderService);
        Holder<Float> outParam = new Holder<Float>(new Float(57.4));        
        testOrderReviewApproved(orderService, outParam);
    }

    @Test
    public void testOrderReviewApprovedWS() throws IOException {
        OrderService orderService =
            node.getService(OrderService.class, "OrderServiceForwardComponent/OrderService");
        assertNotNull(orderService);
        Holder<Float> outParam = new Holder<Float>(new Float(57.4));        
        testOrderReviewApproved(orderService, outParam);
    }
 
    @Test
    public void testOrderReviewRejectedSCA() throws IOException {
        OrderService orderService =
            node.getService(OrderService.class, "OrderServiceComponent/OrderService");
        assertNotNull(orderService);
        Holder<Float> outParam = new Holder<Float>(new Float(57.4));        
        testOrderReviewRejected(orderService, outParam);
    }

    @Test
    public void testOrderReviewRejectedWS() throws IOException {
        OrderService orderService =
            node.getService(OrderService.class, "OrderServiceForwardComponent/OrderService");
        assertNotNull(orderService);
        Holder<Float> outParam = new Holder<Float>(new Float(57.4));        
        testOrderReviewRejected(orderService, outParam);
    }

    @Test
    public void testOrderReviewRandomSCA() throws IOException {
        OrderService orderService =
            node.getService(OrderService.class, "OrderServiceComponent/OrderService");
        assertNotNull(orderService);
        Holder<Float> outParam = new Holder<Float>(new Float(57.4));
        testOrderReviewRandom(orderService, outParam);
    }

    @Test
    public void testOrderReviewRandomWS() throws IOException {
        OrderService orderService =
            node.getService(OrderService.class, "OrderServiceForwardComponent/OrderService");
        assertNotNull(orderService);
        Holder<Float> outParam = new Holder<Float>(new Float(57.4));
        testOrderReviewRandom(orderService, outParam);
    }

    
    
    @Test
    public void testOrderReviewApprovedTwoInOutsSCA() throws IOException {
        OrderService orderService =
            node.getService(OrderService.class, "OrderServiceComponent/OrderService");
        assertNotNull(orderService);
        Holder<Float> outParam = new Holder<Float>(new Float(111));        
        testOrderReviewApproved(orderService, outParam);
    }

    @Test
    public void testOrderReviewApprovedTwoInOutsWS() throws IOException {
        OrderService orderService =
            node.getService(OrderService.class, "OrderServiceForwardComponent/OrderService");
        assertNotNull(orderService);
        Holder<Float> outParam = new Holder<Float>(new Float(111));        
        testOrderReviewApproved(orderService, outParam);
    }
 
    @Test
    public void testOrderReviewRejectedTwoInOutsSCA() throws IOException {
        OrderService orderService =
            node.getService(OrderService.class, "OrderServiceComponent/OrderService");
        assertNotNull(orderService);
        Holder<Float> outParam = new Holder<Float>(new Float(111));        
        testOrderReviewRejected(orderService, outParam);
    }

    @Test
    public void testOrderReviewRejectedTwoInOutsWS() throws IOException {
        OrderService orderService =
            node.getService(OrderService.class, "OrderServiceForwardComponent/OrderService");
        assertNotNull(orderService);
        Holder<Float> outParam = new Holder<Float>(new Float(111));        
        testOrderReviewRejected(orderService, outParam);
    }

    @Test
    public void testOrderReviewRandomTwoInOutsSCA() throws IOException {
        OrderService orderService =
            node.getService(OrderService.class, "OrderServiceComponent/OrderService");
        assertNotNull(orderService);
        Holder<Float> outParam = new Holder<Float>(new Float(111));
        testOrderReviewRandom(orderService, outParam);
    }

    @Test
    public void testOrderReviewRandomTwoInOutsWS() throws IOException {
        OrderService orderService =
            node.getService(OrderService.class, "OrderServiceForwardComponent/OrderService");
        assertNotNull(orderService);
        Holder<Float> outParam = new Holder<Float>(new Float(111));
        testOrderReviewRandom(orderService, outParam);
    }

    @Test
    public void testOrderReviewTwoOutHoldersSCA() throws IOException {
        OrderService orderService =
            node.getService(OrderService.class, "OrderServiceComponent/OrderService");
        assertNotNull(orderService);
        testOrderReviewTwoOutHolders(orderService);
    }

    @Test
    public void testOrderReviewTwoOutHoldersWS() throws IOException {
        OrderService orderService =
            node.getService(OrderService.class, "OrderServiceForwardComponent/OrderService");
        assertNotNull(orderService);
        testOrderReviewTwoOutHolders(orderService);
    }

    @Test
    public void testOrderReviewTwoInOutsThenInSCA() throws IOException {
        OrderService orderService =
            node.getService(OrderService.class, "OrderServiceComponent/OrderService");
        assertNotNull(orderService);
        testOrderReviewOrderTwoInOutsThenIn(orderService);
    }
    
    @Test
    public void testOrderReviewTwoInOutsThenInWS() throws IOException {
        OrderService orderService =
            node.getService(OrderService.class, "OrderServiceForwardComponent/OrderService");
        assertNotNull(orderService);
        testOrderReviewOrderTwoInOutsThenIn(orderService);
    }
    
    private void testOrderReviewApproved(OrderService orderService, Holder<Float> outParam) throws IOException {    
        Order order = new Order();
        order.setStatus( Status.CREATED );
        order.setCustomerId("cust1234");
        order.setTotal( 50.0 );

        System.out.println( ">>> Order submitted=" + order );
        Holder<Order> holder = new Holder<Order>( order );
        String[] returnValue = null;
        if (outParam.value.equals(new Float("111"))) {
        	returnValue = orderService.reviewOrderTwoInOuts( holder, outParam );
        } else {
        	returnValue = orderService.reviewOrder( holder, outParam );
        }
        System.out.println( ">>> Order returned=" + holder.value );
        assertTrue( holder.value.getStatus() == Status.APPROVED );
        assertEquals("retval1", returnValue[0]);
        assertEquals("retval2", returnValue[1]);
        assertTrue(outParam.value.floatValue() == 97);
    }

    private void testOrderReviewRejected(OrderService orderService, Holder<Float> outParam) throws IOException {
        Order order = new Order();
        order.setStatus( Status.CREATED );
        order.setCustomerId("cust2345");
        order.setTotal( 50000.0 );

        System.out.println( ">>> Order submitted=" + order );
        Holder<Order> holder = new Holder<Order>( order );
        String[] returnValue = null;
        if (outParam.value.equals(new Float("111"))) {
        	returnValue = orderService.reviewOrderTwoInOuts( holder, outParam );
        } else {
        	returnValue = orderService.reviewOrder( holder, outParam );
        }
        System.out.println( ">>> Order returned=" + holder.value );
        System.out.println( ">>> return values: " + returnValue[0] + " " + returnValue[1]);
        assertTrue( holder.value.getStatus() == Status.REJECTED );
        assertEquals("retval1", returnValue[0]);
        assertEquals("retval2", returnValue[1]);
        assertTrue(outParam.value.floatValue() == 97);
    }

    private void testOrderReviewRandom(OrderService orderService, Holder<Float> outParam) throws IOException {
        Order order = new Order();
        order.setStatus( Status.CREATED );
        order.setCustomerId("cust3456");
        order.setTotal( 600.0 );
        
        System.out.println( ">>> Order submitted=" + order );
        Holder<Order> holder = new Holder<Order>( order );
        String[] returnValue = null;
        if (outParam.value.equals(new Float("111"))) {
        	returnValue = orderService.reviewOrderTwoInOuts( holder, outParam );
        } else {
        	returnValue = orderService.reviewOrder( holder, outParam );
        }
        System.out.println( ">>> Order returned=" + holder.value );
        assertTrue( holder.value.getStatus() != Status.CREATED );
        assertEquals("retval1", returnValue[0]);
        assertEquals("retval2", returnValue[1]);
        assertTrue(outParam.value.floatValue() == 97);
    }

    private void testOrderReviewTwoOutHolders(OrderService orderService) throws IOException {
        Order order = new Order();
        order.setStatus( Status.CREATED );
        order.setCustomerId("cust3456");
        order.setTotal( 600.0 );
        
        System.out.println( ">>> Order submitted=" + order );
        Holder<Order> holder = new Holder<Order>( order );
        Holder<Float> outParam = new Holder<Float>(new Float(57.4));
        String[] returnValue = orderService.reviewOrderTwoOutHolders( holder, outParam );
        System.out.println( ">>> Order returned=" + holder.value );
        assertTrue( holder.value.getStatus() == Status.REJECTED  );
        assertEquals("retval1", returnValue[0]);
        assertEquals("retval2", returnValue[1]);
        assertTrue(outParam.value.floatValue() == 97);
    }
    
    private void testOrderReviewOrderTwoInOutsThenIn(OrderService orderService) throws IOException {    
        Order order = new Order();
        order.setStatus( Status.CREATED );
        order.setCustomerId("cust1234");
        order.setTotal( 50.0 );

        System.out.println( ">>> Order submitted=" + order );
        Holder<Order> holder = new Holder<Order>( order );
        Holder<Float> outParam = new Holder<Float>( new Float("1820.234"));
        String[] returnValue = null;
        Integer checkForMe = new Integer("23");
        returnValue = orderService.reviewOrderTwoInOutsThenIn( holder, outParam, checkForMe);
        System.out.println( ">>> Order returned=" + holder.value );
        assertTrue( holder.value.getStatus() == Status.APPROVED );
        assertEquals("retval1", returnValue[0]);
        assertEquals("retval2", returnValue[1]);
        assertTrue(outParam.value.floatValue() == 97);
    }
    
    @After
    public void stopServer() throws Exception {
        if (node != null)
            node.stop();
    }

}
