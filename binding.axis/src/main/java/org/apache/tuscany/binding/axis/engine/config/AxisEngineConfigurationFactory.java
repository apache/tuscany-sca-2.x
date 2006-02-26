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
import java.net.URL;
import java.util.Iterator;

import javax.servlet.ServletConfig;
import javax.wsdl.Definition;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.EngineConfigurationFactory;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.description.JavaServiceDesc;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.providers.java.MsgProvider;
import org.apache.tuscany.binding.axis.assembly.WebServiceBinding;
import org.apache.tuscany.binding.axis.handler.WebServiceEntryPointBean;
import org.apache.tuscany.binding.axis.handler.WebServiceOperationMetaData;
import org.apache.tuscany.binding.axis.handler.WebServicePortMetaData;
import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.webapp.TuscanyWebAppRuntime;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.osoa.sca.ServiceRuntimeException;

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

        AssemblyFactory assemblyFactory = new AssemblyFactoryImpl();

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
                URL url = bundleContext.getResource("org/apache/tuscany/binding/axis/config/impl/server-config.wsdd");
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
                    WebServiceBinding wsBinding = (WebServiceBinding) binding;
                    Definition definition=wsBinding.getWSDLDefinition();
                    Port port=wsBinding.getWSDLPort();
                    QName qname = new QName(definition.getTargetNamespace(), port.getName());
                    if (qname != null) {
                        WebServicePortMetaData wsdlPortInfo = new WebServicePortMetaData(definition, port, null, false);

                        // Create a new message SOAP service
                        SOAPService service = new SOAPService(new MsgProvider());
                        service.setOption(MsgProvider.OPTION_WSDL_TARGETNAMESPACE, wsdlPortInfo.getPortName().getNamespaceURI());
                        service.setOption(MsgProvider.OPTION_WSDL_SERVICEELEMENT, wsdlPortInfo.getServiceName().getLocalPart());
                        service.setOption(MsgProvider.OPTION_WSDL_SERVICEPORT, wsdlPortInfo.getPortName().getLocalPart());
                        service.setOption(MsgProvider.OPTION_CLASSNAME, WebServiceEntryPointBean.class.getName());
                        service.setOption(MsgProvider.OPTION_WSDL_PORTTYPE, wsdlPortInfo.getPortTypeName().getLocalPart());

                        // Create a service description
                        JavaServiceDesc serviceDesc = (JavaServiceDesc) service.getServiceDescription();
                        serviceDesc.setName(entryPoint.getName());
                        String wsdlFile = definition.getDocumentBaseURI();
                        if (wsdlFile.startsWith("file:"))
                            wsdlFile = wsdlFile.substring(5);
                        serviceDesc.setWSDLFile(wsdlFile);
                        serviceDesc.setDefaultNamespace(wsdlPortInfo.getPortName().getNamespaceURI());
                        boolean rpcStyle = wsdlPortInfo.getStyle().equals(Style.RPC_STR);
                        serviceDesc.setStyle(rpcStyle ? Style.RPC : Style.DOCUMENT);
                        serviceDesc.setUse(wsdlPortInfo.getUse().equals(Use.ENCODED_STR) ? Use.ENCODED : Use.LITERAL);
                        serviceDesc.setImplClass(WebServiceEntryPointBean.class);

                        // Create operation descriptions for all the operations
                        PortType wsdlPortType = wsdlPortInfo.getPortType();
                        for (Iterator j = wsdlPortType.getOperations().iterator(); j.hasNext();) {
                            Operation wsdlOperation = (Operation) j.next();
                            String operationName = wsdlOperation.getName();

                            OperationDesc operationDesc = new OperationDesc();
                            operationDesc.setName(operationName);
                            operationDesc.setMessageOperationStyle(OperationDesc.MSG_METHOD_SOAPENVELOPE);
                            operationDesc.setMethod(WebServiceEntryPointBean.SERVICE_METHOD);
                            String soapAction = wsdlPortInfo.getOperationMetaData(operationName).getSOAPAction();
                            if (soapAction != null)
                                operationDesc.setSoapAction(soapAction);

                            if (rpcStyle) {
                                operationDesc.setElementQName(new QName("", operationName));
                            } else {
                                WebServiceOperationMetaData operationMetaData = wsdlPortInfo.getOperationMetaData(operationName);
                                operationDesc.setElementQName((QName) operationMetaData.getOperationSignature().get(0));
                            }

                            Message inputMessage=wsdlOperation.getInput()!=null? wsdlOperation.getInput().getMessage():null;
                            if (inputMessage!=null & !inputMessage.getParts().isEmpty()) {
                                ParameterDesc parameterDesc = new ParameterDesc();
                                QName anyQName = new QName(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, "any");
                                parameterDesc.setTypeQName(anyQName);
                                parameterDesc.setQName(anyQName);
                                parameterDesc.setJavaType(Object.class);
                                operationDesc.addParameter(parameterDesc);
                            }

                            Message outputMessage=wsdlOperation.getOutput()!=null? wsdlOperation.getOutput().getMessage():null;
                            if (outputMessage!=null & !outputMessage.getParts().isEmpty()) {
                                QName anyQName = new QName(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, "any");
                                operationDesc.setReturnType(anyQName);
                                operationDesc.setReturnQName(anyQName);
                                operationDesc.setReturnClass(Object.class);
                            }

                            serviceDesc.addOperationDesc(operationDesc);
                        }

                        // Deploy the service
                        dynamicConfigurationProvider.deployService(entryPoint.getName(), service);
                    }

                }
            }
            return dynamicConfigurationProvider;

        } finally {
            tuscanyRuntime.stop();
        }

    }

}
