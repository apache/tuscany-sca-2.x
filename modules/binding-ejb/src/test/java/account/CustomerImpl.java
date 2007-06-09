/**
 * 
 * (C) Copyright IBM Corp. 2004, 2006  All Rights Reserved
 * 
 */
package account;

import org.osoa.sca.ServiceRuntimeException;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

@Service(Customer.class)
public class CustomerImpl implements Customer {

    private BankManagerFacade extEJBService = null;

    public BankManagerFacade getExtEJBService() {
        return extEJBService;
    }

    @Reference
    public void setExtEJBService(BankManagerFacade extEJBService) {
        this.extEJBService = extEJBService;
    }

    // this method invokes external EJB through EJB reference binding
    public Double depositAmount(java.lang.String accountNo, Double amount) {
         
        Double total = null;

        System.out.println("In component implementation. Invoking external EJB through EJB reference binding  ");

        try {
            Double balance = extEJBService.getAccountBalance(accountNo); //invoke external ejb through ejb reference binding 
            total =  balance + amount; 
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
        return total;
    }

}
