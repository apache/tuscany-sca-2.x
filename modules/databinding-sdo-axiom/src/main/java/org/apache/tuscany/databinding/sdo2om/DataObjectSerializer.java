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

package org.apache.tuscany.databinding.sdo2om;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sdo.impl.AttributeImpl;
import org.apache.tuscany.sdo.impl.ReferenceImpl;
import org.apache.tuscany.sdo.util.SDOUtil;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.ExtendedMetaData;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XSDHelper;

public class DataObjectSerializer {
    private static final String ELEMENT_TEXT = "Text Element";

    // static final String ELEMENT_TEXT = "Element Text";
    private static final QName XSI_TYPE_QNAME = new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi");

    private Map<String, String> declaredNamespaceMap = new HashMap<String, String>();

    private NameSpaceContextImpl namespaceContext = new NameSpaceContextImpl();

    private DataObject rootDataObject;

    private String rootElementName;

    private String rootElementURI;

    private XMLStreamWriter xmlWriter;

    private XSDHelper xsdHelper;

    public DataObjectSerializer(DataObject rootObject,
                                XMLStreamWriter xmlWriter,
                                HelperContext helperCtx,
                                TransformationContext context) {
        this.xmlWriter = xmlWriter;
        this.rootDataObject = rootObject;
        this.xsdHelper = helperCtx.getXSDHelper();
        this.rootElementName = xsdHelper.getLocalName(rootObject.getType());
        this.rootElementURI = rootDataObject.getType().getURI();

        if (context != null) {
            DataType dataType = context.getTargetDataType();
            Object targetQName = dataType == null ? null : dataType.getLogical();
            if (targetQName instanceof QName) {
                QName name = (QName)targetQName;
                this.rootElementName = name.getLocalPart();
                this.rootElementURI = name.getNamespaceURI();
            }
        }

    }

    // private void serializeNamespace(String prefix, String URI,
    // XMLStreamWriter writer) throws XMLStreamException {
    // String prefix1 = writer.getPrefix(URI);
    // if (prefix1 == null) {
    // writer.writeNamespace(prefix, URI);
    // writer.setPrefix(prefix, URI);
    // }
    // }

    public DataObjectSerializer(XMLDocument sourceDocument, XMLStreamWriter xmlWriter, HelperContext helperCtx) {
        this.xmlWriter = xmlWriter;
        this.rootDataObject = sourceDocument.getRootObject();
        this.rootElementName = sourceDocument.getRootElementName();
        this.rootElementURI = sourceDocument.getRootElementURI();
        this.xsdHelper = helperCtx.getXSDHelper();
    }

    protected class NameSpaceContextImpl implements NamespaceContext {
        private int counter;

        private Map<String, String> prefixToNamespaceMapping = new HashMap<String, String>();

        public NameSpaceContextImpl() {
            prefixToNamespaceMapping.put("xml", "http://www.w3.org/XML/1998/namespace");
            prefixToNamespaceMapping.put("xmlns", "http://www.w3.org/2000/xmlns/");
            prefixToNamespaceMapping.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        }

        public synchronized QName createQName(String nsURI, String name) {
            String prefix = nsURI != null ? (String)getPrefix(nsURI) : null;
            if (prefix == null && nsURI != null && !nsURI.equals("")) {
                prefix = "p" + (counter++);
            }
            if (prefix == null) {
                prefix = "";
            }
            if (nsURI != null) {
                prefixToNamespaceMapping.put(prefix, nsURI);
                declaredNamespaceMap.put(prefix, nsURI);
            }
            return new QName(nsURI, name, prefix);
        }

        public String getNamespaceURI(String prefix) {
            if (prefix == null) {
                throw new IllegalArgumentException("Prefix is null");
            }

            String ns = (String)prefixToNamespaceMapping.get(prefix);
            if (ns != null) {
                return ns;
            } else {
                return null;
            }
        }

        public String getPrefix(String nsURI) {
            if (nsURI == null) {
                throw new IllegalArgumentException("Namespace is null");
            }
            for (Iterator i = prefixToNamespaceMapping.entrySet().iterator(); i.hasNext();) {
                Map.Entry entry = (Map.Entry)i.next();
                if (entry.getValue().equals(nsURI)) {
                    return (String)entry.getKey();
                }
            }
            return null;
        }

        public Iterator getPrefixes(String nsURI) {
            List prefixList = new ArrayList();
            for (Iterator i = prefixToNamespaceMapping.entrySet().iterator(); i.hasNext();) {
                Map.Entry entry = (Map.Entry)i.next();
                if (entry.getValue().equals(nsURI)) {
                    prefixList.add(entry.getKey());
                }
            }
            return prefixList.iterator();
        }

