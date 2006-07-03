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

import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

/**
 * This client program shows how to create an SCA runtime, start it, locate the Eager Init service and invoke it.
 */
public final class EagerInitClient {
    private EagerInitClient() {
    }

    public static void main(String[] args) throws Exception {
        String name = "World";

        CompositeContext compositeContext = CurrentCompositeContext.getContext();

        // Locate the Eager init service
        EagerInitService eagerInitService =
                compositeContext.locateService(EagerInitService.class, "EagerInitComponent");

        // Invoke the HelloWorld service
        String value = eagerInitService.getGreetings(name);

        System.out.println(value);
        System.out.flush();

    }
}