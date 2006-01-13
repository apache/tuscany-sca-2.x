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

import org.apache.axis.message.SOAPBody;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeader;
import org.apache.axis.soap.SOAPConstants;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public class SOAPEnvelopeImpl extends SOAPEnvelope {

    /**
     * Constructor
     *
     * @param soapConstants
     */
    public SOAPEnvelopeImpl() {
        super(SOAPConstants.SOAP12_CONSTANTS);
    }

    /**
     * @see org.apache.axis.message.NodeImpl#appendChild(org.w3c.dom.Node)
     */
    public Node appendChild(Node newChild) throws DOMException {
        newChild = super.appendChild(newChild);
        if (newChild instanceof SOAPHeader) {
            setHeader((SOAPHeader) newChild);
        } else if (newChild instanceof SOAPBody) {
            setBody((SOAPBody) newChild);
        }
        return newChild;
    }
}