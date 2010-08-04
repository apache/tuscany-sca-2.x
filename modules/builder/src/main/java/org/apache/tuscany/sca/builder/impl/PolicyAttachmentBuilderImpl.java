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

package org.apache.tuscany.sca.builder.impl;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.common.xml.dom.DOMHelper;
import org.apache.tuscany.sca.common.xml.stax.StAXHelper;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.ExternalAttachment;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySubject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A builder that attaches policy sets to the domain composite using the xpath defined by
 * the attachTo attribute. It first creates a DOM model for the composite so that the xpath
 * expression can be evaluated. For the nodes selected by the xpath, caluclate the element
 * URI and add the policy set into the composite model  
 *
 * @version $Rev$ $Date$
 */
public class PolicyAttachmentBuilderImpl implements CompositeBuilder {
    protected static final String BUILDER_VALIDATION_BUNDLE = "org.apache.tuscany.sca.builder.builder-validation-messages";
    
    protected StAXHelper staxHelper;
    protected DOMHelper domHelper;
    protected ExtensionPointRegistry registry;
    protected StAXArtifactProcessor<Composite> processor;

    public PolicyAttachmentBuilderImpl(ExtensionPointRegistry registry) {
        this.registry = registry;
        domHelper = DOMHelper.getInstance(registry);
        staxHelper = StAXHelper.getInstance(registry);
        StAXArtifactProcessorExtensionPoint processors =
            registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        processor = processors.getProcessor(Composite.class);
    }

    public String getID() {
        return "org.apache.tuscany.sca.policy.builder.PolicyAttachmentBuilder";
    }

    public Composite build(Composite composite, BuilderContext context)
        throws CompositeBuilderException {
        try {
            Composite patched = applyXPath(composite, context.getDefinitions(), context.getMonitor());
            return patched;
        } catch (Exception e) {
            throw new CompositeBuilderException(e);
        }
    }

    /**
     * Apply the attachTo XPath against the composite model
     * @param composite The orginal composite
     * @param definitions SCA definitions that contain the policy sets
     * @param monitor The monitor
     * @return A reloaded composite
     * @throws Exception
     */
    private Composite applyXPath(Composite composite, Definitions definitions, Monitor monitor) throws Exception {

        monitor.pushContext("Composite: " + composite.getName().toString());
        
        try {
            if (definitions == null || (definitions.getPolicySets().isEmpty() && definitions.getExternalAttachments().isEmpty()) ) {
                return composite;
            }
     
            
            Document document = null;
    
            for (PolicySet ps : definitions.getPolicySets()) {
            	XPathExpression exp = ps.getAttachToXPathExpression();
            	if ( exp != null ) {
            		if ( document == null ) {
            			document = saveAsDOM(composite);
            		}
            		NodeList nodes = (NodeList) exp.evaluate(document, XPathConstants.NODESET);
            		attachPolicySetToNodes(composite, monitor, nodes, ps);
            	}
            }
            
            for ( ExternalAttachment ea : definitions.getExternalAttachments() ) {
            	XPathExpression exp = ea.getAttachToXPathExpression();
            	if ( exp != null ) {
            		if ( document == null ) {
            			document = saveAsDOM(composite);
            		}
            		NodeList nodes = (NodeList) exp.evaluate(document, XPathConstants.NODESET);
            		for ( PolicySet ps : ea.getPolicySets() ) {            		            		                		                		
                		attachPolicySetToNodes(composite, monitor, nodes, ps);
                	}
            	}
            }
            
            // Recursively apply the xpath against the composites referenced by <implementation.composite>
            // If the composite or component has policy sets attached, we have to ignore policy sets
            // attached to the inner composite. 
            if ( composite.getPolicySets().isEmpty() ) {
            	for (Component component : composite.getComponents()) {
            		if ( component.getPolicySets().isEmpty() ) {
            			Implementation impl = component.getImplementation();
            			if (impl instanceof Composite) {                	               
            				Composite patched = applyXPath((Composite)impl, definitions, monitor);                                       
            				if (patched != impl) {                    	                    	                    	                    	
            					component.setImplementation(patched);
            				}
            			}
            		}
            	}
            }
            
            return composite;
        } finally {
            monitor.popContext();
        }            
    }

