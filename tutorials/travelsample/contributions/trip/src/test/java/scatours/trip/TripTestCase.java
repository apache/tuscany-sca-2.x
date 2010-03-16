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

package scatours.trip;

import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCAContribution;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tuscanyscatours.common.Book;
import com.tuscanyscatours.common.TripItem;

/**
 * 
 */
public class TripTestCase {
    private static SCANode tripNode;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        try {
            tripNode =
                SCANodeFactory.newInstance().createSCANode("trip.composite",
                                                           new SCAContribution("trip", "./target/classes"),
                                                           new SCAContribution("trip-test", "./target/test-classes"));

            tripNode.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testPayment() {
        SCAClient client = (SCAClient)tripNode;
        Book booking = client.getService(Book.class, "Trip/Book");
        TripItem tripItem =
            new TripItem("1234", "5678", TripItem.TRIP, "FS1DEC06", "Florence and Siena pre-packaged tour", "FLR",
                         "06/12/09", "13/12/09", 450, "EUR", "http://localhost:8085/tbd");
        System.out.println("Result = " + booking.book(tripItem) + "\n");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        tripNode.stop();
    }

}
