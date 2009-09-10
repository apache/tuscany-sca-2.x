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

package org.apache.tuscany.sca.binding.jms;

import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.OperationsConfigurator;
import org.apache.tuscany.sca.assembly.WireFormat;
import org.apache.tuscany.sca.binding.jms.BindingProperty;
import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.wireformat.WireFormatJMSBytes;
import org.apache.tuscany.sca.binding.jms.wireformat.WireFormatJMSObject;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;

/**
 * Tests for JMS binding xml
 */
public class JMSBindingProcessorTestCase extends TestCase {
    // Note: If you are adding new JMS binding read test cases,
    // consider adding a similar test case to JMSBindingProcessorWriteTestCase.
    public static final String COMPOSITE =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200903\" targetNamespace=\"http://binding-jms\" name=\"binding-jms\">"
            + " <component name=\"HelloWorldComponent\">"
            + "   <implementation.java class=\"services.HelloWorld\"/>"
            + "      <service name=\"HelloWorldService\">"
            + "          <binding.jms uri=\"jms:testQueue\" />"
            + "      </service>"
            + " </component>"
            + "</composite>";

    public static final String HEADERS1 =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200903\" targetNamespace=\"http://binding-jms\" name=\"binding-jms\">"
            + " <component name=\"HelloWorldComponent\">"
            + "   <implementation.java class=\"services.HelloWorld\"/>"
            + "      <service name=\"HelloWorldService\">"
            + "          <binding.jms uri=\"jms:testQueue\" >"
            + "             <headers JMSType=\"myType\" JMSCorrelationID=\"myCorrelId\" JMSDeliveryMode=\"PERSISTENT\" JMSTimeToLive=\"54321\" JMSPriority=\"5\">"
            + "             </headers>" 
            + "          </binding.jms>"
            + "      </service>"
            + " </component>"
            + "</composite>";
    
    public static final String HEADERS_INVALID_PRIORITY =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200903\" targetNamespace=\"http://binding-jms\" name=\"binding-jms\">"
            + " <component name=\"HelloWorldComponent\">"
            + "   <implementation.java class=\"services.HelloWorld\"/>"
            + "      <service name=\"HelloWorldService\">"
            + "          <binding.jms uri=\"jms:testQueue\" >"
            + "             <headers JMSType=\"myType\" JMSCorrelationID=\"myCorrelId\" JMSDeliveryMode=\"PERSISTENT\" JMSTimeToLive=\"54321\" JMSPriority=\"medium\">"
            + "             </headers>" 
            + "          </binding.jms>"
            + "      </service>"
            + " </component>"
            + "</composite>";

    public static final String PROPERTIES1 =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200903\" targetNamespace=\"http://binding-jms\" name=\"binding-jms\">"
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
    
    public static final String OP_PROPERTIES1 =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200903\" targetNamespace=\"http://binding-jms\" name=\"binding-jms\">"
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

    public static final String OP_NAMES_NO_PROPERTIES1 =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200903\" targetNamespace=\"http://binding-jms\" name=\"binding-jms\">"
            + " <component name=\"HelloWorldComponent\">"
            + "   <implementation.java class=\"services.HelloWorld\"/>"
            + "      <service name=\"HelloWorldService\">"
            + "          <binding.jms uri=\"jms:testQueue\" >"
            + "             <operationProperties name=\"op1\">"
            + "             </operationProperties >" 
            + "             <operationProperties name=\"op2\" nativeOperation=\"nativeOp2\" >"
            + "             </operationProperties >" 
            + "          </binding.jms>"
            + "      </service>"
            + " </component>"
            + "</composite>";

    public static final String SELECTOR =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200903\" targetNamespace=\"http://binding-jms\" name=\"binding-jms\">"
            + " <component name=\"HelloWorldComponent\">"
            + "   <implementation.java class=\"services.HelloWorld\"/>"
            + "      <service name=\"HelloWorldService\">"
            + "          <binding.jms uri=\"jms:testQueue\" >"
            + "             <SubscriptionHeaders JMSSelector=\"prop1 = 2\" />"
            + "          </binding.jms>"
            + "      </service>"
            + " </component>"
            + "</composite>";

