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
package org.apache.tuscany.sca.implementation.osgi.xml;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
import static javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static org.osoa.sca.Constants.SCA_NS;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;


import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.databinding.impl.SimpleTypeMapperImpl;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceIntrospector;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * 
 * Process an <implementation.osgi/> element in a component definition. An instance of
 * OSGiImplementation is created.
 * Also associates the component type file with the implementation.
 * 
 *
 */
public class OSGiImplementationProcessor implements StAXArtifactProcessor<OSGiImplementation> {
    
    public static final QName IMPLEMENTATION_OSGI  = new QName(SCA_NS, "implementation.osgi");
    
    private static final String BUNDLE             = "bundle";
    private static final String BUNDLE_LOCATION    = "bundleLocation";
    private static final String SCOPE              = "scope";
    private static final String IMPORTS            = "imports";
    private static final String ALLOWS_PASS_BY_REF = "allowsPassByReference";
    private static final String INJECT_PROPERTIES  = "injectProperties";
   
    private static final QName PROPERTIES_QNAME    = new QName(SCA_NS, "properties");
    private static final QName PROPERTY_QNAME      = new QName(SCA_NS, "property");
    
    private JavaInterfaceIntrospector interfaceIntrospector;
    private JavaInterfaceFactory javaInterfaceFactory;
    private AssemblyFactory assemblyFactory;
    //private PolicyFactory policyFactory;
    
    private static final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
    static {
        domFactory.setNamespaceAware(true);
    }

    public OSGiImplementationProcessor(JavaInterfaceIntrospector interfaceIntrospector,
            JavaInterfaceFactory javaInterfaceFactory,
            AssemblyFactory assemblyFactory,
            PolicyFactory policyFactory) {
        
        this.interfaceIntrospector = interfaceIntrospector;
        this.assemblyFactory = assemblyFactory;
        this.javaInterfaceFactory = javaInterfaceFactory;
        //this.policyFactory = policyFactory;
    }
    
    public QName getArtifactType() {
        return IMPLEMENTATION_OSGI;
    }

    public Class<OSGiImplementation> getModelType() {
        return OSGiImplementation.class;
    }

    private String[] tokenize(String str) {
        StringTokenizer tokenizer = new StringTokenizer(str);
        String[] tokens = new String[tokenizer.countTokens()];
        for (int i= 0; i < tokens.length; i++) {
            tokens[i] = tokenizer.nextToken();
        }
        
        return tokens;
    }
    
