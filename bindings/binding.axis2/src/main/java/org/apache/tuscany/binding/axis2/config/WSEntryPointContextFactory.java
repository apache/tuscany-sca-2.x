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
package org.apache.tuscany.binding.axis2.config;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.WSDL2AxisServiceBuilder;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.tuscany.binding.axis2.assembly.WebServiceBinding;
import org.apache.tuscany.binding.axis2.entrypoint.WebServiceEntryPointInOutSyncMessageReceiver;
import org.apache.tuscany.binding.axis2.entrypoint.WebServiceEntryPointServlet;
import org.apache.tuscany.binding.axis2.util.SDODataBinding;
import org.apache.tuscany.binding.axis2.util.WebServiceOperationMetaData;
import org.apache.tuscany.binding.axis2.util.WebServicePortMetaData;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.ContextCreationException;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.core.extension.EntryPointContextFactory;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.webapp.ServletHost;

import commonj.sdo.helper.TypeHelper;

/**
 * Creates instances of {@link org.apache.tuscany.core.context.EntryPointContext} configured with the appropriate wire chains and bindings. This
 * implementation serves as a marker for
 * 
 * @version $Rev$ $Date$
 */
public class WSEntryPointContextFactory extends EntryPointContextFactory {

    private ServletHost tomcatHost;

    private WebServiceBinding wsBinding;

    public WSEntryPointContextFactory(String name, MessageFactory messageFactory, ServletHost tomcatHost, WebServiceBinding wsBinding) {
        super(name, messageFactory);
        this.tomcatHost = tomcatHost;
        this.wsBinding = wsBinding;
    }

    public EntryPointContext createContext() throws ContextCreationException {
        EntryPointContext epc = super.createContext();
        initServlet(epc);
        return epc;
    }

    private void initServlet(EntryPointContext epc) {
        AxisService axisService;
        try {
            axisService = createAxisService(epc, wsBinding);
        } catch (AxisFault e) {
            throw new BuilderConfigException(e);
        }
        WebServiceEntryPointServlet servlet = new WebServiceEntryPointServlet(axisService);
        ServletConfig sc = createDummyServletConfig();
        try {
            servlet.init(sc );
        } catch (ServletException e) {
            throw new BuilderConfigException(e);
        }

        String servletMapping = wsBinding.getWebAppName() + "/services/" + epc.getName();
        tomcatHost.registerMapping(servletMapping, servlet);
    }

    private ServletConfig createDummyServletConfig() {
        ServletConfig sc = new ServletConfig() {

            public String getServletName() {
                return "TuscanyWSServlet";
            }

            public ServletContext getServletContext() {
                return new ServletContext() {

                    public ServletContext getContext(String arg0) {
                        return null;
                    }

                    public int getMajorVersion() {
                        return 0;
                    }

                    public int getMinorVersion() {
                        return 0;
                    }

                    public String getMimeType(String arg0) {
                        return null;
                    }

                    public Set getResourcePaths(String arg0) {
                        return null;
                    }

                    public URL getResource(String arg0) throws MalformedURLException {
                        return null;
                    }

                    public InputStream getResourceAsStream(String arg0) {
                        return null;
                    }

                    public RequestDispatcher getRequestDispatcher(String arg0) {
                        return null;
                    }

                    public RequestDispatcher getNamedDispatcher(String arg0) {
                        return null;
                    }

                    public Servlet getServlet(String arg0) throws ServletException {
                        return null;
                    }

                    public Enumeration getServlets() {
                        return null;
                    }

                    public Enumeration getServletNames() {
                        return null;
                    }

                    public void log(String arg0) {
                    }

                    public void log(Exception arg0, String arg1) {
                    }

                    public void log(String arg0, Throwable arg1) {
                    }

                    public String getRealPath(String arg0) {
                        return null;
                    }

                    public String getServerInfo() {
                        return null;
                    }

                    public String getInitParameter(String arg0) {
                        return null;
                    }

                    public Enumeration getInitParameterNames() {
                        return null;
                    }

                    public Object getAttribute(String arg0) {
                        return null;
                    }

                    public Enumeration getAttributeNames() {
                        return null;
                    }

                    public void setAttribute(String arg0, Object arg1) {
                    }

                    public void removeAttribute(String arg0) {
                    }

                    public String getServletContextName() {
                        return null;
                    }};
            }

            public String getInitParameter(String arg0) {
                return null;
            }

            public Enumeration getInitParameterNames() {
                return new Vector().elements();
            }};
        return sc;
    }

    private AxisService createAxisService(EntryPointContext entryPointContext, WebServiceBinding wsBinding) throws AxisFault {

        String entryPointName = entryPointContext.getName();

        Definition definition = wsBinding.getWSDLDefinition();
        WebServicePortMetaData wsdlPortInfo = new WebServicePortMetaData(definition, wsBinding.getWSDLPort(), null, false);

        // AxisServiceGroup serviceGroup = new AxisServiceGroup(axisConfig);
        // serviceGroup.setServiceGroupName(wsdlPortInfo.getServiceName().getLocalPart());
        // axisConfig.addServiceGroup(serviceGroup);

        WSDL2AxisServiceBuilder builder = new WSDL2AxisServiceBuilder(definition, wsdlPortInfo.getServiceName(), wsdlPortInfo.getPort().getName());
        builder.setServerSide(true);
        AxisService axisService = builder.populateService();

        axisService.setName(entryPointName);
        // axisService.setParent(serviceGroup);
        axisService.setServiceDescription("Tuscany configured AxisService for EntryPoint: '" + entryPointName + '\'');

        TypeHelper typeHelper = wsBinding.getTypeHelper();
        ClassLoader cl = wsBinding.getResourceLoader().getClassLoader();

        Class<?> serviceInterface = entryPointContext.getServiceInterface();

        PortType wsdlPortType = wsdlPortInfo.getPortType();
        for (Object o : wsdlPortType.getOperations()) {
            Operation wsdlOperation = (Operation) o;
            String operationName = wsdlOperation.getName();
            QName operationQN = new QName(definition.getTargetNamespace(), operationName);
            Object entryPointProxy = entryPointContext.getInstance(null);

            WebServiceOperationMetaData omd = wsdlPortInfo.getOperationMetaData(operationName);
            QName responseTypeQN = omd.getOutputPart(0).getElementName();

            Method operationMethod = getMethod(serviceInterface, operationName);
            SDODataBinding dataBinding = new SDODataBinding(cl, typeHelper, responseTypeQN, omd.isDocLitWrapped());
            WebServiceEntryPointInOutSyncMessageReceiver msgrec = new WebServiceEntryPointInOutSyncMessageReceiver(entryPointProxy, operationMethod,
                    dataBinding, cl);

            AxisOperation axisOp = axisService.getOperation(operationQN);
            axisOp.setMessageExchangePattern(WSDLConstants.MEP_URI_IN_OUT);
            axisOp.setMessageReceiver(msgrec);
        }

        return axisService;
    }

    protected Method getMethod(Class<?> serviceInterface, String operationName) {
        // Note: this doesn't support overloaded operations
        Method[] methods = serviceInterface.getMethods();
        for (Method m : methods) {
            if (m.getName().equals(operationName)) {
                return m;
            }
            // tolerate WSDL with capatalized operation name
            StringBuilder sb = new StringBuilder(operationName);
            sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
            if (m.getName().equals(sb.toString())) {
                return m;
            }
        }
        throw new BuilderConfigException("no operation named " + operationName + " found on service interface: " + serviceInterface.getName());
    }

}
