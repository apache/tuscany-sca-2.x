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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.dom.DOMSource;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.addressing.EndpointReferenceHelper;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.deployment.DeploymentErrorMsgs;
import org.apache.axis2.deployment.DeploymentException;
import org.apache.axis2.deployment.ModuleBuilder;
import org.apache.axis2.deployment.util.Utils;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.WSDL11ToAxisServiceBuilder;
import org.apache.axis2.description.WSDL2Constants;
import org.apache.axis2.description.WSDLToAxisServiceBuilder;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.engine.MessageReceiver;
import org.apache.axis2.i18n.Messages;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.tuscany.sca.assembly.AbstractContract;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.core.assembly.EndpointReferenceImpl;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.apache.tuscany.sca.policy.security.ws.Axis2ConfigParamPolicy;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;

public class Axis2ServiceProvider {

    private AbstractContract contract;
    private WebServiceBinding wsBinding;
    private ServletHost servletHost;
    private MessageFactory messageFactory;
    private ConfigurationContext configContext;

    public static final QName QNAME_WSA_ADDRESS =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.EPR_ADDRESS);
    public static final QName QNAME_WSA_FROM =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.WSA_FROM);
    public static final QName QNAME_WSA_TO =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.WSA_TO);

    public static final QName QNAME_WSA_REFERENCE_PARAMETERS =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.EPR_REFERENCE_PARAMETERS);

    // TODO: what to do about the base URI?
    // This port number may be used to construct callback URIs.  The value 8085 is used
    // beacuse it matches the service port number used by the simple-callback-ws sample.
    private static final String BASE_URI = "http://localhost:8085/";

    public Axis2ServiceProvider(RuntimeComponent component,
                                AbstractContract contract,
                                WebServiceBinding wsBinding,
                                ServletHost servletHost,
                                MessageFactory messageFactory) {

        this.contract = contract; 
        this.wsBinding = wsBinding;
        this.servletHost = servletHost;
        this.messageFactory = messageFactory;
        
        try {
            TuscanyAxisConfigurator tuscanyAxisConfigurator = new TuscanyAxisConfigurator();
            configContext = tuscanyAxisConfigurator.getConfigurationContext();
            //deployRampartModule();
            configureSecurity();
        } catch (AxisFault e) {
            throw new RuntimeException(e); // TODO: better exception
        } catch ( Exception e ) {
            throw new RuntimeException(e);
        }

        configContext.setContextRoot(servletHost.getContextPath());

        String uri = computeActualURI(BASE_URI, component, contract).normalize().toString();
        if (uri.endsWith("/")) {
            uri = uri.substring(0, uri.length() - 1);
        }
        wsBinding.setURI(uri);
    }
    
    private void engageModules() throws AxisFault {
        if ( wsBinding instanceof PolicySetAttachPoint ) {
            PolicySetAttachPoint policiedBinding = (PolicySetAttachPoint)wsBinding;
            if ( policiedBinding.getPolicySets().size() > 0 ) {
                //TODO: need to verify if one of the policies are concerned with security
                AxisModule m = new AxisModule("rampart");
                m.setFileName(wsBinding.getClass().getClassLoader().getResource("rampart-1.2.mar"));
                configContext.getAxisConfiguration().addModule(m);
                configContext.getAxisConfiguration().engageModule(m, configContext.getAxisConfiguration());
            }
        }
    }

    public void start() {

        // TODO: if <binding.ws> specifies the wsdl service then should create a
        // service for every port

        try {
            AxisService axisService = createAxisService(); 
            configContext.getAxisConfiguration().addService(createAxisService());
        } catch (AxisFault e) {
            throw new RuntimeException(e);
        } 

        Axis2ServiceServlet servlet = new Axis2ServiceServlet();
        servlet.init(configContext);
        String servletURI = wsBinding.getURI();
        servletHost.addServletMapping(servletURI, servlet);
    }

    public void stop() {
        servletHost.removeServletMapping(wsBinding.getURI());
        try {
            configContext.getAxisConfiguration().removeService(wsBinding.getURI());
        } catch (AxisFault e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Compute the endpoint URI based on section 2.1.1 of the WS binding spec 1.
     * The URIs in the endpoint(s) of the referenced WSDL, which may be relative
     * 2. The URI specified by the wsa:Address element of the
     * wsa:EndpointReference, which may be relative 3. The explicitly stated URI
     * in the "uri" attribute of the binding.ws element, which may be relative,
     * 4. The implicit URI as defined by in section 1.7 in the SCA Assembly spec
     * If the <binding.ws> has no wsdlElement but does have a uri attribute then
     * the uri takes precidence over any implicitly used WSDL.
     * 
     * @param parent
     */
    protected URI computeActualURI(String baseURI, RuntimeComponent component, AbstractContract contract) {

        org.apache.axis2.addressing.EndpointReference epr = null;
        URI eprURI = null;
        if (wsBinding.getEndPointReference() != null) {
            epr = getEPR(); 
            if (epr.getAddress() != null) {
                eprURI = URI.create(epr.getAddress());
            }
        }

        URI wsdlURI = null;
        if (wsBinding.getServiceName() != null && wsBinding.getBindingName() == null) {
            // <binding.ws> explicitly points at a wsdl port, may be a relative URI
            wsdlURI = getEndpoint(wsBinding.getPort());
        }

        // if the wsdl port/endpoint has an absolute URI use that
        if (wsdlURI != null && wsdlURI.isAbsolute()) {
            if (wsBinding.getURI() != null) {
// TODO:                throw new IllegalArgumentException("Cannot specify binding URI when wsdl has aboslute URI");
            }
            return URI.create(wsdlURI.toString());
        }

        // if the wsa:EndpointReference has an address element with an absolute URI use that
        if (eprURI != null && eprURI.isAbsolute()) {
            if (wsBinding.getURI() != null) {
                throw new IllegalArgumentException("Cannot specify binding URI when wsa:EndpointReference has aboslute address URI");
            }
            return URI.create(eprURI.toString());
        }
        
        // either there is no wsdl port endpoint URI or that URI is relative

        URI completeURI;
        if (wsBinding.getURI() != null) {
            completeURI = URI.create(wsBinding.getURI());
            if (!completeURI.isAbsolute()) {
                completeURI = URI.create(baseURI + "/" + wsBinding.getURI());
            }
        } else {
            completeURI = URI.create(baseURI + "/" + wsBinding.getName());
        }
        
        if (eprURI != null) {
            // there is a relative URI in the binding EPR
            completeURI = URI.create(completeURI + "/" + eprURI);
        }

        if (wsdlURI != null) {
            // there is a relative URI in the wsdl port
            completeURI = URI.create(completeURI + "/" + wsdlURI);
        }
        
        return completeURI;

    }

    private org.apache.axis2.addressing.EndpointReference getEPR() {
        try {

            XMLStreamReader parser = XMLInputFactory.newInstance().createXMLStreamReader(new DOMSource(wsBinding.getEndPointReference()));
            StAXOMBuilder builder = new StAXOMBuilder(parser);
            OMElement omElement = builder.getDocumentElement();
            org.apache.axis2.addressing.EndpointReference epr = EndpointReferenceHelper.fromOM(omElement);
            return epr;

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        } catch (FactoryConfigurationError e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the endpoint of a given port.
     */
    protected URI getEndpoint(Port wsdlPort) {
        if (wsdlPort != null) {
            List<?> wsdlPortExtensions = wsdlPort.getExtensibilityElements();
            for (Object extension : wsdlPortExtensions) {
                if (extension instanceof SOAPAddress) {
                    return URI.create(((SOAPAddress)extension).getLocationURI());
                }
            }
        }
        return null;
    }

    private AxisService createAxisService() throws AxisFault {
        AxisService axisService;
        if (wsBinding.getWSDLDefinition() != null) {
            axisService = createWSDLAxisService();
        } else {
            axisService = createJavaAxisService();
        }
        initAxisOperations(axisService);
        return axisService;
    }

    /**
     * Create an AxisService from the interface class from the SCA service interface
     */
    protected AxisService createJavaAxisService() throws AxisFault {
        AxisService axisService = new AxisService();
        String path = URI.create(wsBinding.getURI()).getPath();
        axisService.setName(path);
        axisService.setServiceDescription("Tuscany configured AxisService for service: " + wsBinding.getURI());
        axisService.setClientSide(false);
        Parameter classParam =
            new Parameter(Constants.SERVICE_CLASS, ((JavaInterface)contract.getInterfaceContract().getInterface())
                .getJavaClass().getName());
        axisService.addParameter(classParam);
        try {
            Utils.fillAxisService(axisService, configContext.getAxisConfiguration(), null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return axisService;
    }

    /**
     * Create an AxisService from the WSDL doc used by ws binding
     */
    protected AxisService createWSDLAxisService() throws AxisFault {
        Definition definition = wsBinding.getWSDLDefinition().getDefinition();

        // WSDLToAxisServiceBuilder only uses the service and port to find the wsdl4J Binding
        // An SCA service with binding.ws does not require a service or port so we may not have
        // these but ...

        Axis2ServiceClient.setServiceAndPort(wsBinding);
        QName serviceQName = wsBinding.getServiceName();
        String portName = wsBinding.getPortName();

        WSDLToAxisServiceBuilder builder = new WSDL11ToAxisServiceBuilder(definition, serviceQName, portName);
        builder.setServerSide(true);
        AxisService axisService = builder.populateService();

        String path = URI.create(wsBinding.getURI()).getPath();
        axisService.setName(path);
        axisService.setServiceDescription("Tuscany configured AxisService for service: " + wsBinding.getURI());

        // Use the existing WSDL
        Parameter wsdlParam = new Parameter(WSDLConstants.WSDL_4_J_DEFINITION, null);
        wsdlParam.setValue(definition);
        axisService.addParameter(wsdlParam);
        Parameter userWSDL = new Parameter("useOriginalwsdl", "true");
        axisService.addParameter(userWSDL);

        return axisService;
    }

    protected void initAxisOperations(AxisService axisService) {
        for (Iterator<?> i = axisService.getOperations(); i.hasNext();) {
            AxisOperation axisOp = (AxisOperation)i.next();
            Operation op = getOperation(axisOp);
            if (op != null) {

                if (op.isNonBlocking()) {
                    axisOp.setMessageExchangePattern(WSDL2Constants.MEP_URI_IN_ONLY);
                } else {
                    axisOp.setMessageExchangePattern(WSDL2Constants.MEP_URI_IN_OUT);
                }

                MessageReceiver msgrec = null;
                if (op.isNonBlocking()) {
                    msgrec = new Axis2ServiceInMessageReceiver(this, op);
                } else {
                    msgrec = new Axis2ServiceInOutSyncMessageReceiver(this, op);
                }
                axisOp.setMessageReceiver(msgrec);
            }
        }
    }

    protected Operation getOperation(AxisOperation axisOp) {
        String operationName = axisOp.getName().getLocalPart();
        Interface iface = wsBinding.getBindingInterfaceContract().getInterface();
        for (Operation op : iface.getOperations()) {
            if (op.getName().equalsIgnoreCase(operationName)) {
                return op;
            }
        }
        return null;
    }

    // methods for Axis2 message receivers

    public Object invokeTarget(Operation op, Object[] args, MessageContext inMC) throws InvocationTargetException {

        String callbackAddress = null;
        String callbackID = null;
        Object conversationID = null;

        //FIXME: can we use the Axis2 addressing support for this?
        SOAPHeader header = inMC.getEnvelope().getHeader();
        if (header != null) {
            OMElement to = header.getFirstChildWithName(QNAME_WSA_TO);
            if (to != null) {
                OMElement params = to.getFirstChildWithName(QNAME_WSA_REFERENCE_PARAMETERS);
                if (params != null) {
                    OMElement convIDElement =
                        params.getFirstChildWithName(Axis2BindingInvoker.CONVERSATION_ID_REFPARM_QN);
                    if (convIDElement != null) {
                        if (isConversational()) {
                            conversationID = convIDElement.getText();
                        }
                    }
                    OMElement callbackIDElement =
                        params.getFirstChildWithName(Axis2BindingInvoker.CALLBACK_ID_REFPARM_QN);
                    if (callbackIDElement != null) {
                        if (contract.getInterfaceContract().getCallbackInterface() != null) {
                            callbackID = callbackIDElement.getText();
                        }
                    }
                }
            }

            OMElement from = header.getFirstChildWithName(QNAME_WSA_FROM);
            if (from != null) {
                OMElement addrElement = from.getFirstChildWithName(QNAME_WSA_ADDRESS);
                if (addrElement != null && contract.getInterfaceContract().getCallbackInterface() != null) {
                    callbackAddress = addrElement.getText();
                }
            }
        }

        RuntimeWire wire = null;
        try {
            wire = (RuntimeWire)(((RuntimeComponentService)contract).getRuntimeWire(getBinding())).clone();
        } catch (CloneNotSupportedException e) {
            // Should not happen
        }
        EndpointReference source = wire.getSource();
        if (callbackAddress != null) {
            source.setCallbackEndpoint(new EndpointReferenceImpl(callbackAddress));
        }

        EndpointReference to = wire.getTarget();
        if (callbackID != null) {
            to.getReferenceParameters().setCallbackID(callbackID);
        }
        if (conversationID != null) {
            to.getReferenceParameters().setConversationID(conversationID);
        }
        return wire.invoke(op, args);

    }

    public boolean isConversational() {
        return wsBinding.getBindingInterfaceContract().getInterface().isConversational();
    }

    /**
     * Return the binding for this provider as a primitive binding type
     * For use when looking up wires registered against the binding.
     * 
     * @return the binding
     */
    protected Binding getBinding() {
        return wsBinding;
    }
    
    private void configureSecurity() throws AxisFault {
        if ( wsBinding instanceof PolicySetAttachPoint ) {
            PolicySetAttachPoint policiedBinding = (PolicySetAttachPoint)wsBinding; 
            Parameter configParam = null;
            Axis2ConfigParamPolicy axis2ConfigParamPolicy = null;
            for ( PolicySet policySet : policiedBinding.getPolicySets() ) {
                for ( Object policy : policySet.getPolicies() ) {
                    if ( policy instanceof Axis2ConfigParamPolicy ) {
                        axis2ConfigParamPolicy = (Axis2ConfigParamPolicy)policy;
                        for ( String paramName : axis2ConfigParamPolicy.getParamElements().keySet() ) {
                            configParam = new Parameter(paramName, 
                                                        axis2ConfigParamPolicy.getParamElements().get(paramName).getFirstElement());
                            configParam.setParameterElement(axis2ConfigParamPolicy.getParamElements().get(paramName));
                            configContext.getAxisConfiguration().addParameter(configParam);
                        }
                    }
                }
            }
        }
    }
    
    private void deployRampartModule()  throws DeploymentException, AxisFault {
    	ClassLoader tccl = (ClassLoader) org.apache.axis2.java.security.AccessController
        .doPrivileged(new PrivilegedAction() {
            public Object run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });


        AxisModule module = new AxisModule();
        module.setParent(configContext.getAxisConfiguration());
		String moduleName = "rampart-1.2";
		URL moduleurl = TuscanyAxisConfigurator.class.getResource("/org/apache/tuscany/sca/binding/ws/axis2/engine/config/rampart-1.2.mar");
		module.setName(moduleName);
		ClassLoader deploymentClassloader = Utils.createClassLoader(new URL[]{moduleurl},
													tccl,
													true,
													(File)configContext.getAxisConfiguration().getParameterValue(Constants.Configuration.ARTIFACTS_TEMP_DIR));
													
		module.setModuleClassLoader(deploymentClassloader);
		populateModule(module, moduleurl,configContext.getAxisConfiguration());
		module.setFileName(moduleurl);
		TuscanyAxisConfigurator.addNewModule(module, configContext.getAxisConfiguration());
		org.apache.axis2.util.Utils.calculateDefaultModuleVersion(
				configContext.getAxisConfiguration().getModules(), configContext.getAxisConfiguration());
    }
    
    private void populateModule(AxisModule module, URL moduleUrl, AxisConfiguration axisConfig) throws DeploymentException {
        try {
            ClassLoader classLoadere = module.getModuleClassLoader();
            InputStream moduleStream = classLoadere.getResourceAsStream("META-INF/module.xml");
            if (moduleStream == null) {
                moduleStream = classLoadere.getResourceAsStream("meta-inf/module.xml");
            }
            if (moduleStream == null) {
                throw new DeploymentException(
                        Messages.getMessage(
                                DeploymentErrorMsgs.MODULE_XML_MISSING, moduleUrl.toString()));
            }
            ModuleBuilder moduleBuilder = new ModuleBuilder(moduleStream, module, axisConfig);
            moduleBuilder.populateModule();
        } catch (IOException e) {
            throw new DeploymentException(e);
        }
    }
}
