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

import static scatours.launcher.LauncherUtil.locate;

import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCAContribution;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;

import scatours.currencyconverter.CurrencyConverter;

public class CurrencyConverterCORBALauncher {

    public static void main(String[] args) throws Exception {
        SCAContribution currencyCORBAContribution = locate("currency-corba");
        SCAContribution currencyContribution = locate("currency");

        SCANode node =
            SCANodeFactory.newInstance().createSCANode("currency-converter-corba.composite",
                                                       currencyContribution,
                                                       currencyCORBAContribution);
        node.start();

        System.out.println("Quick currency converter test");
        CurrencyConverter converter = ((SCAClient)node).getService(CurrencyConverter.class, "CurrencyConverter");
        System.out.println("USD -> GBP = " + converter.getExchangeRate("USD", "GBP"));
        System.out.println("100 USD = " + converter.convert("USD", "GBP", 100) + "GBP");

        System.out.println("Node started - Press enter to shutdown.");
        System.in.read();
        node.stop();
    }
}
