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

import org.osoa.sca.annotations.Conversational;

/**
 * This is the business interface of the HelloWorld greetings service. This
 * interface is Conversational to test the RMI Conversational Service and
 * Conversational in interface and Scope("CONVERSATION") in Implementation keep
 * the same instance of the references. But only references conversational are
 * Stateful. HelloWorldService is stateless is keep stateless.
 * HelloWorldConversationalService is stateful.
 * 
 * When BindingTestCase take a instance of HelloWorldRmiImpl it is use the same
 * instance of the HelloWorldRmiImpl in all method invocation, how
 * extConversationService is conversational (stateful) too, your state is keep
 * between call, differently of the extService that is stateless.
 * 
 * @version $Rev$ $Date$
 */
@Conversational
public interface HelloWorldRmiService {

	String sayRmiHello(String name);

	String sayRmiHi(String name, String greeter) throws HelloException;

	void sayRmiConversationalHello(String name);

	void sayRmiConversationalHi(String name, String greeter)
			throws HelloException;

	String getConversationalHello();

	String getConversationalHi();
}