    public static final String COMPOSITE_INVALID_URI =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200903\" targetNamespace=\"http://binding-jms\" name=\"binding-jms\">"
            + " <component name=\"HelloWorldComponent\">"
            + "   <implementation.java class=\"services.HelloWorld\"/>"
            + "      <service name=\"HelloWorldService\">"
            + "          <binding.jms uri=\"invalidjms:testQueue\" />"
            + "      </service>"
            + " </component>"
            + "</composite>";

    // Invalid: contains both a response attribute and a response element.
    public static final String COMPOSITE_INVALID_RESPONSE_ATTR_ELEMENT =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200903\" targetNamespace=\"http://binding-jms\" name=\"binding-jms\">"
            + " <component name=\"HelloWorldComponent\">"
            + "   <implementation.java class=\"services.HelloWorld\"/>"
            + "      <service name=\"HelloWorldService\">"
            + "          <binding.jms uri=\"jms:testQueue\" responseConnection=\"responseConnectionAttrName\">"
            + "             <response>"
            + "                <destination name=\"responseConnectionElementName\"/>"
            + "             </response>"
            + "          </binding.jms>"
            + "      </service>"
            + " </component>"
            + "</composite>";

    public static final String DEST_PROPS =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200903\" targetNamespace=\"http://binding-jms\" name=\"binding-jms\">"
            + " <component name=\"HelloWorldComponent\">"
            + "   <implementation.java class=\"services.HelloWorld\"/>"
            + "      <service name=\"HelloWorldService\">"
            + "          <binding.jms>"
            + "             <destination name=\"foo\">"
            + "                <property name=\"xxx\" type=\"yyy\">"
            + "                   some value text"
            + "                </property>"
            + "                <property name=\"two\">"
            + "                   bla"
            + "                </property>"
            + "             </destination>"
            + "          </binding.jms>"
            + "      </service>"
            + " </component>"
            + "</composite>";

    public static final String CF_PROPS =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200903\" targetNamespace=\"http://binding-jms\" name=\"binding-jms\">"
            + " <component name=\"HelloWorldComponent\">"
            + "   <implementation.java class=\"services.HelloWorld\"/>"
            + "      <service name=\"HelloWorldService\">"
            + "          <binding.jms>"
            + "             <connectionFactory name=\"foo\">"
            + "                <property name=\"xxx\" type=\"yyy\">"
            + "                   some value text"
            + "                </property>"
            + "                <property name=\"two\">"
            + "                   bla"
            + "                </property>"
            + "             </connectionFactory>"
            + "          </binding.jms>"
            + "      </service>"
            + " </component>"
            + "</composite>";

    public static final String AS_PROPS =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200903\" targetNamespace=\"http://binding-jms\" name=\"binding-jms\">"
            + " <component name=\"HelloWorldComponent\">"
            + "   <implementation.java class=\"services.HelloWorld\"/>"
            + "      <service name=\"HelloWorldService\">"
            + "          <binding.jms>"
            + "             <activationSpec name=\"foo\">"
            + "                <property name=\"xxx\" type=\"yyy\">"
            + "                   some value text"
            + "                </property>"
            + "                <property name=\"two\">"
            + "                   bla"
            + "                </property>"
            + "             </activationSpec>"
            + "          </binding.jms>"
            + "      </service>"
            + " </component>"
            + "</composite>";

    public static final String RESP_DEST_PROPS =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200903\" targetNamespace=\"http://binding-jms\" name=\"binding-jms\">"
            + " <component name=\"HelloWorldComponent\">"
            + "   <implementation.java class=\"services.HelloWorld\"/>"
            + "      <service name=\"HelloWorldService\">"
            + "          <binding.jms>"
            + "             <response>"
            + "                <destination name=\"foo\">"
            + "                   <property name=\"xxx\" type=\"yyy\">"
            + "                      some value text"
            + "                   </property>"
            + "                   <property name=\"two\">"
            + "                      bla"
            + "                   </property>"
            + "                </destination>"
            + "             </response>"
            + "          </binding.jms>"
            + "      </service>"
            + " </component>"
            + "</composite>";

