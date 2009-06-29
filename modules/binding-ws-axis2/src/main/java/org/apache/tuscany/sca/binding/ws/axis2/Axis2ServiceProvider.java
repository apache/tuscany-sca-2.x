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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.Constants.Configuration;
import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
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
import org.apache.axis2.transport.jms.JMSConstants;
import org.apache.axis2.transport.jms.JMSListener;
import org.apache.axis2.transport.jms.JMSSender;
import org.apache.axis2.transport.jms.JMSUtils;
import org.apache.tuscany.sca.assembly.AbstractContract;
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.axis2.Axis2ServiceClient.URIResolverImpl;
import org.apache.tuscany.sca.binding.ws.axis2.policy.authentication.token.Axis2TokenAuthenticationPolicy;
import org.apache.tuscany.sca.binding.ws.axis2.policy.configuration.Axis2ConfigParamPolicy;
import org.apache.tuscany.sca.binding.ws.axis2.policy.configurator.Axis2BindingBasicAuthenticationConfigurator;
import org.apache.tuscany.sca.binding.ws.axis2.policy.configurator.Axis2BindingHeaderConfigurator;
import org.apache.tuscany.sca.binding.ws.axis2.policy.header.Axis2HeaderPolicy;
import org.apache.tuscany.sca.binding.ws.axis2.policy.header.Axis2SOAPHeaderString;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.assembly.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySubject;
import org.apache.tuscany.sca.policy.authentication.basic.BasicAuthenticationPolicy;
import org.apache.tuscany.sca.runtime.ReferenceParameters;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.apache.tuscany.sca.xsd.XSDefinition;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaExternal;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.handler.WSHandlerResult;
import org.oasisopen.sca.ServiceRuntimeException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Axis2ServiceProvider {
    public static final String IMPORT_TAG = "import";
    public static final String INCLUDE_TAG = "include";

    private static final Logger logger = Logger.getLogger(Axis2ServiceProvider.class.getName());

    private RuntimeComponent component;
    private AbstractContract contract;
    private WebServiceBinding wsBinding;
    private ServletHost servletHost;
    private MessageFactory messageFactory;
    private FactoryExtensionPoint modelFactories;
    private RuntimeAssemblyFactory assemblyFactory;
    private ConfigurationContext configContext;
    private JMSSender jmsSender;
    private JMSListener jmsListener;
    private Map<String, Port> urlMap = new HashMap<String, Port>();

    private BasicAuthenticationPolicy basicAuthenticationPolicy = null;
    private Axis2TokenAuthenticationPolicy axis2TokenAuthenticationPolicy = null;
    private List<Axis2HeaderPolicy> axis2HeaderPolicies = new ArrayList<Axis2HeaderPolicy>();

    public static final QName QNAME_WSA_ADDRESS =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.EPR_ADDRESS);
    public static final QName QNAME_WSA_FROM =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.WSA_FROM);
    public static final QName QNAME_WSA_REFERENCE_PARAMETERS =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.EPR_REFERENCE_PARAMETERS);

    private static final QName TRANSPORT_JMS_QUALIFIED_INTENT =
        new QName("http://docs.oasis-open.org/ns/opencsa/sca/200903", "transport.jms");
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

    /**
     * This classloader is used in OSGi to work around XXXFactory.newInstance()
     */
    private static class MultiParentClassLoader extends ClassLoader {
        private final Set<ClassLoader> classLoaders = new HashSet<ClassLoader>();

        /**
         * @param parent The parent classloaders
         * @param loaders A list of classloaders to be used to load classes or resources
         */
        public MultiParentClassLoader(ClassLoader parent, ClassLoader[] loaders) {
            super(parent);
            if (loaders != null) {
                for (ClassLoader cl : loaders) {
                    if (cl != null && cl != parent) {
                        this.classLoaders.add(cl);
                    }
                }
            }
        }

        @Override
        protected Class<?> findClass(String className) throws ClassNotFoundException {
            for (ClassLoader parent : classLoaders) {
                try {
                    return parent.loadClass(className);
                } catch (ClassNotFoundException e) {
                    continue;
                }
            }
            throw new ClassNotFoundException(className);
        }

        @Override
        protected URL findResource(String resName) {
            for (ClassLoader parent : classLoaders) {
                URL url = parent.getResource(resName);
                if (url != null) {
                    return url;
                }
            }
            return null;
        }

        @Override
        protected Enumeration<URL> findResources(String resName) throws IOException {
            Set<URL> urlSet = new HashSet<URL>();
            for (ClassLoader parent : classLoaders) {
                Enumeration<URL> urls = parent.getResources(resName);
                if (urls != null) {
                    while (urls.hasMoreElements()) {
                        urlSet.add(urls.nextElement());
                    }
                }
            }
            return Collections.enumeration(urlSet);
        }
    }

    public Axis2ServiceProvider(RuntimeComponent component,
                                AbstractContract contract,
                                WebServiceBinding wsBinding,
                                ServletHost servletHost,
                                MessageFactory messageFactory,
                                final FactoryExtensionPoint modelFactories) {

        this.component = component;
        this.contract = contract;
        this.wsBinding = wsBinding;
        this.servletHost = servletHost;
        this.messageFactory = messageFactory;
        this.modelFactories = modelFactories;
        this.assemblyFactory = (RuntimeAssemblyFactory)modelFactories.getFactory(AssemblyFactory.class);

        final boolean isRampartRequired = AxisPolicyHelper.isRampartRequired(wsBinding);
        try {
            // TuscanyAxisConfigurator tuscanyAxisConfigurator = new TuscanyAxisConfigurator();
            // Allow privileged access to read properties. Requires PropertyPermission read in
            // security policy.
            configContext = AccessController.doPrivileged(new PrivilegedExceptionAction<ConfigurationContext>() {
                public ConfigurationContext run() throws AxisFault {
                    ClassLoader cl1 = modelFactories.getFactory(XMLInputFactory.class).getClass().getClassLoader();
                    ClassLoader cl2 =
                        modelFactories.getFactory(DocumentBuilderFactory.class).getClass().getClassLoader();
                    ClassLoader tccl = Thread.currentThread().getContextClassLoader();
                    ClassLoader newTccl = tccl;
                    if (cl1 != tccl || cl2 != tccl) {
                        newTccl = new MultiParentClassLoader(null, new ClassLoader[] {cl1, cl2});
                    }
                    if (newTccl != null && newTccl != tccl) {
                        Thread.currentThread().setContextClassLoader(newTccl);
                    }
                    try {
                        return new TuscanyAxisConfigurator(isRampartRequired).getConfigurationContext();
                    } finally {
                        if (newTccl != null && newTccl != tccl) {
                            Thread.currentThread().setContextClassLoader(tccl);
                        }
                    }
                }
            });
            // deployRampartModule();
            // configureSecurity();
        } catch (PrivilegedActionException e) {
            throw new ServiceRuntimeException(e.getException());
        }

        configContext.setContextRoot(servletHost.getContextPath());

        // Enable MTOM if the policy intent is specified.
        if (AxisPolicyHelper.isIntentRequired(wsBinding, AxisPolicyHelper.MTOM_INTENT)) {
            configContext.getAxisConfiguration().getParameter(Configuration.ENABLE_MTOM).setLocked(false);
            configContext.getAxisConfiguration().getParameter(Configuration.ENABLE_MTOM).setValue("true");
        }

        // Update port addresses with runtime information, and create a
        // map from endpoint URIs to WSDL ports that eliminates duplicate
        // ports for the same endpoint.
        for (Object port : wsBinding.getService().getPorts().values()) {
            String portAddress = getPortAddress((Port)port);
            String endpointURI = computeEndpointURI(portAddress, servletHost);
            setPortAddress((Port)port, endpointURI);
            urlMap.put(endpointURI, (Port)port);
        }

        /*
        // find out which policies are active
        if (wsBinding instanceof PolicySubject) {
            List<PolicySet> policySets = ((PolicySubject)wsBinding).getApplicablePolicySets();
            for (PolicySet ps : policySets) {
                for (Object p : ps.getPolicies()) {
                    if (BasicAuthenticationPolicy.class.isInstance(p)) {
                        basicAuthenticationPolicy = (BasicAuthenticationPolicy)p;
                    } else if (Axis2TokenAuthenticationPolicy.class.isInstance(p)) {
                        axis2TokenAuthenticationPolicy = (Axis2TokenAuthenticationPolicy)p;
                    } else if (Axis2HeaderPolicy.class.isInstance(p)) {
                        axis2HeaderPolicies.add((Axis2HeaderPolicy)p);
                    } else {
                        // etc. check for other types of policy being present
                    }
                }
            }
        } 
        */
    }

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

    // TODO - EPR - what to do with URI
    /*
    private EndpointReference2 createEndpointReference() {
        FactoryExtensionPoint factories =
            component.getComponentContext().getExtensionPointRegistry().getExtensionPoint(FactoryExtensionPoint.class);
        RuntimeAssemblyFactory factory = (RuntimeAssemblyFactory)factories.getFactory(AssemblyFactory.class);
        return factory.createEndpointReference();
    }
    */

    private String computeEndpointURI(String uri, ServletHost servletHost) {

        if (uri == null) {
            return null;
        }

        // pull out the binding intents to see what sort of transport is required
        PolicySet transportJmsPolicySet = AxisPolicyHelper.getPolicySet(wsBinding, TRANSPORT_JMS_QUALIFIED_INTENT);
        if (transportJmsPolicySet != null) {
            if (!uri.startsWith("jms:/")) {
                uri = "jms:" + uri;
            }

            // construct the rest of the URI based on the policy. All the details are put
            // into the URI here rather than being place directly into the Axis configuration 
            // as the Axis JMS sender relies on parsing the target URI      
            Axis2ConfigParamPolicy axis2ConfigParamPolicy = null;
            for (Object policy : transportJmsPolicySet.getPolicies()) {
                if (policy instanceof Axis2ConfigParamPolicy) {
                    axis2ConfigParamPolicy = (Axis2ConfigParamPolicy)policy;
                    Iterator paramIterator =
                        axis2ConfigParamPolicy.getParamElements().get(DEFAULT_QUEUE_CONNECTION_FACTORY)
                            .getChildElements();

                    if (paramIterator.hasNext()) {
                        StringBuffer uriParams = new StringBuffer("?");

                        while (paramIterator.hasNext()) {
                            OMElement parameter = (OMElement)paramIterator.next();
                            uriParams.append(parameter.getAttributeValue(new QName("", "name")));
                            uriParams.append("=");
                            uriParams.append(parameter.getText());

                            if (paramIterator.hasNext()) {
                                uriParams.append("&");
                            }
                        }

                        uri = uri + uriParams;
                    }
                }
            }
        } else {
            if (!uri.startsWith("jms:")) {
                uri = servletHost.getURLMapping(uri).toString();
            }
        }

        return uri;
    }

    public void start() {

        try {
            createPolicyHandlers();
            for (Map.Entry<String, Port> entry : urlMap.entrySet()) {
                AxisService axisService = createAxisService(entry.getKey(), entry.getValue());
                configContext.getAxisConfiguration().addService(axisService);
            }

            Axis2ServiceServlet servlet = null;
            for (String endpointURL : urlMap.keySet()) {
                if (endpointURL.startsWith("http://") || endpointURL.startsWith("https://")
                    || endpointURL.startsWith("/")) {
                    if (servlet == null) {
                        servlet = new Axis2ServiceServlet();
                        servlet.init(configContext);
                    }
                    //[nash] configContext.setContextRoot(endpointURL);
                    servletHost.addServletMapping(endpointURL, servlet);
                } else if (endpointURL.startsWith("jms")) {
                    logger.log(Level.INFO, "Axis2 JMS URL=" + endpointURL);

                    jmsListener = new JMSListener();
                    jmsSender = new JMSSender();
                    ListenerManager listenerManager = configContext.getListenerManager();
                    TransportInDescription trsIn =
                        configContext.getAxisConfiguration().getTransportIn(Constants.TRANSPORT_JMS);

                    // get JMS transport parameters from the computed URL
                    Map<String, String> jmsProps = JMSUtils.getProperties(endpointURL);

                    // collect the parameters used to configure the JMS transport
                    OMFactory fac = OMAbstractFactory.getOMFactory();
                    OMElement parms = fac.createOMElement(DEFAULT_QUEUE_CONNECTION_FACTORY, null);

                    for (String key : jmsProps.keySet()) {
                        OMElement param = fac.createOMElement("parameter", null);
                        param.addAttribute("name", key, null);
                        param.addChild(fac.createOMText(param, jmsProps.get(key)));
                        parms.addChild(param);
                    }

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
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        if (jmsListener != null) {
            jmsListener.stop();
            jmsListener.destroy();
        } else {
            for (String endpointURL : urlMap.keySet()) {
                servletHost.removeServletMapping(endpointURL);
            }
        }

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
     * Create an AxisService from the interface class from the SCA service interface
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
        for (Iterator i = axisService.getEndpoints().values().iterator(); i.hasNext();) {
            AxisEndpoint ae = (AxisEndpoint)i.next();
            if (endpointURL.startsWith("jms")) {
                Parameter qcf = new Parameter(JMSConstants.CONFAC_PARAM, null);
                qcf.setValue(DEFAULT_QUEUE_CONNECTION_FACTORY);
                axisService.addParameter(qcf);
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

        // create a message object and set the args as its body
        Message msg = messageFactory.createMessage();
        msg.setBody(args);
        msg.setOperation(op);

        //FIXME: can we use the Axis2 addressing support for this?
        SOAPHeader header = inMC.getEnvelope().getHeader();
        if (header != null) {
            OMElement from = header.getFirstChildWithName(QNAME_WSA_FROM);
            if (from != null) {
                OMElement callbackAddrElement = from.getFirstChildWithName(QNAME_WSA_ADDRESS);
                if (callbackAddrElement != null) {
                    if (contract.getInterfaceContract().getCallbackInterface() != null) {
                        callbackAddress = callbackAddrElement.getText();
                    }
                }
                
/* TODO - EPR - not required by OASIS                
                OMElement params = from.getFirstChildWithName(QNAME_WSA_REFERENCE_PARAMETERS);
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
                        callbackID = callbackIDElement.getText();
                    }
                }
*/
            }

            // get policy specified headers
            for (Axis2HeaderPolicy policy : axis2HeaderPolicies) {
                //Axis2BindingHeaderConfigurator.getHeader(inMC, msg, policy.getHeaderName());
            }

            if (axis2TokenAuthenticationPolicy != null) {
                Axis2SOAPHeaderString tokenHeader = new Axis2SOAPHeaderString();
                Axis2BindingHeaderConfigurator.getHeader(inMC,
                                                         msg,
                                                         axis2TokenAuthenticationPolicy.getTokenName(),
                                                         tokenHeader);
            }
        }

        //fill message with QoS context info 
        fillQoSContext(msg, inMC);

        // Create a from EPR to hold the details of the callback endpoint
        EndpointReference from = null;
        if (callbackAddress != null ) {
            from = assemblyFactory.createEndpointReference();
            Endpoint fromEndpoint = assemblyFactory.createEndpoint();
            from.setTargetEndpoint(fromEndpoint);
            msg.setFrom(from);   
            Endpoint callbackEndpoint = assemblyFactory.createEndpoint();
            callbackEndpoint.setURI(callbackAddress);
            callbackEndpoint.setUnresolved(true);
            from.setCallbackEndpoint(callbackEndpoint);
        }

        /* TODO - EPR - not required in OASIS
        // set the reference parameters into the "From" EPR
        if (callbackAddress != null) {
            parameters.setCallbackReference(createEndpointReference(callbackAddress));
        }
        if (callbackID != null) {
            parameters.setCallbackID(callbackID);
        }
        if (conversationID != null) {
            parameters.setConversationID(conversationID);
        }

        if (basicAuthenticationPolicy != null) {
            Axis2BindingBasicAuthenticationConfigurator.parseHTTPHeader(inMC, msg, basicAuthenticationPolicy);
        }
        */

        // find the runtime wire and invoke it with the message
        RuntimeWire wire = ((RuntimeComponentService)contract).getRuntimeWire(getBinding());
        Object response = wire.invoke(op, msg);

        return response;
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

    private void createPolicyHandlers() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        if (wsBinding instanceof PolicySubject) {

            // code to create policy handlers using a policy SPI based
            // on policy providers
            /*            
                        List<PolicyProvider> policyProviders = ((RuntimeComponentService)contract).getPolicyProviders(wsBinding);
                        
                        for (PolicyProvider policyProvider : policyProviders){
                            policyHandler = policyProvider.createHandler();
                            if (policyHandler != null) {
                                policyHandlerList.add(policyHandler);
                            } 
                        }
            */
        }
    }

    private void fillQoSContext(Message message, MessageContext axis2MsgCtx) {
        if (axis2MsgCtx.getProperty(WSHandlerConstants.RECV_RESULTS) != null && axis2MsgCtx
            .getProperty(WSHandlerConstants.RECV_RESULTS) instanceof Vector) {
            Vector recvResults = (Vector)axis2MsgCtx.getProperty(WSHandlerConstants.RECV_RESULTS);
            for (int count1 = 0; count1 < recvResults.size(); ++count1) {
                if (recvResults.elementAt(count1) instanceof WSHandlerResult) {
                    WSHandlerResult wshr = (WSHandlerResult)recvResults.elementAt(count1);
                    Vector results = wshr.getResults();
                    for (int count2 = 0; count2 < results.size(); ++count2) {
                        if (results.elementAt(count2) instanceof WSSecurityEngineResult) {
                            WSSecurityEngineResult securityResult =
                                (WSSecurityEngineResult)wshr.getResults().elementAt(count2);
                            if (securityResult.get("principal") != null) {
                                message.getHeaders().add(securityResult.get("principal"));
                            }
                        }
                    }
                }
            }

        }
    }
}
