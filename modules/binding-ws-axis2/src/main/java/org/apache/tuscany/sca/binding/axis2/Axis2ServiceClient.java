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
package org.apache.tuscany.sca.binding.axis2;

import java.util.List;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
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
import org.apache.tuscany.sca.core.invocation.ThreadMessageContext;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

public class Axis2ServiceClient {

    private RuntimeComponent component;
    private AbstractContract contract; // not used currently
    private WebServiceBinding wsBinding;
    private ServletHost servletHost;
    private MessageFactory messageFactory;
    private ServiceClient serviceClient;
    private WebServiceBinding callbackBinding;

    public Axis2ServiceClient(RuntimeComponent component,
                              AbstractContract contract,
                              WebServiceBinding wsBinding,
                              ServletHost servletHost,
                              MessageFactory messageFactory,
                              WebServiceBinding callbackBinding) {
        this.component = component;
        this.contract = contract;
        this.wsBinding = wsBinding;
        this.servletHost = servletHost;
        this.messageFactory = messageFactory;
        this.callbackBinding = callbackBinding;

        serviceClient = createServiceClient();
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
            QName serviceQName = wsBinding.getServiceName();
            String portName = wsBinding.getPortName();
            Definition wsdlDefinition = wsBinding.getWSDLDefinition().getDefinition();
            AxisService axisService =
                AxisService.createClientSideAxisService(wsdlDefinition, serviceQName, portName, new Options());

            return new ServiceClient(configContext, axisService);
        } catch (AxisFault e) {
            throw new RuntimeException(e); // TODO: better exception
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
        EndpointReference epTo = getPortLocationEPR(wsBinding);
        if (epTo == null) {
            org.apache.tuscany.sca.runtime.EndpointReference epr = ThreadMessageContext.getMessageContext().getTo();
            if (epr != null) {
                epTo = new EndpointReference(epr.getURI());
            } else {
                throw new RuntimeException("Unable to determine destination endpoint");
            }
        }

        Options options = new Options();
        options.setTo(epTo);
        if (callbackBinding != null) {
            options.setFrom(getPortLocationEPR(callbackBinding));
        }
        options.setProperty(HTTPConstants.CHUNKED, Boolean.FALSE);

        String operationName = operation.getName();

        String soapAction = getSOAPAction(operationName);
        if (soapAction != null && soapAction.length() > 1) {
            options.setAction(soapAction);
        }

        options.setTimeOutInMilliSeconds(30 * 1000); // 30 seconds

        SOAPFactory soapFactory = OMAbstractFactory.getSOAP11Factory();
        QName wsdlOperationQName = new QName(operationName);

        Axis2BindingInvoker invoker;
        if (operation.isNonBlocking()) {
            invoker = new Axis2OneWayBindingInvoker(serviceClient, wsdlOperationQName, options, soapFactory);
        } else {
            invoker = new Axis2BindingInvoker(serviceClient, wsdlOperationQName, options, soapFactory);
        }
        return invoker;
    }

    protected EndpointReference getPortLocationEPR(WebServiceBinding binding) {
        String ep = binding.getURI();
        if (ep == null && binding.getPort() != null) {
            List wsdlPortExtensions = binding.getPort().getExtensibilityElements();
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
