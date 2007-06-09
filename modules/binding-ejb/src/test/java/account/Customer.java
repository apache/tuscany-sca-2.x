/**
 * 
 * (C) Copyright IBM Corp. 2004, 2006  All Rights Reserved
 * 
 */
package account;

import org.osoa.sca.annotations.Remotable;
import org.osoa.sca.annotations.Service;

@Remotable
@Service
public interface Customer {

    /**
     * This method deposits the amount. method accesses external EJB to get the 
     * current balance and add the amount to existing balance.
     *
     * @param String amount to be deposited
     * @return total amount in customer accound after deposit
     */
    Double depositAmount(java.lang.String accountNo, Double amount);

}
