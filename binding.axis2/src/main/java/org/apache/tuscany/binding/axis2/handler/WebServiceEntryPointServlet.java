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

package org.apache.tuscany.binding.axis2.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.SessionContext;
import org.apache.axis2.deployment.DeploymentConstants;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.AxisServiceGroup;
import org.apache.axis2.description.InOutAxisOperation;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.engine.AxisEngine;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HTTPTransportUtils;
import org.apache.axis2.transport.http.ListingAgent;
import org.apache.axis2.transport.http.ServletBasedOutTransportInfo;
import org.apache.tuscany.binding.axis2.assembly.WebServiceBinding;
import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Module;

/**
 * @version $Rev: 383148 $ $Date: 2006-03-04 08:07:17 -0800 (Sat, 04 Mar 2006) $
 */
public class WebServiceEntryPointServlet extends HttpServlet {

    private static final long serialVersionUID = -2085869393709833372L;

    private static final String CONFIGURATION_CONTEXT = "CONFIGURATION_CONTEXT";

    public static final String SESSION_ID = "SessionId";

    private ConfigurationContext configContext;

    private AxisConfiguration axisConfiguration;

    private ListingAgent lister;
    
    private  boolean tuscanyGetDefaultAxis2xmlChecked= false;

    private MessageContext createAndSetInitialParamsToMsgCtxt(SessionContext sessionContext, MessageContext msgContext, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) throws AxisFault {
        msgContext = new MessageContext();
        msgContext.setConfigurationContext(configContext);
        msgContext.setSessionContext(sessionContext);
        msgContext.setTransportIn(axisConfiguration.getTransportIn(new QName(Constants.TRANSPORT_HTTP)));
        msgContext.setTransportOut(axisConfiguration.getTransportOut(new QName(Constants.TRANSPORT_HTTP)));

        msgContext.setProperty(Constants.OUT_TRANSPORT_INFO, new ServletBasedOutTransportInfo(httpServletResponse));
        msgContext.setProperty(MessageContext.TRANSPORT_HEADERS, getTransportHeaders(httpServletRequest));
        msgContext.setProperty(SESSION_ID, httpServletRequest.getSession().getId());

        return msgContext;
    }

