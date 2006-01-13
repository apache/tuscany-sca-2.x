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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.wsdl.Input;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import org.apache.axis.message.SOAPEnvelope;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.sdo.EProperty;
import org.eclipse.emf.ecore.sdo.EType;
import org.eclipse.emf.ecore.sdo.util.SDOUtil;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.xml.type.AnyType;
import org.eclipse.emf.ecore.xml.type.XMLTypeFactory;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.osoa.sca.ModuleContext;

import org.apache.tuscany.binding.axis.handler.WebServiceOperationMetaData;
import org.apache.tuscany.binding.axis.handler.WebServicePortMetaData;
import org.apache.tuscany.binding.axis.mediator.SOAPMediator;
import org.apache.tuscany.core.deprecated.sdo.util.DataFactory;
import org.apache.tuscany.core.deprecated.sdo.util.HelperProvider;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.sdo.MessageElement;
import org.apache.tuscany.model.types.wsdl.WSDLOperationType;

/**
 */
public class SOAPRPCLiteralMediatorImpl
        extends SOAPDocumentLiteralMediatorImpl
        implements SOAPMediator {

    public void readRequest(ModuleContext moduleContext, SOAPEnvelope soapEnvelope, Message message,
                            WSDLOperationType operationType)
            throws IOException, SOAPException {
        MessageElement messageElement = (MessageElement) message;

        // Read the SOAP envelope
        Message soapMessage = readSOAPEnvelope(moduleContext, soapEnvelope);
        MessageElement soapMessageElement = (MessageElement) soapMessage;

        // Get the header element
        Sequence headers = messageElement.getHeaderElement().getAny();
        Sequence soapHeaders = soapMessageElement.getHeaderElement().getAny();
        int size = headers.size();
        for (int i = 0; i < size; i++) {
            soapHeaders.add(i, headers.getProperty(i), headers.getValue(i));
        }

        // Wrap the SOAP body into the expected wrapper DataObject
        Sequence sequence = soapMessageElement.getBodyElement().getAny();
        Type type = operationType.getInputType();

        HelperProvider helperProvider = getHelperProvider(moduleContext);
        DataFactory dataFactory = helperProvider.getDataFactory();
        DataObject request = type == null ? null : dataFactory.create(type);

        FeatureMap featureMap = ((AnyType) sequence.getValue(0)).getAny(); // The first element is the rpc operation wrapper
        List values = new ArrayList();
        for (int i = 0; i < featureMap.size(); i++) {
            Object value = featureMap.getValue(i);
            values.add(value);
        }
        for (int i = 0; i < values.size(); i++) {
            Object value = values.get(i);
            value = getValue(type, i, value);
            request.set(i, value);
        }

        message.setBody(request);
    }

    private Object getValue(Type type, int propertyIndex, Object value) {
        if (value instanceof AnyType) {
            Property part = (Property) type.getProperties().get(propertyIndex);
            EClassifier partType = ((EType) part.getType()).getEClassifier();
            if (partType instanceof EDataType) {
                AnyType anyType = (AnyType) value;
                String str = (String) anyType.getMixed().getValue(0);
                value = EcoreUtil.createFromString((EDataType) partType, str);
            }
        }
        return value;
    }

    public void readResponse(ModuleContext moduleContext, SOAPEnvelope soapEnvelope, Message message,
                             WSDLOperationType operationType)
            throws IOException, SOAPException {
        MessageElement messageElement = (MessageElement) message;

        // Read the SOAP envelope
        Message soapMessage = readSOAPEnvelope(moduleContext, soapEnvelope);
        MessageElement soapMessageElement = (MessageElement) soapMessage;
        messageElement.setHeaderElement(soapMessageElement.getHeaderElement());

        // Get the header element
        Sequence headers = messageElement.getHeaderElement().getAny();
        Sequence soapHeaders = soapMessageElement.getHeaderElement().getAny();
        int size = headers.size();
        for (int i = 0; i < size; i++) {
            soapHeaders.add(i, headers.getProperty(i), headers.getValue(i));
        }

        // Wrap the SOAP body into the expected wrapper DataObject
        Sequence sequence = soapMessageElement.getBodyElement().getAny();
        Type type = operationType.getOutputType();

        HelperProvider helperProvider = getHelperProvider(moduleContext);
        DataFactory dataFactory = helperProvider.getDataFactory();
        DataObject response = type == null ? null : dataFactory.create(type);

        FeatureMap featureMap = ((AnyType) sequence.getValue(0)).getAny(); // The first element is the rpc operation wrapper
        List values = new ArrayList();
        for (int i = 0; i < featureMap.size(); i++) {
            Object value = featureMap.getValue(i);
            values.add(value);
        }
        for (int i = 0; i < values.size(); i++) {
            Object value = values.get(i);
            value = getValue(type, i, value);
            response.set(i, value);
        }
        message.setBody(response);

    }

    public void writeRequest(ModuleContext moduleContext, Message message, WSDLOperationType operationType,
                             SOAPEnvelope soapEnvelope)
            throws IOException, SOAPException {
        Object body = message.getBody();

        message.setBody(null);
        MessageElement messageElement = (MessageElement) message;
        Sequence sequence = messageElement.getBodyElement().getAny();

        // Create an element to represent the RPC operation
        WebServiceOperationMetaData operationMetaData = portMetaData.getOperationMetaData(operationType.getName());
        QName opName = operationMetaData.getRPCOperationName();
        ExtendedMetaData extendedMetaData = new BasicExtendedMetaData();
        EStructuralFeature feature = extendedMetaData.demandFeature(opName.getNamespaceURI(), opName.getLocalPart(),
                true);
        feature.setDerived(false);
        feature.setEType(XMLTypePackage.eINSTANCE.getAnyType());

        // Add "xsd:any"
        AnyType anyType = XMLTypeFactory.eINSTANCE.createAnyType();
        Property rpcOperation = SDOUtil.adaptProperty(feature);
        sequence.add(rpcOperation, anyType);

        if (body != null) {
            Operation operation = operationType.getWSDLOperation();
            Input input = operation.getInput();
            List parts = input.getMessage().getOrderedParts(null);
            Type type = operationType.getInputType();
            List properties = type.getProperties();
            int index = 0;
            for (Iterator i = parts.iterator(); i.hasNext(); index++) {
                Part part = (Part) i.next();
                Property property = (Property) properties.get(index);
                EStructuralFeature partFeature = ((EProperty) property).getEStructuralFeature();
                /**
                 * Basic Profile 1.1
                 * 4.7.20 Part Accessors
                 * For rpc-literal envelopes, WSDL 1.1 is not clear what namespace, if any, the accessor elements 
                 * for parameters and return value are a part of. Different implementations make different choices, 
                 * leading to interoperability problems. 
                 * R2735 An ENVELOPE described with an rpc-literal binding MUST place the part accessor elements for 
                 * parameters and return value in no namespace. 
                 * R2755 The part accessor elements in a MESSAGE described with an rpc-literal binding MUST have a 
                 * local name of the same value as the name attribute of the corresponding wsdl:part element.
                 * Settling on one alternative is crucial to achieving interoperability. The Profile places the part 
                 * accessor elements in no namespace as doing so is simple, covers all cases, and does not lead to 
                 * logical inconsistency. 
                 */
                partFeature = extendedMetaData.demandFeature(null, part.getName(), true,
                        partFeature instanceof EReference);
                Object value = ((DataObject) body).get(index);
                anyType.getAny().add(index, partFeature, value);
            }
        }
        writeSOAPEnvelope(moduleContext, message, soapEnvelope);

        // Restore the original body
        message.setBody(body);

    }

    public void writeResponse(ModuleContext moduleContext, Message message, WSDLOperationType operationType,
                              SOAPEnvelope soapEnvelope)
            throws IOException, SOAPException {
        Object body = message.getBody();

        message.setBody(null);
        MessageElement messageElement = (MessageElement) message;
        Sequence sequence = messageElement.getBodyElement().getAny();

        // Create an element to represent the RPC operation
        WebServiceOperationMetaData operationMetaData = portMetaData.getOperationMetaData(operationType.getName());
        QName opName = operationMetaData.getRPCOperationName();
        ExtendedMetaData extendedMetaData = new BasicExtendedMetaData();
        /**
         * Basic Profile 1.1
         * 4.7.19 Response Wrappers
         * WSDL 1.1 Section 3.5 could be interpreted to mean the RPC response wrapper element must be named 
         * identical to the name of the wsdl:operation.
         * R2729 An ENVELOPE described with an rpc-literal binding that is a response MUST have a wrapper element 
         * whose name is the corresponding wsdl:operation name suffixed with the string "Response". 
         */
        EStructuralFeature feature = extendedMetaData.demandFeature(opName.getNamespaceURI(), opName.getLocalPart()
                + "Response", true);
        feature.setDerived(false);
        feature.setEType(XMLTypePackage.eINSTANCE.getAnyType());

        // Add "xsd:any"
        AnyType anyType = XMLTypeFactory.eINSTANCE.createAnyType();
        Property rpcOperation = SDOUtil.adaptProperty(feature);
        sequence.add(rpcOperation, anyType);

        if (body != null) {
            Operation operation = operationType.getWSDLOperation();
            Output output = operation.getOutput();
            List parts = output.getMessage().getOrderedParts(null);
            Type type = operationType.getOutputType();

            List properties = type.getProperties();

            int index = 0;
            for (Iterator i = parts.iterator(); i.hasNext(); index++) {
                Part part = (Part) i.next();
                Property property = (Property) properties.get(index);
                EStructuralFeature partFeature = ((EProperty) property).getEStructuralFeature();
                partFeature = extendedMetaData.demandFeature(null, part.getName(), true,
                        partFeature instanceof EReference);
                Object value = ((DataObject) body).get(index);
                anyType.getAny().add(index, partFeature, value);
            }
        }
        writeSOAPEnvelope(moduleContext, message, soapEnvelope);

        // Restore the original body
        message.setBody(body);

    }

    public SOAPRPCLiteralMediatorImpl(WebServicePortMetaData portMetaData) {
        super(portMetaData);
    }
}
