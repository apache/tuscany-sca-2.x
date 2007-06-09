package org.apache.tuscany.sca.binding.ejb.tests;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;

import account.Customer;

/**
 * This shows how to test the Calculator service component.
 */
public class EJBReferenceTestCase extends TestCase {

    private SCADomain scaDomain;

    protected void setUp() throws Exception {
        scaDomain = SCADomain.newInstance("account/account.composite");
    }

    protected void tearDown() throws Exception {
        scaDomain.close();
    }

    public void testCalculator() throws Exception {
        Customer customer = scaDomain.getService(Customer.class, "CustomerComponent");
        String accountNo = "1234567890"; // This is one of the customer numbers in bank application running on
                                            // Geronimo
        Double balance = customer.depositAmount(accountNo, new Double(100));
        System.out.println("Balance amount for account " + accountNo + " is $" + balance);
    }
}
