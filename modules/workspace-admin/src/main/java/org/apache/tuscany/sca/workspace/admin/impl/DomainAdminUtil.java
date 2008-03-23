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

package org.apache.tuscany.sca.workspace.admin.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.xml.namespace.QName;

/**
 * Common functions and constants used by the admin components.
 *
 * @version $Rev$ $Date$
 */
public final class DomainAdminUtil {

    static final String DEPLOYMENT_CONTRIBUTION_URI = "http://tuscany.apache.org/cloud";

    /**
     * Extracts a qname from a key expressed as contributionURI;namespace;localpart.
     * @param key
     * @return
     */
    static QName compositeQName(String key) {
        int i = key.indexOf(';');
        key = key.substring(i + 1);
        i = key.indexOf(';');
        return new QName(key.substring(0, i), key.substring(i + 1));
    }

    /**
     * Returns a composite title expressed as contributionURI - namespace;localpart.
     * @param qname
     * @return
     */
    static String compositeTitle(String uri, QName qname) {
        if (uri.equals(DEPLOYMENT_CONTRIBUTION_URI)) {
            return qname.getLocalPart();
        } else {
            return uri + " - " + qname.getNamespaceURI() + ";" + qname.getLocalPart();
        }
    }

    /**
     * Extracts a contribution uri from a key expressed as contributionURI;namespace;localpart.
     * @param key
     * @return
     */
    static String contributionURI(String key) {
        int i = key.indexOf(';');
        return key.substring("composite:".length(), i);
    }

    /**
     * Returns a composite key expressed as contributionURI;namespace;localpart.
     * @param qname
     * @return
     */
    static String compositeKey(String uri, QName qname) {
        return "composite:" + uri + ';' + qname.getNamespaceURI() + ';' + qname.getLocalPart();
    }

    /**
     * Returns a link to the source of a composite
     * @param contributionURI
     * @param qname
     * @return
     */
    static String compositeSourceLink(String contributionURI, QName qname) {
        return "/composite-source/" + compositeKey(contributionURI, qname);
    }

    /**
     * Returns a composite title expressed as contributionURI - namespace;localpart.
     * @param qname
     * @return
     */
    static String compositeSimpleTitle(String uri, QName qname) {
        if (uri.equals(DomainAdminUtil.DEPLOYMENT_CONTRIBUTION_URI)) {
            return qname.getLocalPart();
        } else {
            return qname.getNamespaceURI() + ";" + qname.getLocalPart();
        }
    }

    /**
     * Returns a URL from a location string.
     * @param location
     * @return
     * @throws MalformedURLException
     */
    static URL locationURL(String location) throws MalformedURLException {
        URI uri = URI.create(location);
        String scheme = uri.getScheme();
        if (scheme == null) {
            File file = new File(location);
            return file.toURI().toURL();
        } else if (scheme.equals("file")) {
            File file = new File(location.substring(5));
            return file.toURI().toURL();
        } else {
            return uri.toURL();
        }
    }

    /**
     * Returns a link to a deployable composite.
     * 
     * If the containing contribution is a local directory, return the URI of  the local composite file
     * inside the contribution.
     * 
     * If the containing contribution is a local or remote file, return a URI of the form:
     * jar: contribution URI !/ composite URI.
     *  
     * @param contributionLocation
     * @param deployableURI
     * @return
     */
    static String compositeAlternateLink(String contributionLocation, String deployableURI) {
        URI uri = URI.create(contributionLocation);
        if ("file".equals(uri.getScheme())) {
            String path = uri.toString().substring(5);
            File file = new File(path);
            if (file.isDirectory()) {
                if (contributionLocation.endsWith("/")) {
                    return contributionLocation + deployableURI;
                } else {
                    return contributionLocation + "/" + deployableURI;
                }
            } else {
                return contributionLocation + "!/" + deployableURI; 
            }
        } else {
            return contributionLocation + "!/" + deployableURI;
        }
    }

    /**
     * Extract a node URI from an ATOM entry content.
     * 
     * @param content
     * @return
     */
    static String nodeURI(String content) {
        if (content != null) {
            int bs = content.indexOf("<span id=\"nodeURI\">");
            if (bs != -1) {
                content = content.substring(bs + 19);
                int es = content.indexOf("</span>");
                if (es != -1) {
                    return content.substring(0, es);
                }
            }
        }
        return null;
    }
}