        public void registerMapping(String prefix, String nsURI) {
            prefixToNamespaceMapping.put(prefix, nsURI);
        }

        public void removeMapping(String prefix) {
            prefixToNamespaceMapping.remove(prefix);
        }
    }

    protected static class NameValuePair implements Map.Entry {
        private Object key;

        private Object value;

        public NameValuePair(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public Object setValue(Object value) {
            Object v = this.value;
            this.value = value;
            return v;
        }

    }

    private static boolean isTransient(Property property, Object type) {
        // HACK: We need some SDOUtil extension to understand a property is
        // derived
        EStructuralFeature feature = (EStructuralFeature)property;
        if (ExtendedMetaData.INSTANCE.getGroup(feature) != null) {
            return false;
        }
        feature = ExtendedMetaData.INSTANCE.getAffiliation((EClass)type, feature);
        if (feature != null && feature != property) {
            return false;
        }
        if (property instanceof ReferenceImpl) {
            ReferenceImpl r = (ReferenceImpl)property;
            if (r.isTransient()) {
                return true;
            }
            EReference opposite = r.getEOpposite();
            if (opposite != null && opposite.isContainment()) {
                return true;
            }
        } else if (property instanceof AttributeImpl) {
            AttributeImpl a = (AttributeImpl)property;
            if (a.isTransient()) {
                return true;
            }
            EDataType d = (EDataType)a.getEType();
            if (!d.isSerializable()) {
                return true;
            }
        }
        return false;
    }

    private void addListValue(List<NameValuePair> propertyList,
                              List<DataObject> children,
                              Property property,
                              List objList) throws XMLStreamException {
        if (objList != null) {
            for (int j = 0; j < objList.size(); j++) {
                Object object = objList.get(j);
                addSingleValue(propertyList, children, property, object);
            }
        }
    }

    private void addProperty(List<NameValuePair> propertyList,
                             List<DataObject> children,
                             Property property,
                             Object value,
                             DataObject dataObject) throws XMLStreamException {

        if (property.isMany() && property.getContainingType().isOpen() && value instanceof Sequence) {
            addSequenceValue(propertyList, children, (Sequence)value);
        } else if (SDOUtil.isMany(property, dataObject) && value instanceof List) {
            addListValue(propertyList, children, property, (List)value);
        } else {
            // Complex Type
            addSingleValue(propertyList, children, property, value);
        }
    }

    private void addSequenceValue(List<NameValuePair> elements, List<DataObject> children, Sequence seq)
        throws XMLStreamException {
        if (seq != null && seq.size() > 0) {
            for (int j = 0; j < seq.size(); j++) {
                Object o = seq.getValue(j);
                Property p = seq.getProperty(j);
                addSingleValue(elements, children, p, o);
            }
        }
    }

    private void addSingleValue(List<NameValuePair> propertyList,
                                List<DataObject> children,
                                Property property,
                                Object value) throws XMLStreamException {
        String uri = xsdHelper.getNamespaceURI(property);
        String name = xsdHelper.getLocalName(property);
        QName qname = namespaceContext.createQName(uri, name);
        Type propertyType = property.getType();

        if (property.getName().equals("value") && uri == null && name.equals(":0")) {
            propertyList.add(new NameValuePair(ELEMENT_TEXT, value.toString()));
        } else if (value == null) {
            NameValuePair entry = new NameValuePair(qname, null);
            propertyList.add(entry);
        } else if (propertyType.isDataType()) {
            NameValuePair entry = new NameValuePair(qname, SDOUtil.convertToString(propertyType, value));
            propertyList.add(entry);
        } else {
            children.add((DataObject)value);
        }
    }

    private void registerNamespace(String prefix, String uri) {
        if (!uri.equals(namespaceContext.getNamespaceURI(prefix))) {
            namespaceContext.registerMapping(prefix, uri);
            declaredNamespaceMap.put(prefix, uri);
        }
    }

    public void serialize() throws XMLStreamException {
        xmlWriter.setNamespaceContext(namespaceContext);
        writeDataObject(rootDataObject, rootElementName, rootElementURI);
        xmlWriter.flush();
    }

