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
package org.apache.tuscany.host.embedded.impl;

import java.net.URI;
import java.net.URL;

import org.apache.tuscany.host.embedded.SCARuntimeActivator;
import org.osoa.sca.ComponentContext;

/**
 * Default implementation of SCARuntime.
 * 
 * @version $Rev$ $Date$
 */
@SuppressWarnings("deprecation")
public class DefaultSCARuntimeActivator extends SCARuntimeActivator {

    protected MiniRuntimeImpl runtime;

    protected void startup(URL applicationSCDL, String compositePath) throws Exception {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URI contributionURI = URI.create("/default");
        SimpleRuntimeInfo runtimeInfo = new SimpleRuntimeInfoImpl(cl, contributionURI, applicationSCDL, compositePath);
        runtime = new MiniRuntimeImpl(runtimeInfo);

        try {
            runtime.start();
        } catch (Exception e) {
            throw e;
        }

    }

    protected void shutdown() throws Exception {
        runtime.stop();
    }

    @Override
    protected ComponentContext getContext(String componentName) {
        return runtime.getComponentContext(componentName);
    }

    protected <B> B locate(Class<B> businessInterface, String componentName, String serviceName) {
        if (serviceName != null) {
            return runtime.locateService(businessInterface, componentName, serviceName);
        } else {
            return locate(businessInterface, componentName);
        }
    }

    protected <B> B locate(Class<B> businessInterface, String componentName) {
        return runtime.locateService(businessInterface, componentName);
    }

}
