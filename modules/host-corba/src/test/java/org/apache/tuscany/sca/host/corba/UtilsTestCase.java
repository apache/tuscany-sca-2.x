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

package org.apache.tuscany.sca.host.corba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.host.corba.CorbanameDetails;
import org.apache.tuscany.sca.host.corba.CorbaHostUtils;
import org.junit.Test;

public class UtilsTestCase {

    private void assertDetailsAreOk(CorbanameDetails details, String host, int port, String nameService, List<String> namePath) {
        assertTrue(details.getHost().equals(host));
        assertTrue(details.getNameService().equals(nameService));
        assertTrue(details.getPort() == port);
        assertTrue(details.getNamePath().size() == namePath.size());
        for (int i = 0; i < namePath.size(); i++) {
            assertTrue(details.getNamePath().get(i).equals(namePath.get(i)));
        }
    }

    @Test
    public void test_validCorbaname() {
        String testUri = null;
        CorbanameDetails details = null;
        List<String> namePath = null;
        
        testUri = "corbaname:ignore:host:1234/Service#Reference";
        details = CorbaHostUtils.getServiceDetails(testUri);
        namePath = new ArrayList<String>();
        namePath.add("Reference");
        assertDetailsAreOk(details, "host", 1234, "Service", namePath);

        testUri = "corbaname:ignore:host:/Service#Reference";
        details = CorbaHostUtils.getServiceDetails(testUri);
        namePath = new ArrayList<String>();
        namePath.add("Reference");
        assertDetailsAreOk(details, "host", CorbaHostUtils.DEFAULT_PORT, "Service", namePath);
        
        testUri = "corbaname:ignore:host/Service#Reference";
        details = CorbaHostUtils.getServiceDetails(testUri);
        namePath = new ArrayList<String>();
        namePath.add("Reference");
        assertDetailsAreOk(details, "host", CorbaHostUtils.DEFAULT_PORT, "Service", namePath);
        
        testUri = "corbaname:ignore:/Service#Reference";
        details = CorbaHostUtils.getServiceDetails(testUri);
        namePath = new ArrayList<String>();
        namePath.add("Reference");
        assertDetailsAreOk(details, CorbaHostUtils.DEFAULT_HOST, CorbaHostUtils.DEFAULT_PORT, "Service", namePath);
        
        testUri = "corbaname:ignore/Service#Reference";
        details = CorbaHostUtils.getServiceDetails(testUri);
        namePath = new ArrayList<String>();
        namePath.add("Reference");
        assertDetailsAreOk(details, CorbaHostUtils.DEFAULT_HOST, CorbaHostUtils.DEFAULT_PORT, "Service", namePath);

        testUri = "corbaname:/Service#Reference";
        details = CorbaHostUtils.getServiceDetails(testUri);
        namePath = new ArrayList<String>();
        namePath.add("Reference");
        assertDetailsAreOk(details, CorbaHostUtils.DEFAULT_HOST, CorbaHostUtils.DEFAULT_PORT, "Service", namePath);
        
        testUri = "corbaname/Service#Reference";
        details = CorbaHostUtils.getServiceDetails(testUri);
        namePath = new ArrayList<String>();
        namePath.add("Reference");
        assertDetailsAreOk(details, CorbaHostUtils.DEFAULT_HOST, CorbaHostUtils.DEFAULT_PORT, "Service", namePath);
        
        testUri = "corbaname#Reference";
        details = CorbaHostUtils.getServiceDetails(testUri);
        namePath = new ArrayList<String>();
        namePath.add("Reference");
        assertDetailsAreOk(details, CorbaHostUtils.DEFAULT_HOST, CorbaHostUtils.DEFAULT_PORT, CorbaHostUtils.DEFAULT_NAME_SERVICE, namePath);

        testUri = "corbaname#Parent/Mid/Reference";
        details = CorbaHostUtils.getServiceDetails(testUri);
        namePath = new ArrayList<String>();
        namePath.add("Parent");
        namePath.add("Mid");
        namePath.add("Reference");
        assertDetailsAreOk(details, CorbaHostUtils.DEFAULT_HOST, CorbaHostUtils.DEFAULT_PORT, CorbaHostUtils.DEFAULT_NAME_SERVICE, namePath);
    }
    
    @Test
    public void test_invalidCorbaname() {
        String testUri = null;
     
        try {
            testUri = "this.string.should.not.appear.in.the.beggining:ignore:host:1234/Service#Reference";
            CorbaHostUtils.getServiceDetails(testUri);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
        
        try {
            testUri = "corbaname:ignore:host:1234/Service#";
            CorbaHostUtils.getServiceDetails(testUri);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
    }
    
    @Test
    public void test_creatingCorbanameURI() {
        String uri = CorbaHostUtils.createCorbanameURI("SomeName", "SomeHost", 1000);
        assertEquals("corbaname::SomeHost:1000#SomeName", uri);
    }
}
