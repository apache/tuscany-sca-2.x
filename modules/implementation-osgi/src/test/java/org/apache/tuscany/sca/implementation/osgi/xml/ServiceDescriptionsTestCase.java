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

import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.implementation.osgi.ServiceDescription;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 */
public class ServiceDescriptionsTestCase {
    private static final String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<endpoint-descriptions xmlns=\"http://www.osgi.org/xmlns/rsa/v1.0.0\" "
            + "xmlns:sca=\"http://docs.oasis-open.org/ns/opencsa/sca/200912\">"
            + "<endpoint-description>"
            + "<property name=\"objectClass\" value=\"calculator.operations.AddService\"/>"
            + "<property name=\"service.intents\" value=\"sca:SOAP sca:HTTP\"/>"
            + "<property name=\"service.imported.configs\" value=\"org.osgi.sca\"/>"
            + "</endpoint-description>"
            + "<endpoint-description>"
            + "<property name=\"service.intents\">"
            + "    <list>"
            + "        <value>SOAP</value>"
            + "        <value>HTTP</value>"
            + "    </list>"
            + "</property>"
            + "<property name=\"endpoint.id\" value=\"http://ws.acme.com:9000/hello\"/>"
            + "<property name=\"objectClass\" value=\"com.acme.Foo\"/>"
            + "<property name=\"endpoint.package.version.com.acme\" value=\"4.2\"/>"
            + "<property name=\"service.imported.configs\" value=\"com.acme\"/>"
            + "<property name=\"com.acme.ws.xml\">"
            + "<xml>"
            + "              <config xmlns=\"http://acme.com/defs\">"
            + "                  <port>1029</port>"
            + "              <host>www.acme.com</host>"
            + "          </config>"
            + "      </xml>"
            + "  </property>"
            + "</endpoint-description>"
            + "</endpoint-descriptions>";

    private static ServiceDescriptionsProcessor processor;
    private static XMLStreamReader reader;

    private static ProcessorContext context;

    @BeforeClass
    public static void setUp() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        context = new ProcessorContext(extensionPoints);

        XMLInputFactory factory = XMLInputFactory.newInstance();
        StAXArtifactProcessorExtensionPoint staxProcessors =
            extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        StAXArtifactProcessor staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, factory, null);

        processor = new ServiceDescriptionsProcessor(extensionPoints, staxProcessor);

        reader = factory.createXMLStreamReader(new StringReader(xml));
    }

    @Test
    public void testLoad() throws Exception {
        List<ServiceDescription> descriptions = processor.read(reader, context);
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
