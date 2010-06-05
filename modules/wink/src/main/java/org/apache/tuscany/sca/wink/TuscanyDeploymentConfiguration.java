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

package org.apache.tuscany.sca.wink;

import org.apache.wink.common.internal.registry.InjectableFactory;
import org.apache.wink.server.internal.DeploymentConfiguration;

/**
 * Subclass the default Wink DeploymentConfiguration and override
 * the initRegistries method so the InjectableFactory instance can
 * be set to the Tuscany one. 
 */
public class TuscanyDeploymentConfiguration extends DeploymentConfiguration {

    @Override
    protected void initRegistries() {
        super.initRegistries();
        InjectableFactory.setInstance(new TuscanyInjectableFactory());
    }

}
