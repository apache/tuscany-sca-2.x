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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.Port;
import javax.wsdl.Types;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.deployment.URLBasedAxisConfigurator;
import org.apache.axis2.deployment.util.Utils;
import org.apache.axis2.description.AxisEndpoint;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.TransportInDescription;
import org.apache.axis2.description.TransportOutDescription;
import org.apache.axis2.description.WSDL11ToAxisServiceBuilder;
import org.apache.axis2.description.WSDL2Constants;
import org.apache.axis2.description.WSDLToAxisServiceBuilder;
import org.apache.axis2.engine.ListenerManager;
import org.apache.axis2.engine.MessageReceiver;
import org.apache.axis2.transport.jms.JMSListener;
import org.apache.axis2.transport.jms.JMSSender;
import org.apache.axis2.transport.local.LocalResponder;
import org.apache.tuscany.sca.assembly.AbstractContract;
import org.apache.tuscany.sca.assembly.AssemblyFactory;

import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.axis2.Axis2ServiceClient.URIResolverImpl;
import org.apache.tuscany.sca.binding.ws.axis2.policy.mtom.Axis2MTOMPolicyProvider;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.assembly.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.extensibility.ClassLoaderContext;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.util.PolicyHelper;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.xsd.XSDefinition;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaExternal;
import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;
import org.oasisopen.sca.ServiceRuntimeException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ctc.wstx.stax.WstxInputFactory;


public class Axis2EngineIntegration {
    private static final Logger logger = Logger.getLogger(Axis2EngineIntegration.class.getName());
    
    private RuntimeEndpoint endpoint;
    private RuntimeComponent component;
    private AbstractContract contract;
    private WebServiceBinding wsBinding;
    private ServletHost servletHost;
    private MessageFactory messageFactory;
    private FactoryExtensionPoint modelFactories;
    private RuntimeAssemblyFactory assemblyFactory;
    
    private ConfigurationContext configContext;   
    
    // a map of all endpoint URIs to WSDL ports for which service will be started 
    // 
    private Map<String, Port> urlMap = new HashMap<String, Port>();
    
    // default provided intents
    public static final String SCA_11_NS = org.apache.tuscany.sca.assembly.xml.Constants.SCA11_NS;
    public static final String SCA_11_TUSCANY_NS = org.apache.tuscany.sca.assembly.xml.Constants.SCA11_TUSCANY_NS;
    
    // TODO - move these constants to assembly
    public static final QName AUTHENTICATION_INTENT = new QName(SCA_11_NS, "authentication");
    public static final QName CONFIDENTIALITY_INTENT = new QName(SCA_11_NS, "confidentiality");
    public static final QName INTEGRITY_INTENT = new QName(SCA_11_NS, "integrity");
    public static final QName MTOM_INTENT = new QName(SCA_11_TUSCANY_NS, "MTOM");
    public static final QName SOAP_INTENT = new QName(SCA_11_NS, "SOAP");
    public static final QName SOAP11_INTENT = new QName(SCA_11_NS, "SOAP.1_1");
    public static final QName SOAP12_INTENT = new QName(SCA_11_NS, "SOAP.1_2");
    
    private boolean isSOAP12Required = false;
    private boolean isRampartRequired = false;
    private boolean isMTOMRequired = false;
    private boolean isJMSRequired = false;
    
    // JMS transport support
    private JMSSender jmsSender;
    private JMSListener jmsListener;
    
    //=========================================================
    // most of the following is related to rewriting WSDL imports 
    // I'd like to move this but don't know where to yet. 
    
    public static final String IMPORT_TAG = "import";
    public static final String INCLUDE_TAG = "include";