    public static final String RESP_CF_PROPS =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200903\" targetNamespace=\"http://binding-jms\" name=\"binding-jms\">"
            + " <component name=\"HelloWorldComponent\">"
            + "   <implementation.java class=\"services.HelloWorld\"/>"
            + "      <service name=\"HelloWorldService\">"
            + "          <binding.jms>"
            + "             <response>"
            + "                <connectionFactory name=\"foo\">"
            + "                   <property name=\"xxx\" type=\"yyy\">"
            + "                      some value text"
            + "                   </property>"
            + "                   <property name=\"two\">"
            + "                      bla"
            + "                   </property>"
            + "                </connectionFactory>"
            + "             </response>"
            + "          </binding.jms>"
            + "      </service>"
            + " </component>"
            + "</composite>";

    public static final String RESP_AS_PROPS =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200903\" targetNamespace=\"http://binding-jms\" name=\"binding-jms\">"
            + " <component name=\"HelloWorldComponent\">"
            + "   <implementation.java class=\"services.HelloWorld\"/>"
            + "      <service name=\"HelloWorldService\">"
            + "          <binding.jms>"
            + "             <response>"
            + "                <activationSpec name=\"foo\">"
            + "                   <property name=\"xxx\" type=\"yyy\">"
            + "                      some value text"
            + "                   </property>"
            + "                   <property name=\"two\">"
            + "                      bla"
            + "                   </property>"
            + "                </activationSpec>"
            + "             </response>"
            + "          </binding.jms>"
            + "      </service>"
            + " </component>"
            + "</composite>";

    public static final String OP_PROPS_PROPS =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200903\" targetNamespace=\"http://binding-jms\" name=\"binding-jms\">"
            + " <component name=\"HelloWorldComponent\">"
            + "   <implementation.java class=\"services.HelloWorld\"/>"
            + "      <service name=\"HelloWorldService\">"
            + "          <binding.jms>"
            + "             <operationProperties name=\"op1\">"
            + "                   <property name=\"xxx\" type=\"yyy\">"
            + "                      some value text"
            + "                   </property>"
            + "                   <property name=\"two\">"
            + "                      bla"
            + "                   </property>"
            + "             </operationProperties >" 
            + "          </binding.jms>"
            + "      </service>"
            + " </component>"
            + "</composite>";

    public static final String RES_ADPT_PROPS =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200903\" targetNamespace=\"http://binding-jms\" name=\"binding-jms\">"
            + " <component name=\"HelloWorldComponent\">"
            + "   <implementation.java class=\"services.HelloWorld\"/>"
            + "      <service name=\"HelloWorldService\">"
            + "          <binding.jms>"
            + "             <resourceAdapter name=\"r1\">"
            + "                   <property name=\"xxx\" type=\"yyy\">"
            + "                      some value text"
            + "                   </property>"
            + "                   <property name=\"two\">"
            + "                      bla"
            + "                   </property>"
            + "             </resourceAdapter>" 
            + "          </binding.jms>"
            + "      </service>"
            + " </component>"
            + "</composite>";

    public static final String CONFIGURED_OPERATIONS =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200903\" targetNamespace=\"http://binding-jms\" name=\"binding-jms\">"
            + " <component name=\"HelloWorldComponent\">"
            + "   <implementation.java class=\"services.HelloWorld\"/>"
            + "      <service name=\"HelloWorldService\">"
            + "          <binding.jms uri=\"jms:testQueue\" >"
            + "             <operationProperties name=\"op1\">"
            + "             </operationProperties >" 
            + "             <operation name=\"op1\" requires=\"IntentOne IntentTwo\"/>"
            + "          </binding.jms>"
            + "      </service>"
            + " </component>"
            + "</composite>";
    
    public static final String WIRE_FORMAT =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200903\" targetNamespace=\"http://binding-jms\" xmlns:tuscany=\"http://tuscany.apache.org/xmlns/sca/1.1\" name=\"binding-jms\">"
            + " <component name=\"HelloWorldComponent\">"
            + "   <implementation.java class=\"services.HelloWorld\"/>"
            + "      <service name=\"HelloWorldService\">"
            + "          <binding.jms uri=\"jms:testQueue\" >"
            + "              <response>" 
            + "                  <destination name=\"responseConnectionElementName\"/>"            
            + "                  <tuscany:wireFormat.jmsBytes/>"            
            + "              </response>"            
            + "              <tuscany:wireFormat.jmsObject/>"
            + "          </binding.jms>"
            + "      </service>"
            + " </component>"
            + "</composite>";     

