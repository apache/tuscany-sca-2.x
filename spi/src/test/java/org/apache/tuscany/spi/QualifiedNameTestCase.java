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
package org.apache.tuscany.spi;

import junit.framework.TestCase;

/**
 * Tests parsing of naming patters
 *
 * @version $Rev$ $Date$
 */
public class QualifiedNameTestCase extends TestCase {

    public void testSimpleName() throws Exception {
        QualifiedName name = new QualifiedName("Foo");
        assertEquals("Foo", name.getPartName());
        assertEquals(null, name.getPortName());
    }

    public void testCompoundName() throws Exception {
        QualifiedName name = new QualifiedName("Foo/Bar");
        assertEquals("Foo", name.getPartName());
        assertEquals("Bar", name.getPortName());
    }

    public void testCompoundMultiName() throws Exception {
        QualifiedName name = new QualifiedName("Foo/Bar/Baz");
        assertEquals("Foo", name.getPartName());
        assertEquals("Bar/Baz", name.getPortName());
    }

    public void testInvalidName() throws Exception {
        try {
            new QualifiedName("/Foo/Bar");
            fail("Invalid name exception not thrown");
        } catch (InvalidNameException e) {
            //expected
        }
    }

    public void testQualifiedName() throws Exception {
        QualifiedName name = new QualifiedName("Foo/Bar");
        assertEquals("Foo/Bar", name.getQualifiedName());
    }

    public void testToString() throws Exception {
        QualifiedName name = new QualifiedName("Foo/Bar");
        assertEquals("Foo/Bar", name.toString());
    }

}
