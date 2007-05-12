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

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.tuscany.databinding.jaxb.fault.InvalidSymbolFault;
import org.apache.tuscany.databinding.jaxb.fault.InvalidSymbolFault_Exception;
import org.apache.tuscany.sca.databinding.jaxb.JAXBDataBinding;
import org.apache.tuscany.sca.databinding.jaxb.JAXBExceptionHandler;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

/**
 * Test case for JAXBExceptionHandler
 */
public class JAXBExceptionHandlerTestCase extends TestCase {
    private static final QName ELEMENT = new QName("http://www.example.com/stock", "InvalidSymbolFault");
    private JAXBExceptionHandler handler;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        this.handler = new JAXBExceptionHandler();
    }

    public void testGetFaultType() {
        DataType exType = new DataTypeImpl<XMLType>(InvalidSymbolFault_Exception.class, XMLType.UNKNOWN);
        DataType<?> dataType = handler.getFaultType(exType);
        assertEquals(InvalidSymbolFault.class, dataType.getPhysical());
        assertEquals(ELEMENT, ((XMLType) dataType.getLogical()).getElementName());
        assertEquals(JAXBDataBinding.NAME, dataType.getDataBinding());
    }

    public void testCreate() {
        DataType execType = new DataTypeImpl<XMLType>(InvalidSymbolFault_Exception.class, XMLType.UNKNOWN);
        DataType<?> faultType = handler.getFaultType(execType);
        InvalidSymbolFault fault = new InvalidSymbolFault();
        fault.setMessage("ABC");
        fault.setSymbol("IBM0");
        DataType<DataType> exType = new DataTypeImpl<DataType>(InvalidSymbolFault_Exception.class, faultType);
        Exception ex = handler.createException(exType, "Invalid symbol", fault, null);
        assertTrue(ex instanceof InvalidSymbolFault_Exception);
        InvalidSymbolFault_Exception exception = (InvalidSymbolFault_Exception)ex;
        assertEquals("Invalid symbol", exception.getMessage());
        assertSame(fault, exception.getFaultInfo());
    }

    public void testGetFaultInfo() {
        InvalidSymbolFault fault = new InvalidSymbolFault();
        fault.setMessage("ABC");
        fault.setSymbol("IBM0");
        InvalidSymbolFault_Exception exception = new InvalidSymbolFault_Exception("Invalid symbol", fault);
        Object faultInfo = handler.getFaultInfo(exception);
        assertSame(fault, faultInfo);
    }

}