	private void attachPolicySetToNodes(Composite composite,
			Monitor monitor, NodeList nodes, PolicySet ps) {	
					  		 
		    for (int i = 0; i < nodes.getLength(); i++) {
		        Node node = nodes.item(i);
		        
		        if ( isAttachedToProperty(node) ) {
		        	   Monitor.error(monitor, 
			                      this, 
			                      BUILDER_VALIDATION_BUNDLE, 
			                      "PolicyAttachedToProperty", 
			                      ps.getName().toString());		        
		        }
		        
		      
		        // The node can be a component, service, reference or binding
		        String index = getStructuralURI(node);
		        PolicySubject subject = lookup(composite, index);
		        if (subject != null) {
		        	ps.setIsExternalAttachment(true);
		        	subject.getPolicySets().add(ps);
		        } else {
		        	// raise a warning that the XPath node didn't match a node in the 
		        	// models
		        	Monitor.warning(monitor, 
		        			this, 
		        			BUILDER_VALIDATION_BUNDLE, 
		        			"PolicyDOMModelMissmatch", 
		        			ps.getName().toString(),
		        			index);
		        }
		        
		    }				
	}

	/**
	 * 	POL_40002 - you can't attach a policy to a property node 
	 * or one of it's children. walk backwards up the node tree 
	 * looking for an element called property and raise an error 
	 * if we find one
	 * @param node
	 * @return
	 */
	private boolean isAttachedToProperty(Node node) {

		Node testNode = node;
		while (testNode != null){
		    if ((node.getNodeType() == Node.ELEMENT_NODE) &&
		        (node.getLocalName().equals("property"))){
		    	return true;		       
		    }                    	
		    testNode = testNode.getParentNode();
		}
		return false;
	}

    protected Document saveAsDOM(Composite composite) throws XMLStreamException, ContributionWriteException, IOException,
        SAXException {
        // First write the composite into a DOM document so that we can apply the xpath
        StringWriter sw = new StringWriter();
        XMLStreamWriter writer = staxHelper.createXMLStreamWriter(sw);
        // Write the composite into a DOM document
        processor.write(composite, writer, new ProcessorContext(registry));
        writer.close();

        Document document = domHelper.load(sw.toString());
        
        // Debugging
        //System.out.println("<!-- DOM to which XPath will be applies is -->\n" + sw.toString());
        
        return document;
    }

    private static final QName COMPONENT = new QName(Base.SCA11_NS, "component");
    private static final QName SERVICE = new QName(Base.SCA11_NS, "service");
    private static final QName REFERENCE = new QName(Base.SCA11_NS, "reference");

