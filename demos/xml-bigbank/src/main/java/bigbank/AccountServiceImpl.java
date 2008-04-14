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

package bigbank;

import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.osoa.sca.ServiceRuntimeException;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * @version $Rev$ $Date$
 */
@Service(AccountService.class)
public class AccountServiceImpl implements AccountService {
    private static final String STOCK_QUOTE_REQUEST =
        "<q:GetQuote xmlns:q=\"http://www.webserviceX.NET/\"><q:symbol>IBM GOOG MSFT</q:symbol></q:GetQuote>";

    private XMLInputFactory factory = XMLInputFactory.newInstance();

    @Reference
    protected ExchangeRate exchangeRate;

    @Reference
    protected StockQuote stockQuote;

    @Reference
    protected AccountData accountData;

    @Reference
    protected StockValue stockValue;

    @Property
    protected String currency;

    public double getTotalValue() {
        try {
            double rate = exchangeRate.getExchangeRate(currency);

            System.out.println("Loading account data...");
            XMLStreamReader accounts = accountData.getAccounts();

            System.out.println("Getting stock quote...");
            XMLStreamReader request = factory.createXMLStreamReader(new StringReader(STOCK_QUOTE_REQUEST));

            OMElement quotes = stockQuote.GetQuote(request);

            String xml = quotes.getText();
            System.out.println(xml);
            XMLStreamReader qts = factory.createXMLStreamReader(new StringReader(xml));
            System.out.println("Calculating total value...");
            double value = stockValue.calculate(qts, accounts);

            System.out.println("Total Value=USD " + value);

            return value * rate;
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

}
