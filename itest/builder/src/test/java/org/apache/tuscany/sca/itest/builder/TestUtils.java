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
import javax.wsdl.Port;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamWriter;
import junit.framework.TestCase;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;

/**
 * Static utility methods for use by test cases.
 * 
 * @version $Rev$ $Date$
 */
public class TestUtils {

    protected static void checkProblems(CustomCompositeBuilder customBuilder) throws Exception {
        boolean problems = false;
        for (Problem problem : customBuilder.getMonitor().getProblems()) {
            if (problem.getCause() != null) {
                problem.getCause().printStackTrace();
            }
            problems = true;
        }
        assert !problems;
    }

    protected static String getPortAddress(Port port) {
        Object ext = port.getExtensibilityElements().get(0);
        if (ext instanceof SOAPAddress) {
            return ((SOAPAddress)ext).getLocationURI();
        }
        if (ext instanceof SOAP12Address) {
            return ((SOAP12Address)ext).getLocationURI();
        }
        return null;
    }

    protected static Component getComponent(Composite composite, String name) {
        for (Component component : composite.getComponents()) {
            if (name.equals(component.getName())) {
                return component;
            }
            // process implementation composites recursively
            Implementation impl = component.getImplementation();
            if (impl instanceof Composite) {
                Component comp = getComponent((Composite)impl, name);
                if (comp != null) {
                    return comp;
                }
            }
        }
        return null;
    }

    protected static Composite getComposite(Composite composite, QName name) {
        if (name.equals(composite.getName())) {
            return composite;
        }
        for (Component component : composite.getComponents()) {
            // process implementation composites recursively
            Implementation impl = component.getImplementation();
            if (impl instanceof Composite) {
                Composite comp = getComposite((Composite)impl, name);
                if (comp != null) {
                    return comp;
                }
            }
        }
        return null;
    }

    protected static void printResults(CustomCompositeBuilder customBuilder) throws Exception {
        for (Problem problem : customBuilder.getMonitor().getProblems()) {
            if (problem.getCause() != null) {
                problem.getCause().printStackTrace();
            }
        }
        Composite domainComposite = customBuilder.getDomainComposite();
        printComposite(domainComposite, customBuilder);
    }

    private static void printComposite(Composite composite, CustomCompositeBuilder customBuilder) throws Exception {
        // process implementation composites recursively
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                printComposite((Composite)implementation, customBuilder);
            }
        }

        // write out the SCDL
        writeSCDL(composite, customBuilder);

        // find all the component service and reference bindings     
        for (Component component : composite.getComponents()) {
            for (ComponentService componentService : component.getServices()) {
                for (Binding binding : componentService.getBindings()) {
                    if (binding instanceof WebServiceBinding) {
                        writeWSDL(component, componentService, ((WebServiceBinding)binding).getWSDLDocument());
                    }
                }
            }
            for (ComponentReference componentReference : component.getReferences()) {
                for (Binding binding : componentReference.getBindings()) {
                    if (binding instanceof WebServiceBinding) {
                        writeWSDL(component, componentReference, ((WebServiceBinding)binding).getWSDLDocument());
                    }
                }
            }
        }

        // find all the composite service and reference bindings     
        for (Service service : composite.getServices()) {
            for (Binding binding : service.getBindings()) {
                if (binding instanceof WebServiceBinding) {
                    writeWSDL(null, service, ((WebServiceBinding)binding).getWSDLDocument());
                }
            }
        }
        for (Reference reference : composite.getReferences()) {
            for (Binding binding : reference.getBindings()) {
                if (binding instanceof WebServiceBinding) {
                    writeWSDL(null, reference, ((WebServiceBinding)binding).getWSDLDocument());
                }
            }
        }
    }

    private static void writeSCDL(Composite composite, CustomCompositeBuilder customBuilder) throws Exception {
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

    private static void writeWSDL(Component component, Contract contract, Definition definition) {
        if (definition == null) {
            System.out.println("-->No generated WSDL for " + (component != null ? component.getName() : "") + "/" + contract.getName());
        } else {
            try {
                System.out.println("-->Generated WSDL for " + (component != null ? component.getName() : "") + "/" + contract.getName());
                WSDLWriter writer =  WSDLFactory.newInstance().newWSDLWriter();
                writer.writeWSDL(definition, System.out);
            } catch (WSDLException e) {
                // ignore
            }
        }
    }

}
