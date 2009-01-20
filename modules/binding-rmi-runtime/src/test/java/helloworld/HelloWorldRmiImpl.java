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
package helloworld;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * This class implements the HelloWorld service. The Scope is CONVERSATION to
 * test the RMI Conversational Service and Conversational in interface and
 * Scope("CONVERSATION") in Implementation keep the same instance of the
 * references. But only references conversational are Stateful.
 * HelloWorldService is stateless is keep stateless.
 * HelloWorldConversationalService is stateful.
 * 
 * When BindingTestCase take a instance of HelloWorldRmiImpl it is use the same
 * instance of the HelloWorldRmiImpl to all method invocation, how
 * extConversationService is conversational (stateful) too, your state is keep
 * between call, differently of the extService that is stateless.
 * 
 * @version $Rev$ $Date$
 */
@Service(HelloWorldRmiService.class)
@Scope("CONVERSATION")
public class HelloWorldRmiImpl implements HelloWorldRmiService {
	private HelloWorldService extService;
	private HelloWorldConversationalService extConversationalService;
	private static final String COMPLEMENT = " thro the RMI Reference";

	public HelloWorldService getExtService() {
		return extService;
	}

	@Reference
	public void setExtService(HelloWorldService extService) {
		this.extService = extService;
	}

	@Reference
	public void setExtConversationalService(
			HelloWorldConversationalService extConversationalService) {
		this.extConversationalService = extConversationalService;
	}

	public String sayRmiHello(String name) {
		return extService.sayHello(name) + COMPLEMENT;
	}

	public String sayRmiHi(String name, String greeter) throws HelloException {
		return extService.sayHi(name, greeter) + COMPLEMENT;
	}

	public String getConversationalHello() {
		return extConversationalService.getHello() + COMPLEMENT;
	}

	public String getConversationalHi() {
		return extConversationalService.getHi() + COMPLEMENT;
	}

	public void sayRmiConversationalHello(String name) {
		extConversationalService.sayHello(name);

	}

	public void sayRmiConversationalHi(String name, String greeter)
			throws HelloException {
		extConversationalService.sayHi(name, greeter);
	}

}
