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
package org.apache.tuscany.sca.binding.ws.wsdlgen;

//FIXME: trim the import list down to what's really needed

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.wsdl.extensions.soap12.SOAP12Binding;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.dom.DOMSource;

import org.apache.tuscany.sca.assembly.AbstractContract;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.builder.impl.ProblemImpl;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.apache.tuscany.sca.policy.security.ws.Axis2ConfigParamPolicy;
import org.apache.tuscany.sca.policy.util.PolicyHandler;
import org.apache.tuscany.sca.policy.util.PolicyHandlerTuple;
import org.apache.tuscany.sca.policy.util.PolicyHandlerUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * WSDLServiceGenerator generates a binding WSDL service document.
 *
 * @version $Rev$ $Date$
 */
public class WSDLServiceGenerator {
    // the following switch is temporary for debugging
    public static boolean printWSDL;  // external code sets this to print generated WSDL
    
    private static final Logger logger = Logger.getLogger(WSDLServiceGenerator.class.getName());
    private static final QName TRANSPORT_JMS_QUALIFIED_INTENT =
        new QName("http://www.osoa.org/xmlns/sca/1.0", "transport.jms");
    private static final String DEFAULT_QUEUE_CONNECTION_FACTORY = "TuscanyQueueConnectionFactory";
    private static final String ADDRESS = "Address";

    private WSDLServiceGenerator() {
        // this class has static methods only and cannot be instantiated
    }

    /**
     * Log a warning message.
     * @param problem
     */
    private static void logWarning(Problem problem) {
        Logger problemLogger = Logger.getLogger(problem.getSourceClassName(), problem.getBundleName());
        if (problemLogger != null){
            problemLogger.logp(Level.WARNING, problem.getSourceClassName(), null, problem.getMessageId(), problem.getMessageParams());
        } else {
            logger.severe("Can't get logger " + problem.getSourceClassName()+ " with bundle " + problem.getBundleName());
        }
    }

    /**
     * Report a warning.
     * @param message
     * @param binding
     * @param parameters
     */
    private static void warning(Monitor monitor, String message, WebServiceBinding wsBinding, String... messageParameters) {
        Problem problem = new ProblemImpl(WSDLServiceGenerator.class.getName(), "wsdlgen-validation-messages", Severity.WARNING, wsBinding, message, (Object[])messageParameters);
        if (monitor != null) {
            monitor.problem(problem);
        } else {
            logWarning(problem);
        }
    }

    /**
     * Report an error.
     * @param message
     * @param binding
     * @param parameters
     */
    private static void error(Monitor monitor, String message, WebServiceBinding wsBinding, String... messageParameters) {
        Problem problem = new ProblemImpl(WSDLServiceGenerator.class.getName(), "wsdlgen-validation-messages", Severity.ERROR, wsBinding, message, (Object[])messageParameters);
        if (monitor != null) {
            monitor.problem(problem);
        } else {
            throw new WSDLGenerationException(problem.toString(), null, problem);
        }
    }

