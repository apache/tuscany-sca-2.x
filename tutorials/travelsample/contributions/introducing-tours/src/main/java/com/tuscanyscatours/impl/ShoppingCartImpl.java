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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.tuscanyscatours.Checkout;
import com.tuscanyscatours.Updates;

public class ShoppingCartImpl implements Checkout, Updates {
    private static List<String> bookedTrips = new ArrayList<String>();

    protected String currency = "USD";

    public void makePayment(BigDecimal amount, String cardInfo) {
        System.out.print("Charged " + currency + " " + amount
            + " to card "
            + cardInfo
            + " for "
            + (bookedTrips.size() > 1 ? "trips" : "trip"));
        for (String trip : bookedTrips) {
            System.out.print(" " + trip);
        }
        System.out.println();
        bookedTrips.clear();
    }

    public void addTrip(String resCode) {
        bookedTrips.add(resCode);
    }
}
