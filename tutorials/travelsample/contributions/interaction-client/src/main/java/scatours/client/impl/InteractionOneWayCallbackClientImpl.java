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

package scatours.client.impl;

import java.util.concurrent.CountDownLatch;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

import com.tuscanyscatours.common.Search;
import com.tuscanyscatours.common.SearchCallback;
import com.tuscanyscatours.common.TripItem;
import com.tuscanyscatours.common.TripLeg;

@Scope("COMPOSITE")
@Service(Runnable.class)
public class InteractionOneWayCallbackClientImpl implements Runnable, SearchCallback {

    @Reference
    protected Search hotelSearchOneWayCallback;

    CountDownLatch resultsReceivedCountdown;

    public void run() {
        System.out.println("\nCalling hotel component using both one way and callback interation patterns");
        resultsReceivedCountdown = new CountDownLatch(1);
        TripLeg tripLeg = getTestTripLeg();
        hotelSearchOneWayCallback.searchAsynch(tripLeg);

        // start other searched here while the hotel search progresses

        // wait for responses to come back
        try {
            resultsReceivedCountdown.await();
        } catch (InterruptedException ex) {
        }
    }

    public void searchResults(TripItem[] items) {
        for (TripItem tripItem : items) {
            System.out.println("Found hotel - " + tripItem.getName());
        }
        resultsReceivedCountdown.countDown();
    }

    public void setPercentComplete(String searchComponent, int percentComplete) {
        // Not used in this sample
    }

    private TripLeg getTestTripLeg() {
        TripLeg tripLeg = new TripLeg();
        tripLeg.setFromLocation("LGW");
        tripLeg.setToLocation("FLR");
        tripLeg.setFromDate("06/12/09 00:00");
        tripLeg.setToDate("13/12/09 00:00");
        tripLeg.setNoOfPeople("1");
        tripLeg.setId("TRIP27");
        return tripLeg;
    }
}
