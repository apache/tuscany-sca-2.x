package customerinfo;

import org.apache.tuscany.databinding.sdo.helper.SDOHelper;

import commonj.sdo.DataObject;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.XSDHelper;

/**
 * Implementation of the CustomerInfo service.
 */
public class CustomerInfoServiceImpl {

    @SDOHelper
    DataFactory dataFactory;
    
    /**
     * Constructs a new CustomerInfoServiceImpl.
     */
    public CustomerInfoServiceImpl() {
        //FIXME workaround for JIRA TUSCANY-179
        if (dataFactory == null) {
            dataFactory = DataFactory.INSTANCE;
            XSDHelper.INSTANCE.define(CustomerInfoServiceImpl.class.getClassLoader().getResourceAsStream("wsdl/customer.xsd"), "wsdl/customer.xsd");
        }
    }
    
    public DataObject getCustomerInfo(String customerID) {

        DataObject customer = dataFactory.create("http://customer", "Customer");
        customer.setString("customerID", customerID);
        customer.setString("firstName", "Jane");
        customer.setString("lastName", "Doe");
        return customer;
    }

}
