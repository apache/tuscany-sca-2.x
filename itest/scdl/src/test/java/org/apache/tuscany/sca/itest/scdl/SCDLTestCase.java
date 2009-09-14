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

package org.apache.tuscany.sca.itest.scdl;

import java.io.InputStream;

import javax.xml.stream.XMLStreamException;

import junit.framework.Assert;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jsonp.JSONPBinding;
import org.apache.tuscany.sca.binding.rmi.RMIBinding;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.scdl.SCDLUtils;
import org.junit.Test;

/**
 * Test case for reading and writing a composite
 */
public class SCDLTestCase {

    @Test
    public void testRead() throws ContributionReadException, XMLStreamException {
        InputStream r = getClass().getClassLoader().getResourceAsStream("test.composite");
        Composite composite = SCDLUtils.readComposite(r);
        Assert.assertNotNull(composite);

        Component JavaComp = composite.getComponent("JavaComponent");
        Assert.assertNotNull(JavaComp);

        Component bpelComp = composite.getComponent("BPELComponent");
        Assert.assertNotNull(bpelComp);

        Service jsonpService = composite.getService("JSONPService");
        Assert.assertNotNull(jsonpService);
        Assert.assertEquals(1, jsonpService.getBindings().size());
        JSONPBinding jsonpBinding = jsonpService.getBinding(JSONPBinding.class);
        Assert.assertNotNull(jsonpBinding);
    
        Service jmsService = composite.getService("JMSService");
        Assert.assertNotNull(jmsService);
        Assert.assertEquals(1, jmsService.getBindings().size());
        JMSBinding jmsBinding = jmsService.getBinding(JMSBinding.class);
        Assert.assertNotNull(jmsBinding);

        Service rmiService = composite.getService("RMIService");
        Assert.assertNotNull(rmiService);
        Assert.assertEquals(1, rmiService.getBindings().size());
        RMIBinding rmiBinding = rmiService.getBinding(RMIBinding.class);
        Assert.assertNotNull(rmiBinding);

// TODO: WS binding drags in entire runtime
//        Service wsService = composite.getService("WSService");
//        Assert.assertNotNull(wsService);
//        Assert.assertEquals(1, wsService.getBindings().size());
//        WSBinding wsBinding = wsService.getBinding(WSBinding.class);
//        Assert.assertNotNull(wsBinding);
    }

}
