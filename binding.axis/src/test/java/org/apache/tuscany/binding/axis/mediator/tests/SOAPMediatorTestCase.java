/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.tuscany.binding.axis.mediator.tests;

import javax.wsdl.Definition;
import javax.xml.namespace.QName;

import commonj.sdo.DataObject;
import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.binding.axis.handler.WebServicePortMetaData;
import org.apache.tuscany.binding.axis.mediator.SOAPMediator;
import org.apache.tuscany.binding.axis.mediator.impl.SOAPDocumentLiteralMediatorImpl;
import org.apache.tuscany.binding.axis.mediator.impl.SOAPEnvelopeImpl;
import org.apache.tuscany.binding.axis.mediator.impl.SOAPRPCLiteralMediatorImpl;
import org.apache.tuscany.model.util.ConfiguredResourceSet;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.TuscanyModuleComponentContext;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.impl.TuscanyModuleComponentContextImpl;
import org.apache.tuscany.core.context.scope.DefaultScopeStrategy;
import org.apache.tuscany.core.deprecated.sdo.util.HelperProvider;
import org.apache.tuscany.core.deprecated.sdo.util.impl.HelperProviderImpl;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.impl.AssemblyModelContextImpl;
import org.apache.tuscany.model.assembly.loader.AssemblyModelLoader;
import org.apache.tuscany.model.types.wsdl.WSDLServiceContract;
import org.apache.tuscany.model.types.wsdl.WSDLOperationType;
import org.apache.tuscany.model.types.wsdl.WSDLTypeHelper;

/**
 * Test case for SOAP mediators
 *
 */
public class SOAPMediatorTestCase extends TestCase {
    // private TuscanyRuntime tuscany;
    private TuscanyModuleComponentContext context;
    private HelperProvider provider;

    private AssemblyModelContext modelContext;

    @Override
    protected void setUp() throws Exception {
        System.out.println("Starting SCA runtime...");

        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        modelContext = new AssemblyModelContextImpl();

        AssemblyModelLoader loader = modelContext.getAssemblyLoader();
        Module module = loader.loadModule(getClass().getResource("sca.module").toString());
        module.initialize(modelContext);

        AssemblyFactory factory = new AssemblyFactoryImpl();

        ModuleComponent moduleComponent = factory.createModuleComponent();
        moduleComponent.setName(module.getName());
        moduleComponent.setURI(null);
        moduleComponent.setModuleImplementation(module);
        moduleComponent.setComponentImplementation(module);

        // Create the module component context
        EventContext eventContext = new EventContextImpl();
        DefaultScopeStrategy scopeStrategy = new DefaultScopeStrategy();
        context = new TuscanyModuleComponentContextImpl(moduleComponent, eventContext, scopeStrategy, modelContext);
        // tuscany = new TuscanyRuntime("TestModule", "org/apache/tuscany/binding/axis/mediator/tests");

        // tuscany.start();

        // context = (TuscanyModuleComponentContext) CurrentModuleContext.getContext();
        provider = new HelperProviderImpl((ConfiguredResourceSet) modelContext.getAssemblyLoader());
        System.out.println("SCA runtime is now started.");
    }

    @Override
    protected void tearDown() throws Exception {
        System.out.println("Stopping SCA runtime...");
        // tuscany.stop();
        System.out.println("SCA runtime is now stopped.");
    }

    public void testDocLitWrapped() throws Exception {
        System.out.println("testDocLitWrapped...");
        MessageFactory messageFactory = new MessageFactoryImpl();
        Message message = messageFactory.createMessage();

        WSDLServiceContract interfaceType = modelContext.getWSDLTypeHelper().getWSDLInterfaceType("http://www.example.org/Test/DocLitWrapped#Test");
        WSDLOperationType operationType = (WSDLOperationType) interfaceType.getOperationType("indexOf");

        DataObject input = provider.getDataFactory().create(operationType.getInputType());
        DataObject args = input.createDataObject(0);
        args.setString(0, "123");
        args.setString(1, "2");
        message.setBody(input);

        WebServicePortMetaData portMetaData = getPortMetaData(interfaceType.getWSDLPortType().getQName().getNamespaceURI());
        SOAPMediator mediator = new SOAPDocumentLiteralMediatorImpl(portMetaData);
        SOAPEnvelopeImpl envelope = new SOAPEnvelopeImpl();
        mediator.writeRequest(context, message, operationType, envelope);

        String xml = envelope.getAsString();
        System.out.println("Request:\n" + xml);
        Assert.assertTrue(xml.contains("<wrapped:indexOf xmlns:wrapped=\"http://www.example.org/Test/DocLitWrapped\">"));
        Assert.assertTrue(xml.contains("<source>123</source>"));
        Assert.assertTrue(xml.contains("<target>2</target>"));

        mediator.readRequest(context, envelope, message, operationType);
        args = ((DataObject) message.getBody()).getDataObject(0);
        Assert.assertEquals(args.get(0), "123");
        Assert.assertEquals(args.get(1), "2");

        message = messageFactory.createMessage();
        DataObject output = provider.getDataFactory().create(operationType.getOutputType());
        args = output.createDataObject(0);
        args.setInt(0, 1);
        message.setBody(output);
        envelope = new SOAPEnvelopeImpl();
        mediator.writeResponse(context, message, operationType, envelope);

        xml = envelope.getAsString();
        System.out.println("Response:\n" + xml);
        Assert.assertTrue(xml.contains("<index>1</index>"));

        mediator.readResponse(context, envelope, message, operationType);
        args = ((DataObject) message.getBody()).getDataObject(0);
        Assert.assertEquals(args.getInt(0), 1);

    }

