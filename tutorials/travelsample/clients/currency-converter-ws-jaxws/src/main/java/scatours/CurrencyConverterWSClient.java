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
package scatours;

import scatours.currencyconverter.client.CurrencyConverter;
import scatours.currencyconverter.client.CurrencyConverterService;

public class CurrencyConverterWSClient {

    public static void main(String[] args) throws Exception {
        CurrencyConverterService service = new CurrencyConverterService();
        CurrencyConverter converter = service.getPort(CurrencyConverter.class);

        System.out.println("USD -> GBP = " + converter.getExchangeRate("USD", "GBP"));
        System.out.println("100 USD = " + converter.convert("USD", "GBP", 100.0) + "GBP");
    }
}
