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
import java.lang.reflect.Method;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.deployment.DeploymentConstants;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.AxisServiceGroup;
import org.apache.axis2.description.InOutAxisOperation;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.transport.http.AxisServlet;
import org.apache.tuscany.binding.axis2.assembly.WebServiceBinding;
import org.apache.tuscany.binding.axis2.util.ClassLoaderHelper;
import org.apache.tuscany.binding.axis2.util.DataBinding;
import org.apache.tuscany.binding.axis2.util.SDODataBinding;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.core.webapp.TuscanyServletListener;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Module;
import org.apache.wsdl.WSDLConstants;

import commonj.sdo.helper.TypeHelper;

/**
 * @version $Rev: 383148 $ $Date: 2006-03-04 08:07:17 -0800 (Sat, 04 Mar 2006) $
 */
public class WebServiceEntryPointServlet extends AxisServlet {

    private static final long serialVersionUID = 1L;

    private boolean tuscanyGetDefaultAxis2xmlChecked;

    public void init(final ServletConfig config) throws ServletException {
        ClassLoaderHelper.initApplicationClassLoader();
        try {
            ClassLoaderHelper.setSystemClassLoader();
            try {

                super.init(config);
                initTuscany(configContext.getAxisConfiguration(), config);

            } catch (Exception e) {
                throw new ServletException(e);
            }
        } finally {
            ClassLoaderHelper.setApplicationClassLoader();
        }
    }

    @SuppressWarnings("deprecation")
    private void initTuscany(AxisConfiguration axisConfig, ServletConfig config) throws AxisFault {

        ServletContext servletContext = config.getServletContext();
        CompositeContext moduleContext = (CompositeContext) servletContext.getAttribute(TuscanyServletListener.MODULE_COMPONENT_NAME);
        Module module = (Module) moduleContext.getComposite();

        for (EntryPoint entryPoint : module.getEntryPoints()) {
            for (Binding binding : entryPoint.getBindings()) {
                if (binding instanceof WebServiceBinding) {
                    String entryPointName = entryPoint.getName();
                    EntryPointContext entryPointContext = (EntryPointContext) moduleContext.getContext(entryPointName);
                    addAxisService(axisConfig, entryPointName, entryPointContext, (WebServiceBinding) binding);
                }
            }
        }
    }

    private void addAxisService(AxisConfiguration axisConfig, String entryPointName, EntryPointContext entryPointContext, WebServiceBinding wsBinding)
            throws AxisFault {

        // TODO: really require using WebServicePortMetaData/WebServiceOperationMetaData?
        Definition definition = wsBinding.getWSDLDefinition();
        WebServicePortMetaData wsdlPortInfo = new WebServicePortMetaData(definition, wsBinding.getWSDLPort(), null, false);

        AxisServiceGroup serviceGroup = new AxisServiceGroup(axisConfig);
        serviceGroup.setServiceGroupName(wsdlPortInfo.getServiceName().getLocalPart());
        axisConfig.addServiceGroup(serviceGroup);

        AxisService axisService = new AxisService(entryPointName);
        axisService.setParent(serviceGroup);
        axisService.setWSDLDefinition(definition);
        axisService.setServiceDescription("Tuscany configured service EntryPoint name '" + entryPointName + '\'');

        TypeHelper typeHelper = wsBinding.getTypeHelper();

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
            DataBinding dataBinding = new SDODataBinding(typeHelper, responseTypeQN);
            WebServiceEntryPointInOutSyncMessageReceiver msgrec = new WebServiceEntryPointInOutSyncMessageReceiver(entryPointProxy, operationMethod,
                    dataBinding);

            AxisOperation axisOp = new InOutAxisOperation(operationQN);
            axisOp.setMessageExchangePattern(WSDLConstants.MEP_URI_IN_OUT);
            axisOp.setMessageReceiver(msgrec);
            axisService.addOperation(axisOp);
        }

        axisConfig.addService(axisService);
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
        // TODO: throw which ex, or maybe just log and ignore unknown ops?
        throw new RuntimeException("no operation named " + operationName + " found on service interface: " + serviceInterface.getName());
    }

    @SuppressWarnings("deprecation")
    protected synchronized void tuscanyGetDefaultAxis2xml(ServletConfig config) throws IOException {
        if (tuscanyGetDefaultAxis2xmlChecked) {
            // already checked.
            return;
        }
        tuscanyGetDefaultAxis2xmlChecked = true;
        ServletContext context = config.getServletContext();
        String repoDir = context.getRealPath("/WEB-INF");
        String axis2config = repoDir + "/" + DeploymentConstants.DIRECTORY_CONF + "/" + DeploymentConstants.AXIS2_CONFIGURATION_XML;

        File axis2xmlFile = new File(axis2config);
        constructSubDirectories(axis2xmlFile.getParentFile());
        if (axis2xmlFile.exists()) {
            // do nothing if there.
            return;
        }

        URL url = getClass().getResource("/org/apache/tuscany/binding/axis2/engine/config/axis2.xml");
        InputStream defaultAxis2xml = url.openStream();
        try {
            FileOutputStream out = new FileOutputStream(axis2xmlFile);

            try {
                byte[] buff = new byte[1024];
                for (int len; (len = defaultAxis2xml.read(buff)) > 0;) {
                    out.write(buff, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            defaultAxis2xml.close();
        }
    }

    protected void constructSubDirectories(File in) {
        if (in.exists()) {
            return;
        }
        constructSubDirectories(in.getParentFile());
        in.mkdir();
    }

    @Override
    protected void doGet(final HttpServletRequest arg0, final HttpServletResponse arg1) throws ServletException, IOException {
        ClassLoaderHelper.initApplicationClassLoader();
        try {
            ClassLoaderHelper.setSystemClassLoader();
            super.doGet(arg0, arg1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        } finally {
            ClassLoaderHelper.setApplicationClassLoader();
        }
    }

    @Override
    protected void doPost(final HttpServletRequest arg0, final HttpServletResponse arg1) throws ServletException, IOException {
        ClassLoaderHelper.initApplicationClassLoader();
        try {
            ClassLoaderHelper.setSystemClassLoader();
            super.doPost(arg0, arg1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        } finally {
            ClassLoaderHelper.setApplicationClassLoader();
        }
    }
}
