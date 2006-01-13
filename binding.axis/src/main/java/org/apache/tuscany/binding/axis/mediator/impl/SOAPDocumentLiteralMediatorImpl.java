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

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.wsdl.Part;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import org.apache.axis.message.SOAPEnvelope;
import org.eclipse.emf.ecore.sdo.EDataObject;
import org.eclipse.emf.ecore.sdo.util.SDOUtil;
import org.osoa.sca.ModuleContext;

import org.apache.tuscany.binding.axis.handler.WebServiceOperationMetaData;
import org.apache.tuscany.binding.axis.handler.WebServicePortMetaData;
import org.apache.tuscany.binding.axis.mediator.SOAPMediator;
import org.apache.tuscany.core.deprecated.sdo.util.DataFactory;
import org.apache.tuscany.core.deprecated.sdo.util.HelperProvider;
import org.apache.tuscany.core.deprecated.sdo.util.XSDHelper;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.sdo.FaultElement;
import org.apache.tuscany.core.message.sdo.MessageElement;
import org.apache.tuscany.core.message.sdo.MessageElementPackage;
import org.apache.tuscany.model.types.wsdl.WSDLOperationType;

/**
 */
public class SOAPDocumentLiteralMediatorImpl
        extends SOAPBaseMediatorImpl
        implements SOAPMediator {

    /**
     * Constructor
     */
    public SOAPDocumentLiteralMediatorImpl(WebServicePortMetaData portMetaData) {
        super(portMetaData);
    }

    /**
     * @see org.apache.tuscany.binding.axis.mediator.SOAPMediator#writeRequest(ModuleContext, org.apache.tuscany.core.message.Message, org.eclipse.emf.ecore.WSDLOperationType, org.apache.axis.message.SOAPEnvelope)
     */
    public void writeRequest(ModuleContext moduleContext, Message message, WSDLOperationType wsdlOperationType,
                             SOAPEnvelope soapEnvelope)
            throws IOException, SOAPException {

        WebServiceOperationMetaData operationMetaData = portMetaData.getOperationMetaData(wsdlOperationType.getName());

        MessageElement messageElement = (MessageElement) message;

        Sequence headerElements = messageElement.getHeaderElement().getAny();
        Set headerParts = operationMetaData.getInputHeaderParts();

        DataObject body = (DataObject) message.getBody(); // The body is typed by the wsdl:message
        if (body != null) {
            message.setBody(null); // Clean up the sequence
            Sequence bodyElements = messageElement.getBodyElement().getAny();

            Type inputType = wsdlOperationType.getInputType();
            List parts = inputType.getProperties();
            int index = 0;
            for (Iterator i = parts.iterator(); i.hasNext(); index++) {
                Property partProperty = (Property) i.next();
                Object partValue = body.get(partProperty);

                Part part = operationMetaData.getInputPart(index);
                if (headerParts.contains(part)) {
                    headerElements.add(partProperty, partValue);
                } else {
                    bodyElements.add(partProperty, partValue);
                }
            }

            // HACK: to reset the bodySet flag
            messageElement.setBodyElement(messageElement.getBodyElement());
        }

        // Write to the SOAP envelope
        writeSOAPEnvelope(moduleContext, message, soapEnvelope);

        // Restore the original body
        message.setBody(body);
    }

    /**
     * @see org.apache.tuscany.binding.axis.mediator.SOAPMediator#writeResponse(ModuleContext, org.apache.tuscany.core.message.Message, org.eclipse.emf.ecore.WSDLOperationType, org.apache.axis.message.SOAPEnvelope)
     */
    public void writeResponse(ModuleContext moduleContext, Message message, WSDLOperationType wsdlOperationType,
                              SOAPEnvelope soapEnvelope)
            throws IOException, SOAPException {

        WebServiceOperationMetaData operationMetaData = portMetaData.getOperationMetaData(wsdlOperationType.getName());

        MessageElement messageElement = (MessageElement) message;

        Sequence headerElements = messageElement.getHeaderElement().getAny();
        Set headerParts = operationMetaData.getOutputHeaderParts();

        // Adjust the message body to match the given WSDLOperationType
        EDataObject body = (EDataObject) message.getBody();
        if (body != null) {
            message.setBody(null); // Clean up the sequence
            Sequence bodyElements = messageElement.getBodyElement().getAny();

            Type type = wsdlOperationType.getOutputType();
            List parts = type.getProperties();
            int index = 0;
            for (Iterator i = parts.iterator(); i.hasNext(); index++) {
                Property partProperty = (Property) i.next();
                Object partValue = body.get(partProperty);

                Part part = operationMetaData.getInputPart(index);
                if (headerParts.contains(part)) {
                    headerElements.add(partProperty, partValue);
                } else {
                    bodyElements.add(partProperty, partValue);
                }
            }

            // HACK: to reset the bodySet flag
            messageElement.setBodyElement(messageElement.getBodyElement());
        }

        // Write to the SOAP envelope
        writeSOAPEnvelope(moduleContext, message, soapEnvelope);

        // Restore the original body
        message.setBody(body);
    }

    /**
     * @see org.apache.tuscany.binding.axis.mediator.SOAPMediator#readRequest(ModuleContext, org.apache.axis.message.SOAPEnvelope, org.apache.tuscany.core.message.Message, org.eclipse.emf.ecore.WSDLOperationType)
     */
    public void readRequest(ModuleContext moduleContext, SOAPEnvelope soapEnvelope, Message message,
                            WSDLOperationType wsdlOperationType)
            throws IOException, SOAPException {

        WebServiceOperationMetaData operationMetaData = portMetaData.getOperationMetaData(wsdlOperationType.getName());
        Set headerParts = operationMetaData.getInputHeaderParts();
        List bodyPartIndexes = operationMetaData.getBodyPartIndexes(true);

        HelperProvider helperProvider = getHelperProvider(moduleContext);
        XSDHelper xsdHelper = helperProvider.getXSDHelper();

        MessageElement messageElement = (MessageElement) message;

        // Read the SOAP envelope
        Message soapMessage = readSOAPEnvelope(moduleContext, soapEnvelope);
        MessageElement soapMessageElement = (MessageElement) soapMessage;

        Type type = wsdlOperationType.getInputType();

        DataFactory dataFactory = helperProvider.getDataFactory();
        DataObject request = type == null ? null : dataFactory.create(type);

        // Get the header element
        Sequence headers = messageElement.getHeaderElement().getAny();
        Sequence headerElements = soapMessageElement.getHeaderElement().getAny();
        int size = headers.size();
        for (int i = 0; i < size; i++) {
            Property property = (Property) headers.getProperty(i);
            String ns = xsdHelper.getNamespaceURI(property);
            String localName = xsdHelper.getLocalName(property);

            QName elementName = new QName(ns, localName);
            int index = operationMetaData.getHeaderPartIndex(elementName, true);
            if (index == -1) {
                headerElements.add(i, property, headers.getValue(i));
            } else {
                request.set(index, headers.getValue(i));
            }
        }

        // Wrap the SOAP body into the expected wrapper DataObject
        Sequence bodyElements = soapMessageElement.getBodyElement().getAny();

        for (int i = 0; i < bodyElements.size(); i++) {
            Object value = bodyElements.getValue(i);
            int index = ((Integer) bodyPartIndexes.get(i)).intValue();
            request.set(index, value);
        }
        message.setBody(request);

    }

    /**
     * @see org.apache.tuscany.binding.axis.mediator.SOAPMediator#readResponse(ModuleContext, org.apache.axis.message.SOAPEnvelope, org.apache.tuscany.core.message.Message, org.eclipse.emf.ecore.WSDLOperationType)
     */
    public void readResponse(ModuleContext moduleContext, SOAPEnvelope soapEnvelope, Message message,
                             WSDLOperationType wsdlOperationType)
            throws IOException, SOAPException {

        WebServiceOperationMetaData operationMetaData = portMetaData.getOperationMetaData(wsdlOperationType.getName());
        Set headerParts = operationMetaData.getOutputHeaderParts();
        List bodyPartIndexes = operationMetaData.getBodyPartIndexes(false);
        HelperProvider helperProvider = getHelperProvider(moduleContext);
        XSDHelper xsdHelper = helperProvider.getXSDHelper();

        MessageElement messageElement = (MessageElement) message;

        // Read the SOAP envelope
        Message soapMessage = readSOAPEnvelope(moduleContext, soapEnvelope);
        MessageElement soapMessageElement = (MessageElement) soapMessage;

        Type type = wsdlOperationType.getOutputType();

        DataFactory dataFactory = helperProvider.getDataFactory();
        DataObject response = type == null ? null : dataFactory.create(type);

        // Get the header element
        Sequence headers = messageElement.getHeaderElement().getAny();
        Sequence headerElements = soapMessageElement.getHeaderElement().getAny();
        int size = headers.size();
        for (int i = 0; i < size; i++) {
            Property property = (Property) headers.getProperty(i);
            String ns = xsdHelper.getNamespaceURI(property);
            String localName = xsdHelper.getLocalName(property);

            QName elementName = new QName(ns, localName);
            int index = operationMetaData.getHeaderPartIndex(elementName, false);
            if (index == -1) {
                headerElements.add(i, property, headers.getValue(i));
            } else {
                response.set(index, headers.getValue(i));
            }
        }

        // Wrap the SOAP body into the expected wrapper DataObject
        Sequence bodyElements = soapMessageElement.getBodyElement().getAny();

        for (int i = 0; i < bodyElements.size(); i++) {
            Object value = bodyElements.getValue(i);
            int index = ((Integer) bodyPartIndexes.get(i)).intValue();
            response.set(index, value);
        }
        message.setBody(response);

    }

    public FaultElement readFault(MessageElement messageElement, WSDLOperationType wsdlOperationType) {
        Property faultProperty = SDOUtil.adaptProperty(MessageElementPackage.eINSTANCE.getDocumentRoot_FaultElement());

        Sequence bodyElements = messageElement.getBodyElement().getAny();
        int size = bodyElements.size();
        if (size == 1) {
            Property property = bodyElements.getProperty(0);
            if (property == faultProperty) {
                FaultElement fault = (FaultElement) bodyElements.getValue(0);
                // Sequence faultParts = fault.getDetail().getAny();
                return fault;
            }
        }
        return null;
    }
}
