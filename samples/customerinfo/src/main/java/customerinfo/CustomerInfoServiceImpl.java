package customerinfo;

import org.apache.tuscany.core.sdo.helper.SDOHelper;

import commonj.sdo.DataObject;
import commonj.sdo.helper.DataFactory;

/**
 * Implementation of the CustomerInfo service.
 */
public class CustomerInfoServiceImpl {

    @SDOHelper
    public DataFactory dataFactory;
    
    public DataObject getCustomerInfo(String customerID) {

        DataObject customer = dataFactory.create("http://customer", "Customer");
        customer.setString("customerID", customerID);
        customer.setString("firstName", "Jane");
        customer.setString("lastName", "Doe");
        return customer;
    }

}
