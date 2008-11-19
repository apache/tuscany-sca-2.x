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

package org.apache.tuscany.sca.host.webapp;

import org.apache.tuscany.sca.host.embedded.impl.DefaultSCADomain;

/**
 * @version $Rev$ $Date$
 */
public class WebSCADomain extends DefaultSCADomain {

    /**
     * @param runtimeClassLoader
     * @param applicationClassLoader
     * @param domainURI
     * @param contributionLocation
     * @param composites
     */
    public WebSCADomain(ClassLoader runtimeClassLoader,
                        ClassLoader applicationClassLoader,
                        String domainURI,
                        String contributionLocation,
                        String... composites) {
        super(runtimeClassLoader, applicationClassLoader, domainURI, contributionLocation, composites);
    }

    @Override
    public void close() {
        // Disable the close() as a hack to keep the WebSCADomain open
    }

    public void destroy() {
        super.close();
    }

}
