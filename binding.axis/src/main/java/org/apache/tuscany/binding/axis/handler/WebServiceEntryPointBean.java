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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.server.ServiceLifecycle;
import javax.xml.rpc.server.ServletEndpointContext;

import org.apache.axis.MessageContext;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SOAPBody;
import org.apache.axis.message.SOAPEnvelope;
import org.osoa.sca.CurrentModuleContext;
import org.osoa.sca.ServiceRuntimeException;
import org.osoa.sca.model.WebServiceBinding;

import org.apache.tuscany.binding.axis.mediator.SOAPMediator;
import org.apache.tuscany.binding.axis.mediator.impl.SOAPDocumentLiteralMediatorImpl;
import org.apache.tuscany.binding.axis.mediator.impl.SOAPEnvelopeImpl;
import org.apache.tuscany.binding.axis.mediator.impl.SOAPRPCEncodedMediatorImpl;
import org.apache.tuscany.binding.axis.mediator.impl.SOAPRPCLiteralMediatorImpl;
import org.apache.tuscany.core.addressing.EndpointReference;
import org.apache.tuscany.core.addressing.impl.AddressingFactoryImpl;
import org.apache.tuscany.core.context.TuscanyModuleComponentContext;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.ServiceContract;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ServiceURI;
import org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl;
import org.apache.tuscany.model.types.InterfaceType;
import org.apache.tuscany.model.types.wsdl.WSDLServiceContract;
import org.apache.tuscany.model.types.wsdl.WSDLOperationType;
import org.apache.tuscany.model.types.wsdl.WSDLTypeHelper;

/**
 *         <p/>
 *         Bean implementation class for web service entry points..
 */
public class WebServiceEntryPointBean implements ServiceLifecycle {
    private TuscanyModuleComponentContext moduleContext;
    private WSDLServiceContract interfaceType;
    private WebServicePortMetaData portMetaData;
    private EndpointReference toEndpointReference;
    private EndpointReference fromEndpointReference;
    private Object proxy;
    private Map<String, Method> methodMap;

    public final static Method SERVICE_METHOD = getServiceMethod();

