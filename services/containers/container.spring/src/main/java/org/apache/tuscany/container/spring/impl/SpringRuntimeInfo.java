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
package org.apache.tuscany.container.spring.impl;

import java.io.File;
import java.net.URL;

import org.apache.tuscany.host.RuntimeInfo;
import org.springframework.context.support.AbstractRefreshableApplicationContext;

/**
 * @author Andy Piper
 * @since 2.1
 */
public class SpringRuntimeInfo implements RuntimeInfo {
    private AbstractRefreshableApplicationContext applicationContext;
    private File appRootDir;

    public SpringRuntimeInfo(File appRootDir, AbstractRefreshableApplicationContext applicationContext) {
        this.appRootDir = appRootDir;
        this.applicationContext = applicationContext;
    }

    public AbstractRefreshableApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(AbstractRefreshableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public File getInstallDirectory() {
        return null;
    }

    public File getApplicationRootDirectory() {
        return appRootDir;
    }

    public URL getBaseURL() {
        return null;
    }
}
