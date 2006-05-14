/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.container.rhino.integration;

/**
 * Integration tests for JavaScript components and composite contexts
 * 
 * @version $Rev$ $Date$
 */
public class E4XTestCase extends AbstractJavaScriptTestCase {

    public void testE4X() throws Exception {
        HelloWorld helloworldService = (HelloWorld) moduleContext.locateService("HelloWorldComponentE4X");
        String response = helloworldService.getGreetings("petra");
        assertEquals("e4xHello petra", response);
    }

}
