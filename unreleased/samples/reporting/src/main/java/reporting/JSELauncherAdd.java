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

package reporting;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.assembly.impl.RuntimeEndpointImpl;
import org.apache.tuscany.sca.core.assembly.impl.RuntimeEndpointReferenceImpl;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.deployment.Deployer;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.configuration.ContributionConfiguration;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.apache.tuscany.sca.node.impl.NodeFactoryImpl;
import org.apache.tuscany.sca.node.impl.NodeImpl;
import org.apache.tuscany.sca.runtime.DomainRegistryFactory;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.ExtensibleDomainRegistryFactory;
import org.apache.tuscany.sca.runtime.RuntimeProperties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.sun.jmx.remote.util.Service;
import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;

import calculator.CalculatorService;

/**
 * This client program shows how to extract useful(?) information from the Tuscany SCA runtime
 */
public class JSELauncherAdd {
    
    private static NodeFactory nodeFactory;
    private static Node node1;
    private static Node node2;
    private static CalculatorService calculator;
    
    
    public static void main(String[] args) throws Exception {
        JSELauncherAdd launcher = new JSELauncherAdd ();
        launcher.setUp();
       
        launcher.waitForInput();

        launcher.tearDown();
    }
    
    @BeforeClass
    public static void setUp() throws Exception {
        try {
/* new      
            org.apache.tuscany.sca.node2.NodeFactory nodeFactoryNew = org.apache.tuscany.sca.node2.NodeFactory.newInstance();
            org.apache.tuscany.sca.node2.Node node1New = nodeFactoryNew.createNode();
            node1New.installContribution("../domain/distributed-calculator/contribution-add/target/classes");
            
*/

/* old */           
            // TUSCANY-3675 - push hazelcast config into factory as adding it to URI doesn't work
            Properties properties = new Properties();
            properties.setProperty("bind", "192.168.0.2");
            nodeFactory = NodeFactory.newInstance(properties); 
            
            // TUSCANY-3675 - push hazelcast config into factory as adding it to URI doesn't work 
            //node1 = nodeFactory.createNode(new Contribution("c1", "../domain/distributed-calculator/contribution-add/target/classes"));            
            //node1 = nodeFactory.createNode(URI.create("tuscany:default?listen=127.0.0.1:14820"), "../domain/distributed-calculator/contribution-add/target/classes");            
            //node1 = nodeFactory.createNode(URI.create("tuscany:default"), "../domain/distributed-calculator/contribution-add/target/classes");
            node1 = nodeFactory.createNode(new File("./target/classes/node-add.xml").toURL());
                    
            node1.start();

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
         node1.stop();
    }

    @Test
    @Ignore
    public void waitForInput(){ 
        System.out.println("Press key to end");
        try {
            System.in.read();
        } catch(Exception ex){
            // do nothing
        }
    }   
    
}
