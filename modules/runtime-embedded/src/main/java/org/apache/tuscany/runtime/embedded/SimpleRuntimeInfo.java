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

package org.apache.tuscany.runtime.embedded;

import java.net.URI;
import java.net.URL;
import java.util.List;

import org.apache.tuscany.host.RuntimeInfo;
import org.apache.tuscany.spi.bootstrap.ComponentNames;

/**
 * @version $Rev$ $Date$
 */
public interface SimpleRuntimeInfo extends RuntimeInfo {
    URI DEFAULT_COMPOSITE = ComponentNames.TUSCANY_APPLICATION_ROOT.resolve("default");

    String DEFAULT_SYSTEM_SCDL = "META-INF/tuscany/default-system.composite";
    String SYSTEM_SCDL = "system.composite";
    String EXTENSION_SCDL = "META-INF/sca/extension.composite";
    String SERVICE_SCDL = "META-INF/sca/service.composite";
    String META_APPLICATION_SCDL = "META-INF/sca/application.composite";
    String APPLICATION_SCDL = "application.composite";

    ClassLoader getClassLoader();

    String getCompositePath();

    URL getSystemSCDL();

    URL getApplicationSCDL();

    List<URL> getExtensionSCDLs();

    URL getContributionRoot();

    URI getContributionURI();

}
