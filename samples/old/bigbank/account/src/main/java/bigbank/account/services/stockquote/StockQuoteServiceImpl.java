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
package bigbank.account.services.stockquote;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

/* Mock StockQuoteService */

public class StockQuoteServiceImpl implements StockQuoteService {

    static Random rn = new Random();

    static Hashtable<String, StockQuote> stocks = new Hashtable<String, StockQuote>();

    public synchronized Map getQuotes(String[] symbols) throws RemoteException {
        Map<String, StockQuote> ret = new Hashtable<String, StockQuote>();
        for (String sym : symbols) {
            sym = sym.toUpperCase();
            if (!ret.containsKey(sym)) {
                StockQuote sq = stocks.get(sym);
                if (sq == null) {
                    sq = new StockQuote();
                    stocks.put(sym, sq);
                    sq.setSymbol(sym);
                    sq.setCompanyName(sym + " INC");
                    float val = rn.nextFloat() * 97.0F + 3.0F;
                    sq.setStockQuote(val + "");
                    sq.setDayHighPrice(val + "");
                    sq.setDayLowPrice(val + "");
                    sq.setOpenPrice(val + "");
                } else {
                    float diff = (rn.nextFloat() * 2.0F) - 1;
                    float newval = new Float(sq.getStockQuote()) + diff; // auto boxing cool.
                    sq.setStockQuote(newval + "");
                    float dh = Math.max(newval, new Float(sq.getDayHighPrice()));
                    sq.setDayHighPrice(dh + "");
                    float dl = Math.min(newval, new Float(sq.getDayLowPrice()));
                    sq.setDayLowPrice(dl + "");

                }
                ret.put(sym, sq.clone());
            }

        }

        return ret;
    }

}
