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
package com.tuscanyscatours.trip.impl;

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

/**
 * An implementation of the Trip service
 */
@Scope("STATELESS")
@Service(interfaces = {Search.class, Book.class})
public class TripImpl implements Search, Book {

    private List<TripInfo> trips = new ArrayList<TripInfo>();

    @Callback
    protected SearchCallback searchCallback;

    @Init
    public void init() {
        trips.add(new TripInfo("FS1DEC06", "Florence and Siena pre-packaged tour", "LGW", "FLR", "06/12/09",
                               "13/12/09", "27", 450, "EUR", "http://localhost:8085/tbd"));
        trips.add(new TripInfo("FS1DEC13", "Florence and Siena pre-packaged tour 2", "LGW", "FLR", "13/12/09",
                               "20/12/09", "27", 550, "EUR", "http://localhost:8085/tbd"));
    }

    public TripItem[] searchSynch(TripLeg tripLeg) {
        List<TripItem> items = new ArrayList<TripItem>();

        // find the pre-package trip
        for (TripInfo trip : trips) {
            if ((trip.getFromLocation().equals(tripLeg.getFromLocation())) && (trip.getToLocation().equals(tripLeg
                .getToLocation()))
                && (trip.getFromDate().equals(tripLeg.getFromDate()))) {
                TripItem item =
                    new TripItem("", "", TripItem.TRIP, trip.getName(), trip.getDescription(),
                                 trip.getFromLocation() + " - " + trip.getToLocation(), trip.getFromDate(), trip
                                     .getToDate(), trip.getPricePerPerson(), trip.getCurrency(), trip.getLink());
                items.add(item);
            }
        }

        return items.toArray(new TripItem[items.size()]);
    }

    public void searchAsynch(TripLeg tripLeg) {
        System.out.println("Starting trip search");

        try {
            Thread.sleep(2000);
        } catch (Exception ex) {
            // do nothing
        }

        // return available hotels
        searchCallback.searchResults(searchSynch(tripLeg));
    }

    public int getPercentComplete() {
        return 100;
    }

    public String book(TripItem tripItem) {
        return "trip1";
    }
}
