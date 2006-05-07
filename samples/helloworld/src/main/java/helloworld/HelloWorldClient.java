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
package helloworld;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.Properties;

import org.osoa.sca.CurrentModuleContext;
import org.osoa.sca.ModuleContext;

import org.apache.tuscany.core.client.TuscanyRuntime;
import org.apache.tuscany.common.monitor.MonitorFactory;
import org.apache.tuscany.common.monitor.impl.JavaLoggingMonitorFactory;

/**
 * This client program shows how to create an SCA runtime, start it,
 * locate the HelloWorld service and invoke it.
 */
public class HelloWorldClient {

    public static final void main(String[] args) throws Exception {
        
        // Setup Tuscany monitoring to use java.util.logging
        LogManager.getLogManager().readConfiguration(HelloWorldClient.class.getResourceAsStream("/logging.properties"));
        Properties levels = new Properties();
        MonitorFactory monitorFactory = new JavaLoggingMonitorFactory(levels, Level.FINEST, "MonitorMessages");

        // Create a Tuscany runtime for the sample module component
        TuscanyRuntime tuscany = new TuscanyRuntime("HelloWorldModuleComponent", null, monitorFactory);

        // Start the Tuscany runtime and associate it with this thread
        tuscany.start();

        // Get the SCA module context.
        ModuleContext moduleContext = CurrentModuleContext.getContext();

        // Locate the HelloWorld service
        HelloWorldService helloworldService = (HelloWorldService) moduleContext.locateService("HelloWorldServiceComponent");
        
        // Invoke the HelloWorld service
        String value = helloworldService.getGreetings("World");
        
        System.out.println(value);
        System.out.flush();

        // Disassociate the application module component
        tuscany.stop();

        // Shut down the runtime
        tuscany.shutdown();
    }
}