    /**
     * Generate a suitably configured WSDL definition
     */
    protected static Definition configureWSDLDefinition(WebServiceBinding wsBinding,
                                                        Component component,
                                                        AbstractContract contract,
                                                        Monitor monitor) {

        //[nash] changes to the builder sequence avoid calling this for a CompositeService
        assert !(contract instanceof CompositeService);
        /*
        // For every promoted composite service, the underlying component
        // gets a copy of the service with the name prefixed by "$promoted$."
        String contractName = (contract instanceof CompositeService ? "$promoted$." : "") + contract.getName();
        */
        String contractName = contract.getName();

        List<Port> ports = new ArrayList<Port>();
        WSDLDefinition wsdlDefinition = wsBinding.getWSDLDefinition();
        if (wsdlDefinition == null) {
            error(monitor, "NoWsdlInterface", wsBinding, component.getName(), contract.getName());
            return null;
        }
        Definition def = wsdlDefinition.getDefinition();
        if (wsdlDefinition.getBinding() == null) {
            // The WSDL document was provided by the user.  Generate a new
            // WSDL document with imports from the user-provided document.
            WSDLFactory factory = null;
            try {
                factory = WSDLFactory.newInstance();
            } catch (WSDLException e) {
                throw new WSDLGenerationException(e);
            }
            Definition newDef = factory.newDefinition();

            // Construct a target namespace from the base URI of the user's
            // WSDL document (is this what we should be using?) and a path
            // computed according to the SCA Web Service binding spec.
            String nsName = component.getName() + "/" + contractName;
            String namespaceURI = null;
            try {
                URI userTNS = new URI(def.getTargetNamespace());
                namespaceURI = userTNS.resolve("/" + nsName).toString();
            } catch (URISyntaxException e1) {
                throw new WSDLGenerationException(e1);
            } catch (IllegalArgumentException e2) {
                throw new WSDLGenerationException(e2);
            }

            // set name and targetNamespace attributes on the definition
            String defsName = component.getName() + "." + contractName;
            newDef.setQName(new QName(namespaceURI, defsName));
            newDef.setTargetNamespace(namespaceURI);
            newDef.addNamespace("tns", namespaceURI);

            // set wsdl namespace prefix on the definition
            newDef.addNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");

            // import the service or reference interface portType
            List<WSDLDefinition> imports = new ArrayList<WSDLDefinition>();
            Interface interfaze = wsBinding.getBindingInterfaceContract().getInterface();
            if (interfaze instanceof WSDLInterface) {
                PortType portType = ((WSDLInterface)interfaze).getPortType();
                boolean ok = importPortType(portType, wsdlDefinition, newDef, imports);
                if (!ok) {
                    error(monitor, "PortTypeNotFound", wsBinding, portType.getQName().toString(),
                          component.getName(), contract.getName());
                }
            }

            // import an existing binding if specified
            Binding binding = wsBinding.getBinding();
            if (binding != null) {
                boolean ok = importBinding(binding, wsdlDefinition, newDef, imports);
                if (ok) {
                    boolean ok2 = importPortType(binding.getPortType(), wsdlDefinition, newDef, imports);
                    if (!ok2) {
                        error(monitor, "PortTypeNotFound", wsBinding, binding.getPortType().getQName().toString(),
                              component.getName(), contract.getName());
                    }
                } else {
                    error(monitor, "BindingNotFound", wsBinding, binding.getQName().toString(),
                          component.getName(), contract.getName());
                }
            }

            // import bindings and portTypes needed by services and ports 
            QName serviceQName = wsBinding.getServiceName();
            String portName = wsBinding.getPortName();
            if (serviceQName != null) {
                Service service = def.getService(serviceQName); 
                if (portName != null) {
                    Port port = service.getPort(portName);
                    Port newPort = copyPort(newDef, port, wsBinding);
                    if (newPort != null) {
                        importBinding(port.getBinding(), wsdlDefinition, newDef, imports);
                        ports.add(newPort);
                    } else {
                        error(monitor, "InvalidPort", wsBinding, serviceQName.toString(), portName,
                              component.getName(), contract.getName());
                    }
                } else {
                    for (Object port : service.getPorts().values()) {
                        Port newPort = copyPort(newDef, (Port)port, wsBinding);
                        if (newPort != null) {
                            importBinding(((Port)port).getBinding(), wsdlDefinition, newDef, imports);
                            ports.add(newPort);
                        } else {
                            // not an error, just ignore the port
                            warning(monitor, "IgnoringPort", wsBinding, serviceQName.toString(), ((Port)port).getName(),
                                    component.getName(), contract.getName());
                        }
                    }
                    if (ports.size() == 0) {
                        error(monitor, "NoValidPorts", wsBinding, serviceQName.toString(),
                              component.getName(), contract.getName());
                    }
                }
            }

            // replace original WSDL definition by the generated definition
            def = newDef;

        } else {
            // The WSDL definition was generated by Interface2WSDLGenerator.
            // Reuse it instead of creating a new definition here.
        }

        // add a service and ports to the generated definition  
        WSDLDefinitionGenerator helper =
                new WSDLDefinitionGenerator(BindingWSDLGenerator.requiresSOAP12(wsBinding));
        WSDLInterface wi = (WSDLInterface)wsBinding.getBindingInterfaceContract().getInterface();
        PortType portType = wi.getPortType();
        Service service = helper.createService(def, portType);
        if (wsBinding.getBinding() == null && ports.size() == 0) {
            Binding binding = helper.createBinding(def, portType);
            if (BindingWSDLGenerator.requiresSOAP12(wsBinding)) {
                def.addNamespace("SOAP12", "http://schemas.xmlsoap.org/wsdl/soap12/");
            } else {
                def.addNamespace("SOAP11", "http://schemas.xmlsoap.org/wsdl/soap/");
            }
            helper.createBindingOperations(def, binding, portType);
            binding.setUndefined(false);
            def.addBinding(binding);
            
            String endpointURI = computeActualURI(wsBinding, null);
            Port port = helper.createPort(def, binding, service, endpointURI);
            wsBinding.setService(service);
            wsBinding.setPort(port);
        } else {
            if (ports.size() > 0) {
                // there are one or more user-specified valid ports
                for (Port port : ports) {
                    service.addPort(port);
                }
                if (ports.size() == 1) {
                    // only one port, so use it
                    wsBinding.setPort(ports.get(0));
                } else {
                    // multiple ports, make them all available
                    wsBinding.setPort(null);
                }
            } else {
                // no valid user-specified ports, so create a suitably configured port
                String endpointURI = computeActualURI(wsBinding, null);
                Port port = helper.createPort(def, wsBinding.getBinding(), service, endpointURI);
                if (BindingWSDLGenerator.requiresSOAP12(wsBinding)) {
                    def.addNamespace("SOAP12", "http://schemas.xmlsoap.org/wsdl/soap12/");
                } else {
                    def.addNamespace("SOAP11", "http://schemas.xmlsoap.org/wsdl/soap/");
                }
                wsBinding.setPort(port);
            }
            wsBinding.setService(service);
        }

        // for debugging
        if (printWSDL) {
            try {
                System.out.println("Generated WSDL for " + component.getName() + "/" + contractName);
                WSDLWriter writer =  javax.wsdl.factory.WSDLFactory.newInstance().newWSDLWriter();
                writer.writeWSDL(def, System.out);
            } catch (WSDLException e) {
                throw new WSDLGenerationException(e);
            }
        }

        return def;
    }

