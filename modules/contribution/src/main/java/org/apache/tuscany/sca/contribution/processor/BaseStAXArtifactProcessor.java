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

package org.apache.tuscany.sca.contribution.processor;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.Collection;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Extensible;
import org.apache.tuscany.sca.assembly.Extension;
import org.apache.tuscany.sca.common.xml.stax.StAXHelper;


/**
 * A base class with utility methods for the other artifact processors in this module.
 *
 * @version $Rev$ $Date$
 */
public abstract class BaseStAXArtifactProcessor {
    /**
     * The StAXHelper without states
     */
    private static final StAXHelper helper = new StAXHelper(null, null, null);
    /**
     * Returns a QName from a string.
     * @param reader
     * @param value
     * @return
     */
    protected QName getQNameValue(XMLStreamReader reader, String value) {
        return StAXHelper.getValueAsQName(reader, value);
    }

    /**
     * Returns the boolean value of an attribute.
     * @param reader
     * @param name
     * @return
     */
    protected boolean getBoolean(XMLStreamReader reader, String name) {
        Boolean attr = StAXHelper.getAttributeAsBoolean(reader, name);
        if (attr == null) {
            return false;
        } else {
            return attr.booleanValue();
        }
    }

    /**
     * Returns the QName value of an attribute.
     * @param reader
     * @param name
     * @return
     */
    protected QName getQName(XMLStreamReader reader, String name) {
        return StAXHelper.getAttributeAsQName(reader, name);
    }

    /**
     * Returns the value of an attribute as a list of QNames.
     * @param reader
     * @param name
     * @return
     */
    protected List<QName> getQNames(XMLStreamReader reader, String name) {
        return StAXHelper.getAttributeAsQNames(reader, name);
    }

    /**
     * Returns the string value of an attribute.
     * @param reader
     * @param name
     * @return
     */
    protected String getString(XMLStreamReader reader, String name) {
        return StAXHelper.getAttributeAsString(reader, name);
    }

    /**
     * Test if an attribute is explicitly set
     * @param reader
     * @param name
     * @return
     */
    protected boolean isSet(XMLStreamReader reader, String name) {
        return StAXHelper.isAttributePresent(reader, name);
    }

    /**
     * Returns the value of xsi:type attribute
     * @param reader The XML stream reader
     * @return The QName of the type, if the attribute is not present, null is
     *         returned.
     */
    protected QName getXSIType(XMLStreamReader reader) {
        return StAXHelper.getXSIType(reader);
    }

