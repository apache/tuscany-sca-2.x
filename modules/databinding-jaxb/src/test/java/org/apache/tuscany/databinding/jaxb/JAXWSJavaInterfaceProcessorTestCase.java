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

package org.apache.tuscany.databinding.jaxb;

import javax.jws.WebService;

import junit.framework.TestCase;

import org.apache.tuscany.sca.databinding.jaxb.JAXWSJavaInterfaceProcessor;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceImpl;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;

/**
 * 
 */
public class JAXWSJavaInterfaceProcessorTestCase extends TestCase {
    private JAXWSJavaInterfaceProcessor interfaceProcessor;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        interfaceProcessor = new JAXWSJavaInterfaceProcessor();
    }

    /**
     * Test method for
     * {@link org.apache.tuscany.sca.databinding.jaxb.JAXWSJavaInterfaceProcessor#visitInterface(JavaInterface)}.
     */
    public final void testProcessor() throws Exception {
        JavaInterface contract = new MockJavaInterfaceImpl();
        contract.setJavaClass(WebServiceInterfaceWithoutAnnotation.class);
        
        interfaceProcessor.visitInterface(contract);
        assertFalse(contract.isRemotable());
        
        contract.setJavaClass(WebServiceInterfaceWithAnnotation.class);
        interfaceProcessor.visitInterface(contract);
        assertTrue(contract.isRemotable());
    }

    @WebService
    private static interface WebServiceInterfaceWithAnnotation  {
        
    }
    
    private static interface WebServiceInterfaceWithoutAnnotation {
        
    }
    
    private class MockJavaInterfaceImpl extends InterfaceImpl implements JavaInterface {
        private Class<?> javaClass;
        
        public Class<?> getCallbackClass() {
            // TODO Auto-generated method stub
            return null;
        }

        public Class<?> getJavaClass() {
            // TODO Auto-generated method stub
            return javaClass;
        }

        public String getName() {
            // TODO Auto-generated method stub
            return null;
        }

        public void setCallbackClass(Class<?> callbackClass) {
            // TODO Auto-generated method stub
            
        }

        public void setJavaClass(Class<?> javaClass) {
            this.javaClass = javaClass;
        }

        public void setName(String className) {
            // TODO Auto-generated method stub
            
        }
    }
}
