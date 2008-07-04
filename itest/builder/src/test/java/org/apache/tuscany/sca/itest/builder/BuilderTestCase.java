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
package org.apache.tuscany.sca.itest.builder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamWriter;
import junit.framework.TestCase;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;

/**
 * Load and build some composites and inspect the results.
 * 
 * @version $Rev$ $Date$
 */
public class BuilderTestCase extends TestCase {
    private CustomCompositeBuilder customBuilder;
    
    @Override
    protected void setUp() throws Exception {
    }

    @Override
    protected void tearDown() throws Exception {
    }

    public void testScenario1() throws Exception {
        customBuilder = new CustomCompositeBuilder();
        customBuilder.loadContribution("src/main/resources/scenario1.composite", 
                                       "TestContribution", "src/main/resources/");
        printResults();
    }

    private void printResults() throws Exception {
        for (Problem problem : customBuilder.getMonitor().getProblems()) {
            if (problem.getCause() != null) {
                problem.getCause().printStackTrace();
            }
        }
        Composite domainComposite = customBuilder.getDomainComposite();
        printComposite(domainComposite);
    }

    private void printComposite(Composite composite) throws Exception {
        // process implementation composites recursively
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                printComposite((Composite)implementation);
            }
        }

        // write out the SCDL
        writeSCDL(composite);

        // find all the component service bindings     
        for (Component component : composite.getComponents()) {
            for (ComponentService componentService : component.getServices()) {
                for (Binding binding : componentService.getBindings()) {
                    if (binding instanceof WebServiceBinding) {
                        writeWSDL(component, componentService, ((WebServiceBinding)binding).getWSDLDocument());
                    }
                }
            }
        }
    }

    private void writeSCDL(Composite composite) throws Exception {
        // Print out a composite
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLStreamWriter writer = customBuilder.getOutputFactory().createXMLStreamWriter(bos);
        customBuilder.getModelProcessor().write(composite, writer);
        
        // Parse and write again to pretty format it
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(new ByteArrayInputStream(bos.toByteArray()));
        OutputFormat format = new OutputFormat();
        format.setIndenting(true);
        format.setIndent(2);
        XMLSerializer serializer = new XMLSerializer(System.out, format);
        System.out.println("-->Runtime SCDL model for composite " + composite.getName());
        serializer.serialize(document);
    }

    private void writeWSDL(Component component, ComponentService service, Definition definition) {
        if (definition == null) {
            System.out.println("-->No generated WSDL for " + component.getName() + "/" + service.getName());
        } else {
            try {
                System.out.println("-->Generated WSDL for " + component.getName() + "/" + service.getName());
                WSDLWriter writer =  WSDLFactory.newInstance().newWSDLWriter();
                writer.writeWSDL(definition, System.out);
            } catch (WSDLException e) {
                // ignore
            }
        }
    }


}
