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

package itest.client.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import itest.common.intf.ClientIntf;
import itest.privatecopy.intf.ServiceIntf;
import itest.privatecopy.types.Name;

import org.oasisopen.sca.annotation.Reference;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class ClientImpl implements ClientIntf {

    @Reference
    public ServiceIntf service;
    
    @Override
    public void callJAXBCrossContribution() {    
        Name name = new Name();
        name.setFirstName("Leora");
        name.setLastName("Mayora");
        Name retVal = service.greet(name);
        assertEquals("Hi Leora", retVal.getFirstName());
        assertEquals("Ms. Mayora", retVal.getLastName());        
    }

    @Override
    public void callObjectGraphCheck(int caseNumber) {
        switch (caseNumber) {
        case 1: 
            Name name1 = new Name();
            name1.setFirstName("First");
            name1.setLastName("Last");
            boolean retVal1 = service.areNamesTheSameObjects(name1, name1);
            assertTrue(retVal1);        
            break;
        case 2: 
            Name name2a = new Name();
            name2a.setFirstName("First");
            name2a.setLastName("Last");
            Name name2b = new Name();
            name2b.setFirstName("First");
            name2b.setLastName("Last");
            boolean retVal2 = service.areNamesTheSameObjects(name2a, name2b);
            assertFalse(retVal2);
            break;
            
        }
    }
    
    @Override
    public void callJSON() {    
        Name name = new Name();
        name.setFirstName("Jason");
        name.setLastName("Nosaj");
        String retVal = service.greetJSON(name);
        assertEquals("good", retVal);
    }
    
//    @Override
    public void callDOM() {    
        Name name = new Name();
        name.setFirstName("DOM");
        name.setLastName("MOD");
        String retVal = service.greetDOM(name);
        //assertEquals("good", retVal);
    }
    
    @Override
    public void testRoundTripDOMIdentity() {    	
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();

            String nameString = "<?xml version=\"1.0\" ?>" + 
            					"<name>BOB</name>";
            InputSource is = new InputSource( new StringReader(nameString) );
            Document doc = builder.parse(is);
            Node name = doc.getDocumentElement();
            Node retVal = service.returnDOM(name);
            assertNotSame("PBV should result in different object", name, retVal);
        } catch (Exception e) {
        	throw new RuntimeException(e);
        }

    }
    
    
}
