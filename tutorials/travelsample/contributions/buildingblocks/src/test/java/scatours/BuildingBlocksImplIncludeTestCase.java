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

package scatours;

import java.math.BigDecimal;

import com.tuscanyscatours.Bookings;
import com.tuscanyscatours.Checkout;

import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the Building Blocks Composite Implementation Include scenario
 */
public class BuildingBlocksImplIncludeTestCase {

    private Node node;

    @Before
    public void startServer() throws Exception {
        node = NodeFactory.getInstance().createNode("tours-impl-include.composite",
                   new Contribution("introducing-trips", "../introducing-trips/target/classes"),
                   new Contribution("buildingblocks", "./target/classes"));
        node.start();
    }

    @Test
    public void testImplInclude() {
        Bookings bookings = ((Node)node).getService(Bookings.class, "MyTours/BookTrip");
        String bookingCode = bookings.newBooking("FS1APR4", 1);
        System.out.println("Booking code is " + bookingCode);

        Checkout checkout = ((Node)node).getService(Checkout.class, "MyTours/Checkout");
        checkout.makePayment(new BigDecimal("1995.00"), "1234567801234567 11/10");
    }

    @After
    public void stopServer() throws Exception {
        if (node != null) {
            node.stop();
        }
    }
}
