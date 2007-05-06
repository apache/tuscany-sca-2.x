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

package org.apache.tuscany.sca.webapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.tuscany.host.embedded.SCADomain;

/**
 * Utility class to initialize the SCADomian associated with a webapp
 */
public class SCADomainHelper {

    public static final String SCA_DOMAIN_ATTRIBUTE = "org.apache.tuscany.sca.SCADomain";

    public static final String DEPLOYABLE_COMPOSITES_PARAM = "deployableComposites";
    public static final String DEPLOYABLE_COMPOSITES_DIR_PARAM = "deployableCompositesDirectory";
    public static final String DEFAULT_COMPOSITES_DIR = "/WEB-INF/classes";

    public static final String DOMAIN_URI_PARAM = "domainURI";
    public static final String CONTRABUTION_LOCATION_PARAM = "contrabutionLocation";

    /**
     * Initializes the SCADomian associated with a webapp context. If a SCADomain
     * doesn't exist already then one is create based on the webapp config.
     */
    public static SCADomain initSCADomain(ServletContext servletContext) {
        SCADomain scaDomain = (SCADomain)servletContext.getAttribute(SCA_DOMAIN_ATTRIBUTE);
        if (scaDomain == null) {
            String[] compositeNames = getDeployableComposites(servletContext);
            String domainURI = getDomainURI(servletContext);
            String contrabutionLocation = getContrabutionLocation(servletContext);
            scaDomain = SCADomain.newInstance(domainURI, contrabutionLocation, compositeNames);
            servletContext.setAttribute(SCA_DOMAIN_ATTRIBUTE, scaDomain);
        }
        return scaDomain;
    }

    protected static String getDomainURI(ServletContext servletContext) {
        String domainURI = servletContext.getInitParameter(DOMAIN_URI_PARAM);
        if (domainURI == null || domainURI.length() < 1) {
            domainURI = "http:/" + servletContext.getContextPath();
        }
        return domainURI;
    }

    protected static String getContrabutionLocation(ServletContext servletContext) {
        String location = servletContext.getInitParameter(CONTRABUTION_LOCATION_PARAM);
        if (location == null || location.length() < 1) {
            location = ".";
        }
        return location;
    }

    /**
     * Gets all the deployable composites. These may be specified by naming them
     * individualy using the DEPLOYABLE_COMPOSITES_PARAM init-param, or else by
     * naming a directory that contains all the composites to be deployable using
     * the DEPLOYABLE_COMPOSITES_DIR_PARAM init-param. If neither of those are
     * specified then it defaults to all the composites in the DEFAULT_COMPOSITES_DIR
     * directory.
     */
    protected static String[] getDeployableComposites(ServletContext servletContext) {
        String composites = servletContext.getInitParameter(DEPLOYABLE_COMPOSITES_PARAM);
        if (composites != null && composites.length() > 0) {
            return composites.split(",");
        }

        String compositeDir = servletContext.getInitParameter(DEPLOYABLE_COMPOSITES_DIR_PARAM);
        if (compositeDir == null || compositeDir.length() < 1) {
            compositeDir = DEFAULT_COMPOSITES_DIR;
        }

        List<String> compositeNames = new ArrayList<String>();
        Set resources = servletContext.getResourcePaths(compositeDir);
        for (Object o : resources) {
            String resource = o.toString();
            if (resource.endsWith(".composite")) {
                String compositeName = resource.substring(resource.lastIndexOf('/')+1);
                compositeNames.add(compositeName);
            }
        }

        return compositeNames.toArray(new String[compositeNames.size()]);
    }
}
