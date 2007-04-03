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
package org.apache.tuscany.binding.jms;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.parsers.ParserConfigurationException;

import org.activemq.broker.BrokerContainer;
import org.activemq.broker.impl.BrokerContainerImpl;
import org.activemq.store.vm.VMPersistenceAdapter;
import org.apache.tuscany.test.SCATestCase;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;
import org.xml.sax.SAXException;

public class JMSBindingTestCaseX extends SCATestCase {

    private IntroService introService;
    private BrokerContainer broker;

    protected static final String REQUEST_XML =
        "<ns:getGreetings xmlns:ns=\"http://helloworld\"><ns:name>petra</ns:name></ns:getGreetings>";
    protected static final String REPLY_XML =
        "<ns1:getGreetingsResponse xmlns:ns1=\"http://helloworld\"><ns1:getGreetingsReturn>Hello petra</ns1:getGreetingsReturn></ns1:getGreetingsResponse>";

    public void testJMSBinding() throws InvocationTargetException, SAXException, IOException,
        ParserConfigurationException, NamingException, JMSException {
        String reply = introService.greet("Rajith");
        assertEquals("Hello Rajith", reply);

        // TODO: the rest should be in a seperate test method but that doesn't
        // work as you get broker conflicts
        JMSTargetInvoker invoker = createJMSInvoker();
        Object[] response = (Object[])invoker.invokeTarget(new Object[] {REQUEST_XML}, (short)0);

        Diff diff = XMLUnit.compareXML(REPLY_XML, response[0].toString());
        assertTrue(diff.toString(), diff.similar());

    }

    private JMSTargetInvoker createJMSInvoker() throws NamingException, JMSException {
        JMSBindingDefinition binding = new JMSBindingDefinition();
        binding.setInitialContextFactoryName("org.activemq.jndi.ActiveMQInitialContextFactory");
        binding.setConnectionFactoryName("ConnectionFactory");
        binding.setJNDIProviderURL("tcp://localhost:61616");
        binding.setDestinationName("dynamicQueues/HelloworldServiceQueue");
        binding.setTimeToLive(3000);
        binding.setXMLFormat(true);
        JMSResourceFactory rf = new SimpleJMSResourceFactory(binding);
        Destination requestDest = rf.lookupDestination(binding.getDestinationName());
        DefaultOperationAndDataBinding odb = new DefaultOperationAndDataBinding(binding);
        JMSTargetInvoker invoker = new JMSTargetInvoker(rf, binding, "getGreetings", odb, odb, requestDest, null);
        return invoker;
    }

    protected void setUp() throws Exception {
        startBroker();
        setApplicationSCDL(IntroService.class, "META-INF/sca/default.scdl");
        addExtension("jms.binding", getClass().getClassLoader().getResource("META-INF/sca/jms.system.scdl"));
        addExtension("idl.wsdl", getClass().getClassLoader().getResource("META-INF/sca/idl.wsdl.scdl"));
        addExtension("databinding.axiom", getClass().getResource("/META-INF/sca/databinding.axiom.scdl"));
        super.setUp();
        CompositeContext context = CurrentCompositeContext.getContext();
        introService = context.locateService(IntroService.class, "IntroServiceComponent");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        broker.stop();
    }

    public static void main(String[] args) {
        JMSBindingTestCaseX test = new JMSBindingTestCaseX();
        try {
            test.setUp();
            test.testJMSBinding();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startBroker() throws Exception {
        broker = new BrokerContainerImpl("JMS BindingDefinition Test");
        // configure the broker
        broker.addConnector("tcp://localhost:61616");
        broker.setPersistenceAdapter(new VMPersistenceAdapter());
        broker.start();
    }
}
