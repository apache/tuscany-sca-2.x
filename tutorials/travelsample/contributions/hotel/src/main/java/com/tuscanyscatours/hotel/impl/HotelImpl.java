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
package com.tuscanyscatours.hotel.impl;

import java.util.ArrayList;
import java.util.List;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

import com.tuscanyscatours.common.Book;
import com.tuscanyscatours.common.Search;
import com.tuscanyscatours.common.SearchCallback;
import com.tuscanyscatours.common.TripItem;
import com.tuscanyscatours.common.TripLeg;
import com.tuscanyscatours.hotel.HotelInfo;
import com.tuscanyscatours.hotel.HotelManagement;

/**
 * An implementation of the Hotel service
 */
@Scope("STATELESS")
@Service(interfaces = {Search.class, Book.class, HotelManagement.class})
public class HotelImpl implements Search, Book, HotelManagement {

    private List<HotelInfo> hotels = new ArrayList<HotelInfo>();

    @Callback
    protected SearchCallback searchCallback;

    @Init
    public void init() {
        hotels.add(new HotelInfo("Deep Bay Hotel", "Wonderful sea views and a relaxed atmosphere", "FLR", "06/12/09",
                                 "200", 100, "EUR", "http://localhost:8085/tbd"));
        hotels.add(new HotelInfo("Long Bay Hotel", "Friendly staff and an ocean breeze", "FLR", "06/12/09", "200", 100,
                                 "EUR", "http://localhost:8085/tbd"));
        hotels.add(new HotelInfo("City Hotel", "Smart rooms and early breakfasts", "FLR", "06/12/09", "200", 100,
                                 "EUR", "http://localhost:8085/tbd"));
        hotels.add(new HotelInfo("County Hotel", "The smell of the open country", "FLR", "06/12/09", "200", 100, "EUR",
                                 "http://localhost:8085/tbd"));
    }

    public TripItem[] searchSynch(TripLeg tripLeg) {
        List<TripItem> items = new ArrayList<TripItem>();

        // find available hotels
        for (HotelInfo hotel : hotels) {
            if (hotel.getLocation().equals(tripLeg.getToLocation())) {
                TripItem item =
                    new TripItem("", "", TripItem.HOTEL, hotel.getName(), hotel.getDescription(), hotel.getLocation(),
                                 tripLeg.getFromDate(), tripLeg.getToDate(), hotel.getPricePerBed(), hotel
                                     .getCurrency(), hotel.getLink());
                items.add(item);
            }
        }

        return items.toArray(new TripItem[items.size()]);
    }

    public void searchAsynch(TripLeg tripLeg) {
        System.out.println("Starting hotel search");

        // pretend that this processing takes some time to complete
        try {
            Thread.sleep(3000);
        } catch (Exception ex) {
            // do nothing
        }

        // return available hotels
        TripItem[] items = searchSynch(tripLeg);
        searchCallback.searchResults(items);
    }

    public int getPercentComplete() {
        return 100;
    }

    public String book(TripItem tripItem) {
        return "hotel1";
    }

    public void addHotelInfo(HotelInfo hotelInfo) {
        hotels.add(hotelInfo);
        System.out.println("Added hotel info - " + hotelInfo.getName());
    }
}
