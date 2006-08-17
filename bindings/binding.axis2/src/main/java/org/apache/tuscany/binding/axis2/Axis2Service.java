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

import java.lang.reflect.Method;

import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.WSDL11ToAxisServiceBuilder;
import org.apache.axis2.description.WSDLToAxisServiceBuilder;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.axis2.wsdl.WSDLConstants.WSDL20_2004Constants;
import org.apache.tuscany.binding.axis2.util.SDODataBinding;
import org.apache.tuscany.binding.axis2.util.WebServiceOperationMetaData;
import org.apache.tuscany.binding.axis2.util.WebServicePortMetaData;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ServiceExtension;
import org.apache.tuscany.spi.host.ServletHost;
import org.apache.tuscany.spi.wire.WireService;
import org.osoa.sca.annotations.Destroy;

import commonj.sdo.helper.TypeHelper;

/**
 * An implementation of a {@link ServiceExtension} configured with the Axis2 binding
 * 
 * @version $Rev$ $Date$
 */
public class Axis2Service<T> extends ServiceExtension<T> {

    private ServletHost servletHost;

    private ConfigurationContext configContext;

    private WebServiceBinding binding;
    
    private TypeHelper typeHelper;

    public Axis2Service(String theName, Class<T> interfaze, CompositeComponent parent, WireService wireService, WebServiceBinding binding,
            ServletHost servletHost, ConfigurationContext configContext, TypeHelper typeHelper) {

        super(theName, interfaze, parent, wireService);

        this.binding = binding;
        this.servletHost = servletHost;
        this.configContext = configContext;
        this.typeHelper = typeHelper;
    }

    public void start() {
        super.start();

        try {
            configContext.getAxisConfiguration().addService(createAxisService(binding));
        } catch (AxisFault e) {
            throw new RuntimeException(e);
        }

        Axis2ServiceServlet servlet = new Axis2ServiceServlet();
        servlet.init(configContext);
        servletHost.registerMapping("/" + getName(), servlet);
    }

    @Destroy
    public void stop() {
        servletHost.unregisterMapping("/" + getName());
        try {
            configContext.getAxisConfiguration().removeService(getName());
        } catch (AxisFault e) {
            throw new RuntimeException(e);
        }
        super.stop();
    }

    private AxisService createAxisService(WebServiceBinding wsBinding) throws AxisFault {
        Definition definition = wsBinding.getWSDLDefinition();
        WebServicePortMetaData wsdlPortInfo = new WebServicePortMetaData(definition, wsBinding.getWSDLPort(), null, false);

        // TODO investigate if this is 20 wsdl what todo?
        WSDLToAxisServiceBuilder builder = new WSDL11ToAxisServiceBuilder(definition, wsdlPortInfo.getServiceName(), wsdlPortInfo.getPort().getName());
        builder.setServerSide(true);
        AxisService axisService = builder.populateService();

        axisService.setName(this.getName());
        axisService.setServiceDescription("Tuscany configured AxisService for service: '" + this.getName() + '\'');

        // Use the existing WSDL
        Parameter wsdlParam = new Parameter(WSDLConstants.WSDL_4_J_DEFINITION, null);
        wsdlParam.setValue(definition);
        axisService.addParameter(wsdlParam);
        Parameter userWSDL = new Parameter("useOriginalwsdl", "true");
        axisService.addParameter(userWSDL );

        Class<?> serviceInterface = this.getInterface();

        PortType wsdlPortType = wsdlPortInfo.getPortType();
        for (Object o : wsdlPortType.getOperations()) {
            Operation wsdlOperation = (Operation) o;
            String operationName = wsdlOperation.getName();
            QName operationQN = new QName(definition.getTargetNamespace(), operationName);
            Object entryPointProxy = this.getServiceInstance();

            WebServiceOperationMetaData omd = wsdlPortInfo.getOperationMetaData(operationName);
            QName responseQN = omd.getOutputPart(0).getElementName();

            Method operationMethod = getMethod(serviceInterface, operationName);
            // outElementQName is not needed when calling fromOMElement method, and we can not get elementQName for
            // oneway operation.
            SDODataBinding dataBinding = new SDODataBinding(typeHelper, omd.isDocLitWrapped(), responseQN);
            Axis2ServiceInOutSyncMessageReceiver msgrec = new Axis2ServiceInOutSyncMessageReceiver(entryPointProxy, operationMethod,
                    dataBinding);

            AxisOperation axisOp = axisService.getOperation(operationQN);
            axisOp.setMessageExchangePattern(WSDL20_2004Constants.MEP_URI_IN_OUT);
            axisOp.setMessageReceiver(msgrec);
        }

        return axisService;
    }

    /**
     * Get the Method from an interface matching the WSDL operation name
     */
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

    TypeHelper getTypeHelper() {
        return typeHelper;
    }

}