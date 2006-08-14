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
package org.apache.tuscany.binding.axis2;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.WSDL11ToAxisServiceBuilder;
import org.apache.axis2.description.WSDLToAxisServiceBuilder;
import org.apache.axis2.transport.http.AxisServlet;
import org.apache.axis2.wsdl.WSDLConstants.WSDL20_2004Constants;
import org.apache.tuscany.binding.axis2.util.SDODataBinding;
import org.apache.tuscany.binding.axis2.util.WebServiceOperationMetaData;
import org.apache.tuscany.binding.axis2.util.WebServicePortMetaData;
import org.apache.tuscany.spi.builder.BuilderConfigException;

import commonj.sdo.helper.TypeHelper;

/**
 * @version $Rev$ $Date$
 */
public class WebServiceEntryPointServlet extends AxisServlet {

    private static final long serialVersionUID = 1L;

    private AxisService axisService;

    // TODO need to remove replace with ServletHost mechanism.
    public static Axis2Service axis2Service;

    public WebServiceEntryPointServlet() {
        System.err.println("Default constructor");
    };

    public WebServiceEntryPointServlet(AxisService axisService) {
        this.axisService = axisService;
    }

    public void init(final ServletConfig config) throws ServletException {
        try {
            try {
                axis2Service = Axis2Service.currentAxis2Service;
                super.init(config);
                axisService = createAxisService(axis2Service.getWsBinding());
                configContext.getAxisConfiguration().addService(axisService);
            } catch (Exception e) {
                throw new ServletException(e);
            }
        } finally {
        }
    }

    @Override
    protected void doGet(final HttpServletRequest arg0, final HttpServletResponse arg1) throws ServletException, IOException {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        ClassLoader scl = this.getClass().getClassLoader();
        try {
            if (tccl != scl) {
                Thread.currentThread().setContextClassLoader(scl);
            }

            try {
                super.doGet(arg0, arg1);
            } catch (Exception e) {
                e.printStackTrace();
                throw new ServletException(e);
            }

        } finally {
            if (tccl != scl) {
                Thread.currentThread().setContextClassLoader(tccl);
            }
        }
    }

    @Override
    protected void doPost(final HttpServletRequest arg0, final HttpServletResponse arg1) throws ServletException, IOException {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        ClassLoader scl = this.getClass().getClassLoader();
        try {
            if (tccl != scl) {
                Thread.currentThread().setContextClassLoader(scl);
            }

            try {
                super.doPost(arg0, arg1);
            } catch (Exception e) {
                e.printStackTrace();
                throw new ServletException(e);
            }

        } finally {
            if (tccl != scl) {
                Thread.currentThread().setContextClassLoader(tccl);
            }
        }
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

    private AxisService createAxisService(WebServiceBinding wsBinding) throws AxisFault {
        Definition definition = wsBinding.getWSDLDefinition();
        WebServicePortMetaData wsdlPortInfo = new WebServicePortMetaData(definition, wsBinding.getWSDLPort(), null, false);

        // AxisServiceGroup serviceGroup = new AxisServiceGroup(axisConfig);
        // serviceGroup.setServiceGroupName(wsdlPortInfo.getServiceName().getLocalPart());
        // axisConfig.addServiceGroup(serviceGroup);

        // TODO investigate if this is 20 wsdl what todo?
        WSDLToAxisServiceBuilder builder = new WSDL11ToAxisServiceBuilder(definition, wsdlPortInfo.getServiceName(), wsdlPortInfo.getPort().getName());
        builder.setServerSide(true);
        AxisService axisService = builder.populateService();

        axisService.setName(axis2Service.getName());
        // axisService.setParent(serviceGroup);
        axisService.setServiceDescription("Tuscany configured AxisService for Service: '" + axis2Service.getName() + '\'');

        // FIXME:
        // TypeHelper typeHelper = wsBinding.getTypeHelper();
        // ClassLoader cl = wsBinding.getResourceLoader().getClassLoader();
        TypeHelper typeHelper = null;
        ClassLoader cl = null;

        Class<?> serviceInterface = axis2Service.getInterface();

        PortType wsdlPortType = wsdlPortInfo.getPortType();
        for (Object o : wsdlPortType.getOperations()) {
            Operation wsdlOperation = (Operation) o;
            String operationName = wsdlOperation.getName();
            QName operationQN = new QName(definition.getTargetNamespace(), operationName);
            Object entryPointProxy = axis2Service.getServiceInstance();

            WebServiceOperationMetaData omd = wsdlPortInfo.getOperationMetaData(operationName);

            Method operationMethod = getMethod(serviceInterface, operationName);
            // outElementQName is not needed when calling fromOMElement method, and we can not get elementQName for
            // oneway operation.
            SDODataBinding dataBinding = new SDODataBinding(cl, typeHelper, omd.isDocLitWrapped(), null);
            WebServiceEntryPointInOutSyncMessageReceiver msgrec = new WebServiceEntryPointInOutSyncMessageReceiver(entryPointProxy, operationMethod,
                    dataBinding, cl);

            AxisOperation axisOp = axisService.getOperation(operationQN);
            axisOp.setMessageExchangePattern(WSDL20_2004Constants.MEP_URI_IN_OUT);
            axisOp.setMessageReceiver(msgrec);
        }

        return axisService;
    }

}
