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
package stockquote.demo;

import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * This server program shows how to bootstrap SCA from a simple J2SE program
 * and start it which activates the StockQuote Web service endpoint.
 */
public class StockQuoteServer {

    public static void main(String[] args) throws Exception {

        System.out.println("Starting the Sample SCA StockQuote Service...");
        
        SCADomain domain = SCADomain.newInstance("http://localhost:8080", "/", "StockQuote.composite");

        System.out.println("Press Enter to Exit...");
        System.in.read();

        domain.close();
        System.out.println("Bye");
    }

}
