/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.     
 */
package helloworld;

import org.activemq.broker.BrokerContainer;
import org.activemq.broker.impl.BrokerContainerImpl;
import org.activemq.store.vm.VMPersistenceAdapter;
import org.apache.tuscany.test.SCATestCase;
import org.osoa.sca.CurrentCompositeContext;


/**
 * @author administrator
 */
public class HelloWorldJmsServer extends SCATestCase{
    private BrokerContainer broker;
    
    public HelloWorldJmsServer() {
        if (broker == null)
            broker = new  BrokerContainerImpl("JMS BindingDefinition Test");
    }
    
    private void startBroker() throws Exception {
        // configure the broker
        broker.addConnector("tcp://localhost:61616");
        broker.setPersistenceAdapter(new VMPersistenceAdapter());
        broker.start();
    }
    
    protected void setUp() throws Exception {
        startBroker();
        setApplicationSCDL(HelloWorldService.class, "META-INF/sca/default.scdl");
        addExtension("jms.binding", getClass().getClassLoader().getResource("META-INF/sca/jms.system.scdl"));
        addExtension("idl.wsdl", getClass().getClassLoader().getResource("META-INF/sca/idl.wsdl.scdl"));
        addExtension("databinding.axiom", getClass().getResource("/META-INF/sca/databinding.axiom.scdl"));
        super.setUp();
        CurrentCompositeContext.getContext();
    }
    public void testJMSServer(){
        try {
            System.out.println("Service Started and Running...");
            System.out.println("Hit ENTER to exit");
            System.in.read();
            System.out.println("Server Stopped!");
            System.exit(0);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        try {
            HelloWorldJmsServer helloWorldJmsServer = new HelloWorldJmsServer();
            helloWorldJmsServer.setUp();
            helloWorldJmsServer.testJMSServer();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
