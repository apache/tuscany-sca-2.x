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
package xquery.quote;

import java.io.IOException;
import java.math.BigInteger;


import org.example.avail.AvailFactory;
import org.example.avail.AvailQuote;
import org.example.avail.AvailRequest;
import org.example.price.PriceFactory;
import org.example.price.PriceQuote;
import org.example.price.PriceRequest;
import org.example.price.PriceRequests;
import org.example.price.ShipAddress;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLHelper;

public class QuoteDataUtil {

    public static AvailQuote buildAvailQuoteData() {
        AvailQuote availQuote = AvailFactory.INSTANCE.createAvailQuote();
        AvailRequest availRequest = AvailFactory.INSTANCE.createAvailRequest();
        availRequest.setWidgetId(BigInteger.valueOf(12));
        availRequest.setRequestedQuantity(10);
        availRequest.setQuantityAvail(true);
        availRequest.setShipDate("2003-03-22");
        availQuote.getAvailRequest().add(availRequest);

        availRequest = AvailFactory.INSTANCE.createAvailRequest();
        availRequest.setWidgetId(BigInteger.valueOf(134));
        availRequest.setRequestedQuantity(345);
        availRequest.setQuantityAvail(false);
        availRequest.setShipDate("BackOrder");
        availQuote.getAvailRequest().add(availRequest);

        availRequest = AvailFactory.INSTANCE.createAvailRequest();
        availRequest.setWidgetId(BigInteger.valueOf(211));
        availRequest.setRequestedQuantity(100);
        availRequest.setQuantityAvail(true);
        availRequest.setShipDate("2003-04-21");
        availQuote.getAvailRequest().add(availRequest);

        return availQuote;
    }

    public static PriceQuote buildPriceQuoteData() {
        PriceQuote priceQuote = PriceFactory.INSTANCE.createPriceQuote();
        priceQuote.setCustomerName("Acme Inc");

        ShipAddress shipAddress = PriceFactory.INSTANCE.createShipAddress();
        shipAddress.setStreet("12 Springs Rd");
        shipAddress.setCity("Morris Plains");
        shipAddress.setState("nj");
        shipAddress.setZip("07960");
        priceQuote.setShipAddress(shipAddress);

        PriceRequests priceRequests = PriceFactory.INSTANCE.createPriceRequests();
        PriceRequest priceRequest = PriceFactory.INSTANCE.createPriceRequest();
        priceRequest.setWidgetId(BigInteger.valueOf(12));
        priceRequest.setPrice(1.00f);
        priceRequests.getPriceRequest().add(priceRequest);

        priceRequest = PriceFactory.INSTANCE.createPriceRequest();
        priceRequest.setWidgetId(BigInteger.valueOf(134));
        priceRequest.setPrice(34.10f);
        priceRequests.getPriceRequest().add(priceRequest);

        priceRequest = PriceFactory.INSTANCE.createPriceRequest();
        priceRequest.setWidgetId(BigInteger.valueOf(211));
        priceRequest.setPrice(10.00f);
        priceRequests.getPriceRequest().add(priceRequest);

        priceQuote.setPriceRequests(priceRequests);

        return priceQuote;
    }

    public static void serializeToSystemOut(DataObject object, String name) {
        XMLHelper helper = XMLHelper.INSTANCE;

        try {
            helper.save(object, null, name, System.out);
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
