/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.message.tests;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.tuscany.common.resource.loader.ResourceLoaderFactory;
import org.apache.tuscany.core.addressing.EndpointReference;
import org.apache.tuscany.core.addressing.impl.AddressingFactoryImpl;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;
import org.apache.tuscany.sdo.helper.HelperProviderImpl;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLHelper;

/**
 * FIXME commented out
 */
public class AddressingTestCase extends TestCase {
	
	/**
	 * 
	 */
	public AddressingTestCase() {
		super();
	}
	
	public void testAddressing() throws IOException {
		
//		EndpointReference to=new AddressingFactoryImpl().createEndpointReference();
//		to.setAddress("http://org.apache.tuscany/test");
//		
//		String id=new AddressingFactoryImpl().createMessageID();
//		
//		Message message=new MessageFactoryImpl().createMessage();
//		message.setMessageID(id);
//		message.setTo(to);
//		message.setBody("Hello World");
//		
//		ConfiguredResourceSet configuredResourceSet=new ConfiguredResourceSetImpl(ResourceLoaderFactory.getResourceLoader(getClass().getClassLoader()));
//		XMLHelper helper=new HelperProviderImpl(configuredResourceSet).getXMLHelper();
//		helper.print((DataObject)message, System.out);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
	}

}
