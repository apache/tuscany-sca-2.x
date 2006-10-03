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
package org.apache.tuscany.core.implementation.processor;

import org.apache.tuscany.spi.implementation.java.ImplementationProcessorService;

import junit.framework.TestCase;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;

/**
 * @version $Rev$ $Date$
 */
public class ImplementationProcessorServiceUniqueTestCase extends TestCase {

    private ImplementationProcessorService service =
        new ImplementationProcessorServiceImpl(new JavaInterfaceProcessorRegistryImpl());

    public void testUniquess1() throws Exception {
        Class[] classes = new Class[2];
        classes[0] = String.class;
        classes[1] = Integer.class;
        assertTrue(service.areUnique(classes));
    }

    public void testUniquess2() throws Exception {
        Class[] classes = new Class[2];
        classes[0] = String.class;
        classes[1] = String.class;
        assertFalse(service.areUnique(classes));
    }

    public void testUniquess3() throws Exception {
        Class[] classes = new Class[1];
        classes[0] = String.class;
        assertTrue(service.areUnique(classes));
    }

    public void testUniquess4() throws Exception {
        Class[] classes = new Class[3];
        classes[0] = String.class;
        classes[1] = Integer.class;
        classes[2] = String.class;
        assertFalse(service.areUnique(classes));
    }

    public void testUniquess5() throws Exception {
        Class[] classes = new Class[0];
        assertTrue(service.areUnique(classes));
    }

}
