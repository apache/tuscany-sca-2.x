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
package org.apache.tuscany.binding.axis.handler;

import java.util.List;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.ServiceFactory;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import org.apache.axis.client.Call;
import org.apache.axis.message.SOAPEnvelope;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.sdo.EProperty;
import org.eclipse.emf.ecore.sdo.EType;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.osoa.sca.ServiceUnavailableException;
import org.osoa.sca.model.WebServiceBinding;

import org.apache.tuscany.binding.axis.mediator.SOAPMediator;
import org.apache.tuscany.binding.axis.mediator.impl.SOAPDocumentLiteralMediatorImpl;
import org.apache.tuscany.binding.axis.mediator.impl.SOAPEnvelopeImpl;
import org.apache.tuscany.binding.axis.mediator.impl.SOAPRPCEncodedMediatorImpl;
import org.apache.tuscany.binding.axis.mediator.impl.SOAPRPCLiteralMediatorImpl;
import org.apache.tuscany.model.util.ConfiguredResourceSet;
import org.apache.tuscany.core.addressing.EndpointReference;
import org.apache.tuscany.core.context.TuscanyModuleComponentContext;
import org.apache.tuscany.core.deprecated.sdo.util.HelperProvider;
import org.apache.tuscany.core.deprecated.sdo.util.impl.HelperProviderImpl;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.handler.MessageHandler;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl;
import org.apache.tuscany.model.types.wsdl.WSDLOperationType;
import org.apache.tuscany.model.types.wsdl.WSDLTypeHelper;

/**
 *         Operation handler
 */
public class ExternalWebServiceHandler implements MessageHandler {
    private TuscanyModuleComponentContext moduleContext;
    private Service jaxrpcService;
    private WebServicePortMetaData portMetaData;
    private WSDLTypeHelper typeHelper;
    private Call call;
    private WSDLOperationType wsdlOperationType;
    private boolean rpcStyle;
    private boolean rpcEncoded;
    private SOAPMediator mediator;
    private Boolean wrapped;

