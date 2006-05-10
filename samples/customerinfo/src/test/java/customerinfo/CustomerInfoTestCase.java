/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package customerinfo;

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.apache.tuscany.core.client.TuscanyRuntime;
import org.osoa.sca.CurrentModuleContext;
import org.osoa.sca.ModuleContext;

import commonj.sdo.DataObject;

/**
 * This shows how to test the CustomerInfo service component.
 */
public class CustomerInfoTestCase extends TestCase {
    
    private TuscanyRuntime tuscany;
    
    protected void setUp() throws Exception {
        super.setUp();
        
        // Create a Tuscany runtime for the sample module component
        tuscany = new TuscanyRuntime("CustomerInfoModuleComponent", "http://customerinfo");

        // Start the Tuscany runtime and associate it with this thread
        tuscany.start();
    }
    
    public void testCustomerInfo() throws Exception {

        // Get the SCA module context.
        ModuleContext moduleContext = CurrentModuleContext.getContext();

        // Locate the CustomerInfo service
        Object customerInfoService = moduleContext.locateService("CustomerInfoServiceComponent");
        
        // Invoke the CustomerInfo service
        Method getCustomerInfo = customerInfoService.getClass().getMethod("getCustomerInfo", new Class[] {String.class});
        DataObject customer = (DataObject)getCustomerInfo.invoke(customerInfoService, "12345");
        
        assertEquals("12345", customer.getString("customerID"));
        assertEquals("Jane", customer.getString("firstName"));
        assertEquals("Doe", customer.getString("lastName"));
        
    }
    
    protected void tearDown() throws Exception {
        
        // Stop the Tuscany runtime
        tuscany.stop();
        
        // Shutdown the runtime
        tuscany.shutdown();
        
        super.tearDown();
    }
}
