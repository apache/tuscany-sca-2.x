package org.apache.tuscany.databinding.json;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * The XMLStreamSerializer pulls events from the XMLStreamReader and dumps into the XMLStreamWriter
 */
public class XMLStreamSerializer implements XMLStreamConstants {
    public static final String NAMESPACE_PREFIX = "ns";
    private static int namespaceSuffix;

    /*
     * The behavior of the serializer is such that it returns when it encounters the starting element for the second
     * time. The depth variable tracks the depth of the serilizer and tells it when to return. Note that it is assumed
     * that this serialization starts on an Element.
     */

    /**
     * Field depth
     */
    private int depth;

    /**
     * Generates a unique namespace prefix that is not in the scope of the NamespaceContext
     * 
     * @param nsCtxt
     * @return string
     */
    private String generateUniquePrefix(NamespaceContext nsCtxt) {
        String prefix = NAMESPACE_PREFIX + namespaceSuffix++;
        // null should be returned if the prefix is not bound!
        while (nsCtxt.getNamespaceURI(prefix) != null) {
            prefix = NAMESPACE_PREFIX + namespaceSuffix++;
        }

        return prefix;
    }

    /**
     * Method serialize.
     * 
     * @param node
     * @param writer
     * @throws XMLStreamException
     */
    public void serialize(XMLStreamReader node, XMLStreamWriter writer) throws XMLStreamException {
        serializeNode(node, writer);
    }

    /**
     * @param reader
     * @param writer
     * @throws XMLStreamException
     */
    protected void serializeAttributes(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        int count = reader.getAttributeCount();
        String prefix;
        String namespaceName;
        String writerPrefix;
        for (int i = 0; i < count; i++) {
            prefix = reader.getAttributePrefix(i);
            namespaceName = reader.getAttributeNamespace(i);
            /*
             * Due to parser implementations returning null as the namespace URI (for the empty namespace) we need to
             * make sure that we deal with a namespace name that is not null. The best way to work around this issue is
             * to set the namespace uri to "" if it is null
             */
            if (namespaceName == null) {
                namespaceName = "";
            }

            writerPrefix = writer.getNamespaceContext().getPrefix(namespaceName);

            if (!"".equals(namespaceName)) {
                // prefix has already being declared but this particular
                // attrib has a
                // no prefix attached. So use the prefix provided by the
                // writer
                if (writerPrefix != null && (prefix == null || prefix.equals(""))) {
                    writer.writeAttribute(writerPrefix, namespaceName, reader.getAttributeLocalName(i), reader
                        .getAttributeValue(i));

                    // writer prefix is available but different from the
                    // current
                    // prefix of the attrib. We should be decalring the new
                    // prefix
                    // as a namespace declaration
                } else if (prefix != null && !"".equals(prefix) && !prefix.equals(writerPrefix)) {
                    writer.writeNamespace(prefix, namespaceName);
                    writer.writeAttribute(prefix, namespaceName, reader.getAttributeLocalName(i), reader
                        .getAttributeValue(i));

                    // prefix is null (or empty), but the namespace name is
                    // valid! it has not
                    // being written previously also. So we need to generate
                    // a prefix
                    // here
                } else if (prefix == null || prefix.equals("")) {
                    prefix = generateUniquePrefix(writer.getNamespaceContext());
                    writer.writeNamespace(prefix, namespaceName);
                    writer.writeAttribute(prefix, namespaceName, reader.getAttributeLocalName(i), reader
                        .getAttributeValue(i));
                } else {
                    writer.writeAttribute(prefix, namespaceName, reader.getAttributeLocalName(i), reader
                        .getAttributeValue(i));
                }
            } else {
                // empty namespace is equal to no namespace!
                writer.writeAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
            }

        }
    }

    /**
     * Method serializeCData.
     * 
     * @param reader
     * @param writer
     * @throws XMLStreamException
     */
    protected void serializeCData(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeCData(reader.getText());
    }

