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

package org.apache.tuscany.sca.common.http.cors.xml;

import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.Assert;

import org.apache.tuscany.sca.common.http.cors.CORSConfiguration;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.junit.BeforeClass;
import org.junit.Test;

public class CORSConfigurationProcessorTestCase {

    private static final String COMPOSITE =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
            + "            <tuscany:corsConfiguration xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200912\" xmlns:tuscany=\"http://tuscany.apache.org/xmlns/sca/1.1\" targetNamespace=\"http://cors\" >"
            + "               <tuscany:allowCredentials>true</tuscany:allowCredentials>"
            + "               <tuscany:maxAge>3600</tuscany:maxAge>"            
            + "               <tuscany:allowOrigins>"
            + "                 <tuscany:origin>http://www.apache.org</tuscany:origin>"
            + "               </tuscany:allowOrigins>"
            + "               <tuscany:allowMethods>"
            + "                 <tuscany:method>POST</tuscany:method>"
            + "                 <tuscany:method>PUT</tuscany:method>"            
            + "               </tuscany:allowMethods>"
            + "               <tuscany:allowHeaders>"
            + "                 <tuscany:header>X-custom-1</tuscany:header>"
            + "                 <tuscany:header>X-custom-2</tuscany:header>"            
            + "               </tuscany:allowHeaders>"
            + "               <tuscany:exposeHeaders>"
            + "                 <tuscany:header>X-custom-1</tuscany:header>"            
            + "               </tuscany:exposeHeaders>"
            + "            </tuscany:corsConfiguration>";

    private static XMLInputFactory inputFactory;
    private static StAXArtifactProcessor<Object> staxProcessor;
    private static ProcessorContext context;

    @BeforeClass
    public static void setUp() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        context = new ProcessorContext(extensionPoints);
        inputFactory = XMLInputFactory.newInstance();
        
        StAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint(extensionPoints);
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, null);
    }

    /**
     * Test parsing valid composite definition. Valid composite populated with correct values expected.
     * @throws Exception
     */
    @Test
    public void testLoadValidComposite() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(COMPOSITE));
        
        //Composite composite = (Composite)staxProcessor.read(reader, context);
        CORSConfiguration config = (CORSConfiguration) staxProcessor.read(reader, context);
        
        Assert.assertNotNull(config);
        Assert.assertTrue(config.isAllowCredentials());
        Assert.assertEquals(3600, config.getMaxAge());
        Assert.assertEquals(1,config.getAllowOrigins().size());
        Assert.assertEquals(2,config.getAllowMethods().size());
        Assert.assertEquals(2,config.getAllowHeaders().size());
        Assert.assertEquals(1,config.getExposeHeaders().size());        
    }
}
