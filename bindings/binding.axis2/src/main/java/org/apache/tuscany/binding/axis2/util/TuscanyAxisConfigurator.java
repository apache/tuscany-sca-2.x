/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.apache.tuscany.binding.axis2.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.deployment.AxisConfigBuilder;
import org.apache.axis2.deployment.DeploymentEngine;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.engine.AxisConfigurator;
import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;
import org.osoa.sca.ServiceRuntimeException;

/**
 * Helps configure Axis2 from a resource in binding.axis2
 * instead of Axis2.xml
 *
 */
public class TuscanyAxisConfigurator implements AxisConfigurator {

    protected AxisConfiguration axisConfiguration = null;

    protected final ResourceLoader resourceLoader;

    /**
     * @param resourceLoader desired resource loader if null use thread context.
     * @param axisConfiguration Starting axis configuration, null then use uninitialized configuration. 
     */
    public TuscanyAxisConfigurator(ResourceLoader resourceLoader, AxisConfiguration axisConfiguration) {
        this.resourceLoader = resourceLoader != null ? resourceLoader : new ResourceLoaderImpl(getClass().getClassLoader());
        this.axisConfiguration = axisConfiguration == null ? new AxisConfiguration() : axisConfiguration;
    }

    public AxisConfiguration getAxisConfiguration() {
       
        return axisConfiguration;
    }

    public ConfigurationContext getConfigurationContext() throws ServiceRuntimeException {
       

        
        try {
            URL url = resourceLoader.getResource("org/apache/tuscany/binding/axis2/engine/config/axis2.xml");

            InputStream serviceInputStream = url.openStream();
            AxisConfigBuilder axisConfigBuilder = new AxisConfigBuilder(serviceInputStream, new DeploymentEngine(), axisConfiguration);
            axisConfigBuilder.populateConfig();
            serviceInputStream.close();
            return new ConfigurationContextFactory().createConfigurationContext(this);
        } catch (IOException e) {

            throw new ServiceRuntimeException(e);
        }

    }

}
