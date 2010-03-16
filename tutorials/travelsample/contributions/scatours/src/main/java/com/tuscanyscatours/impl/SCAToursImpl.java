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
package com.tuscanyscatours.impl;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

import com.tuscanyscatours.SCAToursBooking;
import com.tuscanyscatours.SCAToursCart;
import com.tuscanyscatours.SCAToursSearch;
import com.tuscanyscatours.common.TripItem;
import com.tuscanyscatours.common.TripLeg;
import com.tuscanyscatours.shoppingcart.CartCheckout;
import com.tuscanyscatours.shoppingcart.CartInitialize;
import com.tuscanyscatours.travelcatalog.TravelCatalogSearch;
import com.tuscanyscatours.tripbooking.TripBooking;

/**
 * An implementation of the SCA tours component. it's just a pass through and allows
 * the outward facing bindings to be changed without changing the individual contributions
 */
@Scope("COMPOSITE")
@Service(interfaces = {SCAToursSearch.class, SCAToursBooking.class, SCAToursCart.class})
public class SCAToursImpl implements SCAToursSearch, SCAToursBooking, SCAToursCart {

    @Reference
    protected TravelCatalogSearch travelCatalogSearch;

    @Reference
    protected TripBooking tripBooking;

    @Reference
    protected CartInitialize cartInitialize;

    @Reference
    protected CartCheckout cartCheckout;

    // SCAToursSearch methods

    public TripItem[] search(TripLeg tripLeg) {
        return travelCatalogSearch.search(tripLeg);
    }

    // SCAToursBooking methods

    public String bookTrip(String cartId, TripItem trip) {
        TripItem bookedTrip = tripBooking.bookTrip(cartId, trip);
        return bookedTrip.getBookingCode();
    }

    // SCAToursCart methods

    public String newCart() {
        String cartId = cartInitialize.newCart();
        return cartId;
    }

    public TripItem[] getTrips(String cartId) {
        return cartInitialize.getTrips(cartId);
    }

    public void checkout(String cartId) {
        // need to get the user id from the context here but
        // just make one up for the time being
        cartCheckout.checkout(cartId, "c-0");
    }
}
