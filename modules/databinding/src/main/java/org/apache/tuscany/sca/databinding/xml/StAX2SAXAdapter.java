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
package org.apache.tuscany.sca.databinding.xml;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Adapter that converts from StAX to SAX event streams. Currently the following
 * SAX events are not generated:
 * <ul>
 * <li>ignorableWhitespace</li>
 * <li>skippedEntity</li>
 * <ul>
 * Also the following StAX events are not mapped:
 * <ul>
 * <li>CDATA</li>
 * <li>COMMENT</li>
 * <li>DTD</li>
 * <li>ENTITY_DECLARATION</li>
 * <li>ENTITY_REFERENCE</li>
 * <li>NOTATION_DECLARATION</li>
 * <li>SPACE</li>
 * </ul>
 * StAX ATTRIBUTE events are ignored but the equivalent attributes (derived from
 * the START_ELEMENT event) are supplied in the SAX startElement event's
 * Attributes parameter. If the adaptor is configured to pass namespace prefixes
 * then namespace information will also be included in the Attributes; StAX
 * NAMESPACE events are ignored. <p/> Another issue is namespace processing. If
 * the reader is positioned at a sub-node, we cannot capture all the in-scope
 * namespace bindings. Therefore we cannot re-create a proper SAX event stream
 * from a StAX parser. <p/> For example <p/> &lt;a:root xmlns:a="foo"
 * xmlns:b="bar"&gt;&lt;b:sub&gt;a:foo&lt;/b:sub&gt;&lt;/a:root&gt; <p/> And if
 * you are handed a parser at &lt;b:sub&gt;, then your SAX events should look
 * like: <p/> &lt;b:sub xmlns:a="foo" xmlns:b="bar"&gt;a:foo&lt;/b:sub&gt; <p/>
 * not: <p/> &lt;b:sub&gt;a:foo&lt;/b:sub&gt; <p/> <p/> Proposal: we change the
 * receiver of SAX events (SDOXMLResourceImpl) so that it uses NamespaceContext
 * to resolve prefix (as opposed to record start/endPrefixMappings and use it
 * for resolution.)
 * 
 * @version $Rev$ $Date$
 */
public class StAX2SAXAdapter {
    private final boolean namespacePrefixes;

    /**
     * Construct a new StAX to SAX adapter that will convert a StAX event stream
     * into a SAX event stream.
     * 
     * @param namespacePrefixes whether xmlns attributes should be included in
     *            startElement events;
     */
    public StAX2SAXAdapter(boolean namespacePrefixes) {
        this.namespacePrefixes = namespacePrefixes;
    }

    /**
     * Pull events from the StAX stream and dispatch to the SAX ContentHandler.
     * The StAX stream would typically be located on a START_DOCUMENT or
     * START_ELEMENT event and when this method returns it will be located on
     * the associated END_DOCUMENT or END_ELEMENT event. Behaviour with other
     * start events is undefined.
     * 
     * @param reader StAX event source to read
     * @param handler SAX ContentHandler for processing events
     * @throws XMLStreamException if there was a problem reading the stream
     * @throws SAXException passed through from the ContentHandler
     */
    public void parse(XMLStreamReader reader, ContentHandler handler) throws XMLStreamException, SAXException {
        handler.setDocumentLocator(new LocatorAdaptor(reader.getLocation()));

        // remembers the nest level of elements to know when we are done
        int level = 0;
        int event = reader.getEventType();
        while (true) {
            switch (event) {
                case XMLStreamConstants.START_DOCUMENT:
                    level++;
                    handler.startDocument();
                    break;
                case XMLStreamConstants.START_ELEMENT:
                    level++;
                    handleStartElement(reader, handler);
                    break;
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                    handler.processingInstruction(reader.getPITarget(), reader.getPIData());
                    break;
                case XMLStreamConstants.CHARACTERS:
                    handler.characters(reader.getTextCharacters(), reader.getTextStart(), reader
                        .getTextLength());
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    handleEndElement(reader, handler);
                    level--;
                    if (level == 0) {
                        return;
                    }
                    break;
                case XMLStreamConstants.END_DOCUMENT:
                    handler.endDocument();
                    return;
                    /*
                     * uncomment to handle all events rather than just mapped
                     * ones // StAX events that are not mapped to SAX case
                     * XMLStreamConstants.COMMENT: case
                     * XMLStreamConstants.SPACE: case
                     * XMLStreamConstants.ENTITY_REFERENCE: case
                     * XMLStreamConstants.DTD: case XMLStreamConstants.CDATA:
                     * case XMLStreamConstants.NOTATION_DECLARATION: case
                     * XMLStreamConstants.ENTITY_DECLARATION: break; // StAX
                     * events handled in START_ELEMENT case
                     * XMLStreamConstants.ATTRIBUTE: case
                     * XMLStreamConstants.NAMESPACE: break; default: throw new
                     * AssertionError("Unknown StAX event: " + event);
                     */
            }
            event = reader.next();
        }
    }

