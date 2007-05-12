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

package org.apache.tuscany.contribution.service.impl;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.contribution.ContentType;
import org.apache.tuscany.contribution.service.TypeDescriber;
import org.apache.tuscany.sca.contribution.service.util.FileHelper;

/**
 * Implementation of the content describer
 *
 * @version $Rev$ $Date$
 */
public class PackageTypeDescriberImpl implements TypeDescriber {
    private final Map<String, String> contentTypeRegistry = new HashMap<String, String>();

    public PackageTypeDescriberImpl() {
        super();
        init();
    }

    /**
     * Initialize contentType registry with know types based on known file extensions
     */
    private void init() {
        contentTypeRegistry.put("JAR", ContentType.JAR);
    }

    protected String resolveContentyTypeByExtension(URL resourceURL) {
        String artifactExtension = FileHelper.getExtension(resourceURL.getPath());
        if (artifactExtension == null) {
            return null;
        }
        return contentTypeRegistry.get(artifactExtension.toUpperCase());
    }

    /**
     * Build contentType for a specific resource. We first check if the file is
     * a supported one (looking into our registry based on resource extension)
     * If not found, we try to check file contentType Or we return
     * defaultContentType provided
     * 
     * @param url
     * @param defaultContentType
     * @return
     */
    public String getType(URL resourceURL, String defaultContentType) {
        URLConnection connection = null;
        String contentType = defaultContentType;

        if (resourceURL.getProtocol().equals("file") && FileHelper.toFile(resourceURL).isDirectory()) {
            // Special case : contribution is a folder
            contentType = ContentType.FOLDER;
        } else {
            contentType = resolveContentyTypeByExtension(resourceURL);
            if (contentType == null) {
                try {
                    connection = resourceURL.openConnection();
                    contentType = connection.getContentType();
    
                    if (contentType == null || contentType.equals("content/unknown")) {
                        // here we couldn't figure out from our registry or from URL and it's not a special file
                        // return defaultContentType if provided
                        contentType = defaultContentType;
                    }
                } catch (IOException io) {
                    // could not access artifact, just ignore and we will return
                    // null contentType
                }
            }
        }
        return contentType == null ? defaultContentType : contentType;
    }

}