    public OSGiImplementation read(XMLStreamReader reader) throws ContributionReadException {
        
        assert IMPLEMENTATION_OSGI.equals(reader.getName());
        
        try {
            String bundleName = reader.getAttributeValue(null, BUNDLE);
            String bundleLocation = reader.getAttributeValue(null, BUNDLE_LOCATION);
            String imports = reader.getAttributeValue(null, IMPORTS);
            String[] importList;
            if (imports != null)
                importList = tokenize(imports);
            else
                importList = new String[0];
            String scope = reader.getAttributeValue(null, SCOPE);  
            String allowsPassByRef = reader.getAttributeValue(null, ALLOWS_PASS_BY_REF);
            String[] allowsPassByRefList;
            if (allowsPassByRef != null)
                allowsPassByRefList = tokenize(allowsPassByRef);
            else
                allowsPassByRefList = new String[0];
            
            boolean injectProperties = !"false".equalsIgnoreCase(reader.getAttributeValue(null, INJECT_PROPERTIES));
            
            
            Hashtable<String, List<ComponentProperty>> refProperties = 
                new Hashtable<String, List<ComponentProperty>>();
            Hashtable<String, List<ComponentProperty>> serviceProperties = 
                new Hashtable<String, List<ComponentProperty>>();
            
            while (reader.hasNext()) {
                
                int next = reader.next();
                if (next == END_ELEMENT && IMPLEMENTATION_OSGI.equals(reader.getName())) {
                    break;
                }
                else if (next == START_ELEMENT && PROPERTIES_QNAME.equals(reader.getName())) {
                    
                    // FIXME: This is temporary code which allows reference and service properties used
                    //        for filtering OSGi services to be specified in <implementation.osgi/>
                    //        This should really be provided in the component type file since these
                    //        properties are associated with an implementation rather than a configured
                    //        instance of an implementation.
                    String refName = reader.getAttributeValue(null, "reference");
                    String serviceName = reader.getAttributeValue(null, "service");
                    List<ComponentProperty> props = readProperties(reader);
                    if (refName != null)
                        refProperties.put(refName, props);
                    else if (serviceName != null)
                        serviceProperties.put(serviceName, props);
                    else
                        throw new ContributionReadException("Properties in implementation.osgi should specify service or reference");                }
            }
                
            OSGiImplementation implementation = new OSGiImplementation(
                    bundleName, 
                    bundleLocation,
                    importList, 
                    scope,
                    allowsPassByRefList,
                    refProperties,
                    serviceProperties,
                    injectProperties);
            
            
            processComponentType(bundleName, implementation);
            
            implementation.setUnresolved(true);
            
            return implementation;
            
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
    }

    public void resolve(OSGiImplementation impl, ModelResolver resolver) throws ContributionResolveException {
        
        try {
            
            impl.setUnresolved(false);
            
            // FIXME: Tuscany will only process the component type file if it is visible
            //        to its classloader. So it can't really be located anywhere in the
            //        directory structure like the bundle. So even though the bundle name
            //        is used to find the relative pathname of the component type file,
            //        its absolute location is obtained from the classloader. This doesn't
            //        seem right, since the bundle could potentially be located anywhere.
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            String bundleName = impl.getBundleName();
            String ctName = bundleName.replaceAll("\\.", "/") + ".componentType";
            String ctURI = cl.getResource(ctName).toString();
            
            impl.setURI(ctURI);
            ComponentType componentType = resolver.resolveModel(ComponentType.class, impl);
            if (componentType.isUnresolved()) {
                throw new ContributionResolveException("missing .componentType side file");
            }
            
            List<Service> services = componentType.getServices();
            for (Service service : services) {
                Interface interfaze = service.getInterfaceContract().getInterface();
                if (interfaze instanceof JavaInterface) {
                    JavaInterface javaInterface = (JavaInterface)interfaze;
                    if (javaInterface.getJavaClass() == null) {
                        Class<?> javaClass = Class.forName(javaInterface.getName());
                        javaInterface.setJavaClass(javaClass);
                    }
                    Service serv = createService(service, javaInterface.getJavaClass());
                    impl.getServices().add(serv);
                }
            }
            
            List<Reference> references = componentType.getReferences();
            for (Reference reference : references) {
                Interface interfaze = reference.getInterfaceContract().getInterface();
                if (interfaze instanceof JavaInterface) {
                    JavaInterface javaInterface = (JavaInterface)interfaze;
                    if (javaInterface.getJavaClass() == null) {
                        Class<?> javaClass = Class.forName(javaInterface.getName());
                        javaInterface.setJavaClass(javaClass);
                    }
                    Reference ref = createReference(reference, javaInterface.getJavaClass());
                    impl.getReferences().add(ref);
                }
                else
                    impl.getReferences().add(reference);
            }
            
            List<Property> properties = componentType.getProperties();
            for (Property property : properties) {
                impl.getProperties().add(property);
            }
            impl.setConstrainingType(componentType.getConstrainingType());
            
            
        } catch (Exception e) {
            throw new ContributionResolveException(e);
        }
        
    }
    
    private Service createService(Service serv, Class<?> interfaze) throws InvalidInterfaceException {
        Service service = assemblyFactory.createService();
        JavaInterfaceContract interfaceContract = javaInterfaceFactory.createJavaInterfaceContract();
        service.setInterfaceContract(interfaceContract);

        // create a relative URI
        service.setName(serv.getName());

        JavaInterface callInterface = interfaceIntrospector.introspect(interfaze);
        service.getInterfaceContract().setInterface(callInterface);
        if (callInterface.getCallbackClass() != null) {
            JavaInterface callbackInterface = interfaceIntrospector.introspect(callInterface.getCallbackClass());
            service.getInterfaceContract().setCallbackInterface(callbackInterface);
        }
        return service;
    }
    
    private Reference createReference(Reference ref, Class<?> clazz) throws InvalidInterfaceException {
        org.apache.tuscany.sca.assembly.Reference reference = assemblyFactory.createReference();
        JavaInterfaceContract interfaceContract = javaInterfaceFactory.createJavaInterfaceContract();
        reference.setInterfaceContract(interfaceContract);
        
        reference.setName(ref.getName());
        reference.setMultiplicity(ref.getMultiplicity());

        JavaInterface callInterface = interfaceIntrospector.introspect(clazz);
        reference.getInterfaceContract().setInterface(callInterface);
        if (callInterface.getCallbackClass() != null) {
            JavaInterface callbackInterface = interfaceIntrospector.introspect(callInterface.getCallbackClass());
            reference.getInterfaceContract().setCallbackInterface(callbackInterface);
        }
       
        return reference;
    }

    public void write(OSGiImplementation model, XMLStreamWriter outputSource) throws ContributionWriteException {
    }
    
    private void processComponentType(String bundleName, OSGiImplementation implementation) {
        
        // Form the URI of the expected .componentType file;
        String ctName = bundleName.replaceAll("\\.", "/") + ".componentType";
        String uri = ctName;

        
        implementation.setURI(uri);
        implementation.setUnresolved(true);
    }

    private QName getQNameValue(XMLStreamReader reader, String value) {
        if (value != null) {
            int index = value.indexOf(':');
            String prefix = index == -1 ? "" : value.substring(0, index);
            String localName = index == -1 ? value : value.substring(index + 1);
            String ns = reader.getNamespaceContext().getNamespaceURI(prefix);
            if (ns == null) {
                ns = "";
            }
            return new QName(ns, localName, prefix);
        } else {
            return null;
        }
    }
    
    private void declareNamespace(Element element, String prefix, String ns) {
        String qname = null;
        if ("".equals(prefix)) {
            qname = "xmlns";
        } else {
            qname = "xmlns:" + prefix;
        }
        Node node = element;
        boolean declared = false;
        while (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
            NamedNodeMap attrs = node.getAttributes();
            if (attrs == null) {
                break;
            }
            Node attr = attrs.getNamedItem(qname);
            if (attr != null) {
                declared = ns.equals(attr.getNodeValue());
                break;
            }
            node = node.getParentNode();
        }
        if (!declared) {
            org.w3c.dom.Attr attr = element.getOwnerDocument().createAttributeNS(XMLNS_ATTRIBUTE_NS_URI, qname);
            attr.setValue(ns);
            element.setAttributeNodeNS(attr);
        }
    }
    
    private Element createElement(Document document, QName name) {
        String prefix = name.getPrefix();
        String qname = (prefix != null && prefix.length() > 0) ? prefix + ":" + name.getLocalPart() : name
            .getLocalPart();
        return document.createElementNS(name.getNamespaceURI(), qname);
    }

    private void loadElement(XMLStreamReader reader, Element root) throws XMLStreamException {
        Document document = root.getOwnerDocument();
        Node current = root;
        while (true) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    QName name = reader.getName();
                    Element child = createElement(document, name);

                    // push the new element and make it the current one
                    current.appendChild(child);
                    current = child;

                    declareNamespace(child, name.getPrefix(), name.getNamespaceURI());

                    int count = reader.getNamespaceCount();
                    for (int i = 0; i < count; i++) {
                        String prefix = reader.getNamespacePrefix(i);
                        String ns = reader.getNamespaceURI(i);
                        declareNamespace(child, prefix, ns);
                    }

                    // add the attributes for this element
                    count = reader.getAttributeCount();
                    for (int i = 0; i < count; i++) {
                        String ns = reader.getAttributeNamespace(i);
                        String prefix = reader.getAttributePrefix(i);
                        String localPart = reader.getAttributeLocalName(i);
                        String value = reader.getAttributeValue(i);
                        child.setAttributeNS(ns, localPart, value);
                        declareNamespace(child, prefix, ns);
                    }

                    break;
                case XMLStreamConstants.CDATA:
                    current.appendChild(document.createCDATASection(reader.getText()));
                    break;
                case XMLStreamConstants.CHARACTERS:
                    current.appendChild(document.createTextNode(reader.getText()));
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    // if we are back at the root then we are done
                    if (current == root) {
                        return;
                    }

                    // pop the element off the stack
                    current = current.getParentNode();
            }
        }
    }
    
    private Document readPropertyValue(XMLStreamReader reader, QName type)
            throws XMLStreamException, ParserConfigurationException {
    
        Document doc = domFactory.newDocumentBuilder().newDocument();

        // root element has no namespace and local name "value"
        Element root = doc.createElementNS(null, "value");
        if (type != null) {
            org.w3c.dom.Attr xsi = doc.createAttributeNS(XMLNS_ATTRIBUTE_NS_URI, "xmlns:xsi");
             xsi.setValue(W3C_XML_SCHEMA_INSTANCE_NS_URI);
            root.setAttributeNodeNS(xsi);

            String prefix = type.getPrefix();
            if (prefix == null || prefix.length() == 0) {
                prefix = "ns";
            }

            declareNamespace(root, prefix, type.getNamespaceURI());

            org.w3c.dom.Attr xsiType = doc.createAttributeNS(W3C_XML_SCHEMA_INSTANCE_NS_URI, "xsi:type");
            xsiType.setValue(prefix + ":" + type.getLocalPart());
            root.setAttributeNodeNS(xsiType);
        }
        doc.appendChild(root);

        loadElement(reader, root);
        return doc;
    }
    
    private  void readProperty(ComponentProperty prop, XMLStreamReader reader)
            throws XMLStreamException, ContributionReadException {
        
    
        prop.setName(reader.getAttributeValue(null, "name"));
        String xsdType = reader.getAttributeValue(null, "type");
        if (xsdType != null)       
            prop.setXSDType(getQNameValue(reader, xsdType));
        else
            prop.setXSDType(SimpleTypeMapperImpl.XSD_STRING);
        
        try {
            Document value = readPropertyValue(reader, prop.getXSDType());
            prop.setValue(value);
        } catch (ParserConfigurationException e) {
            throw new ContributionReadException(e);
        }
    }
    
    private  List<ComponentProperty> readProperties(XMLStreamReader reader)
            throws XMLStreamException, ContributionReadException {
        
        List<ComponentProperty> properties = new ArrayList<ComponentProperty>();
        
        while (reader.hasNext()) {
            
            int next = reader.next();
            if (next == END_ELEMENT && PROPERTIES_QNAME.equals(reader.getName())) {
                break;
            }
            else if (next == START_ELEMENT && PROPERTY_QNAME.equals(reader.getName())) {
                
                ComponentProperty componentProperty = assemblyFactory.createComponentProperty();
                readProperty(componentProperty, reader);
                properties.add(componentProperty);
            }
        }
        
        return properties;

    }
}
