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

package com.tuscanyscatours.common;

public class TripItem {

    public static String FLIGHT = "Flight";
    public static String HOTEL = "Hotel";
    public static String CAR = "Car";
    public static String TRIP = "Trip";

    private String id;
    private String tripId;
    private String type;
    private String name;
    private String description;
    private String location;
    private String fromDate;
    private String toDate;
    private double price;
    private String currency;
    private String link;
    private TripItem[] tripItems; // used for a trip made up of trip items
    private String customerDetails;
    private String agentDetails;
    private String bookingCode;

    public TripItem() {
    }

    public TripItem(TripItem item) {
        this.id = item.getId();
        this.tripId = item.getTripId();
        this.type = item.getType();
        this.name = item.getName();
        this.description = item.getDescription();
        this.location = item.getLocation();
        this.fromDate = item.getFromDate();
        this.toDate = item.getToDate();
        this.price = item.getPrice();
        this.currency = item.getCurrency();
        this.link = item.getLink();
    }

    public TripItem(String id,
                    String tripId,
                    String type,
                    String name,
                    String description,
                    String location,
                    String fromDate,
                    String toDate,
                    double price,
                    String currency,
                    String link) {
        this.id = id;
        this.tripId = tripId;
        this.type = type;
        this.name = name;
        this.description = description;
        this.location = location;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.price = price;
        this.currency = currency;
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public TripItem[] getTripItems() {
        return tripItems;
    }

    public void setTripItems(TripItem[] tripItems) {
        this.tripItems = tripItems;
    }

    public String getCustomerDetails() {
        return customerDetails;
    }

    public void setCustomerDetails(String customerDetails) {
        this.customerDetails = customerDetails;
    }

    public String getAgentDetails() {
        return agentDetails;
    }

    public void setAgentDetails(String agentDetails) {
        this.agentDetails = agentDetails;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    @Override
    public String toString() {
        String tripString =
            "Trip: id " + this.tripId
                + " type "
                + this.type
                + " name "
                + this.name
                + " description "
                + this.description
                + " location "
                + this.location
                + " fromDate "
                + this.fromDate
                + " toDate "
                + this.toDate
                + " price "
                + this.price
                + " currency "
                + this.currency
                + " link "
                + this.link;
        return tripString;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof TripItem) {
            if (((TripItem)obj).getId().equals(getId())) {
                return true;
            }
        }

        return super.equals(obj);
    }
}
