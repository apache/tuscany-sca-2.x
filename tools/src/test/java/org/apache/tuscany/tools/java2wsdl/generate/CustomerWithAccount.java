package org.apache.tuscany.tools.java2wsdl.generate;

/**
 * 
 * @author jjojo
 */
public class CustomerWithAccount {

	private Customer customer = new Customer();

	private Account[] accounts;

	public int getValue(String custId, String stockSymbol) {
		return 123;
	}

	public Customer getCustomerDetails(String custId) {
		return customer;
	}

	public Account getCustomerAccount(String custId, String accountId) {
		return accounts[0];
	}

	public Account[] getAccounts() {
		return accounts;
	}

	public void setAccounts(Account[] accounts) {
		this.accounts = accounts;
	}
}