    public void destroy() {
        super.destroy();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MessageContext msgContext = null;
        OutputStream out = null;

        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        ClassLoader mycl = getClass().getClassLoader();
        try {
            if (tccl != mycl) {
                Thread.currentThread().setContextClassLoader(mycl);
            }

            SessionContext sessionContext = getSessionContext(request);
            Map<String, String> map = request.getParameterMap();

            msgContext = createAndSetInitialParamsToMsgCtxt(sessionContext, msgContext, response, request);
            msgContext.setDoingREST(true);
            msgContext.setServerSide(true);
            out = response.getOutputStream();

            boolean processed = HTTPTransportUtils.processHTTPGetRequest(
                    msgContext,
                    request.getInputStream(),
                    out,
                    request.getContentType(),
                    request.getHeader(HTTPConstants.HEADER_SOAP_ACTION),
                    request.getRequestURL().toString(),
                    configContext,
                    map);

            if (!processed) {
                try {
                    lister.handle(request, response, out);
                } catch (Exception e) {
                    throw new ServletException(e.getMessage(), e);
                }
            }
        } catch (AxisFault e) {
            if (msgContext != null) {
                handleFault(msgContext, out, e);
            } else {
                throw new ServletException(e);
            }
        } finally {
            if (tccl != mycl) {
                Thread.currentThread().setContextClassLoader(tccl);
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MessageContext msgContext = null;
        OutputStream out = null;

        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        ClassLoader mycl = getClass().getClassLoader();
        try {
            if (tccl != mycl) {
                Thread.currentThread().setContextClassLoader(mycl);
            }
            SessionContext sessionContext = getSessionContext(request);

            msgContext = createAndSetInitialParamsToMsgCtxt(sessionContext, msgContext, response, request);

            // adding ServletContext into msgContext;
            msgContext.setProperty(Constants.SERVLET_CONTEXT, sessionContext);
            out = response.getOutputStream();
            HTTPTransportUtils.processHTTPPostRequest(
                    msgContext,
                    request.getInputStream(),
                    out,
                    request.getContentType(),
                    request.getHeader(HTTPConstants.HEADER_SOAP_ACTION),
                    request.getRequestURL().toString());

            Object contextWritten = msgContext.getOperationContext().getProperty(Constants.RESPONSE_WRITTEN);

            response.setContentType("text/xml; charset=" + msgContext.getProperty(MessageContext.CHARACTER_SET_ENCODING));

            if ((contextWritten == null) || !Constants.VALUE_TRUE.equals(contextWritten)) {
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
            }
        } catch (AxisFault e) {
            if (msgContext != null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                handleFault(msgContext, out, e);
            } else {
                throw new ServletException(e);
            }
        } finally {
            if (tccl != mycl) {
                Thread.currentThread().setContextClassLoader(tccl);
            }
        }
    }

    private void handleFault(MessageContext msgContext, OutputStream out, AxisFault e) throws AxisFault {
        msgContext.setProperty(MessageContext.TRANSPORT_OUT, out);

        AxisEngine engine = new AxisEngine(configContext);
        MessageContext faultContext = engine.createFaultMessageContext(msgContext, e);

        engine.sendFault(faultContext);
    }

    public void init(ServletConfig config) throws ServletException {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        ClassLoader mycl = getClass().getClassLoader();
        try {
            if (tccl != mycl) {
                Thread.currentThread().setContextClassLoader(mycl);
            }
            tuscanyGetDefaultAxis2xml(config);
            configContext = initConfigContext(config);
            initTuscany(configContext.getAxisConfiguration(), config);
            lister = new ListingAgent(configContext);
            axisConfiguration = configContext.getAxisConfiguration();
            config.getServletContext().setAttribute(CONFIGURATION_CONTEXT, configContext);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        } finally {
            if (tccl != mycl) {
                Thread.currentThread().setContextClassLoader(tccl);
            }
        }
    }

    private void initTuscany(final AxisConfiguration axisConfig, ServletConfig config) throws AxisFault, ServletException {

        // Get the current SCA module context

        ServletContext servletContext = config.getServletContext();
        
        AggregateContext moduleContext = (AggregateContext) servletContext.getAttribute("org.apache.tuscany.core.webapp.ModuleComponentContext");
        Module module = (Module) moduleContext.getAggregate();
       

        for (Iterator<EntryPoint> i = module.getEntryPoints().iterator(); i.hasNext();) {
            EntryPoint entryPoint = i.next();
            final String epName = entryPoint.getName();

            InstanceContext entryPointContext = moduleContext.getContext(epName);

            Binding binding = entryPoint.getBindings().get(0);
            if (binding instanceof WebServiceBinding) {

                WebServiceBinding wsBinding = (WebServiceBinding) binding;
                Definition definition = wsBinding.getWSDLDefinition();
                Port port = wsBinding.getWSDLPort();
                QName qname = new QName(definition.getTargetNamespace(), port.getName());

                WebServicePortMetaData wsdlPortInfo = new WebServicePortMetaData(definition, port, null,
                        false);

                WebServiceEntryPointInOutSyncMessageReceiver msgrec = new WebServiceEntryPointInOutSyncMessageReceiver(
                        moduleContext,
                        entryPoint,
                        (EntryPointContext) entryPointContext,
                        wsdlPortInfo);

                AxisServiceGroup serviceGroup = new AxisServiceGroup(axisConfig);
                axisConfig.addMessageReceiver(WebServiceEntryPointInOutSyncMessageReceiver.MEP_URL, msgrec);
                serviceGroup.setServiceGroupName(wsdlPortInfo.getServiceName().getLocalPart());

                // to create service from wsdl stream --->
                // AxisServiceBuilder axisServiceBuilder = new AxisServiceBuilder();
                // return axisServiceBuilder.getAxisService(in);

                AxisService axisService = new AxisService(epName);
                axisService.setParent(serviceGroup);
                axisService.setServiceDescription("Tuscany configured service EntryPoint name '" + epName + '\'');
                axisService.addMessageReceiver(WebServiceEntryPointInOutSyncMessageReceiver.MEP_URL, msgrec);

                // Create operation descriptions for all the operations
                PortType wsdlPortType = wsdlPortInfo.getPortType();
                for (Iterator<Operation> j = wsdlPortType.getOperations().iterator(); j.hasNext();) {
                    Operation wsdlOperation = j.next();
                    String operationName = wsdlOperation.getName();
                    QName name = new QName(qname.getNamespaceURI(), operationName);
                    AxisOperation axisOp = new InOutAxisOperation(name);
                    axisOp.setMessageReceiver(msgrec);
                    axisService.addOperation(axisOp);
                    axisOp.setMessageExchangePattern(WebServiceEntryPointInOutSyncMessageReceiver.MEP_URL);

                    axisConfig.addService(axisService);

                }
                axisConfig.addServiceGroup(serviceGroup);
            }
        }
    }
    
    protected synchronized void tuscanyGetDefaultAxis2xml( ServletConfig config) throws ServletException {

        if (tuscanyGetDefaultAxis2xmlChecked)
            return; // already checked.
        tuscanyGetDefaultAxis2xmlChecked = true;
        ServletContext context = config.getServletContext();
        String repoDir = context.getRealPath("/WEB-INF");
        String axis2config = repoDir + "/" + DeploymentConstants.AXIS2_CONFIGURATION_XML;
        File axis2xmlFile = new File(axis2config);
        if (axis2xmlFile.exists())
            return; // do nothing if there.
        
        AggregateContext moduleContext = (AggregateContext) config.getServletContext().getAttribute("org.apache.tuscany.core.webapp.ModuleComponentContext");
        Module module = (Module) moduleContext.getAggregate();


        ResourceLoader resourceLoader = new ResourceLoaderImpl(module.getClass().getClassLoader());

        try {

            URL url = resourceLoader.getResource("org/apache/tuscany/binding/axis2/engine/config/axis2.xml");
            InputStream defaultAxis2xml = url.openStream();
            FileOutputStream out = new FileOutputStream(axis2xmlFile);

            byte[] buff = new byte[1024];
            for (int len = -1; (len = defaultAxis2xml.read(buff)) > 0;) {
                out.write(buff, 0, len);
            }

            defaultAxis2xml.close();
            out.close();

        } catch (IOException e1) {
            throw new ServletException(e1);
        }

    }

    /**
     * Initialize the Axis configuration context
     * 
     * @param config
     *            Servlet configuration
     * @throws AxisFault
     *             if there was a problem configuring the Axis engine
     */
    protected ConfigurationContext initConfigContext(ServletConfig config) throws AxisFault {
        ServletContext context = config.getServletContext();
        String repoDir = context.getRealPath("/WEB-INF");
        ConfigurationContextFactory erfac = new ConfigurationContextFactory();
        ConfigurationContext configContext = erfac.createConfigurationContextFromFileSystem(repoDir);
        configContext.setProperty(Constants.CONTAINER_MANAGED, Constants.VALUE_TRUE);
        configContext.setRootDir(new File(context.getRealPath("/WEB-INF")));
        return configContext;
    }

    private static SessionContext getSessionContext(HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession(true);
        SessionContext sessionContext = (SessionContext) httpSession.getAttribute(Constants.SESSION_CONTEXT_PROPERTY);

        if (sessionContext == null) {
            sessionContext = new SessionContext(null);
            httpSession.setAttribute(Constants.SESSION_CONTEXT_PROPERTY, sessionContext);
        }

        return sessionContext;
    }

    private static Map getTransportHeaders(HttpServletRequest req) {
        HashMap<String, String> headerMap = new HashMap<String, String>();
        Enumeration<String> headerNames = req.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = req.getHeader(key);
            headerMap.put(key, value);
        }

        return headerMap;
    }
}
