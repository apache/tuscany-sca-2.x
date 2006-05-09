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

import org.apache.tuscany.core.client.TuscanyRuntime;
import org.osoa.sca.CurrentModuleContext;
import org.osoa.sca.ModuleContext;

/**
 * This client program shows how to create an SCA runtime, start it,
 * locate the HelloWorld service and invoke it.
 */
public class HelloWorldClient {

    public static final void main(String[] args) throws Exception {
        
        // Create a Tuscany runtime for the sample module component
        TuscanyRuntime tuscany = new TuscanyRuntime("HelloWorldModuleComponent", "http://helloworld");

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

        // Disassociate the runtime from this thread
        tuscany.stop();

        // Shut down the runtime
        tuscany.shutdown();
    }
}