    private void writeDataObject(DataObject obj, String elementName, String elementURI) throws XMLStreamException {
        List<NameValuePair> elementList = new ArrayList<NameValuePair>();
        List<DataObject> children = new ArrayList<DataObject>();
        List<NameValuePair> attributes = new ArrayList<NameValuePair>();

        String typeName;
        QName realTypeName = null;

        if (elementName != null) {
            realTypeName = namespaceContext.createQName(elementURI, elementName);
            String typeQName = realTypeName.getPrefix() + ":" + realTypeName.getLocalPart();
            declaredNamespaceMap.put(realTypeName.getPrefix(), realTypeName.getNamespaceURI());
            attributes.add(new NameValuePair(XSI_TYPE_QNAME, typeQName));
            registerNamespace(XSI_TYPE_QNAME.getPrefix(), XSI_TYPE_QNAME.getNamespaceURI());
        } else {

            typeName = xsdHelper.getLocalName(obj.getContainmentProperty());
            realTypeName = namespaceContext.createQName(obj.getType().getURI(), typeName);
            registerNamespace(realTypeName.getPrefix(), realTypeName.getNamespaceURI());
        }

        registerNamespace(realTypeName.getPrefix(), realTypeName.getNamespaceURI());

        if (obj.getType().isSequenced()) {
            Sequence sequence = obj.getSequence();
            for (int i = 0; i < sequence.size(); i++) {
                Property property = sequence.getProperty(i);
                Object value = sequence.getValue(i);
                if (property == null) {
                    elementList.add(new NameValuePair(ELEMENT_TEXT, value));
                } else {
                    addProperty(elementList, children, property, value, obj);
                }
            }

            // Attributes are not in the sequence
            List properties = obj.getInstanceProperties();
            for (Iterator i = properties.iterator(); i.hasNext();) {
                Property property = (Property)i.next();
                if (xsdHelper.isAttribute(property) && obj.isSet(property) && !isTransient(property, obj.getType())) {
                    Object value = obj.get(property);
                    QName name =
                        namespaceContext.createQName(xsdHelper.getNamespaceURI(property), xsdHelper
                            .getLocalName(property));
                    attributes.add(new NameValuePair(name, SDOUtil.convertToString(property.getType(), value)));
                }
            }
        } else {
            Iterator i = obj.getInstanceProperties().iterator();
            while (i.hasNext()) {
                Property p = (Property)i.next();
                if (obj.isSet(p) && !isTransient(p, obj.getType())) {
                    Object value = obj.get(p);
                    if (xsdHelper.isAttribute(p)) {
                        QName name =
                            namespaceContext.createQName(xsdHelper.getNamespaceURI(p), xsdHelper.getLocalName(p));
                        attributes.add(new NameValuePair(name, SDOUtil.convertToString(p.getType(), value)));
                    } else {
                        addProperty(elementList, children, p, value, obj);
                    }
                }
            }
        }

        String prefix = realTypeName.getPrefix();
        String nameSpaceName = realTypeName.getNamespaceURI();

        if (nameSpaceName != null) {
            String writerPrefix = xmlWriter.getPrefix(nameSpaceName);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(nameSpaceName, realTypeName.getLocalPart());
            } else {
                if (prefix != null) {
                    xmlWriter.writeStartElement(prefix, realTypeName.getLocalPart(), nameSpaceName);
                    xmlWriter.writeNamespace(prefix, nameSpaceName);
                    xmlWriter.setPrefix(prefix, nameSpaceName);
                } else {
                    xmlWriter.writeStartElement(nameSpaceName, realTypeName.getLocalPart());
                    xmlWriter.writeDefaultNamespace(nameSpaceName);
                    xmlWriter.setDefaultNamespace(nameSpaceName);
                }
            }
        } else {
            xmlWriter.writeStartElement(realTypeName.getLocalPart());
        }

        for (NameValuePair pair : attributes) {
            QName name = (QName)pair.getKey();
            assert namespaceContext.getPrefix(name.getPrefix()).equals(name.getNamespaceURI());
            xmlWriter.writeAttribute(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), (String)pair
                .getValue());
        }

        for (NameValuePair pair : elementList) {
            if (ELEMENT_TEXT.equals(pair.getKey().toString())) {
                xmlWriter.writeCharacters((String)pair.getValue());
            } else {
                QName name = (QName)pair.getKey();
                xmlWriter.writeStartElement(name.getPrefix(), name.getLocalPart(), name.getNamespaceURI());
                xmlWriter.writeCharacters((String)pair.getValue());
                xmlWriter.writeEndElement();
            }
        }

        for (DataObject child : children) {
            writeDataObject(child, null, null);
        }
        xmlWriter.writeEndElement();

    }

}
