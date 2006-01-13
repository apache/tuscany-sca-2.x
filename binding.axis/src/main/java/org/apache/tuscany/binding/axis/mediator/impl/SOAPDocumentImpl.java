/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.binding.axis.mediator.impl;

import javax.xml.soap.SOAPException;

import org.apache.axis.Constants;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.soap.SOAPConstants;
import org.eclipse.emf.common.util.WrappedException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 */
public class SOAPDocumentImpl extends org.apache.axis.message.SOAPDocumentImpl {

    private SOAPEnvelope soapEnvelope;
    private Element documentElement;

    /**
     * Constructor
     */
    public SOAPDocumentImpl(SOAPEnvelope soapEnvelope) {
        super(null);
        this.soapEnvelope = soapEnvelope;
        this.documentElement = soapEnvelope;
    }

    /**
     * @see org.apache.axis.message.SOAPDocumentImpl#createElementNS(java.lang.String, java.lang.String)
     */
    public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
        try {
            SOAPConstants soapConstants = null;
            if (Constants.URI_SOAP11_ENV.equals(namespaceURI)) {
                soapConstants = SOAPConstants.SOAP11_CONSTANTS;
            } else if (Constants.URI_SOAP12_ENV.equals(namespaceURI)) {
                soapConstants = SOAPConstants.SOAP12_CONSTANTS;
            }

            if (soapConstants != null) {
                // Special SOAP elements
                String localName = qualifiedName.substring(qualifiedName.indexOf(':') + 1);
                if (localName.equals(Constants.ELEM_ENVELOPE)) {
                    return soapEnvelope;
                } else if (localName.equals(Constants.ELEM_HEADER)) {
                    return soapEnvelope.getHeader();
                } else if (localName.equals(Constants.ELEM_BODY)) {
                    return soapEnvelope.getBody();
                }
            }

            // General elements
            return new MessageElement(namespaceURI, qualifiedName);

        } catch (SOAPException e) {
            throw new WrappedException(e);
        }
    }

    /**
     * @see org.apache.axis.message.SOAPDocumentImpl#appendChild(org.w3c.dom.Node)
     */
    public Node appendChild(Node newChild) throws DOMException {
        if (newChild.getParentNode() == null) {
            MessageElement parent = new MessageElement();
            parent.appendChild(newChild);
        }
        documentElement = (Element) newChild;
        return newChild;
    }

    /**
     * @see org.apache.axis.message.SOAPDocumentImpl#getDocumentElement()
     */
    public Element getDocumentElement() {
        return documentElement;
    }

}
