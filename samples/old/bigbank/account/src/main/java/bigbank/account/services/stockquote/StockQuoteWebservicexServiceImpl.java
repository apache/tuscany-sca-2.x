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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import net.x.webservice.StockQuoteSoap;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * This class implements the Stock quote service component.
 */
@Service(StockQuoteService.class)
@Scope("COMPOSITE")
public class StockQuoteWebservicexServiceImpl implements StockQuoteService {

    @Reference
    public StockQuoteSoap stockQuoteService = null; // Injected by the SCA container.

    /**
     * @throws RemoteException
     * @see bigbank.account.services.stockquote.StockQuoteService#getQuotes(String[])
     */
    public Map getQuotes(final String[] symbols) throws RemoteException {
        try {
            assert null != stockQuoteService : "stockQuoteService was not set by the SCA runtime!";
            StringBuilder sb = new StringBuilder(5 * symbols.length);
            for (String sym : symbols) {
                if (sb.length() != 0) {
                    sb.append(' ');
                }
                sb.append(sym);
            }
            String stockdata = stockQuoteService.GetQuote(sb.toString());

            InputStream in = new ByteArrayInputStream(stockdata.getBytes());
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader parser = factory.createXMLStreamReader(in);
            ArrayList<StockQuote> listQuotes = new ArrayList<StockQuote>();
            Hashtable<String, StockQuote> listQuoteHT = new Hashtable<String, StockQuote>();
            MapStock currentStock = null;
            StringBuilder currentText = new StringBuilder(100);
            for (int event = parser.next(); event != XMLStreamConstants.END_DOCUMENT; event = parser.next()) {
                String lname;
                switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    if (parser.getLocalName() == "Stock") {

                        currentStock = new MapStock();
                        listQuotes.add(currentStock);
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    lname = parser.getLocalName();
                    String mname = "set" + lname;
                    try {
                        Method setter = MapStock.class.getMethod(mname, new Class[] { String.class });
                        if (setter != null) {
                            setter.invoke(currentStock, currentText.toString());
                            if (lname.equals("Symbol")) {
                                listQuoteHT.put(currentText.toString(), currentStock);

                            }
                        }
                    } catch (NoSuchMethodException e) {

                    }
                    // System.err.println(parser.getLocalName() + ":" + currentText.toString());
                    currentText.setLength(0);
                    break;
                case XMLStreamConstants.CHARACTERS:
                    // System.out.print(parser.getText());
                    currentText.append(parser.getText());
                    break;
                case XMLStreamConstants.CDATA:
                    // System.out.print(parser.getText());
                    currentText.append(parser.getText());
                    break;
                } // end switch
            } // end while
            parser.close();

            return listQuoteHT;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException(e.getClass().getName() + e.getMessage(), e);
        }
    }

    public static class MapStock extends StockQuote {

        public void setLast(String val) {
            super.setStockQuote(val);
        }

        public void setDate(String val) {
        };

        public void setTime(String val) {
        };

        public void setHigh(String val) {
            super.setDayHighPrice(val);
        }

        public void setLow(String val) {
            super.setDayLowPrice(val);
        }

        public void setOpen(String val) {
            super.setOpenPrice(val);
        }

        public void setMktCap(String val) {
            super.setMarketCap(val);
        }

        public void setName(String val) {
            super.setCompanyName(val);
        }

    }

}
