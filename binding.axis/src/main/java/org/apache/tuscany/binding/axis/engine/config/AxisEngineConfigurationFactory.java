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
package org.apache.tuscany.binding.axis.engine.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.ServiceFactory;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.EngineConfigurationFactory;
import org.apache.axis.MessageContext;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.description.JavaServiceDesc;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.tuscany.binding.axis.assembly.WebServiceBinding;
import org.apache.tuscany.binding.axis.handler.WebServiceEntryPointBean;
import org.apache.tuscany.binding.axis.handler.WebServicePortMetaData;
import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.webapp.TuscanyWebAppRuntime;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Module;
import org.osoa.sca.ServiceRuntimeException;
import org.osoa.sca.ServiceUnavailableException;

/**
 */
public class AxisEngineConfigurationFactory implements EngineConfigurationFactory {

    private TuscanyWebAppRuntime tuscanyRuntime;

    /**
     * Constructor
     *
     */
    public AxisEngineConfigurationFactory(TuscanyWebAppRuntime tuscanyRuntime) {
        this.tuscanyRuntime = tuscanyRuntime;
    }

    /**
     * Creates a new factory.
     *
     * @see org.apache.axis.configuration.EngineConfigurationFactoryFinder
     */
    public static EngineConfigurationFactory newFactory(Object param) {
        if (param instanceof ServletConfig) {

            // Only configure Tuscany apps
            // Get the Tuscany runtime out of the servlet context
            TuscanyWebAppRuntime tuscanyRuntime = (TuscanyWebAppRuntime) ((ServletConfig) param).getServletContext().getAttribute(TuscanyWebAppRuntime.class.getName());
            if (tuscanyRuntime == null)
                return null;

            return new AxisEngineConfigurationFactory(tuscanyRuntime);
        } else {
            return null;
        }
    }

    /**
     * @see org.apache.axis.EngineConfigurationFactory#getClientEngineConfig()
     */
    public EngineConfiguration getClientEngineConfig() {

        // Used only on the server side
        return null;
    }

