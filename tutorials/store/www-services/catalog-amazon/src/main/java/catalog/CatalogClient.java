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
package catalog;

import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * This client program to invoke the Mortgage LoanApproval service
 */
public class CatalogClient {

    public static void main(String[] args) throws Exception {
        System.err.println("Please make sure you have the ids configured in catalog.composite");
        SCADomain domain = SCADomain.newInstance("catalog.composite");
        CatalogService ebay = domain.getService(CatalogService.class, "EBayCatalogService");
        ebay.get();
        CatalogService amazon = domain.getService(CatalogService.class, "AmazonCatalogService");
        amazon.get();

    }
}
