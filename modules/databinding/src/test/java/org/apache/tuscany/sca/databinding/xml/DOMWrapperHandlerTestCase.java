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

package org.apache.tuscany.sca.databinding.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.tuscany.sca.common.xml.dom.DOMHelper;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.impl.OperationImpl;
import org.apache.tuscany.sca.interfacedef.util.ElementInfo;
import org.apache.tuscany.sca.interfacedef.util.WrapperInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Test case for DOMWrapperHandler
 *
 * @version $Rev: 1101239 $ $Date: 2011-05-09 17:54:07 -0400 (Mon, 09 May 2011) $
 */
@Ignore
public class DOMWrapperHandlerTestCase {
	
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

    private DOMHelper domHelper;
    private DOMWrapperHandler handler;
    private Operation op;

    @Before
    public void setUp() throws Exception {
    	ExtensionPointRegistry registry = new DefaultExtensionPointRegistry();
    	this.domHelper = DOMHelper.getInstance(registry);
        this.handler = new DOMWrapperHandler(domHelper);

        List<ElementInfo> elements = new ArrayList<ElementInfo>();
        for (QName inQName : new QName[] { INPUT1, INPUT2, INPUT3, INPUT4 }) {
            ElementInfo e = new ElementInfo(inQName, null);
            e.setNillable(true);
            elements.add(e);
        }
        // INPUT1,4 are like maxOccurs="unbounded"
        elements.get(0).setMany(true);
        elements.get(3).setMany(true);
        // INPUT2 is like minOccurs="0", nillable="false"
        elements.get(1).setOmissible(true);
        elements.get(1).setNillable(false);

        WrapperInfo wrapperInfo = new WrapperInfo(DOMDataBinding.NAME, null, elements);
        this.op = new OperationImpl();
        op.setInputWrapper(wrapperInfo);   
    }
    
    @Test
    public void testGetChildren() {
        try {
            Element wrapperElem = domHelper.load(WRAPPER_XML).getDocumentElement();
            List children = handler.getChildren(wrapperElem, op, true);
            Assert.assertEquals(4, children.size());
            Object[] firstChild = (Object[])children.get(0);
            Assert.assertEquals(3, firstChild.length);
            Assert.assertEquals("input1ContentsB", ((Element)firstChild[2]).getTextContent());
            Object secondChild = children.get(1);
            Assert.assertNull(secondChild);
            Element thirdChild = (Element)children.get(2);
            Assert.assertEquals("input3ContentsA", thirdChild.getTextContent());
            Object[] fourthChild = (Object[])children.get(3);
            Assert.assertEquals(1, fourthChild.length);
        } catch (Exception e) {
            throw new RuntimeException(e); 
        }
    }
    

    @Test
    public void testSetChildren() {        
        Document document = domHelper.newDocument();
        
        QName wrapperQName = new QName("myNamespace", "wrapper", "myns");
        Element wrapper = DOMHelper.createElement(document, wrapperQName);
        
        Element[] in1 = new Element[2];
        in1[0] = DOMHelper.createElement(document, INPUT1);
        in1[1] = DOMHelper.createElement(document, INPUT1);
        Element in2 = null;
        Element in3 = DOMHelper.createElement(document, INPUT3);
        Element[] in4 = new Element[1];
        in4[0] = DOMHelper.createElement(document, INPUT4);
        Object[] parms = new Object[] {in1, in2, in3, in4};

        handler.setChildren(wrapper, parms, op, true);

        NodeList iter = wrapper.getChildNodes();
        Assert.assertEquals(4, iter.getLength());
        Element elem1 = (Element)iter.item(0);
        Element elem2 = (Element)iter.item(1); 
        Element elem3 = (Element)iter.item(2);
        Element elem4 = (Element)iter.item(3);        
       
        Assert.assertEquals(INPUT1, DOMHelper.getQName(elem1));
        Assert.assertEquals(INPUT1, DOMHelper.getQName(elem2));
        Assert.assertEquals(INPUT3, DOMHelper.getQName(elem3));
        Assert.assertEquals(INPUT4, DOMHelper.getQName(elem4));
    }

}

