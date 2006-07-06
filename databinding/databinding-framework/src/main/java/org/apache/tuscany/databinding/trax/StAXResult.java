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

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;

/**
 * javax.xml.transform.StAXResult will come into JSE 6.0
 * 
 * Acts as a holder for an XML Result in the form of a StAX writer,i.e. XMLStreamWriter or XMLEventWriter. StAXResult can be used in all cases that
 * accept a Result, e.g. Transformer, Validator which accept Result as input.
 * 
 */
public class StAXResult {
    private XMLStreamWriter streamWriter;

    private XMLEventWriter eventWriter;

    /**
     * Creates a new instance of a StAXResult by supplying an XMLEventWriter.
     * 
     * XMLEventWriter must be a non-null reference.
     * 
     * 
     * Parameters: xmlEventWriter - XMLEventWriter used to create this StAXResult. Throws: IllegalArgumentException - If xmlEventWriter == null.
     * 
     * @param xmlEventWriter
     */
    public StAXResult(XMLEventWriter xmlEventWriter) {
        this.eventWriter = xmlEventWriter;
    }

    /**
     * Creates a new instance of a StAXResult by supplying an XMLStreamWriter.
     * 
     * XMLStreamWriter must be a non-null reference.
     * 
     * 
     * Parameters: xmlStreamWriter - XMLStreamWriter used to create this StAXResult. Throws: IllegalArgumentException - If xmlStreamWriter == null.
     * 
     * @param xmlStreamWriter
     */
    public StAXResult(XMLStreamWriter xmlStreamWriter) {
        this.streamWriter = xmlStreamWriter;
    }

    /**
     * Get the XMLEventWriter used by this StAXResult.
     * 
     * XMLEventWriter will be null if this StAXResult was created with a XMLStreamWriter.
     * 
     * 
     * Returns: XMLEventWriter used by this StAXResult.
     * 
     * @return
     */
    public XMLEventWriter getXMLEventWriter() {
        return eventWriter;
    }

    /**
     * Get the XMLStreamWriter used by this StAXResult.
     * 
     * XMLStreamWriter will be null if this StAXResult was created with a XMLEventWriter.
     * 
     * 
     * Returns: XMLStreamWriter used by this StAXResult.
     * 
     * @return
     */
    public XMLStreamWriter getXMLStreamWriter() {
        return streamWriter;
    }

    /**
     * In the context of a StAXResult, it is not appropriate to explicitly set the system identifier. The XMLEventWriter or XMLStreamWriter used to
     * construct this StAXResult determines the system identifier of the XML result.
     * 
     * An UnsupportedOperationException is always thrown by this method.
     * 
     * 
     * Specified by: setSystemId in interface Result
     * 
     * @param systemId
     * @throws UnsupportedOperationException -
     *             Is always thrown by this method.
     * 
     */
    public void setSystemId(String systemId) {
        throw new UnsupportedOperationException();
    }

    /**
     * The returned system identifier is always null.
     * 
     * Specified by: getSystemId in interface Result
     * 
     * @return The returned system identifier is always null.
     * 
     */
    public String getSystemId() {
        return null;
    }

}
