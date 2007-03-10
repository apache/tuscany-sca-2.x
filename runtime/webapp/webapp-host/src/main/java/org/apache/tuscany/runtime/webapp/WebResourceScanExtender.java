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
package org.apache.tuscany.runtime.webapp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import javax.servlet.ServletContext;

import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.EagerInit;

import org.apache.tuscany.core.services.extension.AbstractExtensionDeployer;

/**
 * @version $Rev$ $Date$
 */
@EagerInit
public class WebResourceScanExtender extends AbstractExtensionDeployer {
    private final WebappRuntimeInfo runtimeInfo;
    private final String path;

    public WebResourceScanExtender(@Reference WebappRuntimeInfo runtimeInfo,
                                   @Property(name = "path")String path) {
        this.runtimeInfo = runtimeInfo;
        this.path = path;
    }

    @Init
    public void init() {
        ServletContext servletContext = runtimeInfo.getServletContext();
        Set extensions = servletContext.getResourcePaths(path);
        if (extensions == null || extensions.isEmpty()) {
            // no extensions in this webapp
            return;
        }

        for (Object e : extensions) {
            String extensionPath = (String) e;
            URL extension;
            try {
                extension = servletContext.getResource(extensionPath);
            } catch (MalformedURLException e1) {
                // web container should return an invalid URL for a path it gave us
                throw new AssertionError();
            }

            String name = extensionPath.substring(path.length());
            if (name.charAt(name.length() - 1) == '/') {
                // TODO support exploded extensions
                continue;
            }
            if (name.charAt(0) == '.') {
                // hidden file
                continue;
            }

            int lastDot = name.lastIndexOf('.');
            if (lastDot != -1) {
                name = name.substring(0, lastDot);
            }
            deployExtension(name, extension);
        }
    }
}
