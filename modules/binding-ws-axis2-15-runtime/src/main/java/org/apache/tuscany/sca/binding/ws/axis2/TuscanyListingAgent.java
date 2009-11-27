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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.transport.http.ListingAgent;
import org.apache.axis2.transport.http.server.HttpUtils;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaExternal;

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

    /**
     * Override ?xsd processing so that WSDL documents with XSD imports
     * and includes work correctly.  When we move to Axis2 1.4, we may
     * be able to use SchemaSupplier to do this in a cleaner way.  Also
     * ensure that the correct IP address and port are returned by ?wsdl.
     */
    @Override
    public void processListService(HttpServletRequest req,
                                   HttpServletResponse res)
            throws IOException, ServletException {

        String url = req.getRequestURL().toString();
        String query = req.getQueryString();

        // for ?wsdl requests, need to update the WSDL with correct IPaddr and port 
        int wsdl = query.indexOf("wsdl");
        if (wsdl >= 0) {
            String serviceName = extractServiceName(url);
            HashMap services = configContext.getAxisConfiguration().getServices();
            if ((services != null) && !services.isEmpty()) {
                AxisService axisService = (AxisService)services.get(serviceName);
                Parameter wsld4jdefinition = axisService.getParameter("wsdl4jDefinition");
                Definition definition = (Definition)wsld4jdefinition.getValue();
                for (Object s : definition.getServices().values()) {
                    for (Object p : ((Service)s).getPorts().values()) {
                        String endpointURL = Axis2ServiceProvider.getPortAddress((Port)p);
                        String modifiedURL = setIPAddress(endpointURL, url);
                        modifiedURL = addContextRoot(modifiedURL, serviceName);
                        Axis2ServiceProvider.setPortAddress((Port)p, modifiedURL);
                    }
                }
            }
        }

        // handle ?xsd requests here
        int xsd = query.indexOf("xsd");
        if (xsd >= 0) {
            String serviceName = extractServiceName(url);
            HashMap services = configContext.getAxisConfiguration().getServices();
            if ((services != null) && !services.isEmpty()) {
                Object serviceObj = services.get(serviceName);
                if (serviceObj != null) {
                    String xsds = req.getParameter("xsd");
                    if (xsds != null && !"".equals(xsds)) {
                        // a schema name (perhaps with path) is present
                        AxisService axisService = (AxisService)serviceObj;
                        ArrayList schemas = axisService.getSchema();
                        for (Object rootSchema : axisService.getSchema()) {
                            XmlSchema schema = getSchema(((XmlSchema)rootSchema), xsds);
                            if (schema != null) {
                                // found the schema
                                res.setContentType("text/xml");
                                OutputStream out = res.getOutputStream();
                                schema.write(new OutputStreamWriter(out, "UTF8"));
                                out.flush();
                                out.close();
                                return;
                            }
                        }
                    }
                }
            }
        }

        // in all other cases, delegate to the Axis2 code
        super.processListService(req, res);
    }

    private String addContextRoot(String modifiedURL, String serviceName) {
        if (!"/".equals(configContext.getContextRoot())) {
            if (modifiedURL.endsWith(serviceName)) {
                URI uri = URI.create(modifiedURL);
                if (!uri.getPath().startsWith(configContext.getContextRoot())) {
                    modifiedURL = modifiedURL.substring(0, modifiedURL.length() - serviceName.length()) + configContext.getContextRoot() + serviceName;
                }
            }
        }
        return modifiedURL;
    }

    private XmlSchema getSchema(XmlSchema parentSchema, String name) {
        for (Iterator iter = parentSchema.getIncludes().getIterator(); iter.hasNext();) {
            Object obj = iter.next();
            if (obj instanceof XmlSchemaExternal) {
                XmlSchemaExternal extSchema = (XmlSchemaExternal)obj;
                if (extSchema.getSchemaLocation().endsWith(name)) {
                    return extSchema.getSchema();
                } else {
                    XmlSchema schema = getSchema(extSchema.getSchema(), name);
                    if (schema != null) {
                        return schema;
                    }
                }
            }
        }
        return null;
    }

    private String findAxisServiceName(String path) {
        HashMap services = configContext.getAxisConfiguration().getServices();
        if (services == null) {
            return null;
        }
        String[] parts = path.split("/");
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

    private static String setIPAddress(String wsdlURI, String requestURI) {
        try {
            URI wsdlURIObj = new URI(wsdlURI);
            String wsdlHost = wsdlURIObj.getHost();
            int wsdlPort = wsdlURIObj.getPort();
            String wsdlAddr = wsdlHost + (wsdlPort != -1 ? ":" + Integer.toString(wsdlPort) : "");
            URI requestURIObj = new URI(requestURI);
            String ipAddr = HttpUtils.getIpAddress();
            int requestPort = requestURIObj.getPort();
            String newAddr = ipAddr + (requestPort != -1 ? ":" + Integer.toString(requestPort) : "");
            return wsdlURI.replace(wsdlAddr, newAddr);
        } catch (Exception e) {
            // URI string not in expected format, so return the WSDL URI unmodified
            return wsdlURI;
        }
    }

}