    public static final String OP_PROP_NAME =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200903\" targetNamespace=\"http://binding-jms\" name=\"binding-jms\">"
            + " <component name=\"HelloWorldComponent\">"
            + "   <implementation.java class=\"services.HelloWorld\"/>"
            + "      <service name=\"HelloWorldService\">"
            + "          <binding.jms operationProperties=\"foo\"/>"
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
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
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

    /** Test various parsing validation requirements. */
    public void testParsingValidationErrors1() throws Exception {        
        // Composite with malformed URI. 
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(COMPOSITE_INVALID_URI));

        try {
            Composite composite = (Composite)staxProcessor.read(reader);       
        } catch(Exception e) {
            // JMSBindingExceptions are expected with invalid composite.
            if ( !e.getClass().isAssignableFrom( JMSBindingException.class ) )
                throw e;
            // Do assertion to make sure test registers results.
            assertTrue( e.getClass().isAssignableFrom( JMSBindingException.class ) );
        }
    }
    
    public void testParsingValidationErrors2() throws Exception {        
        // Composite with invalid priority
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(HEADERS_INVALID_PRIORITY));

        try {
            Composite composite = (Composite)staxProcessor.read(reader);       
        } catch(Exception e) {
            // JMSBindingExceptions are expected with invalid composite.
            if ( !e.getClass().isAssignableFrom( JMSBindingException.class ) )
                throw e;
            // Do assertion to make sure test registers results.
            assertTrue( e.getClass().isAssignableFrom( JMSBindingException.class ) );
            return;
        }
    }    

    /** Test various model validation requirements. */
    public void testValidationErrors1() throws Exception {
        // Composite with response connection attr and element.
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(COMPOSITE_INVALID_RESPONSE_ATTR_ELEMENT));
        
        try {
            Composite composite = (Composite)staxProcessor.read(reader);
        } catch(Exception e) {
            // JMSBindingExceptions are expected with invalid composite.
            if ( !e.getClass().isAssignableFrom( JMSBindingException.class ) )
                throw e;
            // Do assertion to make sure test registers results.
            assertTrue( e.getClass().isAssignableFrom( JMSBindingException.class ) );
        }
    }

    public void testDestinationProperties() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(DEST_PROPS));
        
        Composite composite = (Composite)staxProcessor.read(reader);
        JMSBinding binding = (JMSBinding)   composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        
        assertNotNull(binding);
        assertNotNull(binding.getDestinationProperties());
        assertEquals(2, binding.getDestinationProperties().size());
        BindingProperty bp = binding.getDestinationProperties().get("xxx");
        assertEquals("xxx", bp.getName());
        assertEquals("yyy", bp.getType());
        assertEquals("some value text", bp.getValue().toString().trim());
        BindingProperty bp2 = binding.getDestinationProperties().get("two");
        assertEquals("two", bp2.getName());
        assertEquals(null, bp2.getType());
        assertEquals("bla", bp2.getValue().toString().trim());
    }
    public void testConnectionFactoryProperties() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(CF_PROPS));
        
        Composite composite = (Composite)staxProcessor.read(reader);
        JMSBinding binding = (JMSBinding)   composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        
        assertNotNull(binding);
        assertNotNull(binding.getConnectionFactoryProperties());
        assertEquals(2, binding.getConnectionFactoryProperties().size());
        BindingProperty bp = binding.getConnectionFactoryProperties().get("xxx");
        assertEquals("xxx", bp.getName());
        assertEquals("yyy", bp.getType());
        assertEquals("some value text", bp.getValue().toString().trim());
        BindingProperty bp2 = binding.getConnectionFactoryProperties().get("two");
        assertEquals("two", bp2.getName());
        assertEquals(null, bp2.getType());
        assertEquals("bla", bp2.getValue().toString().trim());
    }
    public void testActivationSpecProperties() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(AS_PROPS));
        
        Composite composite = (Composite)staxProcessor.read(reader);
        JMSBinding binding = (JMSBinding)   composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        
        assertNotNull(binding);
        assertNotNull(binding.getActivationSpecProperties());
        assertEquals(2, binding.getActivationSpecProperties().size());
        BindingProperty bp = binding.getActivationSpecProperties().get("xxx");
        assertEquals("xxx", bp.getName());
        assertEquals("yyy", bp.getType());
        assertEquals("some value text", bp.getValue().toString().trim());
        BindingProperty bp2 = binding.getActivationSpecProperties().get("two");
        assertEquals("two", bp2.getName());
        assertEquals(null, bp2.getType());
        assertEquals("bla", bp2.getValue().toString().trim());
    }
    public void testResponseDestinationProperties() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(RESP_DEST_PROPS));
        
        Composite composite = (Composite)staxProcessor.read(reader);
        JMSBinding binding = (JMSBinding)   composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        
        assertNotNull(binding);
        assertNotNull(binding.getResponseDestinationProperties());
        assertEquals(2, binding.getResponseDestinationProperties().size());
        BindingProperty bp = binding.getResponseDestinationProperties().get("xxx");
        assertEquals("xxx", bp.getName());
        assertEquals("yyy", bp.getType());
        assertEquals("some value text", bp.getValue().toString().trim());
        BindingProperty bp2 = binding.getResponseDestinationProperties().get("two");
        assertEquals("two", bp2.getName());
        assertEquals(null, bp2.getType());
        assertEquals("bla", bp2.getValue().toString().trim());
    }
    public void testResponseConnectionFactoryProperties() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(RESP_CF_PROPS));
        
        Composite composite = (Composite)staxProcessor.read(reader);
        JMSBinding binding = (JMSBinding)   composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        
        assertNotNull(binding);
        assertNotNull(binding.getResponseConnectionFactoryProperties());
        assertEquals(2, binding.getResponseConnectionFactoryProperties().size());
        BindingProperty bp = binding.getResponseConnectionFactoryProperties().get("xxx");
        assertEquals("xxx", bp.getName());
        assertEquals("yyy", bp.getType());
        assertEquals("some value text", bp.getValue().toString().trim());
        BindingProperty bp2 = binding.getResponseConnectionFactoryProperties().get("two");
        assertEquals("two", bp2.getName());
        assertEquals(null, bp2.getType());
        assertEquals("bla", bp2.getValue().toString().trim());
    }
    public void testResponseActivationSpecProperties() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(RESP_AS_PROPS));
        
        Composite composite = (Composite)staxProcessor.read(reader);
        JMSBinding binding = (JMSBinding)   composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        
        assertNotNull(binding);
        assertNotNull(binding.getResponseActivationSpecProperties());
        assertEquals(2, binding.getResponseActivationSpecProperties().size());
        BindingProperty bp = binding.getResponseActivationSpecProperties().get("xxx");
        assertEquals("xxx", bp.getName());
        assertEquals("yyy", bp.getType());
        assertEquals("some value text", bp.getValue().toString().trim());
        BindingProperty bp2 = binding.getResponseActivationSpecProperties().get("two");
        assertEquals("two", bp2.getName());
        assertEquals(null, bp2.getType());
        assertEquals("bla", bp2.getValue().toString().trim());
    }
    public void testOperationPropertiesProperties() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(OP_PROPS_PROPS));
        
        Composite composite = (Composite)staxProcessor.read(reader);
        JMSBinding binding = (JMSBinding)   composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        
        assertNotNull(binding);
        assertNotNull(binding.getOperationPropertiesProperties("op1"));
        assertEquals(2, binding.getOperationPropertiesProperties("op1").size());
        BindingProperty bp = binding.getOperationPropertiesProperties("op1").get("xxx");
        assertEquals("xxx", bp.getName());
        assertEquals("yyy", bp.getType());
        assertEquals("some value text", bp.getValue().toString().trim());
        BindingProperty bp2 = binding.getOperationPropertiesProperties("op1").get("two");
        assertEquals("two", bp2.getName());
        assertEquals(null, bp2.getType());
        assertEquals("bla", bp2.getValue().toString().trim());
    }
    public void testResouceAdapterProperties() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(RES_ADPT_PROPS));
        
        Composite composite = (Composite)staxProcessor.read(reader);
        JMSBinding binding = (JMSBinding)   composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        
        assertNotNull(binding);
        assertEquals("r1", binding.getResourceAdapterName());
        assertNotNull(binding.getResourceAdapterProperties());
        assertEquals(2, binding.getResourceAdapterProperties().size());
        BindingProperty bp = binding.getResourceAdapterProperties().get("xxx");
        assertEquals("xxx", bp.getName());
        assertEquals("yyy", bp.getType());
        assertEquals("some value text", bp.getValue().toString().trim());
        BindingProperty bp2 = binding.getResourceAdapterProperties().get("two");
        assertEquals("two", bp2.getName());
        assertEquals(null, bp2.getType());
        assertEquals("bla", bp2.getValue().toString().trim());
    }

    /**
     * Tests the APIs:
     *     public Set<String> getOperationNames();
     *     public Object getOperationProperty(String opName, String propName );
     * @throws Exception
     */
    public void testOpProperties2() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(OP_PROPERTIES1));
        
        Composite composite = (Composite)staxProcessor.read(reader);
        JMSBinding binding = (JMSBinding)   composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        
        assertNotNull(binding);

        Set<String> opNames = binding.getOperationNames();
        assertEquals( 2, opNames.size() );
        // Recall that order is not guaranteed iterating over a set.
        for (Iterator<String> it=opNames.iterator(); it.hasNext(); ) {
            String opName = it.next();
            assertTrue( opName.equals( "op1") || opName.equals( "op2"));
        }

        Object value = binding.getOperationProperty( "op1", "p1" );
        assertEquals("bla", value);
        value = binding.getOperationProperty( "op1", "intProp" );
        assertEquals(42, ((Integer)value).intValue());
        
        value = binding.getOperationProperty( "op2", "p2" );
        assertEquals("op2bla", value);
        value = binding.getOperationProperty( "op2", "intProp" );
        assertEquals(77, ((Integer)value).intValue());
    }

    /**
     * Tests the APIs:
     *     public Set<String> getOperationNames();
     * Provides no optional properties or sub elements
     * @throws Exception
     */
    public void testOpProperties3() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(OP_NAMES_NO_PROPERTIES1));
        
        Composite composite = (Composite)staxProcessor.read(reader);
        JMSBinding binding = (JMSBinding)   composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        
        assertNotNull(binding);

        Set<String> opNames = binding.getOperationNames();
        assertEquals( 2, opNames.size() );
        // Recall that order is not guaranteed iterating over a set.
        for (Iterator<String> it=opNames.iterator(); it.hasNext(); ) {
            String opName = it.next();
            assertTrue( opName.equals( "op1") || opName.equals( "op2"));
        }
    }

    /**
     * Tests the APIs:
     *     public Set<String> getOperationNames();
     * Provides no optional properties or sub elements
     * @throws Exception
     */
    public void testConfiguredOperations1() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(CONFIGURED_OPERATIONS));
        
        Composite composite = (Composite)staxProcessor.read(reader);
        JMSBinding binding = (JMSBinding)   composite.getComponents().get(0).getServices().get(0).getBindings().get(0);        
        assertNotNull(binding);
        
        OperationsConfigurator opConfig = ((OperationsConfigurator)binding);
        assertEquals(opConfig.getConfiguredOperations().get(0).getRequiredIntents().size(), 2);
    }

    /**
     * Tests the APIs:
     *     public WireFormat getRequstWireFormat();
     *     public WireFormat getResponseWireFormat();
     * 
     * @throws Exception
     */
    public void testWireFormat() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(WIRE_FORMAT));
        
        Composite composite = (Composite)staxProcessor.read(reader);
        JMSBinding binding = (JMSBinding)   composite.getComponents().get(0).getServices().get(0).getBindings().get(0);        
        assertNotNull(binding);
        
        WireFormat requestWireFormat = binding.getRequestWireFormat();
        assertEquals(WireFormatJMSObject.class, requestWireFormat.getClass());
        
        WireFormat responseWireFormat = binding.getResponseWireFormat();
        assertEquals(WireFormatJMSBytes.class, responseWireFormat.getClass());
    }    

    public void testOpPropertiesName() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(OP_PROP_NAME));
        
        Composite composite = (Composite)staxProcessor.read(reader);
        JMSBinding binding = (JMSBinding)   composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        
        assertNotNull(binding);
        assertEquals( "foo", binding.getOperationPropertiesName().getLocalPart() );
    }
}
