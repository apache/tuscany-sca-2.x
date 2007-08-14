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

package org.apache.tuscany.sca.binding.axis2.itests ;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;

public class HelloWorldNoWSDLTestCase extends TestCase {

    private SCADomain domain;

    public void testHelloWorld() throws Exception {
        HelloWorld helloWorld = domain.getService(HelloWorld.class, "HelloWorldComponent");
        assertEquals("Hello petra", helloWorld.getGreetings("petra"));
    }

    /**
     * Test a a WS call with a complex type
     */
    public void testEchoFoo() throws Exception {
        Echo echo = domain.getService(Echo.class, "EchoComponent");
       
        Foo f = new Foo();
        Bar b1 = new Bar();
        b1.setS("petra");
        b1.setX(1);
        b1.setY(new Integer(2));
        b1.setB(Boolean.TRUE);
        Bar b2 = new Bar();
        b2.setS("beate");
        b2.setX(3);
        b2.setY(new Integer(4));
        b2.setB(Boolean.FALSE);
        f.setBars(new Bar[] { b1, b2} );
       
        Foo f2 = echo.echoFoo(f);

        assertEquals("petra", f2.getBars()[0].getS());
        assertEquals(1, f2.getBars()[0].getX());
        assertEquals(2, f2.getBars()[0].getY().intValue());
        assertTrue(f2.getBars()[0].getB().booleanValue());
        assertEquals("beate", f2.getBars()[1].getS());
        assertEquals(3, f2.getBars()[1].getX());
        assertEquals(4, f2.getBars()[1].getY().intValue());
        assertFalse(f2.getBars()[1].getB().booleanValue());
       
    }
   
    protected void setUp() throws Exception {
        domain = SCADomain.newInstance("org/apache/tuscany/sca/binding/axis2/itests/HelloWorldNoWSDL.composite");
    }
   
    protected void tearDown() throws Exception {
        domain.close();
    }

}