    /**
     * Parse the next child element.
     * @param reader
     * @return
     * @throws XMLStreamException
     */
    protected boolean nextChildElement(XMLStreamReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == END_ELEMENT) {
                return false;
            }
            if (event == START_ELEMENT) {
                return true;
            }
        }
        return false;
    }

    /**
     * Advance the stream to the next END_ELEMENT event skipping any nested
     * content.
     * @param reader the reader to advance
     * @throws XMLStreamException if there was a problem reading the stream
     */
    protected void skipToEndElement(XMLStreamReader reader) throws XMLStreamException {
        StAXHelper.skipToEndElement(reader);
    }

    /**
     *
     * @param writer
     * @param uri
     * @throws XMLStreamException
     */
    private String setPrefix(XMLStreamWriter writer, String uri) throws XMLStreamException {
        if (uri == null) {
            return null;
        }
        String prefix = writer.getPrefix(uri);
        if (prefix != null) {
            return null;
        } else {

            // Find an available prefix and bind it to the given URI
            NamespaceContext nsc = writer.getNamespaceContext();
            for (int i=1; ; i++) {
                prefix = "ns" + i;
                if (nsc.getNamespaceURI(prefix) == null) {
                    break;
                }
            }
            writer.setPrefix(prefix, uri);
            return prefix;
        }

    }

    /**
     * Start an element.
     * @param uri
     * @param name
     * @param attrs
     * @throws XMLStreamException
     */
    protected void writeStart(XMLStreamWriter writer, String uri, String name, XAttr... attrs)
        throws XMLStreamException {
        helper.writeStartElement(writer, "", name, uri);
        writeAttributes(writer, attrs);
    }

    /**
     * Start an element.
     * @param qname
     * @param attrs
     * @throws XMLStreamException
     */
    protected void writeStart(XMLStreamWriter writer, QName qname, XAttr... attrs) throws XMLStreamException {
        writeStart(writer, qname.getNamespaceURI(), qname.getLocalPart(), attrs);
    }

    /**
     * End an element.
     * @param writer
     * @throws XMLStreamException
     */
    protected void writeEnd(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();
    }

    /**
     * Start a document.
     * @param writer
     * @throws XMLStreamException
     */
    protected void writeStartDocument(XMLStreamWriter writer, String uri, String name, XAttr... attrs) throws XMLStreamException {
        writer.writeStartDocument();
        writer.setDefaultNamespace(uri);
        writeStart(writer, uri, name, attrs);
        // writer.writeDefaultNamespace(uri);
    }

    /**
     * Start a document.
     * @param writer
     * @param qname
     * @param attrs
     * @throws XMLStreamException
     */
    protected void writeStartDocument(XMLStreamWriter writer, QName qname, XAttr... attrs) throws XMLStreamException {
        writeStartDocument(writer, qname.getNamespaceURI(), qname.getLocalPart(), attrs);
    }

    /**
     * End a document.
     * @param writer
     * @throws XMLStreamException
     */
    protected void writeEndDocument(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndDocument();
    }

    /**
     * Write attributes to the current element.
     * @param writer
     * @param attrs
     * @throws XMLStreamException
     */
    protected void writeAttributes(XMLStreamWriter writer, XAttr... attrs) throws XMLStreamException {
        for (XAttr attr : attrs) {
            if (attr != null)
                attr.write(writer);
        }
    }

    /**
     *
     * @param reader
     * @param elementName
     * @param extensible
     * @param extensionAttributeProcessor
     * @param extensionAttributeProcessor
     * @param extensionFactory
     * @throws ContributionReadException
     * @throws XMLStreamException
     */
    protected void readExtendedAttributes(XMLStreamReader reader,
                                          Extensible extensible,
                                          StAXAttributeProcessor extensionAttributeProcessor,
                                          AssemblyFactory extensionFactory) throws ContributionReadException,
        XMLStreamException {
        QName elementName = reader.getName();
        for (int a = 0; a < reader.getAttributeCount(); a++) {
            QName attributeName = reader.getAttributeName(a);
            if (attributeName.getNamespaceURI() != null && attributeName.getNamespaceURI().length() > 0) {
                if (!elementName.getNamespaceURI().equals(attributeName.getNamespaceURI())) {
                    Object attributeValue = extensionAttributeProcessor.read(attributeName, reader);
                    Extension attributeExtension;
                    if (attributeValue instanceof Extension) {
                        attributeExtension = (Extension)attributeValue;
                    } else {
                        attributeExtension = extensionFactory.createExtension();
                        attributeExtension.setQName(attributeName);
                        attributeExtension.setValue(attributeValue);
                        attributeExtension.setAttribute(true);
                    }
                    extensible.getAttributeExtensions().add(attributeExtension);
                }
            }
        }
    }

    /**
     *
     * @param attributeModel
     * @param writer
     * @param extensibleElement
     * @param extensionAttributeProcessor
     * @throws ContributionWriteException
     * @throws XMLStreamException
     */
    protected void writeExtendedAttributes(XMLStreamWriter writer,
                                           Extensible extensibleElement,
                                           StAXAttributeProcessor extensionAttributeProcessor)
        throws ContributionWriteException, XMLStreamException {
        for (Extension extension : extensibleElement.getAttributeExtensions()) {
            if (extension.isAttribute()) {
                extensionAttributeProcessor.write(extension, writer);
            }
        }
    }

    protected void readExtendedElement(XMLStreamReader reader,
                                       Extensible extensible,
                                       StAXArtifactProcessor extensionProcessor) throws ContributionReadException,
        XMLStreamException {
        Object ext = extensionProcessor.read(reader);
        if (extensible != null) {
            extensible.getExtensions().add(ext);
        }
    }

    protected void writeExtendedElements(XMLStreamWriter writer,
                                         Extensible extensible,
                                         StAXArtifactProcessor extensionProcessor) throws ContributionWriteException,
        XMLStreamException {
        for (Object ext : extensible.getExtensions()) {
            extensionProcessor.write(ext, writer);
        }
    }

    /**
     * Represents an XML attribute that needs to be written to a document.
     */
    public static class XAttr {

        private static final String SCA11_NS = "http://docs.oasis-open.org/ns/opencsa/sca/200903";

        private String uri = SCA11_NS;
        private String name;
        private Object value;

        public XAttr(String uri, String name, String value) {
            this.uri = uri;
            this.name = name;
            this.value = value;
        }

        public XAttr(String name, String value) {
            this(null, name, value);
        }

        public XAttr(String uri, String name, List<?> values) {
            this.uri = uri;
            this.name = name;
            this.value = values;
        }

        public XAttr(String name, List<?> values) {
            this(null, name, values);
        }

        public XAttr(String uri, String name, Boolean value) {
            this.uri = uri;
            this.name = name;
            this.value = value;
        }

        public XAttr(String name, Boolean value) {
            this(null, name, value);
        }

        public XAttr(String uri, String name, Integer value) {
            this.uri = uri;
            this.name = name;
            this.value = value;
        }

        public XAttr(String name, Integer value) {
            this(null, name, value);
        }

        public XAttr(String uri, String name, Double value) {
            this.uri = uri;
            this.name = name;
            this.value = value;
        }

        public XAttr(String name, Double value) {
            this(null, name, value);
        }

        public XAttr(String uri, String name, QName value) {
            this.uri = uri;
            this.name = name;
            this.value = value;
        }

        public XAttr(String name, QName value) {
            this(null, name, value);
        }
        
        public String toString() {
            return uri == null ? name + "=\"" + value + "\"" : "{" + uri + "}" + name + "=\"" + value + "\"";
        }

        /**
         * Writes a string from a QName and registers a prefix for its namespace.
         * @param reader
         * @param value
         * @return
         */
        private String writeQNameValue(XMLStreamWriter writer, QName qname) throws XMLStreamException {
            if (qname != null) {
                String prefix = helper.writeNamespace(writer, qname.getPrefix(), qname.getNamespaceURI());
                if ("".equals(prefix)) {
                    return qname.getLocalPart();
                } else {
                    return prefix + ":" + qname.getLocalPart();
                }
            }
            return null;
        }

        /**
         * Write to document
         * @param writer
         * @throws XMLStreamException
         */
        public void write(XMLStreamWriter writer) throws XMLStreamException {
            String str;
            if (value instanceof QName) {

                // Write a QName
                str = writeQNameValue(writer, (QName)value);

            } else if (value instanceof Collection) {

                // Write a list of values
                Collection<?> values = (Collection<?>)value;
                if (values.isEmpty()) {
                    return;
                }
                StringBuffer buffer = new StringBuffer();
                for (Object v: values) {
                    if (v == null) {
                        // Skip null values
                        continue;
                    }

                    if (v instanceof XAttr) {
                        // Write an XAttr value
                        ((XAttr)v).write(writer);
                        continue;
                    }

                    if (buffer.length() != 0) {
                        buffer.append(' ');
                    }
                    if (v instanceof QName) {
                        // Write a QName value
                        buffer.append(writeQNameValue(writer, (QName)v));
                    } else {
                        // Write value as a string
                        buffer.append(String.valueOf(v));
                    }
                }
                str = buffer.toString();

            } else {

                // Write a string
                if (value == null) {
                    return;
                }
                str = String.valueOf(value);
            }
            if (str.length() == 0 && (value instanceof Collection)) {
                return;
            }

            helper.writeAttribute(writer, "", name, uri, str);
        }
    }

}