    /**
     * @see org.apache.axis.EngineConfigurationFactory#getServerEngineConfig()
     */
    public EngineConfiguration getServerEngineConfig() {

        // Get the current SCA module context
        AggregateContext moduleContext = tuscanyRuntime.getModuleComponentContext();
        tuscanyRuntime.start();
        try {

            Module module = (Module)moduleContext.getAggregate();
            AssemblyModelContext modelContext = module.getAssemblyModelContext();

            // Load the .wsdd configuration
            ResourceLoader bundleContext = modelContext.getResourceLoader();
            InputStream wsdd;
            try {
                URL url = bundleContext.getResource("org/apache/tuscany/binding/axis/engine/config/server-config.wsdd");
                wsdd = url.openStream();
            } catch (IOException e1) {
                throw new ServiceRuntimeException(e1);
            }
            FileProvider wsddConfigurationProvider = new FileProvider(wsdd);

            // Create our dynamic configuration
            SimpleProvider dynamicConfigurationProvider = new SimpleProvider(wsddConfigurationProvider);

            // Register all the Web service entry points
            for (Iterator i = module.getEntryPoints().iterator(); i.hasNext();) {
                EntryPoint entryPoint = (EntryPoint) i.next();
                Binding binding = (Binding) entryPoint.getBindings().get(0);
                if (binding instanceof WebServiceBinding) {
                    
                    // Get the proxy factory associated with the entry point 
                    final ProxyFactory proxyFactory=(ProxyFactory)entryPoint.getConfiguredService().getProxyFactory();
                    
                    // Create a new SOAP service
                    SOAPService service = new SOAPService(new RPCProvider() {
                        protected Object makeNewServiceObject(MessageContext arg0, String arg1) throws Exception {
                            
                            // The SOAP service will delegate to a dynamic proxy
                            return proxyFactory.createProxy();
                        }
                    });
                    
                    // Get the WSDL definition and port
                    WebServiceBinding wsBinding = (WebServiceBinding) binding;
                    Definition definition=wsBinding.getWSDLDefinition();
                    Port port=wsBinding.getWSDLPort();
                    WebServicePortMetaData wsPortMetaData = new WebServicePortMetaData(definition, port, null, false);
                    
                    // Configure the SOAP service
                    service.setOption(RPCProvider.OPTION_WSDL_TARGETNAMESPACE, wsPortMetaData.getPortName().getNamespaceURI());
                    service.setOption(RPCProvider.OPTION_WSDL_SERVICEELEMENT, wsPortMetaData.getServiceName().getLocalPart());
                    service.setOption(RPCProvider.OPTION_WSDL_SERVICEPORT, wsPortMetaData.getPortName().getLocalPart());
                    service.setOption(RPCProvider.OPTION_CLASSNAME, WebServiceEntryPointBean.class.getName());
                    service.setOption(RPCProvider.OPTION_WSDL_PORTTYPE, wsPortMetaData.getPortTypeName().getLocalPart());

                    // Create a service description
                    JavaServiceDesc serviceDesc = (JavaServiceDesc) service.getServiceDescription();
                    serviceDesc.setName(entryPoint.getName());
                    String wsdlFile = definition.getDocumentBaseURI();
                    if (wsdlFile.startsWith("file:"))
                        wsdlFile = wsdlFile.substring(5);
                    serviceDesc.setWSDLFile(wsdlFile);
                    serviceDesc.setDefaultNamespace(wsPortMetaData.getPortName().getNamespaceURI());
                    serviceDesc.setStyle(Style.DOCUMENT);
                    serviceDesc.setUse(Use.LITERAL);

                    // Get the service interface
                    Class serviceInterface=entryPoint.getConfiguredService().getService().getServiceContract().getInterface();
                    Set methods=JavaIntrospectionHelper.getAllUniqueMethods(serviceInterface);
                    serviceDesc.setAllowedMethods(new ArrayList(methods));

                    // Create a JAX-RPC service
                    QName wsdlServiceName = wsPortMetaData.getService().getQName();
                    Service jaxrpcService;
                    try {
                        URL url=new URL(wsBinding.getWSDLDefinition().getDocumentBaseURI());
                        jaxrpcService = ServiceFactory.newInstance().createService(url, wsdlServiceName);
                    } catch (ServiceException e) {
                        throw new ServiceUnavailableException(e);
                    } catch (MalformedURLException e) {
                        throw new ServiceUnavailableException(e);
                    }

                    // Create operation descriptions for all the operations
                    PortType wsdlPortType = wsPortMetaData.getPortType();
                    for (Iterator j = wsdlPortType.getOperations().iterator(); j.hasNext();) {
                        Operation wsdlOperation = (Operation) j.next();
                        String operationName = wsdlOperation.getName();
                        
                        // Create a JAX RPC call object
                        QName portName = wsPortMetaData.getPortName();
                        org.apache.axis.client.Call call;
                        try {
                            call = (org.apache.axis.client.Call) jaxrpcService.createCall(portName, operationName);
                        } catch (ServiceException e) {
                            throw new IllegalArgumentException(e);
                        }

                        OperationDesc operationDesc = call.getOperation();
                        operationDesc.setName(operationName);
                        List<Class> argTypes=new ArrayList<Class>();
                        for (ParameterDesc parameterDesc : (List<ParameterDesc>)operationDesc.getAllInParams()) {
                            argTypes.add(parameterDesc.getJavaType());
                        }
                        Method method=JavaIntrospectionHelper.findClosestMatchingMethod(operationName, argTypes.toArray(new Class[argTypes.size()]), methods);
                        operationDesc.setMethod(method);
                        
//                        OperationDesc operationDesc = new OperationDesc();
//                        operationDesc.setName(operationName);
//                        Method method=JavaIntrospectionHelper.findClosestMatchingMethod(operationName, new Class[]{String.class}, methods);
//                        operationDesc.setMethod(method);
//                        
//                        WebServiceOperationMetaData wsOperationMetaData = wsPortMetaData.getOperationMetaData(operationName);
//                        String soapAction = wsOperationMetaData.getSOAPAction();
//                        if (soapAction != null && soapAction.length()!=0)
//                            operationDesc.setSoapAction(soapAction);
//
//                        operationDesc.setElementQName((QName) wsOperationMetaData.getOperationSignature().get(0));
//
//                        Message inputMessage=wsdlOperation.getInput()!=null? wsdlOperation.getInput().getMessage():null;
//                        if (inputMessage!=null & !inputMessage.getParts().isEmpty()) {
//                            ParameterDesc parameterDesc = new ParameterDesc();
//                            QName anyQName = new QName(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, "any");
//                            parameterDesc.setTypeQName(anyQName);
//                            parameterDesc.setQName(anyQName);
//                            parameterDesc.setJavaType(Object.class);
//                            operationDesc.addParameter(parameterDesc);
//                        }
//
//                        Message outputMessage=wsdlOperation.getOutput()!=null? wsdlOperation.getOutput().getMessage():null;
//                        if (outputMessage!=null & !outputMessage.getParts().isEmpty()) {
//                            QName anyQName = new QName(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, "any");
//                            operationDesc.setReturnType(anyQName);
//                            operationDesc.setReturnQName(anyQName);
//                            operationDesc.setReturnClass(Object.class);
//                        }

                        serviceDesc.addOperationDesc(operationDesc);
                    }

                    // Deploy the service
                    dynamicConfigurationProvider.deployService(entryPoint.getName(), service);

                }
            }
            return dynamicConfigurationProvider;

        } finally {
            tuscanyRuntime.stop();
        }

    }

}
