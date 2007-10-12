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

package org.apache.tuscany.sca.implementation.data;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * Tests the DAS service
 * 
 * @version $Rev$ $Date$
 */
public class DATATestCase extends TestCase {
    private SCADomain scaDomain;
    private DATA dataService;
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    protected void setUp() throws Exception {
        scaDomain = SCADomain.newInstance("data.composite");
        dataService = scaDomain.getService(DATA.class, "DataComponent/COMPANY");
    }

    /**
     * @throws java.lang.Exception
     */
    @Override
    protected void tearDown() throws Exception {
        scaDomain.close();
    }

    public void testGet() throws Exception {
        System.out.println("testGet");
        
        XMLStreamReader reader = dataService.get(null);

        QName element = null;
        reader.next();
        int increment = 0;
        while (reader.hasNext()) {
            int event = reader.getEventType();
            switch (event) {
                case START_ELEMENT:
                    increment= increment + 3;
                    element = reader.getName();
                    System.out.println(fillSpace(increment) + element.toString());
                    break;
                case XMLStreamConstants.CHARACTERS:
                    System.out.println(fillSpace(increment) + " :: " + reader.getText() + " :: ");
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    element = reader.getName();
                    System.out.println(fillSpace(increment) + element.toString());
                    increment = increment - 3;
                    break;        
            }
            
            //Read the next element
            if (reader.hasNext()) {
                reader.next();
            }
        }
    }
    
    public void testGetByID() throws Exception {
        Integer companyID = new Integer(51);

        XMLStreamReader reader = dataService.get(companyID.toString());
        assertNotNull(reader);
    }
    
    private String fillSpace(int number){
        StringBuffer sb = new StringBuffer(number);
        for(int i=0; i<number; i++) {
            sb.append(" ");
        }
        
        return sb.toString();
    }

}
