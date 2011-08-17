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

package org.apache.tuscany.sca.itest;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import org.oasisopen.sca.annotation.Reference;

public class Service1ClientImpl implements Service1 {

    @Reference
    Service1AsyncClient service1;
    
    static Object threeMutex = new Object();
    static String threeResp;
    static Throwable threeEx;
    
    @Override
    public String operation1(String input) {

        String resp = service1.operation1(input);
        resp += invokeAsyncType1(input);
        resp += invokeAsyncType2(input);
        
        return resp;
    }

    private String invokeAsyncType1(String input) {
        // two
        Response<String> h = service1.operation1Async(input+"TypeOne");
        try {
            return h.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private String invokeAsyncType2(String input) {
        // three
        AsyncHandler<String> ah = new AsyncHandler<String>() {
            @Override
            public void handleResponse(Response<String> res) {
                try {
                    threeResp = res.get();
                } catch (Throwable e) {
                    threeEx = e;
                }
                synchronized (threeMutex) {
                    threeMutex.notifyAll();   
                }
            }
        };
        service1.operation1Async(input+"TypeTwo",ah);
        if (threeResp == null && threeEx == null) {
            synchronized (threeMutex) {
                if (threeResp == null && threeEx == null) {
                    try {
                        threeMutex.wait(3000);
                    } catch (InterruptedException e) {
                    }   
                }
            }
        }

        if (threeResp == null && threeEx == null) {
            throw new RuntimeException("no response recieved");
        }
        if (threeEx != null) {
            throw new RuntimeException("got exception", threeEx);
        }
        return threeResp;
    }
}
