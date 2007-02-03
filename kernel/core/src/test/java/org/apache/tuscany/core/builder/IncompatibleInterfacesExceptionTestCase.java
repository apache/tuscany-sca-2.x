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
package org.apache.tuscany.core.builder;

import java.net.URI;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class IncompatibleInterfacesExceptionTestCase extends TestCase {

    public void testInstantiation() throws Exception {
        URI sourceUri = URI.create("source");
        URI targetUri = URI.create("target");
        IncompatibleInterfacesException e = new IncompatibleInterfacesException(sourceUri, targetUri);
        assertNotNull(e.getMessage());
        assertEquals("source", e.getSourceUri().toString());
        assertEquals("target", e.getTargetUri().toString());
    }
}
