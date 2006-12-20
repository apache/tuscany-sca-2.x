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
package org.apache.tuscany.core.implementation.java;

import junit.framework.TestCase;


/**
 * Performs rudimentary negative testing by using malformed metadata on a POJO
 *
 * @version $Rev $Date
 */
public class JavaAtomicComponentNegativeMetadataTestCase extends TestCase {

    /**
     * Tests that a pojo with <code>@ComponentName</code> specified on a non-String type generates an error.
     * <p/>
     * <strong>NB:</strong> the test assumes an error with a message containing "@ComponentName" is generated
     */
    public void testBadNameType() throws Exception {
        // TODO implement
    }

    /**
     * Tests that a pojo with <code>@Context</code> specified on a non-CompositeContext type generates an error.
     * <p/>
     * <strong>NB:</strong> the test assumes an error with a message containing "@Context" is generated
     */
    public void testContextType() throws Exception {
        // TODO implement
    }

}
