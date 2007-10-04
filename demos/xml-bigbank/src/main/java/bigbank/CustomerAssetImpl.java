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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.tuscany.sca.databinding.xml.XMLStreamReader2Node;
import org.osoa.sca.ServiceRuntimeException;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;
import org.w3c.dom.Node;

import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;

/**
 * @version $Rev$ $Date$
 */
@Service(CustomerAsset.class)
public class CustomerAssetImpl implements CustomerAsset {
    private XMLInputFactory factory = XMLInputFactory.newInstance();

    @Reference(required = false)
    protected CurrencyExchange exchangeRate;

    @Reference
    protected StockQuote stockQuote;

    @Reference(required = false)
    protected AccountData accountData;

    @Reference
    protected StockValue stockValue;

    public double getTotalValue(String currency) {
        try {
            System.out.println("Retrieving exchange rate...");
            Feed feed = exchangeRate.getRates();
            Entry entry = (Entry)feed.getEntries().get(0);
            String rateTable = entry.getSummary().getValue();
            // System.out.println(rateTable);
            XMLStreamReader rates = factory.createXMLStreamReader(new StringReader(rateTable));
            XMLStreamReader2Node t = new XMLStreamReader2Node();
            Node node = t.transform(rates, null);
            XPath path = XPathFactory.newInstance().newXPath();
            XPathExpression exp = path.compile("/TABLE/TR[TD[1]='" + currency.toUpperCase() + "']/TD[2]");
            Node rateNode = (Node)exp.evaluate(node, XPathConstants.NODE);
            double rate = Double.valueOf(rateNode.getTextContent().trim());
            System.out.println("Exchange rate: USD 1.0=" + currency + " " + rate);

            System.out.println("Loading account data...");
            XMLStreamReader accounts = accountData.getAccounts();

            System.out.println("Getting stock quote...");
            String ticker =
                "<q:GetQuotes xmlns:q=\"http://swanandmokashi.com\"><q:QuoteTicker>IBM,GOOG,MSFT</q:QuoteTicker></q:GetQuotes>";
            XMLStreamReader request = factory.createXMLStreamReader(new StringReader(ticker));

            XMLStreamReader quotes = stockQuote.GetStockQuotes(request);

            System.out.println("Calculating total value...");
            double value = stockValue.calculate(quotes, accounts);

            System.out.println("Total Value=USD " + value);

            return value * rate;
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

}
