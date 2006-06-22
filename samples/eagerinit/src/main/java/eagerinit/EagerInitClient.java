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
package eagerinit;

import org.osoa.sca.CurrentModuleContext;
import org.osoa.sca.ModuleContext;

/**
 * This client program shows how to create an SCA runtime, start it, locate the Eager Init service and invoke it.
 */
public class EagerInitClient {

    public static final void main(String[] args) throws Exception {
        String name = "";

        name = name.trim();
        if (name.length() == 0)
            name = "World";// nothing specified use "World".

        // Create a Tuscany runtime for the sample module component

        // NOT AVAILABLE TuscanyRuntime tuscany = logging? new TuscanyRuntime("HelloWorldModuleComponent", "http://helloworld", new
        // JavaLoggingMonitorFactory(levels, Level.FINEST, "MonitorMessages") ):
        // NOT AVAILABLE new TuscanyRuntime("HelloWorldModuleComponent", "http://helloworld");

        // Start the Tuscany runtime and associate it with this thread
        // NOT AVAILABLE tuscany.start();

        // Get the SCA module context.
        ModuleContext moduleContext = CurrentModuleContext.getContext();

        // Locate the Eager init service
        EagerInitService eagerInitService = (EagerInitService) moduleContext.locateService("EagerInitComponent");

        // Invoke the HelloWorld service
        String value = eagerInitService.getGreetings(name);

        System.out.println(value);
        System.out.flush();

        // Disassociate the runtime from this thread
        // NOT AVAILABLE tuscany.stop();

        // Shut down the runtime
        // NOT AVAILABLE tuscany.shutdown();
    }
}