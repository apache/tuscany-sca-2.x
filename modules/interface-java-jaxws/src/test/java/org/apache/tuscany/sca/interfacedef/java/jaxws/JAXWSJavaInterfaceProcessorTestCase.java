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

package org.apache.tuscany.sca.interfacedef.java.jaxws;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.DefaultDataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.jaxb.DefaultXMLAdapterExtensionPoint;
import org.apache.tuscany.sca.databinding.jaxb.XMLAdapterExtensionPoint;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;

import com.example.stock.StockExceptionTest;

/**
 *
 * @version $Rev$ $Date$
 */
public class JAXWSJavaInterfaceProcessorTestCase extends TestCase {
    private JAXWSJavaInterfaceProcessor interfaceProcessor;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        DataBindingExtensionPoint db = new DefaultDataBindingExtensionPoint();
        XMLAdapterExtensionPoint xa = new DefaultXMLAdapterExtensionPoint();
        interfaceProcessor = new JAXWSJavaInterfaceProcessor(db, new JAXWSFaultExceptionMapper(db, xa), xa);
    }

    public void testWrapper() throws Exception {
        DefaultJavaInterfaceFactory iFactory = new DefaultJavaInterfaceFactory();
        JavaInterface contract = iFactory.createJavaInterface(StockExceptionTest.class);

        interfaceProcessor.visitInterface(contract);
        Operation op = contract.getOperations().get(0);
        Assert.assertTrue(!op.isWrapperStyle());
        Assert.assertEquals(new QName("http://www.example.com/stock", "stockQuoteOffer"), op.getWrapper().getInputWrapperElement().getQName());
        Assert.assertEquals(new QName("http://www.example.com/stock", "stockQuoteOfferResponse"), op.getWrapper().getOutputWrapperElement().getQName());
    }

    /**
     * Test method for
     * {@link org.apache.tuscany.sca.interfacedef.java.jaxws.JAXWSJavaInterfaceProcessor#visitInterface(JavaInterface)}.
     */
    public final void testProcessor() throws Exception {
        DefaultJavaInterfaceFactory iFactory = new DefaultJavaInterfaceFactory();
        JavaInterface contract = iFactory.createJavaInterface(WebServiceInterfaceWithoutAnnotation.class);

        interfaceProcessor.visitInterface(contract);
        assertFalse(contract.isRemotable());

        contract = iFactory.createJavaInterface(WebServiceInterfaceWithAnnotation.class);
        interfaceProcessor.visitInterface(contract);
        assertTrue(contract.isRemotable());

        Operation op1 = contract.getOperations().get(0);
        Operation op2 = contract.getOperations().get(1);

        Operation op = null;
        if ("m1".equals(op1.getName())) {
            op = op1;
        } else {
            op = op2;
        }

        assertTrue(op.isWrapperStyle());

        if ("M2".equals(op2.getName())) {
            op = op2;
        } else {
            op = op1;
        }
        assertTrue(!op.isWrapperStyle() && op.getWrapper() != null);

    }

    @WebService
    private static interface WebServiceInterfaceWithAnnotation {

        @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
        @WebMethod(operationName = "m1")
        String m1(String str);

        @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
        @WebMethod(operationName = "M2")
        String m2(String str, int i);
    }

    private static interface WebServiceInterfaceWithoutAnnotation {

    }
}
