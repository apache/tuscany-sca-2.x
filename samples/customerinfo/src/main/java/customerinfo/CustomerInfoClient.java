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
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;

import org.apache.tuscany.common.monitor.MonitorFactory;
import org.apache.tuscany.common.monitor.impl.JavaLoggingMonitorFactory;
import org.apache.tuscany.core.client.TuscanyRuntime;
import org.osoa.sca.CurrentModuleContext;
import org.osoa.sca.ModuleContext;

import commonj.sdo.DataObject;

/**
 * This client program shows how to create an SCA runtime, start it,
 * locate the CustomerInfo service and invoke it.
 */
public class CustomerInfoClient {

    public static final void main(String[] args) throws Exception {
        
        // Setup Tuscany monitoring to use java.util.logging
        LogManager.getLogManager().readConfiguration(CustomerInfoClient.class.getResourceAsStream("/logging.properties"));
        Properties levels = new Properties();
        MonitorFactory monitorFactory = new JavaLoggingMonitorFactory(levels, Level.FINEST, "MonitorMessages");

        // Create a Tuscany runtime for the sample module component
        TuscanyRuntime tuscany = new TuscanyRuntime("CustomerInfoModuleComponent", "http://customerinfo", monitorFactory);

        // Start the Tuscany runtime and associate it with this thread
        tuscany.start();

        // Get the SCA module context.
        ModuleContext moduleContext = CurrentModuleContext.getContext();

        // Locate the CustomerInfo service
        Object customerInfoService = moduleContext.locateService("CustomerInfoServiceComponent");
        
        // Invoke the CustomerInfo service
        Method getCustomerInfo = customerInfoService.getClass().getMethod("getCustomerInfo", new Class[] {String.class});
        DataObject customer = (DataObject)getCustomerInfo.invoke(customerInfoService, "12345");
        
        System.out.println("customerID = " + customer.getString("customerID"));
        System.out.println("firstName = " + customer.getString("firstName"));
        System.out.println("lastName = " + customer.getString("lastName"));
        System.out.flush();

        // Disassociate the runtime from this thread
        tuscany.stop();

        // Shut down the runtime
        tuscany.shutdown();
    }
}
