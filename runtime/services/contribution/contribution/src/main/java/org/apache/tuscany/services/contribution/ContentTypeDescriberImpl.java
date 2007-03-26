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

package org.apache.tuscany.services.contribution;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Service;

import org.apache.tuscany.services.contribution.spi.ContentTypeDescriber;

import org.apache.tuscany.services.contribution.util.FileHelper;

/**
 * Implementation of the content describer
 *
 * @version $Rev$ $Date$
 */
@EagerInit
@Service(ContentTypeDescriber.class)
public class ContentTypeDescriberImpl implements ContentTypeDescriber {
    private final Map<String, String> contentTypeRegistry = new HashMap<String, String>();

    public ContentTypeDescriberImpl() {
        super();
        init();
    }

    /**
     * Initialize contentType registry with know types based on known file extensions
     */
    private void init() {
        contentTypeRegistry.put("SCDL", "application/v.tuscany.scdl");
        contentTypeRegistry.put("COMPOSITE", "application/v.tuscany.scdl");
        contentTypeRegistry.put("WSDL", "application/v.tuscany.wsdl");
        contentTypeRegistry.put("JAR", "application/x-compressed");
    }

    protected String resolveContentyTypeByExtension(URL resourceURL) {
        String artifactExtension = FileHelper.getExtension(resourceURL.getPath());
        if (artifactExtension == null) {
            return null;
        }
        return contentTypeRegistry.get(artifactExtension.toUpperCase());
    }

    /**
     * Build contentType for a specific resource. We first check if the file is a supported one (looking into our
     * registry based on resource extension) If not found, we try to check file contentType Or we return
     * defaultContentType provided
     *
     * @param url
     * @param defaultContentType
     * @return
     */
    public String getContentType(URL resourceURL, String defaultContentType) {
        URLConnection connection = null;
        String contentType = defaultContentType;

        contentType = resolveContentyTypeByExtension(resourceURL);
        if (contentType == null) {
            try {
                connection = resourceURL.openConnection();
                contentType = connection.getContentType();

                if (contentType == null || contentType.equals("content/unknown")) {
                    // here we couldn't figure out from our registry or from URL
                    // return defaultContentType if provided
                    contentType = defaultContentType;
                }
            } catch (IOException io) {
                // could not access artifact, just ignore and we will return
                // null contentType
            }
        }
        return contentType == null ? defaultContentType : contentType;
    }

}
