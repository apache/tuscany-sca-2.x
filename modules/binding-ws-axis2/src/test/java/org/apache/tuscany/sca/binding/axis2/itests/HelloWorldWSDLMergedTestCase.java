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

package org.apache.tuscany.sca.binding.axis2.itests;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMText;
import org.apache.tuscany.sca.host.embedded.SCADomain;

public class HelloWorldWSDLMergedTestCase extends TestCase {

    private SCADomain domain;
    private HelloWorldOM helloWorld;

    public void testHelloWorld() throws Exception {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMElement requestOM = fac.createOMElement("getGreetings", "http://helloworld-om", "helloworld");
        OMElement parmE = fac.createOMElement("name", "http://helloworld-om", "helloworld");
        requestOM.addChild(parmE);
        parmE.addChild(fac.createOMText("petra"));
        OMElement responseOM = helloWorld.getGreetings(requestOM);
        OMElement child = (OMElement)responseOM.getFirstElement();
        Assert.assertEquals("Hello petra", ((OMText)child.getFirstOMChild()).getText());
    }

    protected void setUp() throws Exception {
        domain = SCADomain.newInstance("org/apache/tuscany/sca/binding/axis2/itests/helloworld-om-merged.composite");
        helloWorld = domain.getService(HelloWorldOM.class, "HelloWorldWSDLMergedComponent");
    }
    
    protected void tearDown() throws Exception {
        domain.close();
    }

}
