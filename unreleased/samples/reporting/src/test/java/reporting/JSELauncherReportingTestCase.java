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
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

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
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jmx.remote.util.Service;
import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;

import calculator.CalculatorService;

/**
 * This client program shows how to extract useful(?) information from the Tuscany SCA runtime
 */
public class JSELauncherReportingTestCase {
    
    private static NodeFactory nodeFactory;
    private static Node node1;
    private static Node node2;
    private static CalculatorService calculator;
    
    
    public static void main(String[] args) throws Exception {
        JSELauncherReportingTestCase launcher = new JSELauncherReportingTestCase();
        launcher.setUp();
       
        launcher.callCalulator();
/*         
        launcher.listNodes();
        launcher.listNodeConfigurations();
        launcher.listContributions();
        launcher.listDomainDefinitions();        
        launcher.listEndpoints();
*/        
        launcher.listWires();
        launcher.tearDown();
    }
    
    @BeforeClass
    public static void setUp() throws Exception {
        try {
/* new      
            org.apache.tuscany.sca.node2.NodeFactory nodeFactoryNew = org.apache.tuscany.sca.node2.NodeFactory.newInstance();
            org.apache.tuscany.sca.node2.Node node1New = nodeFactoryNew.createNode();
            node1New.installContribution("../domain/distributed-calculator/contribution-add/target/classes");
            
            org.apache.tuscany.sca.node2.Node node2New = nodeFactoryNew.createNode();
            node2New.installContribution("../domain/distributed-calculator/contribution-calculator/target/classes");
            
            calculator = node2New.getService(CalculatorService.class, "CalculatorServiceComponent");
*/

/* old */
            nodeFactory = NodeFactory.newInstance();
            node1 = nodeFactory.createNode(new Contribution("c1", "../domain/distributed-calculator/contribution-add/target/classes"));
            node1.start();
            
            node2 = nodeFactory.createNode(new Contribution("c1", "../domain/distributed-calculator/contribution-calculator/target/classes"));
            node2.start();
            
            calculator = node2.getService(CalculatorService.class, "CalculatorServiceComponent");

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
         node1.stop();
         node2.stop();
    }

    @Test
    public void callCalulator(){ 
        printTestName("callCalulator");   
        double result = calculator.add(3, 2);
        System.out.println("3 + 2 = " + result);
    }
        
    @Test
    public void listNodes(){ 
        printTestName("listNodes");
        Map<Object, Node> nodes = ((NodeFactoryImpl)nodeFactory).getNodes();
        for (Object nodeKey : nodes.keySet()){
            System.out.println(nodeKey);
        }
    }
    
    @Test
    public void listNodeConfigurations(){  
        printTestName("listNodeConfigurations");
        Map<Object, Node> nodes = ((NodeFactoryImpl)nodeFactory).getNodes();
        for (Node node : nodes.values()){
            System.out.println("Node: " + ((NodeImpl)node).getURI());
            printXML(((NodeImpl)node).getConfiguration());
        }
    }  
    
    @Test
    public void listContributions(){  
        printTestName("listContributions");
        Map<Object, Node> nodes = ((NodeFactoryImpl)nodeFactory).getNodes();
        for (Node node : nodes.values()){
            System.out.println("Node: " + ((NodeImpl)node).getURI());
            NodeConfiguration nodeConfiguration = ((NodeImpl)node).getConfiguration();
            for (org.apache.tuscany.sca.contribution.Contribution contribution : ((NodeImpl)node).getContributions()){
                System.out.println("Contribution: " + contribution.getURI() + " location " + contribution.getLocation());
                for (Artifact artifact : contribution.getArtifacts()){
                    System.out.println("    Artifact: " + artifact.getURI() + " location " + artifact.getLocation());
                }
            }
        }
    } 
    
    @Test
    public void listDomainComposite(){  
        printTestName("listDomainComposite");
        Map<Object, Node> nodes = ((NodeFactoryImpl)nodeFactory).getNodes();
        for (Node node : nodes.values()){
            System.out.println("Node: " + ((NodeImpl)node).getURI());
            printXML(((NodeImpl)node).getDomainComposite());
        }
    }  
    
    @Test
    public void listDomainDefinitions(){  
        printTestName("listDomainDefinitions");
        Deployer deployer = ((NodeFactoryImpl)nodeFactory).getDeployer();
        Definitions systemDefinitions = deployer.getSystemDefinitions();
        printXML(systemDefinitions);
    }     
    
    @Test
    public void listEndpoints(){  
        printTestName("listEndpoints");
        ExtensionPointRegistry registry = ((NodeFactoryImpl)nodeFactory).getExtensionPointRegistry();
        Map<Object, Node> nodes = ((NodeFactoryImpl)nodeFactory).getNodes();
        Node firstNode = nodes.values().iterator().next();
        NodeConfiguration firstNodeConfig = ((NodeImpl)firstNode).getConfiguration();
        
        DomainRegistryFactory domainRegistryFactory = ExtensibleDomainRegistryFactory.getInstance(registry);
        // TODO - I don't understand where the scheme gets set/used
        EndpointRegistry endpointRegistry = domainRegistryFactory.getEndpointRegistry("vm:" + firstNodeConfig.getDomainRegistryURI(), firstNodeConfig.getDomainURI());

        for (Endpoint endpoint : endpointRegistry.getEndpoints()){
            System.out.println(endpoint);
            printEndpointXML(endpoint);
        }
    }  
    
    @Test
    public void listWires(){  
        printTestName("listWires");
        Map<Object, Node> nodes = ((NodeFactoryImpl)nodeFactory).getNodes();
        for (Node node : nodes.values()){
            System.out.println("Node: " + ((NodeImpl)node).getURI());
            listComponentWires(((NodeImpl)node).getDomainComposite());
        }
    }     
    
    // utils
    
    private void listComponentWires(Composite composite){
        for(Component component : composite.getComponents()){
            if (component.getImplementation() instanceof Composite){
                listComponentWires((Composite)component.getImplementation());
            }
            System.out.println("  Component: " + component.getName());
            
            for(ComponentService service : component.getServices()){
                System.out.println("    Service: " + service.getName());
                for(Endpoint endpoint : service.getEndpoints()){
                    System.out.println("      Endpoint: " + endpoint);
                    System.out.println("      Binding: " + endpoint.getBinding().getType());
                    printInvocationChains(((RuntimeEndpointImpl)endpoint).getInvocationChains());                   
                }
            }
            for(ComponentReference reference : component.getReferences()){
                System.out.println("    Reference: " + reference.getName());
                for(EndpointReference endpointReference : reference.getEndpointReferences()){
                    System.out.println("      EndpointReference: " + endpointReference);
                    Binding binding = endpointReference.getBinding();
                    if (binding != null){
                        System.out.println("      Binding: " + binding.getType());
                        printInvocationChains(((RuntimeEndpointReferenceImpl)endpointReference).getInvocationChains());
                    }
                }
            }            
        }
    }
    
    private void printInvocationChains(List<InvocationChain> chains){  
        for(InvocationChain chain : chains){
            System.out.println("        Operation: " + chain.getTargetOperation().getName());
            Invoker invoker = chain.getHeadInvoker();
            while(invoker != null){
                System.out.println("          Invoker: " + invoker.getClass().getName());
                if (invoker instanceof Interceptor){
                    invoker = ((Interceptor)invoker).getNext();
                } else {
                    invoker = null;
                }
            }
        }
    }
    
    private void printTestName(String name){
        System.out.println("=====================================================================");
        System.out.println(name);
        System.out.println("=====================================================================");
    }
    
    private void printXML(Object model){
        try {
            ExtensionPointRegistry registry = ((NodeFactoryImpl)nodeFactory).getExtensionPointRegistry(); 
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            
            StAXArtifactProcessorExtensionPoint xmlProcessors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
            StAXArtifactProcessor<Object> xmlProcessor = new ExtensibleStAXArtifactProcessor(xmlProcessors, inputFactory, null);
            
            ProcessorContext context = new ProcessorContext(registry);
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            XMLOutputFactory outputFactory = registry.getExtensionPoint(FactoryExtensionPoint.class).getFactory(XMLOutputFactory.class);
            XMLStreamWriter writer = new IndentingXMLStreamWriter(outputFactory.createXMLStreamWriter(bos));
            
            xmlProcessor.write(model, writer, context);
            writer.flush();
            
            System.out.println(bos.toString());
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }  
    
    // TODO - we don't have a processor registered for RuntimeEndpointImpl?
    private void printEndpointXML(Endpoint model){
        try {
            ExtensionPointRegistry registry = ((NodeFactoryImpl)nodeFactory).getExtensionPointRegistry(); 
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            
            StAXArtifactProcessorExtensionPoint xmlProcessors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
            StAXArtifactProcessor<Endpoint> xmlProcessor = xmlProcessors.getProcessor(Endpoint.class);
            
            ProcessorContext context = new ProcessorContext(registry);
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            XMLOutputFactory outputFactory = registry.getExtensionPoint(FactoryExtensionPoint.class).getFactory(XMLOutputFactory.class);
            XMLStreamWriter writer = new IndentingXMLStreamWriter(outputFactory.createXMLStreamWriter(bos));
            
            xmlProcessor.write(model, writer, context);
            writer.flush();
            
            System.out.println(bos.toString());
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }     
    
}
