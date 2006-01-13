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
package org.apache.tuscany.binding.axis.mediator;

import java.io.IOException;
import javax.xml.soap.SOAPException;

import org.apache.axis.message.SOAPEnvelope;
import org.osoa.sca.ModuleContext;

import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.model.types.wsdl.WSDLOperationType;

/**
 */
public interface SOAPMediator {

    /**
     * Write a request message to a SOAP envelope
     *
     * @param moduleContext
     * @param message
     * @param soapEnvelope
     * @throws IOException
     * @throws SOAPException
     */
    public void writeRequest(ModuleContext moduleContext, Message message, WSDLOperationType operationType, SOAPEnvelope soapEnvelope) throws IOException, SOAPException;

    /**
     * Write a response message to a SOAP envelope
     *
     * @param moduleContext
     * @param message
     * @param soapEnvelope
     * @throws IOException
     * @throws SOAPException
     */
    public void writeResponse(ModuleContext moduleContext, Message message, WSDLOperationType operationType, SOAPEnvelope soapEnvelope) throws IOException, SOAPException;

    /**
     * Read a request message from a SOAP envelope
     *
     * @param moduleContext
     * @param soapEnvelope
     * @param message
     * @param bodyEClass
     * @throws IOException
     * @throws SOAPException
     */
    public void readRequest(ModuleContext moduleContext, SOAPEnvelope soapEnvelope, Message message, WSDLOperationType operationType) throws IOException, SOAPException;

    /**
     * Read a response message from a SOAP envelope
     * @param moduleContext
     * @param soapEnvelope
     * @param message
     * @param bodyEClass
     * @throws IOException
     * @throws SOAPException
     */
    public void readResponse(ModuleContext moduleContext, SOAPEnvelope soapEnvelope, Message message, WSDLOperationType operationType) throws IOException, SOAPException;
	
}