    private static boolean importPortType(PortType portType,
                                          WSDLDefinition wsdlDef,
                                          Definition newDef,
                                          List<WSDLDefinition> imports) {
        return addImport(portType.getQName(), PortType.class, wsdlDef, newDef, imports);
    }
    
    private static boolean importBinding(Binding binding,
                                         WSDLDefinition wsdlDef,
                                         Definition newDef,
                                         List<WSDLDefinition> imports) {
        boolean ok = addImport(binding.getQName(), Binding.class, wsdlDef, newDef, imports);
        if (ok) {
            List bindingExtensions = binding.getExtensibilityElements();
            for (final Object extension : bindingExtensions) {
                if (extension instanceof SOAPBinding) {
                    newDef.addNamespace("SOAP11", "http://schemas.xmlsoap.org/wsdl/soap/");
                }
                if (extension instanceof SOAP12Binding) {
                    newDef.addNamespace("SOAP12", "http://schemas.xmlsoap.org/wsdl/soap12/");
                }
            }
        }
        return ok;
    }
    
    private static boolean addImport(QName name,
                                     Class type,
                                     WSDLDefinition wsdlDef,
                                     Definition newDef,
                                     List<WSDLDefinition> imports) {
        String namespace = name.getNamespaceURI();
        if (newDef.getImports(namespace) == null) {
            WSDLDefinition impDef = findDefinition(wsdlDef, name, type);
            if (impDef != null) {
                Import imp = newDef.createImport();
                imp.setNamespaceURI(namespace);
                imp.setLocationURI(impDef.getURI().toString());
                imp.setDefinition(impDef.getDefinition());
                newDef.addNamespace("ns" + imports.size(), namespace);
                newDef.addImport(imp);
                imports.add(impDef);
                return true;
            } else {
                // import was not added because element not found
                return false;
            }
        }
        return true;
    }

    private static WSDLDefinition findDefinition(WSDLDefinition wsdlDef, QName name, Class type) {
        if (wsdlDef == null || name == null) {
            return wsdlDef;
        }
        if (wsdlDef.getURI() != null) {  // not a facade
            Definition def = wsdlDef.getDefinition();
            Map types = type == PortType.class ? def.getPortTypes() : def.getBindings();
            if (types.get(name) != null) {
                return wsdlDef;
            }
        }
        for (WSDLDefinition impDef : wsdlDef.getImportedDefinitions()) {
            WSDLDefinition d = findDefinition(impDef, name, type);
            if (d != null) {
                return d;
            }
        }
        return null;
    }

