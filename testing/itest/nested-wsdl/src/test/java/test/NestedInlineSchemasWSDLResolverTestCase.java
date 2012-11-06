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
package test;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.tuscany.sca.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.impl.WebServiceBindingFactoryImpl;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.DefaultContributionFactory;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.resolver.DefaultModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.DefaultFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;
import org.apache.tuscany.sca.interfacedef.wsdl.xml.WSDLDocumentProcessor;
import org.apache.tuscany.sca.policy.DefaultPolicyFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.junit.Test;

public class NestedInlineSchemasWSDLResolverTestCase extends TestCase {

	protected ExtensionPointRegistry extRegistry = null;
	protected static ContributionFactory contributionFactory = null;
	protected static FactoryExtensionPoint factoryExtensionPoint = null;
	protected static ModelResolverExtensionPoint modelResolverExtensionPoint = null;
    
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		extRegistry = new DefaultExtensionPointRegistry();
		factoryExtensionPoint = new DefaultFactoryExtensionPoint(extRegistry);
		
		contributionFactory = new DefaultContributionFactory();
		WebServiceBindingFactory wsBindingFactory = new WebServiceBindingFactoryImpl();
		PolicyFactory policyFactory = new DefaultPolicyFactory();
		javax.wsdl.factory.WSDLFactory wsdlFactory = javax.wsdl.factory.WSDLFactory.newInstance();
		factoryExtensionPoint.addFactory(wsBindingFactory);
		factoryExtensionPoint.addFactory(policyFactory);
		factoryExtensionPoint.addFactory(wsdlFactory);
		factoryExtensionPoint.addFactory(contributionFactory);
		
		extRegistry.addExtensionPoint(factoryExtensionPoint);

		modelResolverExtensionPoint = new DefaultModelResolverExtensionPoint(extRegistry);
		extRegistry.addExtensionPoint(modelResolverExtensionPoint);

