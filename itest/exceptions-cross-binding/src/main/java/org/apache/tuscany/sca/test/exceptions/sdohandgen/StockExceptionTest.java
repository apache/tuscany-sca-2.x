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
/**
 * StockExceptionTest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: #axisVersion# #today#
 */
package org.apache.tuscany.sca.test.exceptions.sdohandgen;

import org.osoa.sca.annotations.Remotable;

import stockexceptiontestservice.scatesttool.StockOffer;

/*
 * StockExceptionTest java interface
 */

@Remotable
public interface StockExceptionTest {

    /**
     * Auto generated method signatures
     * 
     * @param param0
     */
    StockOffer stockQuoteOffer(StockOffer param0)
        throws java.rmi.RemoteException, InvalidSymbolSDOException, MarketClosedSDOException;

}