    private void handleStartElement(XMLStreamReader reader, ContentHandler handler) throws SAXException {
        // send startPrefixMapping events immediately before startElement event
        int nsCount = reader.getNamespaceCount();
        for (int i = 0; i < nsCount; i++) {
            String prefix = reader.getNamespacePrefix(i);
            if (prefix == null) { // true for default namespace
                prefix = "";
            }
            handler.startPrefixMapping(prefix, reader.getNamespaceURI(i));
        }

        // fire startElement
        QName qname = reader.getName();
        String prefix = qname.getPrefix();
        String rawname;
        if (prefix == null || prefix.length() == 0) {
            rawname = qname.getLocalPart();
        } else {
            rawname = prefix + ':' + qname.getLocalPart();
        }
        Attributes attrs = getAttributes(reader);
        handler.startElement(qname.getNamespaceURI(), qname.getLocalPart(), rawname, attrs);
    }

    private static void handleEndElement(XMLStreamReader reader, ContentHandler handler) throws SAXException {
        // fire endElement
        QName qname = reader.getName();
        handler.endElement(qname.getNamespaceURI(), qname.getLocalPart(), qname.toString());

        // send endPrefixMapping events immediately after endElement event
        // we send them in the opposite order to that returned but this is not
        // actually required by SAX
        int nsCount = reader.getNamespaceCount();
        for (int i = nsCount - 1; i >= 0; i--) {
            String prefix = reader.getNamespacePrefix(i);
            if (prefix == null) { // true for default namespace
                prefix = "";
            }
            handler.endPrefixMapping(prefix);
        }
    }

    /**
     * Get the attributes associated with the current START_ELEMENT event.
     * 
     * @return the StAX attributes converted to org.xml.sax.Attributes
     */
    private Attributes getAttributes(XMLStreamReader reader) {
        assert reader.getEventType() == XMLStreamConstants.START_ELEMENT;

        AttributesImpl attrs = new AttributesImpl();

        // add namespace declarations if required
        if (namespacePrefixes) {
            for (int i = 0; i < reader.getNamespaceCount(); i++) {
                String prefix = reader.getNamespacePrefix(i);
                String uri = reader.getNamespaceURI(i);
                attrs.addAttribute(null, prefix, "xmlns:" + prefix, "CDATA", uri);
            }
        }

        // Regular attributes
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String uri = reader.getAttributeNamespace(i);
            if (uri == null) {
                uri = "";
            }
            String localName = reader.getAttributeLocalName(i);
            String prefix = reader.getAttributePrefix(i);
            String qname;
            if (prefix == null || prefix.length() == 0) {
                qname = localName;
            } else {
                qname = prefix + ':' + localName;
            }
            String type = reader.getAttributeType(i);
            String value = reader.getAttributeValue(i);

            attrs.addAttribute(uri, localName, qname, type, value);
        }

        return attrs;
    }

    /**
     * Adaptor for mapping Locator information.
     */
    private static final class LocatorAdaptor implements Locator {
        private final Location location;

        private LocatorAdaptor(Location location) {
            this.location = location;
        }

        public int getColumnNumber() {
            return location == null ? 0 : location.getColumnNumber();
        }

        public int getLineNumber() {
            return location == null ? 0 : location.getLineNumber();
        }

        public String getPublicId() {
            return location == null ? "" : location.getPublicId();
        }

        public String getSystemId() {
            return location == null ? "" : location.getSystemId();
        }
    }
}