		// URLArtifactProcessorExtensionPoint urlArtifactProcessorExtensionPoint
		// = new DefaultURLArtifactProcessorExtensionPoint(extRegistry);
		// extRegistry.addExtensionPoint(urlArtifactProcessorExtensionPoint);
	}

	@Test
	public void testNestedWSDLParsing() throws MalformedURLException, ContributionReadException, URISyntaxException {
		URL url = NestedInlineSchemasWSDLResolverTestCase.class.getClassLoader().getResource("OrderService.wsdl");

		Contribution contribution = contributionFactory.createContribution();
		ModelResolver resolver = new ExtensibleModelResolver(contribution, modelResolverExtensionPoint, factoryExtensionPoint);
		contribution.setModelResolver(resolver);

		// URLArtifactProcessorExtensionPoint documentProcessors =
		// extRegistry.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
		// URLArtifactProcessor<WSDLDefinition> wsdlProcessor =
		// documentProcessors.getProcessor(WSDLDefinition.class);
		WSDLDocumentProcessor wsdlProcessor = new WSDLDocumentProcessor(extRegistry, null);
		ProcessorContext context = new ProcessorContext();
		final java.net.URI uri = url.toURI();
		WSDLDefinition wsdlDef = (WSDLDefinition) wsdlProcessor.read(null, uri, url, context);
		resolver.addModel(wsdlDef, context);
		wsdlDef = resolver.resolveModel(WSDLDefinition.class, wsdlDef, context);

		Map<?, ?> services = wsdlDef.getDefinition().getAllServices();
		// System.out.println(wsdlDef.getDefinition().getBinding(new QName("http://OrderService/OrderService/rootwsdl",
		// "OrderServiceHttpBinding")).isUndefined());
		assertEquals(1, services.size());
		for (Iterator<?> iterator = services.keySet().iterator(); iterator.hasNext();) {
			QName serviceKey = (QName) iterator.next();
			assertEquals(new QName("http://OrderService/OrderService/rootwsdl", "OrderServiceHttpService"), serviceKey);
			Service service = (Service) services.get(serviceKey);
			Map<?, ?> ports = service.getPorts();
			for (Iterator<?> iterator2 = ports.keySet().iterator(); iterator2.hasNext();) {
				String portKey = (String) iterator2.next();
				assertEquals("OrderServiceHttpPort", portKey);
				Port port = service.getPort(portKey);
				Binding binding = port.getBinding();

				assertEquals(new QName("http://OrderService/OrderService/rootwsdl", "OrderServiceHttpBinding"), binding.getQName());
				assertEquals(new QName("http://OrderService/OrderService/importwsdl", "OrderService"), binding.getPortType().getQName());
				assertEquals(3, binding.getPortType().getOperations().size());
				List<?> bindingOperations = binding.getBindingOperations();
				assertEquals(3, bindingOperations.size());
				List<String> expectedOperations = new ArrayList<String>();
				expectedOperations.add("retrieveOrder");
				expectedOperations.add("writeOrder");
				expectedOperations.add("updateOrderStatus");
				boolean flag = false;
				for (Object obj : bindingOperations) {
					BindingOperation operation = (BindingOperation) obj;
					assertTrue(expectedOperations.contains(operation.getName()));

					if ("retrieveOrder".equals(operation.getName())) {
						assertEquals(new QName("http://OrderService/OrderService/importwsdl", "retrieveOrderRequestMsg"), operation.getOperation().getInput()
								.getMessage().getQName());
						flag = true;

					}
					// System.out.println(operation.getOperation().getInput().getMessage().getParts());
					Map<?, ?> parts = operation.getOperation().getInput().getMessage().getParts();
					assertEquals(1, parts.size());
					for (Iterator<?> iterator3 = parts.keySet().iterator(); iterator3.hasNext();) {
						Object object = (Object) iterator3.next();
						System.out.println("\t\tpart:" + parts.get(object));
					}
				}
				assertTrue(flag);
			}
		}

		// TODO: this should be one bug of open sca
                System.out.println(wsdlDef.getXmlSchemaElement(new QName("http://OrderService/OrderService/importwsdl/importwsdl", "retrieveOrder")));
                System.out.println(wsdlDef.getXmlSchemaElement(new QName("http://OrderService/importwsdl", "OrderElement")));
                System.out.println(wsdlDef.getXmlSchemaType(new QName("http://OrderService/importwsdl", "Order")));
                System.out.println(wsdlDef.getXmlSchemaElement(new QName("http://OrderService/OrderService/importwsdl", "updateOrderStatus")));
// TODO: fails  assertNotNull(wsdlDef.getXmlSchemaElement(new QName("http://OrderService/OrderService/importwsdl/importwsdl", "retrieveOrder")));
		assertNotNull(wsdlDef.getXmlSchemaElement(new QName("http://OrderService/importwsdl", "OrderElement")));
		assertNotNull(wsdlDef.getXmlSchemaType(new QName("http://OrderService/importwsdl", "Order")));
		assertNotNull(wsdlDef.getXmlSchemaElement(new QName("http://OrderService/OrderService/importwsdl", "updateOrderStatus")));
		
		testCreateInterfaceContract(contribution, wsdlDef, services);
	}
	
	//TODO: How to test InterfaceContract
	private void testCreateInterfaceContract(Contribution contribution, WSDLDefinition wsdlDef, Map<?, ?> services) {
		for (Iterator<?> iterator = services.keySet().iterator(); iterator.hasNext();) {
			QName serviceKey = (QName) iterator.next();
			Service service = (Service) services.get(serviceKey);
			Map<?, ?> ports = service.getPorts();
			for (Iterator<?> iterator2 = ports.keySet().iterator(); iterator2.hasNext();) {
				String portKey = (String) iterator2.next();
				Port port = service.getPort(portKey);
				try {
					WSDLFactory wsdlFactory = extRegistry.getExtensionPoint(FactoryExtensionPoint.class).getFactory(WSDLFactory.class);
					WSDLInterface nwi = wsdlFactory.createWSDLInterface(port.getBinding().getPortType(), wsdlDef, contribution.getModelResolver(), null);
					assertNotNull(nwi);
					//nwi.resetDataBinding(BODataBinding.NAME);
					nwi.setWsdlDefinition(wsdlDef);
					WSDLInterfaceContract wsdlIC = wsdlFactory.createWSDLInterfaceContract();
					wsdlIC.setInterface(nwi);
					
					assertEquals(3, nwi.getOperations().size());
					
					assertEquals(1, nwi.getOperations().get(0).getInputType().getLogical().size());
					assertEquals(1, nwi.getOperations().get(1).getInputType().getLogical().size());
					assertEquals(1, nwi.getOperations().get(2).getInputType().getLogical().size());
					assertEquals(1, nwi.getOperations().get(0).getOutputType().getLogical().size());
					assertEquals(1, nwi.getOperations().get(1).getOutputType().getLogical().size());
					assertEquals(1, nwi.getOperations().get(2).getOutputType().getLogical().size());
					
					assertNotNull(nwi.getOperations().get(0).getInputType());
// TODO: fails   			assertEquals(new QName("http://OrderService/OrderService/rootwsdl", "retrieveOrder"), ((XMLType)nwi.getOperations().get(0).getInputType().getLogical().get(0).getLogical()).getElementName());
					assertNotNull(nwi.getOperations().get(0).getOutputType());
// TODO: fails   			assertEquals(new QName("http://OrderService/OrderService/rootwsdl", "retrieveOrderResponse"), ((XMLType)nwi.getOperations().get(0).getOutputType().getLogical().get(0).getLogical()).getElementName());
					assertEquals(0, nwi.getOperations().get(0).getFaultTypes().size());			
					
					assertEquals(0, nwi.getOperations().get(1).getFaultTypes().size());	
					assertEquals(0, nwi.getOperations().get(2).getFaultTypes().size());
					assertEquals(0, nwi.getOperations().get(0).getFaultTypes().size());	
				} catch (InvalidInterfaceException e) {
					e.printStackTrace();
					assertNull(e);
				}
			}
		}
	}
}
