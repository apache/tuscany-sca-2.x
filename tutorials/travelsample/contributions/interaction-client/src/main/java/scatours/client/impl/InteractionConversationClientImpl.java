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

package scatours.client.impl;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

import com.tuscanyscatours.common.TripItem;
import com.tuscanyscatours.shoppingcart.CartStore;

@Scope("COMPOSITE")
@Service(Runnable.class)
public class InteractionConversationClientImpl implements Runnable {

    @Reference
    protected CartStore cartStoreConversation;

    public void run() {
        System.out.println("\nCalling cart store using the conversational interaction pattern");

        // add some trip items to the cart store
        TripItem tripItem = getTestTripItem();
        cartStoreConversation.addTrip(tripItem);

        tripItem.setDescription("2nd trip item");
        cartStoreConversation.addTrip(tripItem);

        tripItem.setDescription("3rd trip item");
        cartStoreConversation.addTrip(tripItem);

        System.out.println("Trip items now in cart");
        TripItem[] tripItems = cartStoreConversation.getTrips();
        for (TripItem item : tripItems) {
            System.out.println("Item - " + item.getDescription());
        }

        System.out.println("Reset the cart");
        cartStoreConversation.reset();

        System.out.println("Trip items now in cart");
        tripItems = cartStoreConversation.getTrips();
        for (TripItem item : tripItems) {
            System.out.println("Item - " + item.getDescription());
        }
    }

    private TripItem getTestTripItem() {
        TripItem tripItem = new TripItem();
        tripItem.setLocation("FLR");
        tripItem.setFromDate("06/12/09 00:00");
        tripItem.setToDate("13/12/09 00:00");
        tripItem.setDescription("1st trip item");
        return tripItem;
    }
}