    /**
     * Method serializeComment.
     * 
     * @param reader
     * @param writer
     * @throws XMLStreamException
     */
    protected void serializeComment(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeComment(reader.getText());
    }

    /**
     * @param reader
     * @param writer
     * @throws XMLStreamException
     */
    protected void serializeElement(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        String prefix = reader.getPrefix();
        String nameSpaceName = reader.getNamespaceURI();
        if (nameSpaceName != null) {
            String writerPrefix = writer.getPrefix(nameSpaceName);
            if (writerPrefix != null) {
                writer.writeStartElement(nameSpaceName, reader.getLocalName());
            } else {
                if (prefix != null) {
                    writer.writeStartElement(prefix, reader.getLocalName(), nameSpaceName);
                    writer.writeNamespace(prefix, nameSpaceName);
                    writer.setPrefix(prefix, nameSpaceName);
                } else {
                    // [rfeng] We need to set default NS 1st before calling writeStateElement
                    writer.setDefaultNamespace(nameSpaceName);
                    writer.writeStartElement(nameSpaceName, reader.getLocalName());
                    writer.writeDefaultNamespace(nameSpaceName);
                }
            }
        } else {
            writer.writeStartElement(reader.getLocalName());
        }

        // add the namespaces
        int count = reader.getNamespaceCount();
        String namespacePrefix;
        for (int i = 0; i < count; i++) {
            namespacePrefix = reader.getNamespacePrefix(i);
            // [rfeng] The following is commented out to allow to default ns
            // if (namespacePrefix != null && namespacePrefix.length() == 0) {
            // continue;
            // }

            serializeNamespace(namespacePrefix, reader.getNamespaceURI(i), writer);
        }

        // add attributes
        serializeAttributes(reader, writer);

    }

    /**
     * Method serializeEndElement.
     * 
     * @param writer
     * @throws XMLStreamException
     */
    protected void serializeEndElement(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();
    }

    /**
     * Method serializeNamespace.
     * 
     * @param prefix
     * @param uri
     * @param writer
     * @throws XMLStreamException
     */
    private void serializeNamespace(String prefix, String uri, XMLStreamWriter writer) throws XMLStreamException {
        String prefix1 = writer.getPrefix(uri);
        if (prefix1 == null) {
            writer.writeNamespace(prefix, uri);
            writer.setPrefix(prefix, uri);
        }
    }

    /**
     * Method serializeNode.
     * 
     * @param reader
     * @param writer
     * @throws XMLStreamException
     */
    protected void serializeNode(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        // TODO We get the StAXWriter at this point and uses it hereafter
        // assuming that this is the only entry point
        // to this class.
        // If there can be other classes calling methodes of this we might
        // need to change methode signatures to
        // OMOutputer
        while (true) {
            int event = reader.getEventType();
            if (event == START_ELEMENT) {
                serializeElement(reader, writer);
                depth++;
            } else if (event == ATTRIBUTE) {
                serializeAttributes(reader, writer);
            } else if (event == CHARACTERS) {
                serializeText(reader, writer);
            } else if (event == COMMENT) {
                serializeComment(reader, writer);
            } else if (event == CDATA) {
                serializeCData(reader, writer);
            } else if (event == END_ELEMENT) {
                serializeEndElement(writer);
                depth--;
            } else if (event == START_DOCUMENT) {
                depth++; // if a start document is found then increment
                writer.writeStartDocument();
                // the depth
            } else if (event == END_DOCUMENT) {
                if (depth != 0) {
                    depth--; // for the end document - reduce the depth
                }
                writer.writeEndDocument();
            }
            if (depth == 0) {
                break;
            }
            if (reader.hasNext()) {
                reader.next();
            } else {
                break;
            }
        }
    }

    /**
     * @param reader
     * @param writer
     * @throws XMLStreamException
     */
    protected void serializeText(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeCharacters(reader.getText());
    }
}