    private static Port copyPort(Definition def, Port port, WebServiceBinding wsBinding) {
        Port newPort = def.createPort();
        newPort.setName(port.getName());
        newPort.setBinding(port.getBinding());
        List portExtensions = port.getExtensibilityElements();
        for (final Object extension : portExtensions) {
            ExtensibilityElement newExt = null;
            if (extension instanceof SOAPAddress) {
                def.addNamespace("SOAP11", "http://schemas.xmlsoap.org/wsdl/soap/");
                try {
                    newExt = def.getExtensionRegistry().createExtension(
                             Port.class, WSDLDefinitionGenerator.SOAP_ADDRESS);
                } catch (WSDLException e) {
                }
                String uri = computeActualURI(wsBinding, port);
                ((SOAPAddress)newExt).setLocationURI(uri);
                newPort.addExtensibilityElement(newExt);
            } else if (extension instanceof SOAP12Address) {
                def.addNamespace("SOAP12", "http://schemas.xmlsoap.org/wsdl/soap12/");
                try {
                    newExt = def.getExtensionRegistry().createExtension(
                             Port.class, WSDLDefinitionGenerator.SOAP12_ADDRESS);
                } catch (WSDLException e) {
                }
                String uri = computeActualURI(wsBinding, port);
                ((SOAP12Address)newExt).setLocationURI(uri);
                newPort.addExtensibilityElement(newExt);
            } else {
                // we don't support ports with other extensibility elements such as HTTPAddress
                return null;
            }
        }
        return newPort;
    }

    /**
     * Compute the endpoint URI based on section 2.1.1 of the WS binding Specification 1.
     * The URIs in the endpoint(s) of the referenced WSDL, which may be relative
     * 2. The URI specified by the wsa:Address element of the
     * wsa:EndpointReference, which may be relative 3. The explicitly stated URI
     * in the "uri" attribute of the binding.ws element, which may be relative,
     * 4. The implicit URI as defined by in section 1.7 in the SCA Assembly Specification
     * If the <binding.ws> has no wsdlElement but does have a uri attribute then
     * the uri takes precedence over any implicitly used WSDL.
     * 
     */
    private static String computeActualURI(WebServiceBinding wsBinding, Port port) {

        URI eprURI = null;
        if (wsBinding.getEndPointReference() != null) {
            eprURI = getEPR(wsBinding); 
        }

        URI wsdlURI = null;
        if (wsBinding.getServiceName() != null && wsBinding.getBindingName() == null) {
            // <binding.ws> explicitly points at a WSDL port, may be a relative URI
            wsdlURI = getEndpoint(port);
        }

        // if the WSDL port/endpoint has an absolute URI use that
        if (wsdlURI != null && wsdlURI.isAbsolute()) {
            return wsdlURI.toString();
        }

        // if the wsa:EndpointReference has an address element with an absolute URI use that
        if (eprURI != null && eprURI.isAbsolute()) {
            return eprURI.toString();
        }
        
        // either there is no WSDL port endpoint URI or that URI is relative
        String actualURI = wsBinding.getURI();
        if (eprURI != null && eprURI.toString().length() != 0) {
            // there is a relative URI in the binding EPR
            actualURI = actualURI + "/" + eprURI;
        }

        if (wsdlURI != null && wsdlURI.toString().length() != 0) {
            // there is a relative URI in the WSDL port
            actualURI = actualURI + "/" + wsdlURI;
        }
        
        if (actualURI != null) {
            actualURI = URI.create(actualURI).normalize().toString();
        }
        
        return actualURI;
    }

    private static URI getEPR(WebServiceBinding wsBinding) {
        NodeList nodeList = wsBinding.getEndPointReference().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            if (childNode instanceof Element && ADDRESS.equals(childNode.getLocalName())) {
                NodeList addrNodes = childNode.getChildNodes();
                for (int j = 0; j < addrNodes.getLength(); j++) {
                    Node addrNode = addrNodes.item(j);
                    if (addrNode instanceof Text) {
                        return URI.create(((Text)addrNode).getWholeText());
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns the endpoint of a given port.
     */
    private static URI getEndpoint(Port wsdlPort) {
        if (wsdlPort != null) {
            List<?> wsdlPortExtensions = wsdlPort.getExtensibilityElements();
            for (Object extension : wsdlPortExtensions) {
                if (extension instanceof SOAPAddress) {
                    String uri = ((SOAPAddress)extension).getLocationURI();
                    return (uri == null || "".equals(uri)) ? null : URI.create(uri);
                }
                if (extension instanceof SOAP12Address) {
                    SOAP12Address address = (SOAP12Address)extension;
                    String uri = address.getLocationURI();
                    return (uri == null || "".equals(uri)) ? null : URI.create(uri);
                }
            }
        }
        return null;
    }

}
