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
package org.apache.tuscany.binding.axis2.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.deployment.AxisConfigBuilder;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.engine.AxisConfigurator;
import org.osoa.sca.ServiceRuntimeException;

/**
 * Helps configure Axis2 from a resource in binding.axis2 instead of Axis2.xml
 * <p/>
 * TODO: Review: should there be a single global Axis ConfigurationContext
 */
public class TuscanyAxisConfigurator implements AxisConfigurator {

    protected AxisConfiguration axisConfiguration;

    //FIXME: how to get component specific classloader
    //protected final ResourceLoader resourceLoader;

    /**
     * @param axisConfiguration Starting axis configuration, null then use uninitialized configuration.
     */
    /*
    public TuscanyAxisConfigurator(ResourceLoader resourceLoader, AxisConfiguration axisConfiguration) {
        this.resourceLoader = resourceLoader != null ? resourceLoader :
            new ResourceLoaderImpl(getClass().getClassLoader());
        this.axisConfiguration = axisConfiguration == null ? new AxisConfiguration() : axisConfiguration;
    }
    */
    public TuscanyAxisConfigurator(AxisConfiguration axisConfiguration) {
        this.axisConfiguration = axisConfiguration == null ? new AxisConfiguration() : axisConfiguration;
    }

    public AxisConfiguration getAxisConfiguration() {
        return axisConfiguration;
    }

    public ConfigurationContext getConfigurationContext() throws ServiceRuntimeException {
        try {
            //FIXME: use component specific classloader to load config file
            //URL url = resourceLoader.getResource("org/apache/tuscany/binding/axis2/engine/config/axis2.xml");
            URL url = this.getClass().getResource("/org/apache/tuscany/binding/axis2/engine/config/axis2.xml");

            InputStream serviceInputStream = url.openStream();
            AxisConfigBuilder axisConfigBuilder =
//                new AxisConfigBuilder(serviceInputStream, new DeploymentEngine(), axisConfiguration);
                new AxisConfigBuilder(serviceInputStream,  axisConfiguration);
            axisConfigBuilder.populateConfig();
            serviceInputStream.close();
            return ConfigurationContextFactory.createConfigurationContext(this);
        } catch (IOException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public void loadServices() {
        // TODO Auto-generated method stub

    }

    public void engageGlobalModules() throws AxisFault {
        // TODO Auto-generated method stub

    }
}
