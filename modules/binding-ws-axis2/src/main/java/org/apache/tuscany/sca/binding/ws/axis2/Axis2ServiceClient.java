/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.sca.binding.ws.axis2;

import java.util.List;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.tuscany.sca.assembly.AbstractContract;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPoint;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

public class Axis2ServiceClient {

    private WebServiceBinding wsBinding;
    private ServiceClient serviceClient;
    private static final QName SOAP12_INTENT = new QName("http://www.osoa.org/xmlns/sca/1.0", "soap12");

    public Axis2ServiceClient(RuntimeComponent component,
                              AbstractContract contract,
                              WebServiceBinding wsBinding,
                              ServletHost servletHost,
                              MessageFactory messageFactory) {

        this.wsBinding = wsBinding;
        this.serviceClient = createServiceClient();
    }

    protected void start() {
    }

    /**
     * Create an Axis2 ServiceClient
     */
    protected ServiceClient createServiceClient() {
        try {
            TuscanyAxisConfigurator tuscanyAxisConfigurator = new TuscanyAxisConfigurator();
            ConfigurationContext configContext = tuscanyAxisConfigurator.getConfigurationContext();

            Definition wsdlDefinition = wsBinding.getWSDLDefinition().getDefinition();
            setServiceAndPort(wsBinding);
            QName serviceQName = wsBinding.getServiceName();
            String portName = wsBinding.getPortName();
            AxisService axisService =
                AxisService.createClientSideAxisService(wsdlDefinition, serviceQName, portName, new Options());

            return new ServiceClient(configContext, axisService);
        } catch (AxisFault e) {
            throw new RuntimeException(e); // TODO: better exception
        }
    }

    /**
     * Ensure the WSDL definition contains a suitable service and port
     */
    protected static void setServiceAndPort(WebServiceBinding wsBinding) {
        Definition wsdlDefinition = wsBinding.getWSDLDefinition().getDefinition();
        QName serviceQName = wsBinding.getServiceName();
        String portName = wsBinding.getPortName();

        // If no service is specified in the binding element, allow for WSDL that
        // only contains a portType and not a service and port.  Synthesize a
        // service and port using WSDL4J and add them to the wsdlDefinition to
        // keep Axis happy.
        //FIXME: it would be better to do this for all WSDLs to explictly control the
        // service and port that Axis will use, rather than just hoping the user has
        // placed a suitable service and/or port first in the WSDL.
        if (serviceQName == null && wsBinding.getBinding() != null) {
            QName bindingQName = wsBinding.getBindingName();
            Port port = wsdlDefinition.createPort();
            portName = "$port$." + bindingQName.getLocalPart();
            port.setName(portName);
            wsBinding.setPortName(portName);
            port.setBinding(wsBinding.getBinding());
            Service service = wsdlDefinition.createService();
            serviceQName = new QName(bindingQName.getNamespaceURI(),
                                     "$service$." + bindingQName.getLocalPart());
            service.setQName(serviceQName);
            wsBinding.setServiceName(serviceQName);
            service.addPort(port);
            wsdlDefinition.addService(service);
        }
    }

    protected void stop() {
        // close all connections that we have initiated, so that the jetty server
        // can be restarted without seeing ConnectExceptions
        HttpClient httpClient =
            (HttpClient)serviceClient.getServiceContext().getConfigurationContext()
                .getProperty(HTTPConstants.CACHED_HTTP_CLIENT);
        if (httpClient != null)
            ((MultiThreadedHttpConnectionManager)httpClient.getHttpConnectionManager()).shutdown();
    }

    /**
     * Create and configure an Axis2BindingInvoker for each operation
     */
    protected Invoker createInvoker(Operation operation) {
        Options options = new Options();
        EndpointReference epTo = getPortLocationEPR(wsBinding);
        if (epTo != null) {
            options.setTo(epTo);
        }
        options.setProperty(HTTPConstants.CHUNKED, Boolean.FALSE);

        String operationName = operation.getName();

        String soapAction = getSOAPAction(operationName);
        if (soapAction != null && soapAction.length() > 1) {
            options.setAction(soapAction);
        }

        options.setTimeOutInMilliSeconds(30 * 1000); // 30 seconds

        SOAPFactory soapFactory = requiresSOAP12() ? OMAbstractFactory.getSOAP12Factory() : OMAbstractFactory.getSOAP11Factory();
        QName wsdlOperationQName = new QName(operationName);

        Axis2BindingInvoker invoker;
        if (operation.isNonBlocking()) {
            invoker = new Axis2OneWayBindingInvoker(serviceClient, wsdlOperationQName, options, soapFactory);
        } else {
            invoker = new Axis2BindingInvoker(serviceClient, wsdlOperationQName, options, soapFactory);
        }
        return invoker;
    }

    private boolean requiresSOAP12() {
        if (wsBinding instanceof IntentAttachPoint) {
            List<Intent> intents = ((IntentAttachPoint)wsBinding).getRequiredIntents();
            for (Intent intent : intents) {
                if (SOAP12_INTENT.equals(intent.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected EndpointReference getPortLocationEPR(WebServiceBinding binding) {
        String ep = binding.getURI();
        if (ep == null && binding.getPort() != null) {
            List<?> wsdlPortExtensions = binding.getPort().getExtensibilityElements();
            for (final Object extension : wsdlPortExtensions) {
                if (extension instanceof SOAPAddress) {
                    ep = ((SOAPAddress)extension).getLocationURI();
                    break;
                }
            }
        }
        return ep != null ? new EndpointReference(ep) : null;
    }

    protected String getSOAPAction(String operationName) {
        Binding binding = wsBinding.getBinding();
        if (binding != null) {
            for (Object o : binding.getBindingOperations()) {
                BindingOperation bop = (BindingOperation)o;
                if (bop.getName().equalsIgnoreCase(operationName)) {
                    for (Object o2 : bop.getExtensibilityElements()) {
                        if (o2 instanceof SOAPOperation) {
                            return ((SOAPOperation)o2).getSoapActionURI();
                        }
                    }
                }
            }
        }
        return null;
    }

}
