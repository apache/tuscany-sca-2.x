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

package org.apache.tuscany.sca.policy.xml;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.policy.xml.ws.WSPolicyProcessor;

import junit.framework.TestCase;

/**
 * Test reading SCA XML assembly documents.
 * 
 * @version $Rev$ $Date$
 */
public class WSPolicyProcessorTestCase extends TestCase {
	
	private static final String VALID_WS_POLICY = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		    + "<definitions xmlns=\"http://www.osoa.org/xmlns/sca/1.0\""
 			+ " targetNamespace=\"http://test\""
 			+ " xmlns:test=\"http://test\""
 			+ " xmlns:sca=\"http://www.osoa.org/xmlns/sca/1.0\">"
            + " "
            + " <policySet name=\"SecureWSPolicy\""
            + " provides=\"test:confidentiality\""
 	        + " appliesTo=\"sca:binding.ws\""
 	        + " xmlns=\"http://www.osoa.org/xmlns/sca/1.0\""
 	        + " xmlns:sp=\"http://schemas.xmlsoap.org/ws/2002/12/secext\""
 	        + " xmlns:wsp=\"http://schemas.xmlsoap.org/ws/2004/09/policy\">"
	        + " <wsp:Policy>"
	        + "    <wsp:ExactlyOne>"
	        + "       <wsp:All>"
	        + "          <sp:SecurityToken>"
	        + "             <sp:TokenType>sp:X509v3</sp:TokenType>"
	        + "          </sp:SecurityToken>"
	        + "          <sp:UsernameToken />" 
	        + "           <sp:SignedParts />"
	        + "          <sp:EncryptedParts>"
	        + "             <sp:Body />"
	        + "          </sp:EncryptedParts>"
	        + "          <sp:TransportBinding>"
	        + "             <sp:IncludeTimeStamp />"
	        + "          </sp:TransportBinding>"
	        + "        </wsp:All>"
	        + "    </wsp:ExactlyOne>"
            + " </wsp:Policy>"
            + " </policySet>"
            + " </definitions>";
	
	private XMLInputFactory inputFactory;
        
    @Override
    public void setUp() throws Exception {
        inputFactory = XMLInputFactory.newInstance();
        
    }

    public void testReadWsPolicy() throws Exception {
    	XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(VALID_WS_POLICY));
    	WSPolicyProcessor processor = new WSPolicyProcessor();
    	Object artifact = null;
    	
        QName name = null;
        reader.next();
        while ( true ) {
            int event = reader.getEventType();
            switch (event) {
                case START_ELEMENT: {
                	name = reader.getName();
                	
                	if(WSPolicyProcessor.WS_POLICY_QNAME.equals(name)) {
                		 artifact = processor.read(reader);
                	}
                	
                    break;
                }
            }
            
            if ( reader.hasNext() ) {
                reader.next();
            } else {
                break;
            }
        }
        assertNotNull(artifact);
    }
}
