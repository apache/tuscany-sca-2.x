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

package org.apache.tuscany.sca.binding.jms.impl;

import java.io.StringReader;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.monitor.impl.DefaultMonitorFactoryImpl;

/**
 * Tests for JMS binding xml
 */
public class JMSBindingProcessorTestCase extends TestCase {
    
    private static final String COMPOSITE =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" targetNamespace=\"http://binding-jms\" name=\"binding-jms\">"
            + " <component name=\"HelloWorldComponent\">"
            + "   <implementation.java class=\"services.HelloWorld\"/>"
            + "      <service name=\"HelloWorldService\">"
            + "          <binding.jms uri=\"jms:testQueue\" />"
            + "      </service>"
            + " </component>"
            + "</composite>";

    private static final String HEADERS1 =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" targetNamespace=\"http://binding-jms\" name=\"binding-jms\">"
            + " <component name=\"HelloWorldComponent\">"
            + "   <implementation.java class=\"services.HelloWorld\"/>"
            + "      <service name=\"HelloWorldService\">"
            + "          <binding.jms uri=\"jms:testQueue\" >"
            + "             <headers JMSType=\"myType\" JMSCorrelationID=\"myCorrelId\" JMSDeliveryMode=\"PERSISTENT\" JMSTimeToLive=\"54321\" JMSPriority=\"5\" >"
            + "             </headers>" 
            + "          </binding.jms>"
            + "      </service>"
            + " </component>"
            + "</composite>";

    private static final String PROPERTIES1 =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" targetNamespace=\"http://binding-jms\" name=\"binding-jms\">"
            + " <component name=\"HelloWorldComponent\">"
            + "   <implementation.java class=\"services.HelloWorld\"/>"
            + "      <service name=\"HelloWorldService\">"
            + "          <binding.jms uri=\"jms:testQueue\" >"
            + "             <headers>"
            + "                <property name=\"p1\">bla</property>"
            + "                <property name=\"intProp\" type=\"int\">42</property>"
            + "             </headers>" 
            + "          </binding.jms>"
            + "      </service>"
            + " </component>"
            + "</composite>";
    
    private static final String OP_PROPERTIES1 =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" targetNamespace=\"http://binding-jms\" name=\"binding-jms\">"
            + " <component name=\"HelloWorldComponent\">"
            + "   <implementation.java class=\"services.HelloWorld\"/>"
            + "      <service name=\"HelloWorldService\">"
            + "          <binding.jms uri=\"jms:testQueue\" >"
            + "             <operationProperties name=\"op1\">"
            + "                <headers JMSType=\"op1Type\" >"
            + "                   <property name=\"p1\">bla</property>"
            + "                   <property name=\"intProp\" type=\"int\">42</property>"
            + "                </headers>" 
            + "             </operationProperties >" 
            + "             <operationProperties name=\"op2\" nativeOperation=\"nativeOp2\" >"
            + "                <headers JMSType=\"op2Type\">"
            + "                   <property name=\"p2\">op2bla</property>"
            + "                   <property name=\"intProp\" type=\"int\">77</property>"
            + "                </headers>" 
            + "             </operationProperties >" 
            + "          </binding.jms>"
            + "      </service>"
            + " </component>"
            + "</composite>";

    private static final String SELECTOR =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" targetNamespace=\"http://binding-jms\" name=\"binding-jms\">"
            + " <component name=\"HelloWorldComponent\">"
            + "   <implementation.java class=\"services.HelloWorld\"/>"
            + "      <service name=\"HelloWorldService\">"
            + "          <binding.jms uri=\"jms:testQueue\" >"
            + "             <SubscriptionHeaders JMSSelector=\"prop1 = 2\" />"
            + "          </binding.jms>"
            + "      </service>"
            + " </component>"
            + "</composite>";

    private XMLInputFactory inputFactory;
    private StAXArtifactProcessor<Object> staxProcessor;
    private Monitor monitor;

    @Override
    protected void setUp() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        inputFactory = XMLInputFactory.newInstance();
        // Create a monitor
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = new DefaultMonitorFactoryImpl();  
        if (monitorFactory != null) {
        	monitor = monitorFactory.createMonitor();
        	utilities.addUtility(monitorFactory);
        }
        StAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint(extensionPoints);
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, null, monitor);
    }

    /**
     * Test parsing valid composite definition. Valid composite populated with correct values expected.
     * @throws Exception
     */
    public void testLoadValidComposite() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(COMPOSITE));
        
        Composite composite = (Composite)staxProcessor.read(reader);
        JMSBinding binding = (JMSBinding)   composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        
        assertNotNull(binding);
        assertEquals("testQueue", binding.getDestinationName());
    }

    public void testHeaders1() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(HEADERS1));
        
        Composite composite = (Composite)staxProcessor.read(reader);
        JMSBinding binding = (JMSBinding)   composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        
        assertNotNull(binding);
        assertEquals("myType", binding.getJMSType());
        assertEquals("myCorrelId", binding.getJMSCorrelationId());
        assertTrue(binding.isdeliveryModePersistent());
        assertEquals(54321, binding.getJMSTimeToLive().longValue());
        assertEquals(5, binding.getJMSPriority().intValue());
    }

    public void testProperties1() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(PROPERTIES1));
        
        Composite composite = (Composite)staxProcessor.read(reader);
        JMSBinding binding = (JMSBinding)   composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        
        assertNotNull(binding);
        assertEquals("bla", binding.getProperty("p1"));
        assertEquals(42, ((Integer)binding.getProperty("intProp")).intValue());
    }

    public void testOpProperties1() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(OP_PROPERTIES1));
        
        Composite composite = (Composite)staxProcessor.read(reader);
        JMSBinding binding = (JMSBinding)   composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        
        assertNotNull(binding);

        Map<String, Object> op1Props = binding.getOperationProperties("op1");
        assertEquals("op1Type", binding.getOperationJMSType("op1"));
        assertEquals("bla", op1Props.get("p1"));
        assertEquals(42, ((Integer)op1Props.get("intProp")).intValue());
        
        assertEquals("op2Type", binding.getOperationJMSType("op2"));
        Map<String, Object> op2Props = binding.getOperationProperties("op2");
        assertEquals("op2bla", op2Props.get("p2"));
        assertEquals(77, ((Integer)op2Props.get("intProp")).intValue());
    }

    public void testSubscriptionHeaders () throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(SELECTOR));
        
        Composite composite = (Composite)staxProcessor.read(reader);
        JMSBinding binding = (JMSBinding)   composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        
        assertNotNull(binding);

        assertEquals("prop1 = 2", binding.getJMSSelector());
    }
}
