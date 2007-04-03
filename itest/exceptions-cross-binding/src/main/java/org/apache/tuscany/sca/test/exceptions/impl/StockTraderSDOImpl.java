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

package org.apache.tuscany.sca.test.exceptions.impl;

import java.rmi.RemoteException;

import org.apache.tuscany.sca.test.exceptions.sdohandgen.InvalidSymbolSDOException;
import org.apache.tuscany.sca.test.exceptions.sdohandgen.MarketClosedSDOException;
import org.apache.tuscany.sca.test.exceptions.sdohandgen.StockExceptionTest;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import stockexceptiontestservice.scatesttool.ScatesttoolFactory;
import stockexceptiontestservice.scatesttool.StockOffer;

/**
 * 
 */
@Service(StockTraderSDO.class)
public class StockTraderSDOImpl implements StockTraderSDO {

    private StockExceptionTest exchangeJaxb;

    /**
     * 
     */

    public StockTraderSDOImpl() {
        // TODO Auto-generated constructor stub
    }

    @Reference
    public void setExchangeJaxb(StockExceptionTest exchangeJaxb) {
        this.exchangeJaxb = exchangeJaxb;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tuscany.sca.test.exceptions.impl.StockTraderSDO#tradingTest()
     */
    public StockOffer testTrading() throws RemoteException, InvalidSymbolSDOException, MarketClosedSDOException {
        StockOffer stockOffer = ScatesttoolFactory.INSTANCE.createStockOffer();
        stockOffer.setName("IBM");
        stockOffer.setSymbol("IBM");
        stockOffer.setPrice(100.00F); // offer to buy at max $100.00

        StockOffer stockOfferAccepted = exchangeJaxb.stockQuoteOffer(stockOffer);

        return stockOfferAccepted;

    }

    public void testInvalidSymbolSDOException() throws RemoteException, InvalidSymbolSDOException,
        MarketClosedSDOException {
        StockOffer stockOffer = ScatesttoolFactory.INSTANCE.createStockOffer();
        // set up for a InvalidSymbolSDOException
        stockOffer.setName("");
        stockOffer.setSymbol("IBM0");

        stockOffer.setPrice(11.0F); // offer to buy at max $100.00
        exchangeJaxb.stockQuoteOffer(stockOffer);

    }

    public void marketClosedSDOExceptionTest() {
        StockOffer stockOffer = ScatesttoolFactory.INSTANCE.createStockOffer();

        // set up for a MarketClosedSDOException
        stockOffer.setName("CLOSED");
        stockOffer.setSymbol("MBI");
        stockOffer.setPrice(Float.NaN); // offer to buy at max $100.00
        try {
            StockOffer stockOfferAccepted = exchangeJaxb.stockQuoteOffer(stockOffer);
            stockOfferAccepted.getPrice(); // the price actually bought.
        } catch (RemoteException e) {

            e.printStackTrace();
        } catch (InvalidSymbolSDOException e) {

            e.printStackTrace();
        } catch (MarketClosedSDOException e) {

            e.printStackTrace();
        }
    }

    public Object  testNotDeclaredAtSourceTest() {
        StockOffer stockOffer = ScatesttoolFactory.INSTANCE.createStockOffer();

        // set up for a MarketClosedSDOException
        stockOffer.setName("testNotDeclaredAtSourceTest");
        stockOffer.setSymbol("TNDAS");
        stockOffer.setPrice(Float.NaN); // offer to buy at max $100.00
        try {
            return  exchangeJaxb.stockQuoteOffer(stockOffer);
            
        } catch (Exception e) {
            return e;
           
        }

    }

}
