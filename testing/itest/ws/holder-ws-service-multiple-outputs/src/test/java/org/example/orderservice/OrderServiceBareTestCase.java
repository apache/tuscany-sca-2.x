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
public class OrderServiceBareTestCase {

    private Node node;

    @Before
    public void startServer() throws Exception {
        try {

            NodeFactory factory = NodeFactory.newInstance();
            String contribution = ContributionLocationHelper.getContributionLocation(OrderServiceBare.class);
            node = factory.createNode("ordersca.bare.composite", new Contribution("order.bare", contribution)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
     
    }

    @Test
    public void testOrderReviewBareApprovedSCA() throws IOException {
        OrderServiceBare orderServiceBare =
            node.getService(OrderServiceBare.class, "OrderServiceBareComponent/OrderServiceBare");
        assertNotNull(orderServiceBare);            
        testOrderReviewApproved(orderServiceBare);    
    }

    @Test
    public void testOrderReviewBareApprovedWS() throws IOException {
        OrderServiceBare orderServiceBare =
            node.getService(OrderServiceBare.class, "OrderServiceBareForwardComponent/OrderServiceBare");
        assertNotNull(orderServiceBare);            
        testOrderReviewApproved(orderServiceBare);    
    }

    @Test
    public void testOrderReviewBareRejectedSCA() throws IOException {
        OrderServiceBare orderServiceBare =
            node.getService(OrderServiceBare.class, "OrderServiceBareComponent/OrderServiceBare");
        assertNotNull(orderServiceBare);            
        testOrderReviewRejected(orderServiceBare);    
    }

    @Test
    public void testOrderReviewBareRejectedWS() throws IOException {
        OrderServiceBare orderServiceBare =
            node.getService(OrderServiceBare.class, "OrderServiceBareForwardComponent/OrderServiceBare");
        assertNotNull(orderServiceBare);            
        testOrderReviewRejected(orderServiceBare);
    }

    
    @Test
    public void testOrderReviewInOutBareApprovedSCA() throws IOException {
        OrderServiceBare orderServiceBare =
            node.getService(OrderServiceBare.class, "OrderServiceBareComponent/OrderServiceBare");
        assertNotNull(orderServiceBare);            
        testOrderReviewApprovedInOutHolder(orderServiceBare);    
    }

    @Test
    public void testOrderReviewInOutBareApprovedWS() throws IOException {
        OrderServiceBare orderServiceBare =
            node.getService(OrderServiceBare.class, "OrderServiceBareForwardComponent/OrderServiceBare");
        assertNotNull(orderServiceBare);            
        testOrderReviewApprovedInOutHolder(orderServiceBare);    
    }

    @Test
    public void testOrderReviewInOutBareRejectedSCA() throws IOException {
        OrderServiceBare orderServiceBare =
            node.getService(OrderServiceBare.class, "OrderServiceBareComponent/OrderServiceBare");
        assertNotNull(orderServiceBare);            
        testOrderReviewRejectedInOutHolder(orderServiceBare);    
    }

    @Test
    public void testOrderReviewInOutBareRejectedWS() throws IOException {
        OrderServiceBare orderServiceBare =
            node.getService(OrderServiceBare.class, "OrderServiceBareForwardComponent/OrderServiceBare");
        assertNotNull(orderServiceBare);            
        testOrderReviewRejectedInOutHolder(orderServiceBare);
    }    
    
    @Test
    public void testOrderReviewOutHolderSCA() throws IOException {
        OrderServiceBare orderServiceBare =
            node.getService(OrderServiceBare.class, "OrderServiceBareComponent/OrderServiceBare");
        assertNotNull(orderServiceBare);            
        testOrderReviewOutHolder(orderServiceBare);    
    }

    @Test
    public void testOrderReviewOutHolderWS() throws IOException {
        OrderServiceBare orderServiceBare =
            node.getService(OrderServiceBare.class, "OrderServiceBareForwardComponent/OrderServiceBare");
        assertNotNull(orderServiceBare);            
        testOrderReviewOutHolder(orderServiceBare);
    }

    private void testOrderReviewApproved(OrderServiceBare orderServiceBare) throws IOException {
        Order order = new Order();
        order.setStatus( Status.CREATED );
        order.setCustomerId("cust1234");
        order.setTotal( 50.0 );
        
        Order returnValue = null;
        returnValue = orderServiceBare.bareReviewOrder(order);
        assertTrue( returnValue.getStatus() == Status.APPROVED );   
    }

    private void testOrderReviewRejected(OrderServiceBare orderServiceBare) throws IOException {
        Order order = new Order();
        order.setStatus( Status.CREATED );
        order.setCustomerId("cust1234");
        order.setTotal( 50000.0 );
        
        Order returnValue = null;
        returnValue = orderServiceBare.bareReviewOrder(order);
        assertTrue( returnValue.getStatus() == Status.REJECTED );   
    }

    private void testOrderReviewApprovedInOutHolder(OrderServiceBare orderServiceBare) throws IOException {
        String customerId = "cust1234";
        Order order = new Order();
        order.setStatus( Status.CREATED );
        order.setCustomerId(customerId);
        order.setTotal( 50.0 );
        
        Holder<Order> holder = new Holder<Order>(order);
        orderServiceBare.bareReviewOrderInOutHolder(holder);
        assertTrue( holder.value.getStatus() == Status.APPROVED );
        assertTrue( holder.value.getCustomerId().equals(customerId));
    }

    private void testOrderReviewRejectedInOutHolder(OrderServiceBare orderServiceBare) throws IOException {
        String customerId = "cust1234";
        Order order = new Order();
        order.setStatus( Status.CREATED );
        order.setCustomerId(customerId);
        order.setTotal( 50000.0 );
        
        Holder<Order> holder = new Holder<Order>(order);
        orderServiceBare.bareReviewOrderInOutHolder(holder);    
        assertTrue( holder.value.getStatus() == Status.REJECTED );   
        assertTrue( holder.value.getCustomerId().equals(customerId));
    }

    private void testOrderReviewOutHolder(OrderServiceBare orderServiceBare) throws IOException {
        Order order = new Order();
        order.setStatus( Status.CREATED );
        order.setCustomerId("cust1234");
        order.setTotal( 50.0 );
        Holder<Order> holder = new Holder<Order>(order);
        orderServiceBare.bareReviewOrderOutHolder(holder);
        assertTrue( holder.value.getStatus() == Status.APPROVED );   
        assertTrue( holder.value.getCustomerId().equals("approved.1234"));
    }        

    
    @After
    public void stopServer() throws Exception {
        if (node != null)
            node.stop();
    }

}
