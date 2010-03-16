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

package scatours.impl;

import java.math.BigDecimal;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import scatours.Bookings;
import scatours.Checkout;

@Service(Runnable.class)
public class ApplClientImpl {
    @Reference
    protected Bookings bookings1, bookings2;

    @Reference
    protected Checkout checkout1, checkout2;

    public ApplClientImpl() {
    }

    public void run() {
        String bookingCode = bookings1.newBooking("FS1APR4", 1);
        System.out.println("Booking code is " + bookingCode);

        checkout1.makePayment(new BigDecimal("1995.00"), "1234567843218765 10/10");

        bookingCode = bookings2.newBooking("AC3MAY9", 2);
        System.out.println("Booking code is " + bookingCode);

        checkout2.makePayment(new BigDecimal("2295.00"), "9876123456784321 11/11");
    }
}
