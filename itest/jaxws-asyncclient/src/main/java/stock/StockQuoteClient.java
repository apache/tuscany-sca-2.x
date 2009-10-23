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

package stock;

import java.util.concurrent.ExecutionException;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import org.oasisopen.sca.annotation.Reference;

public class StockQuoteClient {

    @Reference public StockQuoteRef stockQuote;

    public float getPrice(String ticker) {
        return stockQuote.getPrice(ticker);
    }

    public float getPriceAsyncPoll(String ticker) throws InterruptedException, ExecutionException {
        Response<Float> response = stockQuote.getPriceAsync("foo");
        return response.get();
    }

    float price = 0f;
    Object mutex = new Object();
    Exception exception;

//    public float getPriceAsyncCallback(String ticker) throws Exception {
//        AsyncHandler<Float> callback = new AsyncHandler<Float>() {
//            public void handleResponse(Response<Float> arg) {
//                synchronized (mutex) {
//                    try {
//                        price = arg.get();
//                    } catch (Exception e) {
//                        exception = e;
//                    }
//                    mutex.notify();
//                }
//            }
//        };
//        stockQuote.getPriceAsync("foo", callback);
//        synchronized (mutex) {
//            if (price == 0f)
//                wait(5000); // wait for up to 5 seconds
//        }
//
//        if (exception != null) throw exception;
//        return price;
//    }

}
