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
package com.tuscanyscatours.travelcatalog.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.osoa.sca.ComponentContext;
import org.osoa.sca.RequestContext;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

import com.tuscanyscatours.common.Search;
import com.tuscanyscatours.common.SearchCallback;
import com.tuscanyscatours.common.TripItem;
import com.tuscanyscatours.common.TripLeg;
import com.tuscanyscatours.currencyconverter.CurrencyConverter;
import com.tuscanyscatours.travelcatalog.TravelCatalogSearch;

/**
 * An implementation of the travel catalog service
 */
@Scope("COMPOSITE")
@Service(interfaces = {TravelCatalogSearch.class})
public class TravelCatalogImpl implements TravelCatalogSearch, SearchCallback {

    @Reference
    protected Search hotelSearch;

    @Reference
    protected Search flightSearch;

    @Reference
    protected Search carSearch;

    @Reference
    protected Search tripSearch;

    @Property
    public String quoteCurrencyCode = "USD";

    @Reference
    protected CurrencyConverter currencyConverter;

    @Context
    protected ComponentContext componentContext;

    private List<TripItem> searchResults = new ArrayList<TripItem>();

    CountDownLatch resultsReceivedCountdown;

    // TravelSearch methods

    public TripItem[] search(TripLeg tripLeg) {

        resultsReceivedCountdown = new CountDownLatch(4);
        searchResults.clear();

        ServiceReference<Search> dynamicHotelSearch = componentContext.getServiceReference(Search.class, "hotelSearch");

        dynamicHotelSearch.setCallbackID("HotelSearchCallbackID-" + tripLeg.getId());
        dynamicHotelSearch.getService().searchAsynch(tripLeg);

        flightSearch.searchAsynch(tripLeg);
        carSearch.searchAsynch(tripLeg);
        tripSearch.searchAsynch(tripLeg);

        System.out.println("going into wait");

        try {
            resultsReceivedCountdown.await();
        } catch (InterruptedException ex) {
        }

        for (TripItem tripItem : searchResults) {
            tripItem.setId(UUID.randomUUID().toString());
            tripItem.setTripId(tripLeg.getId());
            tripItem
                .setPrice(currencyConverter.convert(tripItem.getCurrency(), quoteCurrencyCode, tripItem.getPrice()));
            tripItem.setCurrency(quoteCurrencyCode);
        }

        return searchResults.toArray(new TripItem[searchResults.size()]);
    }

    // SearchCallback methods

    public synchronized void searchResults(TripItem[] items) {
        RequestContext requestContext = componentContext.getRequestContext();
        Object callbackID = requestContext.getServiceReference().getCallbackID();
        System.out.println("Asynch response - " + callbackID);

        if (items != null) {
            for (int i = 0; i < items.length; i++) {
                searchResults.add(items[i]);
            }
        }

        resultsReceivedCountdown.countDown();
    }

    public void setPercentComplete(String searchComponent, int percentComplete) {
        // Not used at the moment
    }
}
