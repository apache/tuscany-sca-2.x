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
import java.net.URL;
import java.util.Iterator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Port;
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
import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Module;

/**
 * @version $Rev: 383148 $ $Date: 2006-03-04 08:07:17 -0800 (Sat, 04 Mar 2006) $
 */
public class WebServiceEntryPointServlet extends AxisServlet {

    private static final long serialVersionUID = -2085869393709833372L;
    private  boolean tuscanyGetDefaultAxis2xmlChecked= false;


  // private static final String CONFIGURATION_CONTEXT = "CONFIGURATION_CONTEXT";

  //  public static final String SESSION_ID = "SessionId";

   // private ConfigurationContext configContext;

    // private AxisConfiguration axisConfiguration;

    public void init(ServletConfig config) throws ServletException {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        ClassLoader mycl = getClass().getClassLoader();
        try {
            if (tccl != mycl) {
                Thread.currentThread().setContextClassLoader(mycl);
            }
            tuscanyGetDefaultAxis2xml(config);
            super.init(config);
            configContext = initConfigContext(config);
            initTuscany(configContext.getAxisConfiguration(), config);
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

            Context entryPointContext = moduleContext.getContext(epName);

            Binding binding = entryPoint.getBindings().get(0);
            if (binding instanceof WebServiceBinding) {

                WebServiceBinding wsBinding = (WebServiceBinding) binding;
                Definition definition = wsBinding.getWSDLDefinition();
                Port port = wsBinding.getWSDLPort();
                QName qname = new QName(definition.getTargetNamespace(), port.getName());

                WebServicePortMetaData wsdlPortInfo = new WebServicePortMetaData(definition, port, null,
                        false);

                WebServiceEntryPointInOutSyncMessageReceiver msgrec = new WebServiceEntryPointInOutSyncMessageReceiver(
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
                axisService.setWSDLDefinition(definition);
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
    @Override
    protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        ClassLoader mycl = getClass().getClassLoader();
        try {
            if (tccl != mycl) {
                Thread.currentThread().setContextClassLoader(mycl);
            }
            super.doGet(arg0, arg1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        } finally {
            if (tccl != mycl) {
                Thread.currentThread().setContextClassLoader(tccl);
            }
        }

    
    }
    @Override
    protected void doPost(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
        
        
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        ClassLoader mycl = getClass().getClassLoader();
        try {
            if (tccl != mycl) {
                Thread.currentThread().setContextClassLoader(mycl);
            }
            super.doPost(arg0, arg1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        } finally {
            if (tccl != mycl) {
                Thread.currentThread().setContextClassLoader(tccl);
            }
        }

    }
    
}
