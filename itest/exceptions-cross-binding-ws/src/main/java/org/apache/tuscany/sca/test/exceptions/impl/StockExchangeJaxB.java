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

import org.apache.tuscany.sca.test.exceptions.impl.jaxb.InvalidSymbolFault;
import org.apache.tuscany.sca.test.exceptions.impl.jaxb.InvalidSymbolFault_Exception;
import org.apache.tuscany.sca.test.exceptions.impl.jaxb.MarketClosedFault;
import org.apache.tuscany.sca.test.exceptions.impl.jaxb.ObjectFactory;
import org.apache.tuscany.sca.test.exceptions.impl.jaxb.StockExceptionTest;
import org.apache.tuscany.sca.test.exceptions.impl.jaxb.StockOffer;
import org.apache.tuscany.sca.test.exceptions.impl.jaxb.TestNotDeclaredAtSourceFault;
import org.osoa.sca.annotations.Service;

/**
 * JAXB version of StockExceptionTest impl
 */
@Service(StockExceptionTest.class)
public class StockExchangeJaxB implements StockExceptionTest {

    public StockExchangeJaxB() {

    }

    public StockOffer stockQuoteOffer(StockOffer input) throws InvalidSymbolFault_Exception, MarketClosedFault,
        TestNotDeclaredAtSourceFault {

        System.out.println("stockQuoteOffer '" + input + "'");

        String symbol = input.getSymbol();
        if ("IBM".equals(symbol)) {
            input.setPrice(99.00F);
            return input;

        } else if ("CLOSED".equals(input.getName())) {
            throw new MarketClosedFault("TO LATE!", 3);

        } else if ("UNDECLARED_SOURCE".equals(input.getName())) {

            throw new TestNotDeclaredAtSourceFault("not declared", "fault info");

        } else if ("UNDECLARED_TARGET".equals(input.getName())) {
            throw new IllegalArgumentException("System fault");
        }
        ObjectFactory jaxbOjectFactory = new ObjectFactory();

        InvalidSymbolFault faultinfo = jaxbOjectFactory.createInvalidSymbolFault();

        faultinfo.setOffer(input);

        throw new InvalidSymbolFault_Exception("bad symbol", faultinfo);

    }

}
