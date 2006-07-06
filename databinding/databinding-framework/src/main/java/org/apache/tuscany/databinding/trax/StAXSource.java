/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.databinding.trax;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;

/**
 *  javax.xml.transform.StAXSource will come into JSE 6.0
 * 
 * Acts as a holder for an XML Source in the form of a StAX reader,i.e. XMLStreamReader or XMLEventReader. StAXSource can be used in all cases that
 * accept a Source, e.g. Transformer, Validator which accept Source as input.
 * 
 * StAXSources are consumed during processing and are not reusable.
 * 
 */
public class StAXSource implements Source {
    private XMLStreamReader streamReader = null;

    private XMLEventReader eventReader = null;

    /**
     * Creates a new instance of a StAXSource by supplying an XMLEventReader.
     * 
     * XMLEventReader must be a non-null reference.
     * 
     * XMLEventReader must be in XMLStreamConstants.START_DOCUMENT or XMLStreamConstants.START_ELEMENT state.
     * 
     * 
     * Parameters: xmlEventReader - XMLEventReader used to create this StAXSource. Throws: XMLStreamException - If xmlEventReader access throws an
     * Exception. IllegalArgumentException - If xmlEventReader == null. IllegalStateException - If xmlEventReader is not in
     * XMLStreamConstants.START_DOCUMENT or XMLStreamConstants.START_ELEMENT state.
     * 
     * @param xmlEventReader
     * @throws XMLStreamException
     */
    public StAXSource(XMLEventReader xmlEventReader) throws XMLStreamException {
        if (xmlEventReader == null)
            throw new IllegalArgumentException("XMLEventReader is null");
        this.eventReader = xmlEventReader;
    }

    /**
     * Creates a new instance of a StAXSource by supplying an XMLStreamReader.
     * 
     * XMLStreamReader must be a non-null reference.
     * 
     * XMLStreamReader must be in XMLStreamConstants.START_DOCUMENT or XMLStreamConstants.START_ELEMENT state.
     * 
     * 
     * Parameters: xmlStreamReader - XMLStreamReader used to create this StAXSource. Throws: IllegalArgumentException - If xmlStreamReader == null.
     * IllegalStateException - If xmlStreamReader is not in XMLStreamConstants.START_DOCUMENT or XMLStreamConstants.START_ELEMENT state.
     * 
     * @param xmlStreamReader
     */
    public StAXSource(XMLStreamReader xmlStreamReader) {
        if (xmlStreamReader == null)
            throw new IllegalArgumentException("XMLStreamReader is null");
        this.streamReader = xmlStreamReader;
    }

    /**
     * Get the XMLEventReader used by this StAXSource.
     * 
     * XMLEventReader will be null. if this StAXSource was created with a XMLStreamReader.
     * 
     * 
     * Returns: XMLEventReader used by this StAXSource.
     * 
     * @return
     */
    public XMLEventReader getXMLEventReader() {
        return eventReader;
    }

    /**
     * Get the XMLStreamReader used by this StAXSource.
     * 
     * XMLStreamReader will be null if this StAXSource was created with a XMLEventReader.
     * 
     * 
     * Returns: XMLStreamReader used by this StAXSource.
     * 
     * @return
     */
    public XMLStreamReader getXMLStreamReader() {
        return streamReader;
    }

    /**
     * In the context of a StAXSource, it is not appropriate to explicitly set the system identifier. The XMLStreamReader or XMLEventReader used to
     * construct this StAXSource determines the system identifier of the XML source.
     * 
     * An UnsupportedOperationException is always thrown by this method.
     * 
     * 
     * Specified by: setSystemId in interface Source Parameters: systemId - Ignored. Throws: UnsupportedOperationException - Is always thrown by this
     * method.
     * 
     */
    public void setSystemId(String systemId) {
    }

    /**
     * Get the system identifier used by this StAXSource.
     * 
     * The XMLStreamReader or XMLEventReader used to construct this StAXSource is queried to determine the system identifier of the XML source.
     * 
     * The system identifier may be null or an empty "" String.
     * 
     * 
     * Specified by: getSystemId in interface Source Returns: System identifier used by this StAXSource.
     * 
     */
    public String getSystemId() {
        return null;
    }
}