    private void configureCall() throws ServiceException {

        String soapAction = getSOAPAction();
        // Create a JAX RPC call object for the particular operation
        QName portName = portMetaData.getPortName();
        call = (Call) jaxrpcService.createCall(portName);

        // set operation name
        WebServiceOperationMetaData operationMetaData = portMetaData.getOperationMetaData(wsdlOperationType.getName());

        call.setOperationName(operationMetaData.getRPCOperationName());

        // Set the target endpoint address
        String endpoint = portMetaData.getEndpoint();
        if (endpoint != null) {
            String originalEndpoint = call.getTargetEndpointAddress();
            if (!endpoint.equals(originalEndpoint))
                call.setTargetEndpointAddress(endpoint);
        }

        // Set the SOAP action
        if (soapAction != null) {
            call.setProperty(Call.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
            call.setProperty(Call.SOAPACTION_URI_PROPERTY, soapAction);
        }

        // Set the encoding style
        String bindingStyle = operationMetaData.getStyle();
        rpcStyle = "rpc".equals(bindingStyle);
        String bindingUse = operationMetaData.getUse();
        rpcEncoded = "encoded".equals(bindingUse);
        call.setProperty(Call.OPERATION_STYLE_PROPERTY, (rpcStyle) ? "rpc" : "document");

        // Create the appropriate mediator
        if (rpcStyle) {
            if (rpcEncoded) {
                mediator = new SOAPRPCEncodedMediatorImpl(portMetaData);
            } else {
                mediator = new SOAPRPCLiteralMediatorImpl(portMetaData);
            }
        } else {
            mediator = new SOAPDocumentLiteralMediatorImpl(portMetaData);
        }

    }

    private boolean isWrappedStyle() {
        if (wrapped == null) {
            wrapped = Boolean.valueOf(isWrappedStyle(wsdlOperationType));
        }
        return wrapped.booleanValue();
    }

    public static boolean isWrappedStyle(WSDLOperationType wsdlOperationType) {
        Type inputType = wsdlOperationType.getInputType();
        if (inputType == null)
            return false;
        List properties = inputType.getProperties();
        if (properties.size() != 1)
            return false;
        Property property = (Property) properties.get(0);
        EProperty p = (EProperty) property;
        EType eType = (EType) inputType;

        String elementName = ExtendedMetaData.INSTANCE.getName(p.getEStructuralFeature());
        String operationName = wsdlOperationType.getName();
        if (!operationName.equals(elementName))
            return false;
        List attrs = ExtendedMetaData.INSTANCE.getAttributes((EClass) eType.getEClassifier());
        return attrs.isEmpty();
    }

    /**
     * @see org.apache.tuscany.core.message.handler.MessageHandler#processMessage(org.apache.tuscany.core.message.Message)
     */
    public boolean processMessage(Message message) {
        try {
            // Create a new SOAP envelope
            SOAPEnvelope requestEnvelope = new SOAPEnvelopeImpl();

            Object input=message.getBody();
            Message requestMessage = createRequestMessage(moduleContext, wsdlOperationType, input);

            // Write the SCA message into the SOAP envelope
            mediator.writeRequest(moduleContext, requestMessage, wsdlOperationType, requestEnvelope);

            // Invoke the SOAP operation
            call.setRequestMessage(new org.apache.axis.Message(requestEnvelope));
            call.invoke();

            // Create an SCA message from the response envelope
            SOAPEnvelope responseEnvelope = call.getResponseMessage().getSOAPEnvelope();
            Message responseMessage = new MessageFactoryImpl().createMessage();
            mediator.readResponse(moduleContext, responseEnvelope, responseMessage, wsdlOperationType);

            Object output=getResponse(responseMessage, wsdlOperationType);
            responseMessage.setBody(output);
            
            // Generate a message ID
            responseMessage.setRelatesTo(message.getMessageID());
            responseMessage.setMessageID(EcoreUtil.generateUUID());

            // Send the response message
            EndpointReference replyTo = message.getReplyTo();
            responseMessage.setEndpointReference(replyTo);

            message.getCallbackChannel().send(responseMessage);
            
            return false;

        } catch (Exception e) {
            throw new ServiceUnavailableException(e);
        }
    }

    public static Object getResponse(Message responseMessage, WSDLOperationType wsdlOperationType) {
        Object result = responseMessage.getBody();
        if (result == null)
            return null;

        List properties = wsdlOperationType.getOutputType().getProperties();
        if (isWrappedStyle(wsdlOperationType)) {
            DataObject outputBody = ((DataObject) result).getDataObject(0);
            Property property = (Property) properties.get(0); // the single part
            List argList = property.getType().getProperties();
            Object[] results = new Object[argList.size()];
            for (int i = 0; i < results.length; i++) {
                results[i] = outputBody.get(i);
            }
            if (results.length == 1)
                return results[0];
            else
                return results;

        } else {
            // FIXME: [rfeng] How to deal with multi-parts output
            DataObject outputBody = ((DataObject) result).getDataObject(0);
            return outputBody;
        }
    }

    public static Object[] getRequest(Message requestMessage, WSDLOperationType wsdlOperationType) {
        Object request = requestMessage.getBody();
        if (request == null)
            return null;

        List properties = wsdlOperationType.getInputType().getProperties();
        if (isWrappedStyle(wsdlOperationType)) {
            DataObject inputPart = ((DataObject) request).getDataObject(0); // Get the single part
            Property property = (Property) properties.get(0); // Type of the part
            List argList = property.getType().getProperties();
            Object[] results = new Object[argList.size()];
            for (int i = 0; i < results.length; i++) {
                // Each property of the part is corresponding to an argument
                results[i] = inputPart.get(i);
            }
            return results;
        } else {
            DataObject inputMessage = (DataObject) request;
            Object[] results = new Object[properties.size()];
            for (int i = 0; i < results.length; i++) {
                // Each part is a property of the request message
                results[i] = inputMessage.get(i);
            }
            return results;
        }
    }

    /**
     * @param input
     */
    public static Message createRequestMessage(TuscanyModuleComponentContext context, WSDLOperationType operationType, Object input) {
        Message message = new MessageFactoryImpl().createMessage();
        HelperProvider provider = new HelperProviderImpl((ConfiguredResourceSet) context.getAssemblyModelContext().getAssemblyLoader());
        Type inputType = operationType.getInputType();
        DataObject inputBody = provider.getDataFactory().create(inputType);

        DataObject inputData = null;
        if (isWrappedStyle(operationType)) {
            inputData = inputBody.createDataObject(0);
        } else
            inputData = inputBody;

        Object[] args = (Object[]) input;
        for (int i = 0; i < args.length; i++) {
            inputData.set(i, args[i]);
        }
        message.setBody(inputBody);
        return message;
    }

    /**
     * @param response
     */
    public static Message createResponseMessage(TuscanyModuleComponentContext context, WSDLOperationType operationType, Object response) {
        Message message = new MessageFactoryImpl().createMessage();
        HelperProvider provider = new HelperProviderImpl((ConfiguredResourceSet) context.getAssemblyModelContext().getAssemblyLoader());
        Type outputType = operationType.getOutputType();
        DataObject outputBody = provider.getDataFactory().create(outputType);

        DataObject outputData = null;
        if (isWrappedStyle(operationType)) {
            outputData = outputBody.createDataObject(0);
        } else {
            outputData = outputBody;
        }

        // FIXME: Assume only one part output
        outputData.set(0, response);

        message.setBody(outputBody);
        return message;
    }

    /**
     * Constructor
     *
     * @param moduleContext
     * @param externalService
     * @param wsBinding
     * @param endpointReference
     */
    public ExternalWebServiceHandler(TuscanyModuleComponentContext moduleContext, WSDLOperationType operationType, ExternalService externalService, WebServiceBinding wsBinding,
                             EndpointReference endpointReference) {

        this.moduleContext = moduleContext;
        this.typeHelper = moduleContext.getAssemblyModelContext().getWSDLTypeHelper();
        this.wsdlOperationType = operationType;

        // Get the WSDL port name
        String portName = wsBinding.getPort();
        if (portName != null) {

            // A port name is specified, create a port handler for this port and cache it in the binding model
            AssemblyFactory assemblyFactory = new AssemblyFactoryImpl();
            QName wsdlPortName = assemblyFactory.createQName(portName);
            Definition wsdlDefinition = typeHelper.getWSDLDefinition(wsdlPortName.getNamespaceURI());
            if (wsdlDefinition == null) {
                throw new IllegalArgumentException("WSDL definition cannot be found for " + wsdlPortName);
            }

            // In managed mode, work with a Web service reference
            // FIXME: [rfeng] Need the API to test isManaged
            boolean managed = false;

            if (managed) {
                // Lookup a Web service reference
                String jndiName = "java:comp/env/sca/externalService/" + externalService.getName();

                // TODO Port this to Axis+Tomcat
                jaxrpcService = null;

                if (jaxrpcService != null) {

                    // Get the actual endpoint from the JSR 109 service reference, unless it is overriden
                    // by the wsBinding
                    String endpoint;
                    if (wsBinding.getUri() == null) {
                        Call axisCall;
                        try {
                            axisCall = (Call) jaxrpcService.createCall(wsdlPortName);
                        } catch (ServiceException e) {
                            throw new ServiceUnavailableException(e);
                        }
                        endpoint = axisCall.getTargetEndpointAddress();
                    } else
                        endpoint = wsBinding.getUri();

                    // Create a port info object to hold the port information
                    portMetaData = new WebServicePortMetaData(typeHelper, wsdlDefinition, wsdlPortName, endpoint, managed);

                } else {

                    // Can't create a managed port resource, try unmanaged
                    managed = false;
                }
            }

            // Unmanaged mode
            if (!managed) {

                // Create a port info object to hold the port information
                portMetaData = new WebServicePortMetaData(typeHelper, wsdlDefinition, wsdlPortName, wsBinding.getUri(), managed);

                // Create a JAX-RPC service
                QName wsdlServiceName = portMetaData.getService().getQName();
                try {
                    jaxrpcService = ServiceFactory.newInstance().createService(wsdlServiceName);
                    configureCall();
                } catch (ServiceException e) {
                    throw new ServiceUnavailableException(e);
                }
            }

        } else {

            // No port is specified in the wsBinding, we are expecting the endpoint reference to specify the
            // service name, port name and endpoint

            // Get the port type name, service name, port name, and endpoint address from the
            // endpoint reference
            AssemblyFactory assemblyFactory = new AssemblyFactoryImpl();
            QName wsdlServiceName = assemblyFactory.createQName(endpointReference.getServiceName());
            String wsdlPortName = endpointReference.getPortName();
            QName wsdlPortTypeName = assemblyFactory.createQName(endpointReference.getPortTypeName());
            String endpoint = endpointReference.getAddress();

            // If a service name and/or port name are specified then look up the port
            if (wsdlServiceName != null && wsdlPortName != null) {
                String nsUri = wsdlServiceName.getNamespaceURI();
                Definition wsdlDefinition = typeHelper.getWSDLDefinition(nsUri);

                // Create a port info object to hold the port information
                portMetaData = new WebServicePortMetaData(typeHelper, wsdlDefinition, new QName(wsdlServiceName.getNamespaceURI(), wsdlPortName), endpoint, false);

            } else {

                // Create default service and port names
                if (wsdlServiceName == null)
                    wsdlServiceName = new QName("http://tempuri.org", "TempService");
                if (wsdlPortName == null)
                    wsdlPortName = "TempPort";

                // Create a port info object to hold the port information
                portMetaData = new WebServicePortMetaData(wsdlServiceName, wsdlPortName, wsdlPortTypeName, endpoint);
            }

            // Create a new JAXRPC service
            try {
                jaxrpcService = ServiceFactory.newInstance().createService(wsdlServiceName);
                configureCall();
            } catch (ServiceException e) {
                throw new ServiceUnavailableException(e);
            }
        }

    }

    private String getSOAPAction() {
        Port wsdlPort = portMetaData.getPort();
        if (wsdlPort != null) {
            // Create the method handler
            WebServiceOperationMetaData operationMetaData = portMetaData.getOperationMetaData(wsdlOperationType.getName());
            String soapAction = operationMetaData.getSOAPAction();
            return (soapAction);
        } else {

            // No WSDL binding, assume a default SOAP document literal binding from the operation type
            // Derive the SOAP action from the input type
            String namespace;
            String name;
            if (wsdlOperationType.getInputType() != null) {
                Type type = wsdlOperationType.getInputType();
                namespace = type.getURI();
                name = type.getName();

                // TODO: [rfeng]
                // XSD2Ecore will create an extended metadata for anonymous complex type with key name
                // and value <elementName>_._type or <elementName>_._<index>_._type" if there's
                // a duplicate

                if (name.indexOf("_._") != -1)
                    name = name.substring(0, name.indexOf("_._"));

            } else {

                // Derive the SOAP action from the operation name
                // FIXME:
                namespace = "";
                name = wsdlOperationType.getName();
            }
            String soapAction = namespace + '/' + name;

            // Create the method handler
            return (soapAction);
        }
	}

}