    public void testDocLit() throws Exception {
        System.out.println("testDocLit...");
        MessageFactory messageFactory = new MessageFactoryImpl();
        Message message = messageFactory.createMessage();

        WSDLServiceContract interfaceType = modelContext.getWSDLTypeHelper().getWSDLInterfaceType("http://www.example.org/Test/DocLit#Test");
        WSDLOperationType operationType = (WSDLOperationType) interfaceType.getOperationType("indexOf");

        DataObject input = provider.getDataFactory().create(operationType.getInputType());
        DataObject args = input;
        args.setString(0, "123");
        args.setString(1, "2");
        args.setString(2, "transactionId-0001");
        message.setBody(input);

        WebServicePortMetaData portMetaData = getPortMetaData(interfaceType.getWSDLPortType().getQName().getNamespaceURI());
        SOAPMediator mediator = new SOAPDocumentLiteralMediatorImpl(portMetaData);
        SOAPEnvelopeImpl envelope = new SOAPEnvelopeImpl();
        mediator.writeRequest(context, message, operationType, envelope);

        String xml = envelope.getAsString();
        System.out.println("Request:\n" + xml);

        mediator.readRequest(context, envelope, message, operationType);
        args = (DataObject) message.getBody();
        Assert.assertEquals(args.get(0), "123");
        Assert.assertEquals(args.get(1), "2");
        Assert.assertEquals(args.get(2), "transactionId-0001");

        message = messageFactory.createMessage();
        DataObject output = provider.getDataFactory().create(operationType.getOutputType());
        args = output;
        args.setInt(0, 1);
        message.setBody(output);
        envelope = new SOAPEnvelopeImpl();
        mediator.writeResponse(context, message, operationType, envelope);

        xml = envelope.getAsString();
        System.out.println("Response:\n" + xml);

        mediator.readResponse(context, envelope, message, operationType);
        args = (DataObject) message.getBody();
        Assert.assertEquals(args.getInt(0), 1);

    }

    public void testRpcLit() throws Exception {
        System.out.println("testRpcLit...");
        MessageFactory messageFactory = new MessageFactoryImpl();
        Message message = messageFactory.createMessage();

        WSDLServiceContract interfaceType = modelContext.getWSDLTypeHelper().getWSDLInterfaceType("http://www.example.org/Test/RpcLit#Test");
        WSDLOperationType operationType = (WSDLOperationType) interfaceType.getOperationType("indexOf");

        DataObject input = provider.getDataFactory().create(operationType.getInputType());
        DataObject args = input;
        args.setString(0, "123");
        args.setString(1, "2");
        message.setBody(input);

        WebServicePortMetaData portMetaData = getPortMetaData(interfaceType.getWSDLPortType().getQName().getNamespaceURI());

        SOAPMediator mediator = new SOAPRPCLiteralMediatorImpl(portMetaData);
        SOAPEnvelopeImpl envelope = new SOAPEnvelopeImpl();
        mediator.writeRequest(context, message, operationType, envelope);

        String xml = envelope.getAsString();
        System.out.println("Request:\n" + xml);
        Assert.assertTrue(xml.contains("<part1>123</part1>"));
        Assert.assertTrue(xml.contains("<part2>2</part2>"));

        mediator.readRequest(context, envelope, message, operationType);
        args = (DataObject) message.getBody();
        Assert.assertEquals(args.get(0), "123");
        Assert.assertEquals(args.get(1), "2");

        message = messageFactory.createMessage();
        DataObject output = provider.getDataFactory().create(operationType.getOutputType());
        args = output;
        args.setInt(0, 1);
        message.setBody(output);
        envelope = new SOAPEnvelopeImpl();
        mediator.writeResponse(context, message, operationType, envelope);

        xml = envelope.getAsString();
        System.out.println("Response:\n" + xml);
        Assert.assertTrue(xml.contains("<part1>1</part1>"));

        mediator.readResponse(context, envelope, message, operationType);
        args = (DataObject) message.getBody();
        Assert.assertEquals(args.getInt(0), 1);

    }

    private WebServicePortMetaData getPortMetaData(String ns) {
        WSDLTypeHelper typeHelper = modelContext.getWSDLTypeHelper();
        Definition definition = typeHelper.getWSDLDefinition(ns);
        QName portName = new QName(ns, "TestSOAP");
        WebServicePortMetaData portMetaData = new WebServicePortMetaData(typeHelper, definition, portName, null, false);
        return portMetaData;
    }

}
