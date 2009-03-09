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

package org.apache.tuscany.sca.implementation.osgi.xml;

import java.io.StringReader;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 */
public class ServiceDescriptionsTestCase {
    private static final String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<service-descriptions xmlns=\"http://www.osgi.org/xmlns/sd/v1.0.0\">"
            + "<service-description>"
            + "<provide interface=\"com.iona.soa.pojo.hello.HelloService\"/>"
            + "<property name=\"service.intents\">SOAP HTTP</property>"
            + "<property name=\"osgi.remote.configuration.type\">pojo</property>"
            + "<property name=\"osgi.remote.configuration.pojo.address\">"
            + "http://localhost:9000/hello"
            + "</property>"
            + "</service-description>"
            + "<service-description>"
            + "<provide interface=\"com.iona.soa.pojo.hello.GreeterService\"/>"
            + "<property name=\"service.intents\">SOAP HTTP</property>"
            + "<property name=\"osgi.remote.configuration.type\">pojo</property>"
            + "<property name=\"osgi.remote.configuration.pojo.address\">"
            + "http://localhost:9005/greeter"
            + "</property>"
            + "</service-description>"
            + "</service-descriptions>";

    private static XMLStreamReader reader;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        reader = factory.createXMLStreamReader(new StringReader(xml));
    }

    @Test
    public void testLoad() throws Exception {
        ServiceDescriptionsProcessor processor = new ServiceDescriptionsProcessor();
        List<ServiceDescription> descriptions = processor.read(reader);
        Assert.assertEquals(2, descriptions.size());
        System.out.println(descriptions);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (reader != null) {
            reader.close();
        }
    }

}
