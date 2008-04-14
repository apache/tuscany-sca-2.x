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
declare namespace q="http://www.webserviceX.NET/";
declare namespace c="http://example.com/customer";
declare namespace stock="scaservice:java/bigbank.StockValue";
(: declare namespace currency="scaproperty:java/java.lang.String"; :)

declare function stock:calculate($quoteDoc, $accountDoc) as xs:double {
    let $checking := 
    trace(
    sum(
        for $a in $accountDoc/c:customer/c:accounts/c:checking
        return $a/@balance
    ), "Checking Balance")
    
    let $saving :=
    trace( 
    sum(
        for $a in $accountDoc/c:customer/c:accounts/c:saving
        return $a/@balance
    ), "Saving Blance")
    
    let $value :=
    trace( 
    sum( 
        for $quote in $quoteDoc/StockQuotes/Stock,
            $account in $accountDoc/c:customer/c:accounts/c:stock
        where string($quote/Symbol) = string($account/@symbol)
        return 
            trace(number($quote/Last),"Stock Price") * trace(number($account/@quantity), "Quantity")
    ), "Stock Value")
    return trace($checking + $saving + $value, "Total Value")
};



