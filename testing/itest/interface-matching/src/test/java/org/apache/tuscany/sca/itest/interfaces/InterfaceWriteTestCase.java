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

package org.apache.tuscany.sca.itest.interfaces;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import junit.framework.Assert;



import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.xml.InterfaceContractProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;

import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceContractMapperImpl;
import org.apache.tuscany.sca.interfacedef.util.Audit;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.impl.NodeImpl;
import org.junit.Test;

public class InterfaceWriteTestCase {
    
    /**
     * Looks at writing and reading the Tuscany interface model
     * 
     * @throws Exception
     */
    @Test
    public void testInterfaceWriteRead() throws Exception {
        String [] contributions = {"./target/classes"};
        NodeImpl node1 = (NodeImpl)NodeFactory.newInstance().createNode(URI.create("uri:default"), 
                                                                        "org/apache/tuscany/sca/itest/interfaces/missmatch/distributed/MissmatchDistributedService.composite", 
                                                                        contributions);
        node1.start();
        
        Component serviceComponent = node1.getDomainComposite().getComponents().get(0);
        Service service = serviceComponent.getServices().get(0);
        
        InterfaceContractProcessor processor = new InterfaceContractProcessor(node1.getExtensionPointRegistry());
        ProcessorContext context = new ProcessorContext();
        
        FactoryExtensionPoint modelFactories = node1.getExtensionPointRegistry().getExtensionPoint(FactoryExtensionPoint.class);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLOutputFactory outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(bos);
        processor.write(service.getInterfaceContract(), writer, context);
        writer.close();
        
        String xml = bos.toString();
        System.out.println("Written ouput is:\n" + xml);
        
        ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes());
        XMLInputFactory inputFactory = modelFactories.getFactory(XMLInputFactory.class);
        XMLStreamReader reader = inputFactory.createXMLStreamReader(bis);
        InterfaceContract interfaceContract = processor.read(reader, context);
        
        bos = new ByteArrayOutputStream();
        writer = outputFactory.createXMLStreamWriter(bos);
        processor.write(interfaceContract, writer, context);
        writer.close();
        
        System.out.println("Read ouput is:\n" + bos);
        
        InterfaceContractMapper interfaceContractMapper = new InterfaceContractMapperImpl(node1.getExtensionPointRegistry());
        
        Audit matchAudit = new Audit();
        boolean match = false;
        match = interfaceContractMapper.isCompatibleSubset(service.getInterfaceContract(), 
                                                           interfaceContract, 
                                                           matchAudit);
        
        if (!match){
            System.out.println(matchAudit.toString());
        }
        
        Assert.assertTrue(match);
       
        node1.stop(); 
    }  
    
}
