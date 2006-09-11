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
package org.apache.tuscany.core.services.extension;

import java.io.File;

import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.services.VoidService;
import org.apache.tuscany.spi.services.info.RuntimeInfo;

/**
 * Service that extends the runtime by loading composites located in a directory.
 *
 * @version $Rev$ $Date$
 */
public class DirectoryScanExtender extends AbstractExtensionDeployer implements VoidService {
    private String path;
    private RuntimeInfo runtimeInfo;

    @Property
    public void setPath(String path) {
        this.path = path;
    }

    @Autowire
    public void setRuntimeInfo(RuntimeInfo runtimeInfo) {
        this.runtimeInfo = runtimeInfo;
    }

    @Init(eager = true)
    public void init() {
        assert runtimeInfo != null;
        File extensionDir = new File(runtimeInfo.getInstallDirectory(), path);
        if (!extensionDir.isDirectory()) {
            // we don't have an extension directory, there's nothing to do
            return;
        }

        File[] files = extensionDir.listFiles();
        for (File file : files) {
            deployExtension(file);
        }
    }
}
