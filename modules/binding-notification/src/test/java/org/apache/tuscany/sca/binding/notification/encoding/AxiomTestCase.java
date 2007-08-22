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
package org.apache.tuscany.sca.binding.notification.encoding;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.junit.Assert;

import junit.framework.TestCase;

public class AxiomTestCase extends TestCase {

    private static String wsnt = "http://docs.oasis-open.org/wsn/b-2";
    private static String wsa = "http://schemas.xmlsoap.org/ws/2004/08/addressing";
    private static String testUrl1 = "http://localhost:8081/test";
    private static String testUrl2 = "http://localhost:8082/test";
    private static String testNewProducerResponse =
        "<wsnt:NewProducerResponse xmlns:wsnt=\"" + wsnt + "\" ConsumerSequenceType=\"EndConsumers\">" +
            "<wsa:Address xmlns:wsa=\"" + wsa + "\">" + testUrl1 + "</wsa:Address>" +
            "<wsa:Address xmlns:wsa=\"" + wsa + "\">" + testUrl2 + "</wsa:Address>" +
        "</wsnt:NewProducerResponse>";
    
    public void testAxiom() {
        try {
            StAXOMBuilder builder = new StAXOMBuilder(new ByteArrayInputStream(testNewProducerResponse.getBytes()));
            OMElement element = builder.getDocumentElement();
            Assert.assertNotNull(element);
            
            StringWriter sw = new StringWriter();
            element.serialize(sw);
            sw.flush();
            Assert.assertEquals(sw.toString(),testNewProducerResponse);
        }
        catch(Throwable e) {
            e.printStackTrace();
        }
    }
}