    private static Method getServiceMethod() {
        try {
            return WebServiceEntryPointBean.class.getMethod("processSOAPEnvelope", new Class[]{SOAPEnvelope.class, SOAPEnvelope.class});
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * Constructor.
     */
    public WebServiceEntryPointBean() {
    }

    /**
     * @see javax.xml.rpc.server.ServiceLifecycle#init(java.lang.Object)
     */
    public void init(Object context) throws ServiceException {
        if (context instanceof ServletEndpointContext) {

            // Get the export name (the target service name) from the message context
            MessageContext messageContext = (MessageContext) (((ServletEndpointContext) context).getMessageContext());

            // Initialize
            initialize(messageContext.getTargetService());
        }
    }

    /**
     * @see javax.xml.rpc.server.ServiceLifecycle#destroy()
     */
    public void destroy() {
    }

    /**
     * Initialize.
     */
    protected void initialize(String entryPointName) {
        try {

            // Get the current module component, module and entry point
            moduleContext = (TuscanyModuleComponentContext) CurrentModuleContext.getContext();

            Module module = moduleContext.getModuleComponent().getModuleImplementation();
            EntryPoint entryPoint = module.getEntryPoint(entryPointName);
            if (entryPoint == null) {
                throw new ServiceRuntimeException("Entry point not found: " + entryPointName);
            }

            // Get the target service
            ConfiguredReference referenceValue = entryPoint.getConfiguredReference();
            ConfiguredService targetServiceEndpoint = referenceValue.getTargetConfiguredServices().get(0);

            // Create a service reference for the target  service
            AssemblyFactory assemblyFactory = new AssemblyFactoryImpl();
            toEndpointReference = new AddressingFactoryImpl().createEndpointReference();
            //FIXME
            //toEndpointReference.setModuleContext(moduleContext);
            ServiceURI serviceAddress = assemblyFactory.createServiceURI(moduleContext.getModuleComponent(), targetServiceEndpoint);
            toEndpointReference.setAddress(serviceAddress.getAddress());

            // Get the entry point interface
            ServiceContract serviceInterface = entryPoint.getServiceContract();
            InterfaceType interfaceEClass = serviceInterface.getInterfaceType();
            if (interfaceEClass != null)
                toEndpointReference.setPortTypeName(serviceInterface.getInterface());

            // Create the target service
            //FIXME
            //toEndpointReference.getService();

            // Create a service reference for the entry point itself
            fromEndpointReference = new AddressingFactoryImpl().createEndpointReference();
            //FIXME
            //fromEndpointReference.setModuleContext(moduleContext);
            ServiceURI fromAddress = assemblyFactory.createServiceURI(moduleContext.getModuleComponent(), entryPoint.getConfiguredReference());
            fromEndpointReference.setAddress(fromAddress.getAddress());

            // Get the WSDL port name
            WebServiceBinding wsBinding = (WebServiceBinding) entryPoint.getBindings().get(0);
            QName wsdlPortName = assemblyFactory.createQName(wsBinding.getWSDLPort());

            WSDLTypeHelper typeHelper = moduleContext.getAssemblyModelContext().getWSDLTypeHelper();

            // Get the WSDL definition for this port
            Definition definition = typeHelper.getWSDLDefinition(wsdlPortName.getNamespaceURI());
            if (definition == null) {
                throw new IllegalArgumentException("Unable to locate WSDL package for target namespace " + wsdlPortName.getNamespaceURI());
            }

            // Create a port info object to hold the WSDL port info
            portMetaData = new WebServicePortMetaData(typeHelper, definition, wsdlPortName, null, true);

            // Get the EInterface representing the WSDL portType
            QName portTypeName = portMetaData.getPortTypeName();
            interfaceType = typeHelper.getWSDLInterfaceType(portTypeName);
            if (interfaceType == null) {
                throw new ServiceRuntimeException("Unable to get metadata for WSDL portType " + portTypeName);
            }

            // Create a proxy
            ProxyFactory proxyFactory = (ProxyFactory) targetServiceEndpoint.getProxyFactory();
            proxy = proxyFactory.createProxy();
            Method[] methods = proxy.getClass().getMethods();
            methodMap = new HashMap<String, Method>();
            for (int i = 0; i < methods.length; i++) {
                methodMap.put(methods[i].getName(), methods[i]);
            }

        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    /**
     * Process the SOAP envelope passed by the Axis runtime
     *
     * @param requestEnvelope
     */
    public void processSOAPEnvelope(SOAPEnvelope requestEnvelope, SOAPEnvelope responseEnvelope) {
        try {

            // Get the SOAP body
            SOAPBody soapBody = (SOAPBody) requestEnvelope.getBody();

            WebServiceOperationMetaData operationMetaData = portMetaData.getOperationMetaData(soapBody);
            if (operationMetaData == null) {
                throw new IllegalArgumentException("Failed to resolve operation: " + soapBody);
            }
            // Get the WSDL operation
            Operation wsdlOperation = operationMetaData.getBindingOperation().getOperation();

            String operationName = wsdlOperation.getName();
            WSDLOperationType operationType = (WSDLOperationType) interfaceType.getOperationType(operationName);

            // Create the appropriate mediator
            boolean rpcEncoded = operationMetaData.getUse().equals("encoded");
            SOAPMediator mediator;
            if (operationMetaData.getStyle().equals("rpc")) {
                if (rpcEncoded) {
                    mediator = new SOAPRPCEncodedMediatorImpl(portMetaData);
                } else {
                    mediator = new SOAPRPCLiteralMediatorImpl(portMetaData);
                }
            } else {
                mediator = new SOAPDocumentLiteralMediatorImpl(portMetaData);
            }

            // Read the SOAP envelope into an SCA message
            Message message = new MessageFactoryImpl().createMessage();
            mediator.readRequest(moduleContext, requestEnvelope, message, operationType);

            // Set the message ID
            message.setMessageID(new AddressingFactoryImpl().createMessageID());

            // Set the action header
            message.setAction(operationName);
            message.setOperationName(operationName);

            // Set the endpoint reference header
            message.setEndpointReference(toEndpointReference);

            // Set the callback endpoint reference
            message.setFrom(fromEndpointReference);

            Object[] args = ExternalWebServiceHandler.getRequest(message, operationType);
            Object result = methodMap.get(operationName).invoke(proxy, args);

            // Send the message
            Message responseMessage = ExternalWebServiceHandler.createResponseMessage(moduleContext, operationType, result);

            // Write the response message into the response SOAP envelope
            SOAPEnvelope tempEnvelope = new SOAPEnvelopeImpl();
            mediator.writeResponse(moduleContext, responseMessage, operationType, tempEnvelope);
            for (Iterator i = tempEnvelope.getHeader().getChildElements(); i.hasNext();) {
                responseEnvelope.getHeader().addChildElement((MessageElement) i.next());
            }
            for (Iterator i = tempEnvelope.getBody().getChildElements(); i.hasNext();) {
                responseEnvelope.getBody().addChildElement((MessageElement) i.next());
            }

        } catch (Throwable e) {
            if (e instanceof ServiceRuntimeException) {
                throw (ServiceRuntimeException) e;
            }

            throw new ServiceRuntimeException(e);
        }
    }

}
