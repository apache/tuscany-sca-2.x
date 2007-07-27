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
package org.apache.tuscany.sca.binding.axis2;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.deployment.URLBasedAxisConfigurator;
import org.apache.axis2.engine.AxisConfigurator;

/**
 * Helps configure Axis2 from a resource in binding.axis2 instead of Axis2.xml 
 * <p/> TODO: Review: should there be a single global Axis ConfigurationContext
 */
public class TuscanyAxisConfigurator extends URLBasedAxisConfigurator implements AxisConfigurator {

    public TuscanyAxisConfigurator() throws AxisFault {
        super(TuscanyAxisConfigurator.class.getResource("/org/apache/tuscany/sca/binding/axis2/engine/config/axis2.xml"), null);
    }

    public ConfigurationContext getConfigurationContext() throws AxisFault {
        if (configContext == null) {
            configContext = ConfigurationContextFactory.createConfigurationContext(this);
        }
        return configContext;
    }

}
