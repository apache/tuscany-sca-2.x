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
package org.apache.tuscany.container.javascript.utils.xmlfromxsd;

import junit.framework.TestCase;

public class XMLfromXSDGeneratorTestCase extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*public void testXMLInstance_SDO_based_1()
    {
        String[] arguments = new String[] { "-xsd", "sequences.xsd",
                                            "-st", "MixedQuote",
                                            "-stn", "http://www.example.com/sequences",
                                            "-o", "target/xmlFromxsd-source",
                                            "-of", "sequences_sdo.xml"
                                        };
        
        XMLfromXSDGenerator.main(arguments);
        //File file = new File("target/java2wsdl-source/CustomerValue.wsdl");
        //assertTrue(file.exists() && file.isFile());
    }
    
    public void testXMLInstance_SDO_based_2()
    {
        try
        {
            XMLfromXSDConfiguration config = new XMLfromXSDConfiguration();
            config.setXsdFileName("interopdoc.wsdl");
            config.setSchemaTypeName("ComplexDocument");
            config.setSchemaTypeNamespaceURI("http://soapinterop.org/");
            config.setXmlOutputLocation("target/xmlFromxsd-source");
            config.setXmlFileName("interopdoc_sdo.xml");
            
            XMLGeneratorFactory.getInstance().createGenerator(XMLGenerator.SDO_BASED).generateXML(config);
            //XMLGeneratorFactory.getInstance().createGenerator(XMLGenerator.XMLBEANS_BASED).generateXML(config);
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }
    
    public void testXMLInstance_SDO_based_3()
    {
        try
        {
            XMLfromXSDConfiguration config = new XMLfromXSDConfiguration();
            config.setXsdFileName("helloworld.wsdl");
            //config.setSchemaTypeName("getGreetings");
            config.setSchemaTypeName("ComplexGreetings");
            config.setSchemaTypeNamespaceURI("http://helloworldaxis.samples.tuscany.apache.org");
            config.setXmlOutputLocation("target/xmlFromxsd-source");
            config.setXmlFileName("helloworld_sdo.xml");
            
            XMLGeneratorFactory.getInstance().createGenerator(XMLGenerator.SDO_BASED).generateXML(config);
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }*/

    public void testXMLInstance_XB_based_1() {
        String[] arguments = new String[] { "-xsd", "src/test/resources/sequences.xsd", "-st", "mixedStockQuote", "-stn",
                "http://www.example.com/sequences", "-o", "target/xmlFromxsd-source", "-of", "sequences_xb.xml" };

        XMLfromXSDGenerator.generatorType = XMLGenerator.XMLBEANS_BASED;
        XMLfromXSDGenerator.main(arguments);
        /*
         * File file = new File("target/java2wsdl-source/CustomerValue.wsdl"); assertTrue(file.exists() && file.isFile());
         */
    }

    public void testXMLInstance_XB_based_2() {
        try {
            XMLfromXSDConfiguration config = new XMLfromXSDConfiguration();
            config.setXsdFileName("interopdoc.wsdl");
            config.setSchemaTypeName("ComplexDocument");
            config.setSchemaTypeNamespaceURI("http://soapinterop.org/");
            config.setXmlOutputLocation("target/xmlFromxsd-source");
            config.setXmlFileName("interopdoc_xb.xml");

            XMLGeneratorFactory.getInstance().createGenerator(XMLGenerator.XMLBEANS_BASED).generateXML(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testXMLInstance_XB_based_3() {
        try {
            XMLfromXSDConfiguration config = new XMLfromXSDConfiguration();
            config.setXsdFileName("org/apache/tuscany/container/javascript/rhino/helloworld.wsdl");
            // config.setXsdFileName("helloworld.wsdl");
            config.setSchemaTypeName("getGreetings");
            // config.setSchemaTypeName("ComplexGreetings");
            config.setSchemaTypeNamespaceURI("http://helloworld");
            config.setXmlOutputLocation("target/xmlFromxsd-source");
            config.setXmlFileName("helloworld_xb.xml");

            XMLGeneratorFactory.getInstance().createGenerator(XMLGenerator.XMLBEANS_BASED).generateXML(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
