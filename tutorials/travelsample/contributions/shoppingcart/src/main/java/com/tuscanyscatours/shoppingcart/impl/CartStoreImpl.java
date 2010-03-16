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

import java.util.ArrayList;
import java.util.List;

import org.osoa.sca.annotations.ConversationID;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

import com.tuscanyscatours.common.TripItem;
import com.tuscanyscatours.shoppingcart.CartStore;

/**
 * An implementation of the CartStore service
 */
@Scope("CONVERSATION")
@Service(interfaces = {CartStore.class})
public class CartStoreImpl implements CartStore {

    @ConversationID
    protected String cartId;

    private List<TripItem> trips = new ArrayList<TripItem>();

    @Init
    public void initCart() {
        System.out.println("CartStore init for id: " + cartId);
    }

    @Destroy
    public void destroyCart() {
        System.out.println("CartStore destroy for id: " + cartId);
    }

    public void addTrip(TripItem trip) {
        trips.add(trip);
    }

    public void removeTrip(TripItem trip) {
        trips.remove(trip);
    }

    public TripItem[] getTrips() {
        return trips.toArray(new TripItem[trips.size()]);
    }

    public void reset() {
        trips.clear();
    }
}
