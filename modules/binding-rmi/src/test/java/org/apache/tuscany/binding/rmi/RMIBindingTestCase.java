/**
 *
 * Copyright 2006 The Apache Software Foundation
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
package org.apache.tuscany.binding.rmi;

import junit.framework.TestCase;

public class RMIBindingTestCase extends TestCase {
    
    public void testHost() {
        RMIBindingDefinition binding = new RMIBindingDefinition();
        binding.setHost("foo");
        assertEquals("foo", binding.getHost());
    }

    public void testPort() {
        RMIBindingDefinition binding = new RMIBindingDefinition();
        binding.setPort("foo");
        assertEquals("foo", binding.getPort());
    }

    public void testServiceName() {
        RMIBindingDefinition binding = new RMIBindingDefinition();
        binding.setServiceName("foo");
        assertEquals("foo", binding.getServiceName());
    }
}
