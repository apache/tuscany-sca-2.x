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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.attachments.utils.IOUtils;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.deployment.DeploymentConstants;
import org.apache.axis2.description.AxisDescription;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.PolicyInclude;
import org.apache.axis2.transport.http.ListingAgent;
import org.apache.axis2.util.ExternalPolicySerializer;
import org.apache.axis2.util.JavaUtils;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyRegistry;
import org.apache.ws.commons.schema.XmlSchema;

/**
 * A Tuscany specific Axis2 ListingAgent as the Axis2 one does not work
 * with the Tuscany service names which include slash ('/') characters.
 * Unfortunately it ends up having  to copy a fair amount of Axis2 code to do this. 
 *
 * @version $Rev$ $Date$
 */
public class TuscanyListingAgent extends ListingAgent {

    private static final String LIST_SINGLE_SERVICE_JSP_NAME =
        "listSingleService.jsp";
    
    public TuscanyListingAgent(ConfigurationContext aConfigContext) {
        super(aConfigContext);
    }

    /**
     * This method overrides the Axis2 listing agent's computation of the
     * service name.
     */
    @Override
    public String extractServiceName(String urlString) {
        String serviceName = findAxisServiceName(urlString);
        setContextRoot(urlString, serviceName);
        return serviceName;
    }

    private String findAxisServiceName(String path) {
        HashMap services = configContext.getAxisConfiguration().getServices();
        if (services == null) {
            return null;
        }
        String[] parts = JavaUtils.split(path, '/');
        String serviceName = "";
        for (int i=parts.length-1; i>=0; i--) {
            serviceName = parts[i] + serviceName;
            if (services.containsKey(serviceName)) {
                return serviceName;
            }
            serviceName = "/" + serviceName;
            if (services.containsKey(serviceName)) {
                return serviceName;
            }
        }

        return null;
    }

    /**
     * Hack for Tuscany to get ?wsdl working with Tuscany service names
     * Can go once moved up to Axis2 1.3
     */
    private void setContextRoot(String filePart, String serviceName) {
        String contextRoot = configContext.getContextRoot();
        if (contextRoot != null && contextRoot.length() > 0) {
            if (contextRoot.equals("/")) {
                configContext.setServicePath("/");
            } else {
                int i = filePart.indexOf(contextRoot) + contextRoot.length();
                int j = filePart.lastIndexOf(serviceName);
                if (i>=j || (i+1 == j)) {
                    configContext.setServicePath("/");
                } else {
                    String mapping = filePart.substring(i+1, j);
                    configContext.setServicePath(mapping);
                }
            }
            configContext.setContextRoot(contextRoot);
        }
    }

    private String extractHostAndPort(String filePart, boolean isHttp) {
        int ipindex = filePart.indexOf("//");
        String ip = null;
        if (ipindex >= 0) {
            ip = filePart.substring(ipindex + 2, filePart.length());
            int seperatorIndex = ip.indexOf(":");
            int slashIndex = ip.indexOf("/");
            String port;
            if (seperatorIndex >= 0) {
                port = ip.substring(seperatorIndex + 1, slashIndex);
                ip = ip.substring(0, seperatorIndex);
            } else {
                ip = ip.substring(0, slashIndex);
                port = "80";
            }
            if (isHttp) {
                configContext.setProperty(RUNNING_PORT, port);
            }
        }
        return ip;
    }

    private Policy findPolicy(String id, AxisDescription des) {

        List policyElements = des.getPolicyInclude().getPolicyElements();
        PolicyRegistry registry = des.getPolicyInclude().getPolicyRegistry();

        Object policyComponent;

        Policy policy = registry.lookup(id);

        if (policy != null) {
            return policy;
        }

        for (Iterator iterator = policyElements.iterator(); iterator.hasNext();) {
            policyComponent = iterator.next();

            if (policyComponent instanceof Policy) {
                // policy found for the id

                if (id.equals(((Policy) policyComponent).getId())) {
                    return (Policy) policyComponent;
                }
            }
        }

        AxisDescription child;

        for (Iterator iterator = des.getChildren(); iterator.hasNext();) {
            child = (AxisDescription) iterator.next();
            policy = findPolicy(id, child);

            if (policy != null) {
                return policy;
            }
        }

        return null;
    }

    /**
     * Hack to get ?wsdl working with soap 1.2 
     * Fixed in Axis2 1.3
     */
    private void patchSOAP12Port(AxisService as) throws AxisFault {
        Parameter wsld4jdefinition = as.getParameter(WSDLConstants.WSDL_4_J_DEFINITION);
        Definition definition = (Definition) wsld4jdefinition.getValue();
        setPortAddress(definition, null, as);
    }

    /**
     * This is a copy of the AxisService setPortAddress patched to work with SOAP 1.2 Addresses
     * Fixed in Axis2 1.3
     */
    private void setPortAddress(Definition definition, String requestIP, AxisService axisService) throws AxisFault {
        Iterator serviceItr = definition.getServices().values().iterator();
        while (serviceItr.hasNext()) {
            Service serviceElement = (Service) serviceItr.next();
            Iterator portItr = serviceElement.getPorts().values().iterator();
            while (portItr.hasNext()) {
                Port port = (Port) portItr.next();
                List list = port.getExtensibilityElements();
                for (int i = 0; i < list.size(); i++) {
                    Object extensibilityEle = list.get(i);
                    String locationURI = null;
                    if (requestIP == null) {
                        locationURI = axisService.getEPRs()[0];
                    } else {
// can't do this as the method's not visible, but Tuscany doesn't use this path anyway
//                         locationURI = axisService.getEPRs(requestIP)[0]);
                    }
                    if (extensibilityEle instanceof SOAPAddress) {
                        ((SOAPAddress) extensibilityEle).setLocationURI(locationURI);
                    } else if (extensibilityEle instanceof SOAP12Address) {
                        ((SOAP12Address) extensibilityEle).setLocationURI(locationURI);
                    }
                }
            }
        }
    }
    
}
