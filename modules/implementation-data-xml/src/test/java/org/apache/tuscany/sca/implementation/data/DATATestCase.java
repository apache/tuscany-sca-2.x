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

import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.sca.databinding.xml.XMLStreamReader2String;
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
    
    public void testDeleteByID() throws Exception {
        System.out.println(">testDeleteByID");
        
        Integer companyID = new Integer(52);
        int dRows = dataService.delete(companyID.toString());
        System.out.println("Deleted rows: "+dRows);
    }
    
    public void testGet() throws Exception {
        System.out.println(">testGet");
        
        XMLStreamReader reader = dataService.get(null);
        String xml = new XMLStreamReader2String().transform(reader, null);
        System.out.println(xml);
        reader.close();
    }
    
    public void testGetByID() throws Exception {
        System.out.println(">testGetByID");
        
        Integer companyID = new Integer(51);
        
        XMLStreamReader reader = dataService.get(companyID.toString());
        assertNotNull(reader);
        String xml = new XMLStreamReader2String().transform(reader, null);
        System.out.println(xml);
        reader.close();
    }
    
    public void testDelete() throws Exception {
        System.out.println(">testDelete");
        
        int dRows = dataService.delete(null);
        System.out.println("Deleted rows: "+dRows);
    }
    
}