    public static final QName QNAME_WSA_ADDRESS =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.EPR_ADDRESS);
    public static final QName QNAME_WSA_FROM =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.WSA_FROM);
    public static final QName QNAME_WSA_REFERENCE_PARAMETERS =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.EPR_REFERENCE_PARAMETERS);

    private static final String DEFAULT_QUEUE_CONNECTION_FACTORY = "TuscanyQueueConnectionFactory";

    //Schema element names
    public static final String ELEM_SCHEMA = "schema";

    //Schema URI
    public static final String NS_URI_XSD_1999 = "http://www.w3.org/1999/XMLSchema";
    public static final String NS_URI_XSD_2000 = "http://www.w3.org/2000/10/XMLSchema";
    public static final String NS_URI_XSD_2001 = "http://www.w3.org/2001/XMLSchema";

    //Schema QNames
    public static final QName Q_ELEM_XSD_1999 = new QName(NS_URI_XSD_1999, ELEM_SCHEMA);
    public static final QName Q_ELEM_XSD_2000 = new QName(NS_URI_XSD_2000, ELEM_SCHEMA);
    public static final QName Q_ELEM_XSD_2001 = new QName(NS_URI_XSD_2001, ELEM_SCHEMA);
    public static final List<QName> XSD_QNAME_LIST =
        Arrays.asList(new QName[] {Q_ELEM_XSD_1999, Q_ELEM_XSD_2000, Q_ELEM_XSD_2001});
    
    //=========================================================    

    /**
     * Construct the service provider. This creates the base configuration for
     * Axis2 but with no services deployed yet. Services will be deployed when 
     * this service binding provider is started 
     * 
     * @param endpoint
     * @param component
     * @param contract
     * @param wsBinding
     * @param servletHost
     * @param messageFactory
     * @param modelFactories
     */
    public Axis2EngineIntegration(RuntimeEndpoint endpoint,
                                RuntimeComponent component,
                                AbstractContract contract,
                                WebServiceBinding wsBinding,
                                ServletHost servletHost,
                                MessageFactory messageFactory,
                                final FactoryExtensionPoint modelFactories) {
        this.endpoint = endpoint;
        this.component = component;
        this.contract = contract;
        this.wsBinding = wsBinding;
        this.servletHost = servletHost;
        this.messageFactory = messageFactory;
        this.modelFactories = modelFactories;
        this.assemblyFactory = (RuntimeAssemblyFactory)modelFactories.getFactory(AssemblyFactory.class);
        
        // TODO - taken the Tuscany configurator out for a while 
        //        but may need to re-introduce a simplified version if we feel 
        //        that it's important to not deploy rampart when it's not required
        
        // get the axis configuration context from the Tuscany axis2.xml file
        // Allow privileged access to read properties. Requires PropertyPermission read in
        // security policy.
        try {
            configContext = AccessController.doPrivileged(new PrivilegedExceptionAction<ConfigurationContext>() {
                public ConfigurationContext run() throws AxisFault, MalformedURLException {
                    // collect together the classloaders that Axis2 requireds in order to load
                    // pluggable items such as the Tuscany MessageReceivers and the xerces 
                    // document builder. 
                    ClassLoader wsBindingCL = getClass().getClassLoader();
                    ClassLoader axis2CL = URLBasedAxisConfigurator.class.getClassLoader();
                    ClassLoader xercesCL = DocumentBuilderFactoryImpl.class.getClassLoader();
                    ClassLoader wstxCL = WstxInputFactory.class.getClassLoader();
                    ClassLoader localtransportCL = LocalResponder.class.getClassLoader();
                    ClassLoader oldTCCL = ClassLoaderContext.setContextClassLoader(wsBindingCL, axis2CL, xercesCL, wstxCL, localtransportCL);
                    
                    try {
                        URL axis2xmlURL = wsBindingCL.getResource("org/apache/tuscany/sca/binding/ws/axis2/engine/conf/tuscany-axis2.xml");
                        if (axis2xmlURL != null){
                            URL repositoryURL = new URL(axis2xmlURL.toExternalForm().replaceFirst("conf/tuscany-axis2.xml", "repository/"));
                            return ConfigurationContextFactory.createConfigurationContextFromURIs(axis2xmlURL, repositoryURL);
                        } else {
                            return null;
                        }
                    } finally {
                        if (oldTCCL != null) {
                            Thread.currentThread().setContextClassLoader(oldTCCL);
                        }
                    }
                }
            });
        } catch (PrivilegedActionException e) {
            throw new ServiceRuntimeException(e.getException());
        }        
        
        // set the root context for this instance of Axis
        configContext.setContextRoot(servletHost.getContextPath());
        
        // Determine the configuration from the mayProvides intents
           
        isSOAP12Required = PolicyHelper.isIntentRequired(wsBinding, SOAP12_INTENT);
        
        // this is not correct as there may be other, custom, policies that 
        // require rampart. For example this is not going to pick up the case
        // of external policy attachment
        isRampartRequired = PolicyHelper.isIntentRequired(wsBinding, AUTHENTICATION_INTENT) ||
                            PolicyHelper.isIntentRequired(wsBinding, CONFIDENTIALITY_INTENT) ||
                            PolicyHelper.isIntentRequired(wsBinding, INTEGRITY_INTENT);          
            
        // Update port addresses with runtime information, and create a
        // map from endpoint URIs to WSDL ports that eliminates duplicate
        // ports for the same endpoint.
        // TODO - when do we think we are dealing with more than one port on the service side?
        //        is this an OSOA thing?
        for (Object port : wsBinding.getService().getPorts().values()) {
            String endpointURI = getPortAddress((Port)port);
            if (!endpointURI.startsWith("jms:")) {
                endpointURI = servletHost.getURLMapping(endpointURI).toString();
            } else {
                isJMSRequired = true;
            }
            setPortAddress((Port)port, endpointURI);
            urlMap.put(endpointURI, (Port)port);
        }            
        
        isMTOMRequired = PolicyHelper.isIntentRequired(wsBinding, MTOM_INTENT);
        
     
        // Apply the configuration from any other policies
        
        for (PolicyProvider pp : endpoint.getPolicyProviders()) {
            // we probably want to pass the whole provider in here
            // so that the policy providers can get at the rampart configuration
            pp.configureBinding(configContext);
        }
        
        // Apply the configuration from the mayProvides intents        
        
        if (isRampartRequired){
            // TODO - do we need to go back to configurator?
        }
        
        if (isJMSRequired){
            // TODO - do we need to o go back to configurator?
        }  
        
        if (isMTOMRequired) {
            new Axis2MTOMPolicyProvider(endpoint).configureBinding(configContext);
        }         
       
    }
      
    /**
     * Add the Tuscany services, that this binding instance represents, to the 
     * Axis runtime. 
     */
    public void start() {
        try {
            for (Map.Entry<String, Port> entry : urlMap.entrySet()) {
                AxisService axisService = createAxisService(entry.getKey(), entry.getValue());
                configContext.getAxisConfiguration().addService(axisService);
            }

            Axis2ServiceServlet servlet = null;
            for (String endpointURL : urlMap.keySet()) {
                if (endpointURL.startsWith("http://") || 
                    endpointURL.startsWith("https://") || 
                    endpointURL.startsWith("/")) {
                    if (servlet == null) {
                        servlet = new Axis2ServiceServlet();
                        servlet.init(configContext);
                    }
                    servletHost.addServletMapping(endpointURL, servlet);
                } else if (endpointURL.startsWith("jms")) {
                    logger.log(Level.INFO, "Axis2 JMS URL=" + endpointURL);

                    jmsListener = new JMSListener();
                    jmsSender = new JMSSender();
                    ListenerManager listenerManager = configContext.getListenerManager();
                    TransportInDescription trsIn =
                        configContext.getAxisConfiguration().getTransportIn(Constants.TRANSPORT_JMS);

                    // get JMS transport parameters from the computed URL
// not in Axis2 1.5.1                    
//                    Map<String, String> jmsProps = JMSUtils.getProperties(endpointURL);

                    // collect the parameters used to configure the JMS transport
                    OMFactory fac = OMAbstractFactory.getOMFactory();
                    OMElement parms = fac.createOMElement(DEFAULT_QUEUE_CONNECTION_FACTORY, null);
/*
                    for (String key : jmsProps.keySet()) {
                        OMElement param = fac.createOMElement("parameter", null);
                        param.addAttribute("name", key, null);
                        param.addChild(fac.createOMText(param, jmsProps.get(key)));
                        parms.addChild(param);
                    }
*/
                    Parameter queueConnectionFactory = new Parameter(DEFAULT_QUEUE_CONNECTION_FACTORY, parms);
                    trsIn.addParameter(queueConnectionFactory);

                    trsIn.setReceiver(jmsListener);

                    configContext.getAxisConfiguration().addTransportIn(trsIn);
                    TransportOutDescription trsOut =
                        configContext.getAxisConfiguration().getTransportOut(Constants.TRANSPORT_JMS);
                    //configContext.getAxisConfiguration().addTransportOut( trsOut );
                    trsOut.setSender(jmsSender);

                    if (listenerManager == null) {
                        listenerManager = new ListenerManager();
                        listenerManager.init(configContext);
                    }
                    listenerManager.addListener(trsIn, true);
                    jmsSender.init(configContext, trsOut);
                    jmsListener.init(configContext, trsIn);
                    jmsListener.start();
                }
            }
        } catch (AxisFault e) {
            throw new RuntimeException(e);
        }
/*
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
*/        
    }
   

    public void stop() {
        if (jmsListener != null) {
//            jmsListener.stop();
            jmsListener.destroy();
        } else {
            for (String endpointURL : urlMap.keySet()) {
                servletHost.removeServletMapping(endpointURL);
            }
        }
        
        servletHost = null;

        if (jmsSender != null)
            jmsSender.stop();

        try {
            for (String endpointURL : urlMap.keySet()) {
                // get the path to the service
                URI uriPath = new URI(endpointURL);
                String stringURIPath = uriPath.getPath();

                /* [nash] Need a leading slash for WSDL imports to work with ?wsdl
                // remove any "/" from the start of the path
                if (stringURIPath.startsWith("/")) {
                    stringURIPath = stringURIPath.substring(1, stringURIPath.length());
                }
                */

                configContext.getAxisConfiguration().removeService(stringURIPath);
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (AxisFault e) {
            throw new RuntimeException(e);
        }
    }

    private AxisService createAxisService(String endpointURL, Port port) throws AxisFault {
        AxisService axisService;
        if (wsBinding.getWSDLDocument() != null) {
            axisService = createWSDLAxisService(endpointURL, port);
        } else {
            axisService = createJavaAxisService(endpointURL);
        }
        initAxisOperations(axisService);
        return axisService;
    }

    /**
     * Create an AxisService from the Java interface class of the SCA service interface
     */
    protected AxisService createJavaAxisService(String endpointURL) throws AxisFault {
        
        AxisService axisService = new AxisService();
        String path = URI.create(endpointURL).getPath();
        axisService.setName(path);
        axisService.setServiceDescription("Tuscany configured AxisService for service: " + endpointURL);
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
    protected AxisService createWSDLAxisService(String endpointURL, Port port) throws AxisFault {

        Definition definition = wsBinding.getWSDLDocument();
        QName serviceQName = wsBinding.getService().getQName();
        Definition def = getDefinition(definition, serviceQName);

        final WSDLToAxisServiceBuilder builder = new WSDL11ToAxisServiceBuilder(def, serviceQName, port.getName());
        builder.setServerSide(true);
        // [rfeng] Add a custom resolver to work around WSCOMMONS-228
        // TODO - 228 is resolved, is this still required
        builder.setCustomResolver(new URIResolverImpl(def));
        builder.setBaseUri(def.getDocumentBaseURI());
        // [rfeng]
        // AxisService axisService = builder.populateService();
        // Allow privileged access to read properties. Requires PropertiesPermission read in
        // security policy.
        AxisService axisService;
        try {
            axisService = AccessController.doPrivileged(new PrivilegedExceptionAction<AxisService>() {
                public AxisService run() throws AxisFault {
                    return builder.populateService();
                }
            });
        } catch (PrivilegedActionException e) {
            throw (AxisFault)e.getException();
        }

        String name = URI.create(endpointURL).getPath();
        //[nash] HTTP endpoints need a leading slash for WSDL imports to work with ?wsdl
        if (endpointURL.startsWith("jms")) {
            name = name.startsWith("/") ? name.substring(1) : name;
        }
        axisService.setName(name);
        axisService.setEndpointURL(endpointURL);
        axisService.setDocumentation("Tuscany configured AxisService for service: " + endpointURL);
        
        // TODO - again, do we ever have more than one endpoint
        //        on the service side?
        for (Iterator i = axisService.getEndpoints().values().iterator(); i.hasNext();) {
            AxisEndpoint ae = (AxisEndpoint)i.next();
            if (endpointURL.startsWith("jms")) {
// not in Axis2 1.5.1
//                Parameter qcf = new Parameter(JMSConstants.CONFAC_PARAM, null);
//                qcf.setValue(DEFAULT_QUEUE_CONNECTION_FACTORY);
//                axisService.addParameter(qcf);
                break;
            }
        }

        // Add schema information to the AxisService (needed for "?xsd=" support)
        addSchemas(wsBinding.getWSDLDefinition(), axisService);

        // Use the existing WSDL
        Parameter wsdlParam = new Parameter("wsdl4jDefinition", null);
        wsdlParam.setValue(definition);
        axisService.addParameter(wsdlParam);
        Parameter userWSDL = new Parameter("useOriginalwsdl", "true");
        axisService.addParameter(userWSDL);

        // Modify schema imports and includes to add "servicename?xsd=" prefix.
        // Axis2 does this for schema extensibility elements, but Tuscany has
        // overriden the WSDl4J deserializer to create UnknownExtensibilityElement
        // elements in place of these.
        modifySchemaImportsAndIncludes(definition, name);

        // Axis2 1.3 has a bug with returning incorrect values for the port
        // addresses.  To work around this, compute the values here.
        Parameter modifyAddr = new Parameter("modifyUserWSDLPortAddress", "false");
        axisService.addParameter(modifyAddr);

        return axisService;
    }
    
    /**
     * Workaround for https://issues.apache.org/jira/browse/AXIS2-3205
     * @param definition
     * @param serviceName
     * @return
     */
    private static Definition getDefinition(Definition definition, QName serviceName) {

        if (serviceName == null) {
            return definition;
        }

        if (definition == null) {
            return null;
        }
        Object service = definition.getServices().get(serviceName);
        if (service != null) {
            return definition;
        }
        for (Object i : definition.getImports().values()) {
            List<Import> imports = (List<Import>)i;
            for (Import imp : imports) {
                Definition d = getDefinition(imp.getDefinition(), serviceName);
                if (d != null) {
                    return d;
                }
            }
        }
        return null;
    }    

    private void addSchemas(WSDLDefinition wsdlDef, AxisService axisService) {
        for (XSDefinition xsDef : wsdlDef.getXmlSchemas()) {
            if (xsDef.getSchema() != null) {
                axisService.addSchema(xsDef.getSchema());
                updateSchemaRefs(xsDef.getSchema(), axisService.getName());
            }
        }
        for (WSDLDefinition impDef : wsdlDef.getImportedDefinitions()) {
            addSchemas(impDef, axisService);
        }
    }

    private void updateSchemaRefs(XmlSchema parentSchema, String name) {
        for (Iterator iter = parentSchema.getIncludes().getIterator(); iter.hasNext();) {
            Object obj = iter.next();
            if (obj instanceof XmlSchemaExternal) {
                XmlSchemaExternal extSchema = (XmlSchemaExternal)obj;
                String location = extSchema.getSchemaLocation();
                if (location.length() > 0 && location.indexOf(":/") < 0 && location.indexOf("?xsd=") < 0) {
                    extSchema.setSchemaLocation(name + "?xsd=" + location);
                }
                if (extSchema.getSchema() != null) {
                    updateSchemaRefs(extSchema.getSchema(), name);
                }
            }
        }
    }

    private void modifySchemaImportsAndIncludes(Definition definition, String name) {
        // adjust the schema locations in types section
        Types types = definition.getTypes();
        if (types != null) {
            for (Iterator iter = types.getExtensibilityElements().iterator(); iter.hasNext();) {
                Object ext = iter.next();
                if (ext instanceof UnknownExtensibilityElement && XSD_QNAME_LIST
                    .contains(((UnknownExtensibilityElement)ext).getElementType())) {
                    changeLocations(((UnknownExtensibilityElement)ext).getElement(), name);
                }
            }
        }
        for (Iterator iter = definition.getImports().values().iterator(); iter.hasNext();) {
            Vector values = (Vector)iter.next();
            for (Iterator valuesIter = values.iterator(); valuesIter.hasNext();) {
                Import wsdlImport = (Import)valuesIter.next();
                modifySchemaImportsAndIncludes(wsdlImport.getDefinition(), name);
            }
        }
    }

    private void changeLocations(Element element, String name) {
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            String tagName = nodeList.item(i).getLocalName();
            if (IMPORT_TAG.equals(tagName) || INCLUDE_TAG.equals(tagName)) {
                processImport(nodeList.item(i), name);
            }
        }
    }

    private void processImport(Node importNode, String name) {
        NamedNodeMap nodeMap = importNode.getAttributes();
        for (int i = 0; i < nodeMap.getLength(); i++) {
            Node attribute = nodeMap.item(i);
            if (attribute.getNodeName().equals("schemaLocation")) {
                String location = attribute.getNodeValue();
                if (location.indexOf(":/") < 0 & location.indexOf("?xsd=") < 0) {
                    attribute.setNodeValue(name + "?xsd=" + location);
                }
            }
        }
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
                Axis2ServiceProvider serviceProvider = new Axis2ServiceProvider(endpoint, wsBinding, messageFactory, modelFactories);
                if (op.isNonBlocking()) {
                    msgrec = new Axis2ServiceInMessageReceiver(serviceProvider, op);
                } else {
                    msgrec = new Axis2ServiceInOutSyncMessageReceiver(serviceProvider, op);
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
    
    
    // Utility operations. Should these be available to the client also?
    
    static String getPortAddress(Port port) {
        Object ext = port.getExtensibilityElements().get(0);
        if (ext instanceof SOAPAddress) {
            return ((SOAPAddress)ext).getLocationURI();
        }
        if (ext instanceof SOAP12Address) {
            return ((SOAP12Address)ext).getLocationURI();
        }
        return null;
    }

    static void setPortAddress(Port port, String locationURI) {
        Object ext = port.getExtensibilityElements().get(0);
        if (ext instanceof SOAPAddress) {
            ((SOAPAddress)ext).setLocationURI(locationURI);
        }
        if (ext instanceof SOAP12Address) {
            ((SOAP12Address)ext).setLocationURI(locationURI);
        }
    }    
}
