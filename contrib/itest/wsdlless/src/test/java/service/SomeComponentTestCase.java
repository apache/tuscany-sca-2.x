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
package service;

import static org.junit.Assert.assertEquals;

import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import service.generated.SomeServiceService;

/**
 * @author pxk3
 *
 */
public class SomeComponentTestCase {
    private static SCADomain scaDomain;
    private static SomeService someService;

    @BeforeClass
    public static void setUp() throws Exception {
        scaDomain = SCADomain.newInstance("some.composite");
        someService = scaDomain.getService(SomeService.class, "SomeServicesComponent");
    }

    @AfterClass
    public static void tearDown() throws Exception {
        scaDomain.close();
    }

    @Test
    public void testGetUsingMoreComplexObject() throws Exception {
        String stringParam = "ABC";
        URL url = new URL("http://localhost:8085/SomeServices?wsdl");
        QName serviceQName = new QName("http://service/", "SomeServiceService");
        SomeServiceService service = new SomeServiceService(url, serviceQName);
        service.generated.SomeService proxy = service.getSomeServicePort();
        service.generated.AnObject obj = proxy.getUsingString(stringParam);
        assertEquals(stringParam + "123", obj.getSomeRetValue());

        service.generated.MoreComplexObject obj2 = new service.generated.MoreComplexObject();
        obj2.setStringParam(stringParam);
        obj2.setStringParam2("2");
        obj2.setIntParam(new Integer(0));

        obj = proxy.getUsingMoreComplexObject(obj2);
        assertEquals(stringParam + "123", obj.getSomeRetValue());
    }

    @Test
    public void testLocal() {
        String stringParam = "1234";
        MoreComplexObject moreComplexParam = new MoreComplexObject();
        moreComplexParam.setStringParam(stringParam);

        AnObject anObject = someService.getUsingMoreComplexObject(moreComplexParam);

        assertEquals(stringParam + "123", anObject.getSomeRetValue());
    }

}