    protected static String getStructuralURI(Node node) {
        if (node != null) {
            QName name = new QName(node.getNamespaceURI(), node.getLocalName());
            if (COMPONENT.equals(name)) {
                Element element = (Element)node;
                return element.getAttributeNS(null, "uri");
            } else if (SERVICE.equals(name)) {
                Element component = (Element)node.getParentNode();
                String uri = component.getAttributeNS(null, "uri");
                String service = ((Element)node).getAttributeNS(null, "name");
                return uri + "#service(" + service + ")";
            } else if (REFERENCE.equals(name)) {
                Element component = (Element)node.getParentNode();
                String uri = component.getAttributeNS(null, "uri");
                String reference = ((Element)node).getAttributeNS(null, "name");
                return uri + "#reference(" + reference + ")";
            } else if ( new QName(Base.SCA11_NS, "composite").equals(name)) {
            	return "";
            } else {
                String localName = node.getLocalName();
                if (localName.startsWith("binding.")) {
                    String bindingName = ((Element)node).getAttributeNS(null, "name");
                    Element contract = (Element)node.getParentNode();
                    String contractName = contract.getAttributeNS(null, "name");
                    Element component = (Element)node.getParentNode().getParentNode();
                    String uri = component.getAttributeNS(null, "uri");
                    return uri + "#" + contract.getLocalName() + "(" + contractName + "/" + bindingName + ")";
                } else if (localName.startsWith("implementation.")) {
                    Element component = (Element)node.getParentNode();
                    String uri = component.getAttributeNS(null, "uri");
                    return uri + "#implementation()";
                } else if (localName.startsWith("interface.")) {                
                	Element contract = (Element)node.getParentNode();
                	String contractName = contract.getAttributeNS(null, "name");
                	Element component = (Element)node.getParentNode().getParentNode();
                	String uri = component.getAttributeNS(null, "uri");
                	return uri + "#" + contractName + "#interface()"; //(" + contractName + "/" + interfaceName + ")"
                }
            }
        }
        return null;
    }

    protected Binding getBinding(Contract contract, String name) {
        for (Binding binding : contract.getBindings()) {
            if (name.equals(binding.getName())) {
                return binding;
            }
        }
        return null;
    }

    protected PolicySubject lookup(Composite composite, String structuralURI) {
        if (structuralURI == null) {
            return null;
        } else if ( structuralURI.equals("")) {
        	return composite;
        }
        int index = structuralURI.indexOf('#');
        String componentURI = structuralURI;
        String service = null;
        String reference = null;
        String binding = null;
        boolean isInterface = false;
        boolean impl = false;

        if (index != -1) {
            componentURI = structuralURI.substring(0, index);
            String fragment = structuralURI.substring(index + 1);
            int begin = fragment.indexOf('(');
            int end = fragment.indexOf(')');
            if (begin != -1 && end != -1) {
                String path = fragment.substring(begin + 1, end).trim();
                String prefix = fragment.substring(0, begin).trim();
                if (prefix.equals("implementation")) {
                    impl = true;
                } else {
                    int pos = path.indexOf('/');
                    if (pos != -1) {
                        binding = path.substring(pos + 1);
                        path = path.substring(0, pos);
                        if ("service-binding".equals(prefix)) {
                            service = path;
                        } else if ("reference-binding".equals(prefix)) {
                            reference = path;
                        }
                    }
                    if ("service".equals(prefix)) {
                        service = path;
                    } else if ("reference".equals(prefix)) {
                        reference = path;
                    } else if ( prefix.indexOf("#interface") != -1 ) {
                    	service = prefix.substring(0, prefix.indexOf("#interface"));
                    	isInterface = true;
                    }
                }
            }
        }
        for (Component component : composite.getComponents()) {
            if (component.getURI().equals(componentURI)) {
                if (service != null) {
                    ComponentService componentService = component.getService(service);
                    if ( isInterface ) {
                    	return componentService.getInterfaceContract().getInterface();
                    } else if (binding != null) {
                        Binding b = getBinding(componentService, binding);
                        if (b instanceof PolicySubject) {
                            return (PolicySubject)b;
                        }
                    } else {
                        return componentService;
                    }
                } else if (reference != null) {
                    ComponentReference componentReference = component.getReference(reference);
                    if (binding != null) {
                        Binding b = getBinding(componentReference, binding);
                        if (b instanceof PolicySubject) {
                            return (PolicySubject)b;
                        }
                    } else {
                        return componentReference;
                    }
                } else if (impl) {
                    return component.getImplementation();
                }
                return component;
            } else if (structuralURI.startsWith(component.getURI() + "/")) {
                Implementation implementation = component.getImplementation();
                if (implementation instanceof Composite) {
                    return lookup((Composite)implementation, structuralURI);
                } else {
                    return null;
                }
            }
        }
        return null;
    }
}
