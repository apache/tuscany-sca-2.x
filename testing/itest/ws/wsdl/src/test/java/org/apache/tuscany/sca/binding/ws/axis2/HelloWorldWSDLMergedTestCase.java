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

package org.apache.tuscany.sca.binding.ws.axis2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMText;
import org.apache.tuscany.sca.core.assembly.impl.RuntimeEndpointImpl;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.impl.NodeImpl;
import org.junit.Ignore;

public class HelloWorldWSDLMergedTestCase extends TestCase {

    private NodeFactory nodeFactory;
    private NodeImpl node;
    private HelloWorldOM helloWorld;

    @Override
    protected void setUp() throws Exception {
        String contribution = "target/classes";
        nodeFactory = NodeFactory.newInstance();
        // have to set this to stop the node factory killing itself when we stop and re-start the node
        nodeFactory.setAutoDestroy(false);
        node = (NodeImpl)nodeFactory.createNode("org/apache/tuscany/sca/binding/ws/axis2/helloworld-om-merged.composite", new Contribution("test", contribution));
        node.start();
        helloWorld = node.getService(HelloWorldOM.class, "HelloWorldWSDLMergedComponent");
    }
    
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
    
    public void testHelloWorldRepeating() throws Exception {
        for (int i = 0; i < 2; i++){
            OMFactory fac = OMAbstractFactory.getOMFactory();
            OMElement requestOM = fac.createOMElement("getGreetings", "http://helloworld-om", "helloworld");
            OMElement parmE = fac.createOMElement("name", "http://helloworld-om", "helloworld");
            requestOM.addChild(parmE);
            parmE.addChild(fac.createOMText("petra"));
            OMElement responseOM = helloWorld.getGreetings(requestOM);
            OMElement child = (OMElement)responseOM.getFirstElement();
            Assert.assertEquals("Hello petra", ((OMText)child.getFirstOMChild()).getText());            
            
            node.stop(); 
            node.start();
            
            helloWorld = node.getService(HelloWorldOM.class, "HelloWorldWSDLMergedComponent");
        }
    }
   
    
/*
    public void testWSDLWrite(){
        RuntimeEndpointImpl endpoint = (RuntimeEndpointImpl)node.getDomainComposite().getComponents().get(0).getServices().get(0).getEndpoints().get(0);
        try
        {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(); 
            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
            objectStream.writeObject(endpoint);
            objectStream.close();
            System.out.println(byteStream.toString());
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }        
    }
*/     
    
    @Override
    protected void tearDown() throws Exception {
        node.stop();
    }

}
