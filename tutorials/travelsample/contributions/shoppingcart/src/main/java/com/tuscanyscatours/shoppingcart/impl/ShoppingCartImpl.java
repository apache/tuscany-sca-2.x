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
package com.tuscanyscatours.shoppingcart.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import com.tuscanyscatours.common.TripItem;
import com.tuscanyscatours.payment.Payment;
import com.tuscanyscatours.shoppingcart.CartCheckout;
import com.tuscanyscatours.shoppingcart.CartInitialize;
import com.tuscanyscatours.shoppingcart.CartStore;
import com.tuscanyscatours.shoppingcart.CartUpdates;

/**
 * An implementation of the ShoppingCart service
 */
@Service(interfaces = {CartInitialize.class, CartUpdates.class, CartCheckout.class})
public class ShoppingCartImpl implements CartInitialize, CartUpdates, CartCheckout {

    @Reference
    protected Payment payment;

    @Reference
    protected CartStore cartStore;

    @Context
    protected ComponentContext componentContext;

    private static Map<String, CartStore> cartStores = new HashMap<String, CartStore>();

    public String newCart() {
        String cartId = UUID.randomUUID().toString();
        ServiceReference<CartStore> cartStore = componentContext.getServiceReference(CartStore.class, "cartStore");
        cartStore.setConversationID(cartId);
        cartStores.put(cartId, cartStore.getService());

        return cartId;
    }

    public void addTrip(String cartId, TripItem trip) {
        cartStores.get(cartId).addTrip(trip);
    }

    public void removeTrip(String cartId, TripItem trip) {
        cartStores.get(cartId).addTrip(trip);
    }

    public TripItem[] getTrips(String cartId) {
        return cartStores.get(cartId).getTrips();
    }

    public void checkout(String cartId, String customerName) {
        // get users credentials. Hard coded for now but should
        // come from the security context
        String customerId = customerName;

        // get the total for all the trips
        float amount = (float)0.0;

        TripItem[] trips = getTrips(cartId);

        for (TripItem trip : trips) {
            if (trip.getType().equals(TripItem.TRIP)) {
                amount += trip.getPrice();
            } else {
                for (TripItem tripItem : trip.getTripItems()) {
                    amount += tripItem.getPrice();
                }
            }
        }

        // Take the payment from the customer
        payment.makePaymentMember(customerId, amount);

        // reset the cart store
        cartStores.get(cartId).reset();
        cartStores.remove(cartId);
    }

}
