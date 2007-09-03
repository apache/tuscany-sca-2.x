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
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.attachments.utils.IOUtils;
import org.apache.axis2.Constants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.deployment.DeploymentConstants;
import org.apache.axis2.description.AxisDescription;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.PolicyInclude;
import org.apache.axis2.transport.http.ListingAgent;
import org.apache.axis2.util.ExternalPolicySerializer;
import org.apache.axis2.util.JavaUtils;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyRegistry;
import org.apache.ws.commons.schema.XmlSchema;

/**
 * A Tuscany specific Axis2 ListingAgent as the Axis2 one does not work
 * with the Tuscany sevice names which include slash ('/') characters.
 * Unfortunately it ends up having  to copy a fair amount of Axis2 code to do this. 
 */
public class TuscanyListingAgent extends ListingAgent {

    private static final String LIST_SINGLE_SERVICE_JSP_NAME =
        "listSingleService.jsp";
    
    public TuscanyListingAgent(ConfigurationContext aConfigContext) {
        super(aConfigContext);
    }

    protected String findAxisServiceName(String path) {
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
    
    @Override
    public void processListService(HttpServletRequest req,
                                   HttpServletResponse res)
            throws IOException, ServletException {


        String filePart = req.getRequestURL().toString();
//        String serviceName = filePart.substring(filePart.lastIndexOf("/") + 1,
//                                                filePart.length());
// Change the Axis2 code so as to use the complete ServletPath as the service name
// this line is the only change to to Axis2 code

        String serviceName = findAxisServiceName(filePart);
        setContextRoot(filePart, serviceName);

        String query = req.getQueryString();
        int wsdl2 = query.indexOf("wsdl2");
        int wsdl = query.indexOf("wsdl");
        int xsd = query.indexOf("xsd");
        int policy = query.indexOf("policy");

        HashMap services = configContext.getAxisConfiguration().getServices();
        if ((services != null) && !services.isEmpty()) {
            Object serviceObj = services.get(serviceName);
            if (serviceObj != null) {
                boolean isHttp = "http".equals(req.getScheme());
                if (wsdl2 >= 0) {
                    OutputStream out = res.getOutputStream();
                    res.setContentType("text/xml");
                    String ip = extractHostAndPort(filePart, isHttp);
                    ((AxisService) serviceObj)
                            .printWSDL2(out, ip, configContext.getServiceContextPath());
                    out.flush();
                    out.close();
                    return;
                } else if (wsdl >= 0) {
                    OutputStream out = res.getOutputStream();
                    res.setContentType("text/xml");
                    String ip = extractHostAndPort(filePart, isHttp);
                    ((AxisService) serviceObj).printWSDL(out, ip, configContext.getServicePath());
                    out.flush();
                    out.close();
                    return;
                } else if (xsd >= 0) {
                    OutputStream out = res.getOutputStream();
                    res.setContentType("text/xml");
                    AxisService axisService = (AxisService) serviceObj;
                    //call the populator
                    axisService.populateSchemaMappings();
                    Map schemaMappingtable =
                            axisService.getSchemaMappingTable();
                    ArrayList schemas = axisService.getSchema();

                    //a name is present - try to pump the requested schema
                    String xsds = req.getParameter("xsd");
                    if (!"".equals(xsds)) {
                        XmlSchema schema =
                                (XmlSchema) schemaMappingtable.get(xsds);
                        if (schema != null) {
                            //schema is there - pump it outs
                            schema.write(new OutputStreamWriter(out, "UTF8"));
                            out.flush();
                            out.close();
                        } else {
                            InputStream in = axisService.getClassLoader()
                                    .getResourceAsStream(DeploymentConstants.META_INF + "/" + xsds);
                            if (in != null) {
                                out.write(IOUtils.getStreamAsByteArray(in));
                                out.flush();
                                out.close();
                            } else {
                                res.sendError(HttpServletResponse.SC_NOT_FOUND);
                            }
                        }

                        //multiple schemas are present and the user specified
                        //no name - in this case we cannot possibly pump a schema
                        //so redirect to the service root
                    } else if (schemas.size() > 1) {
                        res.sendRedirect("");
                        //user specified no name and there is only one schema
                        //so pump that out
                    } else {
                        XmlSchema schema = axisService.getSchema(0);
                        if (schema != null) {
                            schema.write(new OutputStreamWriter(out, "UTF8"));
                            out.flush();
                            out.close();
                        }
                    }
                    return;
                } else if (policy >= 0) {

                    OutputStream out = res.getOutputStream();

                    ExternalPolicySerializer serializer = new ExternalPolicySerializer();
                    serializer.setAssertionsToFilter(configContext
                            .getAxisConfiguration().getLocalPolicyAssertions());

                    // check whether Id is set
                    String idParam = req.getParameter("id");

                    if (idParam != null) {
                        // Id is set

                        Policy targetPolicy = findPolicy(idParam, (AxisService) serviceObj);

                        if (targetPolicy != null) {
                            XMLStreamWriter writer;

                            try {
                                writer = XMLOutputFactory.newInstance()
                                        .createXMLStreamWriter(out);

                                res.setContentType("text/xml");
                                targetPolicy.serialize(writer);
                                writer.flush();

                            } catch (XMLStreamException e) {
                                throw new ServletException(
                                        "Error occured when serializing the Policy",
                                        e);

                            } catch (FactoryConfigurationError e) {
                                throw new ServletException(
                                        "Error occured when serializing the Policy",
                                        e);
                            }

                        } else {

                            res.setContentType("text/html");
                            String outStr = "<b>No policy found for id="
                                            + idParam + "</b>";
                            out.write(outStr.getBytes());
                        }

                    } else {

                        PolicyInclude policyInclude = ((AxisService) serviceObj).getPolicyInclude();
                        Policy effecPolicy = policyInclude.getEffectivePolicy();

                        if (effecPolicy != null) {
                            XMLStreamWriter writer;

                            try {
                                writer = XMLOutputFactory.newInstance()
                                        .createXMLStreamWriter(out);

                                res.setContentType("text/xml");
                                effecPolicy.serialize(writer);
                                writer.flush();

                            } catch (XMLStreamException e) {
                                throw new ServletException(
                                        "Error occured when serializing the Policy",
                                        e);

                            } catch (FactoryConfigurationError e) {
                                throw new ServletException(
                                        "Error occured when serializing the Policy",
                                        e);
                            }
                        } else {

                            res.setContentType("text/html");
                            String outStr = "<b>No effective policy for "
                                            + serviceName + " servcie</b>";
                            out.write(outStr.getBytes());
                        }
                    }

                    return;
                } else {
                    req.getSession().setAttribute(Constants.SINGLE_SERVICE,
                                                  serviceObj);
                }
            } else {
                req.getSession().setAttribute(Constants.SINGLE_SERVICE, null);
            }
        }

        renderView(LIST_SINGLE_SERVICE_JSP_NAME, req, res);
    }

    /**
     * Hack for Tuscany to get ?wsdl working with Tuscany service names
     * Can go once moved up to Axis2 1.3
     */
    private void setContextRoot(String filePart, String serviceName) {
        String contextRoot = configContext.getContextRoot();
        if (contextRoot != null && contextRoot.length() > 0) {
            if (contextRoot.equals("/")) {
                configContext.setServicePath("_null_");
            } else {
                int i = filePart.indexOf(contextRoot) + contextRoot.length();
                int j = filePart.lastIndexOf(serviceName);
                if (i==j) {
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

}
