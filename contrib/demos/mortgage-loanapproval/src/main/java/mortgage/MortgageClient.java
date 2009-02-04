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
package mortgage;

import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * This client program to invoke the Mortgage LoanApproval service
 */
public class MortgageClient {
    public static void main(String[] args) throws Exception {

        SCADomain domain = SCADomain.newInstance("Mortgage1.composite");
        LoanApproval loanApplication = domain.getService(LoanApproval.class, "LoanApprovalComponent");

        // Create the customer
        Customer customer = new Customer();
        customer.setSsn("111-22-3333");
        customer.setFirstName("John");
        customer.setLastName("Smith");
        customer.setMonthlyIncome(5000.0d);
        customer.setState("CA");

        // Invoke the service
        boolean result = loanApplication.approve(customer, 200000d, 30);
        System.out.println((result ? "Approved: " : "Rejected: ") + customer);
    }
}
