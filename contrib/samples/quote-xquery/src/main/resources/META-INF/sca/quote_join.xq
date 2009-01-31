(:
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
:)

declare namespace quoteJoin="scaservice:java/xquery.quote.QuoteJoin";

declare namespace quoteCalculator="scareference:java/xquery.quote.QuoteCalculator";

declare namespace quo="http://www.example.org/quote";

declare variable $quoteCalculator external;

declare function quoteJoin:joinPriceAndAvailQuotes($priceQuoteDoc, $availQuoteDoc, $taxRate) {
<quo:quote>
    <quo:name>{ data($priceQuoteDoc/*/customerName) }</quo:name>
    <quo:address>{ concat($priceQuoteDoc/*/shipAddress/@street , ",", $priceQuoteDoc/*/shipAddress/@city ,",", fn:upper-case($priceQuoteDoc/*/shipAddress/@state) , ",", $priceQuoteDoc/*/shipAddress/@zip) }</quo:address>
    {
        for $priceRequest in $priceQuoteDoc/*/priceRequests/priceRequest,
            $availRequest in $availQuoteDoc/*/availRequest
        where data($priceRequest/widgetId) = data($availRequest/widgetId)
        return
            <quo:quoteResponse>
                <quo:widgetId>{ data($priceRequest/widgetId) }</quo:widgetId>
                <quo:unitPrice>{ data($priceRequest/price) }</quo:unitPrice>
                <quo:requestedQuantity>{ data($availRequest/requestedQuantity) }</quo:requestedQuantity>
                <quo:fillOrder>{ data($availRequest/quantityAvail) }</quo:fillOrder>
                {
                    for $shipDate in $availRequest/shipDate
                    return
                        <quo:shipDate>{ data($shipDate) }</quo:shipDate>
                }
                <quo:taxRate>{ $taxRate }</quo:taxRate>
                <quo:totalCost>{ quoteCalculator:calculateTotalPrice(
                				  $quoteCalculator,
                				  
                				  xs:float($taxRate),

                                  $availRequest/requestedQuantity,

                                  $priceRequest/price,

                                  $availRequest/quantityAvail) }</quo:totalCost>
            </quo:quoteResponse>
    }
    </quo:quote>
};
