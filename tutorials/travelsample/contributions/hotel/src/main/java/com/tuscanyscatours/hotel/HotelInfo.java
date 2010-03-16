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

package com.tuscanyscatours.hotel;

public class HotelInfo {

    private String name;
    private String description;
    private String location;
    private String date;
    private String beds;
    private double pricePerBed;
    private String currency;
    private String link;

    public HotelInfo() {
    }

    public HotelInfo(String name,
                     String description,
                     String location,
                     String date,
                     String beds,
                     double pricePerBed,
                     String currency,
                     String link) {

        this.name = name;
        this.description = description;
        this.location = location;
        this.date = date;
        this.beds = beds;
        this.pricePerBed = pricePerBed;
        this.currency = currency;
        this.link = link;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBeds() {
        return beds;
    }

    public void setBeds(String beds) {
        this.beds = beds;
    }

    public double getPricePerBed() {
        return pricePerBed;
    }

    public void setPricePerBed(double pricePerBed) {
        this.pricePerBed = pricePerBed;
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
}
