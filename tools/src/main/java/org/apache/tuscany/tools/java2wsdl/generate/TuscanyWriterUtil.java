package org.apache.tuscany.tools.java2wsdl.generate;

import java.io.IOException;
import java.io.Writer;

/**
 * This class is an exact replica of the Axis2 WriterUtil class that is used by
 * the Axis2 WOM2WSDL11Writer. The TuscanyWOM2WSDL11Writer is being used in
 * place of WOM2WSDL11Writer. The class WriterUtil having 'package access' is
 * not accessible from within TuscanyWOM2WSDL11Writer. Hence
 * TuscanyWOM2WSDL11Writer uses this replica class TuscanyWriterUtil
 * 
 */
class TuscanyWriterUtil {

	/**
	 * Write a start element
	 * 
	 * @param elementName
	 * @param writer
	 * @throws IOException
	 */
	public static void writeStartElement(String elementName, Writer writer)
			throws IOException {
		writer.write("<" + elementName);
	}

	/**
	 * 
	 * @param elementName
	 * @param nsPrefix
	 * @param writer
	 * @throws IOException
	 */
	public static void writeStartElement(String elementName, String nsPrefix,
			Writer writer) throws IOException {
		if (nsPrefix == null) {
			writeStartElement(elementName, writer);
		} else {
			writer.write("<" + nsPrefix + ":" + elementName);
		}

	}

	/**
	 * Close start Element
	 * 
	 * @param elementName
	 * @param writer
	 * @throws IOException
	 */
	public static void writeCloseStartElement(Writer writer) throws IOException {
		writer.write(">\n");
	}

	/**
	 * write an attrib
	 * 
	 * @param attName
	 * @param value
	 * @param writer
	 * @throws IOException
	 */
	public static void writeAttribute(String attName, String value,
			Writer writer) throws IOException {
		writer.write(" " + attName + "=\"" + value + "\"");
	}

	/**
	 * Write end element
	 * 
	 * @param attName
	 * @param value
	 * @param writer
	 * @throws IOException
	 */
	public static void writeEndElement(String eltName, Writer writer)
			throws IOException {
		writer.write("</" + eltName + ">\n");
	}

	/**
	 * Write end element
	 * 
	 * @param writer
	 * @throws IOException
	 */
	public static void writeCompactEndElement(Writer writer) throws IOException {
		writer.write("/>\n");
	}

	/**
	 * Write end element
	 * 
	 * @param attName
	 * @param value
	 * @param writer
	 * @throws IOException
	 */
	public static void writeEndElement(String eltName, String nsPrefix,
			Writer writer) throws IOException {
		if (nsPrefix == null) {
			writeEndElement(eltName, writer);
		} else {
			writer.write("</" + nsPrefix + ":" + eltName + ">\n");
		}
	}

	public static void writeNamespace(String prefix, String namespaceURI,
			Writer writer) throws IOException {
		if (prefix == null || prefix.trim().length() == 0) {
			writeAttribute("xmlns", namespaceURI, writer);
		} else {
			writeAttribute("xmlns:" + prefix, namespaceURI, writer);
		}
	}

	public static void writeComment(String comment, Writer writer)
			throws IOException {
		writer.write("<!--" + comment + "-->");
	}

}
