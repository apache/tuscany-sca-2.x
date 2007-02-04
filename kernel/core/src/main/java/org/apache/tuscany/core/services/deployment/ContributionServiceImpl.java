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
package org.apache.tuscany.core.services.deployment;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.host.deployment.ContributionService;
import org.apache.tuscany.host.deployment.DeploymentException;
import org.apache.tuscany.host.deployment.UnsupportedContentTypeException;
import org.apache.tuscany.spi.deployer.ContributionProcessor;
import org.apache.tuscany.spi.deployer.ContributionProcessorRegistry;

/**
 * @version $Rev$ $Date$
 */
public class ContributionServiceImpl implements ContributionService, ContributionProcessorRegistry {
    private Map<String, ContributionProcessor> registry = new HashMap<String, ContributionProcessor>();

    public void register(ContributionProcessor processor) {
        registry.put(processor.getContentType(), processor);
    }

    public void unregister(String contentType) {
        registry.remove(contentType);
    }

    public URI contribute(URL contribution) throws DeploymentException, IOException {
        if (contribution == null) {
            throw new IllegalArgumentException("contribution is null");
        }

        URI source;
        try {
            source = contribution.toURI();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("contribution cannot be converted to a URI", e);
        }

        URLConnection connection = contribution.openConnection();
        String contentType = connection.getContentType();
        //todo try and figure out content type from the URL
        if (contentType == null) {
            throw new UnsupportedContentTypeException(null, contribution.toString());
        }

        InputStream is = connection.getInputStream();
        try {
            return contribute(source, is, contentType);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    public URI contribute(URI source, InputStream contribution, String contentType)
        throws DeploymentException, IOException {
        if (contentType == null) {
            throw new IllegalArgumentException("contentType was null");
        }

        ContributionProcessor processor = registry.get(contentType);
        if (processor == null) {
            throw new UnsupportedContentTypeException(contentType, source.toString());
        }
        
        return null;
    }

    public void remove(URI contribution) throws DeploymentException {
        // TODO Auto-generated method stub
    }

    public <T> T resolve(URI contribution, Class<T> definitionType, String namespace, String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public URL resolve(URI contribution, String namespace, URI uri, URI baseURI) {
        // TODO Auto-generated method stub
        return null;
    }
}
