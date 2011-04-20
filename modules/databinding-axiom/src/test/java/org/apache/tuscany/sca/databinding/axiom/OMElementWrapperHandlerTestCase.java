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

package org.apache.tuscany.sca.databinding.axiom;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.impl.OperationImpl;
import org.apache.tuscany.sca.interfacedef.util.ElementInfo;
import org.apache.tuscany.sca.interfacedef.util.WrapperInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Test case for OMElementWrapperHandler
 *
 * @version $Rev$ $Date$
 */
public class OMElementWrapperHandlerTestCase {
    private static final QName INPUT1 = new QName("http://ns1", "input1");
    private static final QName INPUT2 = new QName("http://ns2", "input2");
    private static final QName INPUT3 = new QName("http://ns3", "input3");
    private static final QName INPUT4 = new QName("http://ns4", "input4");
    private String WRAPPER_XML =
        "<?xml version=\"1.0\"?>" + "<wrapper"
        + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
        + "  xmlns:ns1=\"http://ns1\""
        + "  xmlns:ns2=\"http://ns2\""
        + "  xmlns:ns3=\"http://ns3\""
        + "  xmlns:ns4=\"http://ns4\">"
        + "  <ns1:input1 xsi:type=\"ns1:Input1Type\">"
        // Eliminating whitespace within the quotes allows us to have a simple compare.        
        +       "input1ContentsA"  
        +   "</ns1:input1>"
        + "  <ns1:input1 xsi:nil=\"true\"/>"
        + "  <ns1:input1>"
        +       "input1ContentsB"  
        +   "</ns1:input1>"        
        + "  <ns3:input3 xsi:type=\"ns3:Input3Type\">"
        +      "input3ContentsA"  
        +   "</ns3:input3>"
        + "  <ns4:input4>"
        +       "input4ContentsA"  
        + "  </ns4:input4>"
        + " </wrapper>";

    private OMElementWrapperHandler handler;

    @Before
    public void setUp() throws Exception {
        this.handler = new OMElementWrapperHandler();
    }
    
    // Would be nice to do a "set" test too.

    @Test
    @Ignore("TUSCANY-3857")
    public void testGetChildren() {
        try {
            OMElement wrapperElem = AXIOMUtil.stringToOM(WRAPPER_XML);
            List<ElementInfo> elements = new ArrayList<ElementInfo>();
            for (QName inQName : new QName[] { INPUT1, INPUT2, INPUT3, INPUT4 }) {
                ElementInfo e = new ElementInfo(inQName, null);
                e.setNillable(true);
                elements.add(e);
            }
            // INPUT1,4 are "many" 
            elements.get(0).setMany(true);
            elements.get(3).setMany(true);
            
            WrapperInfo wrapperInfo = new WrapperInfo(AxiomDataBinding.NAME, null, null, elements, null);
            Operation op = new OperationImpl();
            op.setWrapper(wrapperInfo);
            List children = handler.getChildren(wrapperElem, op, true);
            Assert.assertEquals(4, children.size());
            Object[] firstChild = (Object[])children.get(0);
            Assert.assertEquals(3, firstChild.length);
            Assert.assertEquals("input1ContentsB", ((OMElement)firstChild[2]).getText());
            Object secondChild = children.get(1);
            Assert.assertNull(secondChild);
            OMElement thirdChild = (OMElement)children.get(2);
            Assert.assertEquals("input3ContentsA", thirdChild.getText());
            Object[] fourthChild = (Object[])children.get(3);
            Assert.assertEquals(1, fourthChild.length);

        } catch (XMLStreamException e) {
            throw new RuntimeException(e); 
        }
    }

}

