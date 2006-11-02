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

import org.activemq.broker.BrokerContainer;
import org.activemq.broker.impl.BrokerContainerImpl;
import org.apache.tuscany.test.SCATestCase;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

public class JMSBindingTestCase extends SCATestCase{

	private IntroService introService;
	private BrokerContainer broker;
	
    public void testJMSBinding() {
    	String reply = introService.greet("Rajith");
    	assertEquals("Hello Rajith",reply);
    }
	
	protected void setUp() throws Exception {
		startBroker();
        setApplicationSCDL(IntroService.class, "META-INF/sca/default.scdl");
        addExtension("jms.binding", getClass().getClassLoader().getResource("META-INF/sca/jms.system.scdl"));
        super.setUp();
        CompositeContext context = CurrentCompositeContext.getContext();
        introService = context.locateService(IntroService.class, "IntroServiceComponent");
    }
	
	
	
	protected void tearDown() throws Exception {		
		super.tearDown();
		broker.stop();
	}

	public static void main(String[] args){
		JMSBindingTestCase test = new JMSBindingTestCase();
		try {
			test.setUp();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		test.testJMSBinding();
	}
	
	private void startBroker() throws Exception{
		broker = new BrokerContainerImpl("JMS Binding Test");
        // configure the broker
		broker.addConnector("tcp://localhost:61616");
		broker.start();
	}
}
