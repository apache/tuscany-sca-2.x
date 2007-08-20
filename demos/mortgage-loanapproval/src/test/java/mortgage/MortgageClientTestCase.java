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

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * This shows how to test the Calculator service component.
 */
public class MortgageClientTestCase extends TestCase {
    private SCADomain domain;
    private LoanApproval loanApproval;

    @Override
    protected void setUp() throws Exception {
        domain = SCADomain.newInstance("Mortgage.composite");
        loanApproval = domain.getService(LoanApproval.class, "LoanApprovalComponent");
    }

    @Override
    protected void tearDown() throws Exception {
        if (domain != null) {
            domain.close();
        }
    }

    public void testApprove() throws Exception {
        Customer customer = new Customer();
        customer.setSsn("111-22-3333");
        customer.setFirstName("John");
        customer.setLastName("Smith");
        customer.setMonthlyIncome(5000.0d);
        customer.setState("CA");

        boolean approved = loanApproval.approve(customer, 200000d, 30);
        System.out.println((approved ? "Approved: " : "Rejected: ") + customer);
    }
}
