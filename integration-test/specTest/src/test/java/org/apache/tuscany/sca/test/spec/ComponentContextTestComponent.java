/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.sca.test.spec;

import junit.framework.TestCase;
import org.osoa.sca.annotations.Reference;

/**
 * @version $Rev$ $Date$
 */
public class ComponentContextTestComponent extends TestCase {
    @Reference
    public ComponentContextTester tester;

    public void testContextWasInjected() {
        assertTrue(tester.isContextInjected());
    }

    public void testComponentURI() {
        assertEquals("itest://localhost/testDomain/testHarness/ComponentContextTester", tester.getURI());
    }

    public void testGetService() {
        assertEquals("itest://localhost/testDomain/testHarness/ReferencedService",
                     tester.getServiceIdentity("getServiceTest"));
    }

    public void testGetServiceReference() {
        assertEquals("itest://localhost/testDomain/testHarness/ReferencedService",
                     tester.getServiceReferenceIdentity("getServiceTest"));
    }
}
