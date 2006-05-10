package customerinfo;

import commonj.sdo.DataObject;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.XSDHelper;

/**
 * Implementation of the CustomerInfo service.
 */
public class CustomerInfoServiceImpl {

    //FIXME replace this with an @SDOHelper annotation when TUSCANY-179 gets fixed 
    DataFactory dataFactory = DataFactory.INSTANCE;
    
    //FIXME workaround for JIRA TUSCANY-179
    static { 
        XSDHelper.INSTANCE.define(CustomerInfoServiceImpl.class.getClassLoader().getResourceAsStream("wsdl/customer.xsd"), "wsdl/customer.xsd");
    }
    
    public DataObject getCustomerInfo(String customerID) {

        DataObject customer = dataFactory.create("http://customer", "Customer");
        customer.setString("customerID", customerID);
        customer.setString("firstName", "Jane");
        customer.setString("lastName", "Doe");
        return customer;
    }

}
