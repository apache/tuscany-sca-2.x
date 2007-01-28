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

package helloworldOM;

import junit.framework.Assert;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.tuscany.test.SCATestCase;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

/**
 * Test case for helloworld web service client 
 */
public class HelloWorldWSClient extends SCATestCase {

    private HelloWorldService helloWorldService;

    @Override
    protected void setUp() throws Exception {
        // FIXME: Adding extensions programtically
        addExtension("org.apache.tuscany.binding.axis2.WebServiceBinding", getClass().getClassLoader().getResource("META-INF/tuscany/binding.axis2.scdl"));
        super.setUp();
        CompositeContext compositeContext = CurrentCompositeContext.getContext();
        helloWorldService = compositeContext.locateService(HelloWorldService.class, "HelloWorldServiceComponent");
    }

    public void testWSClient() {
        OMFactory fac= OMAbstractFactory.getOMFactory();
        OMElement requestOM = fac.createOMElement("getGreetings", "http://helloworldOM", "helloworld");
        OMElement parmE = fac.createOMElement("name", "http://helloworldOM", "helloworld");
        requestOM.addChild(parmE);
        parmE.addChild(fac.createOMText("petra"));
        OMElement responseOM = helloWorldService.getGreetings(requestOM);
        Assert.assertEquals("Hello John", "");
    }

}
