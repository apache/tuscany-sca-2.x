/**
 *
 * Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.tomcat;

import javax.servlet.Servlet;

import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.core.webapp.ServletHost;

/**
 * SCA Component that acts as a proxy for the Tomcat Host container that created the runtime.
 *
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class TomcatHost implements ServletHost {
    private TuscanyHost host;

    public void setHost(TuscanyHost host) {
        this.host = host;
    }

    public TuscanyHost getHost() {
        return host;
    }

    public void registerMapping(String mapping, Servlet servlet) {
        host.registerMapping(mapping, servlet);
    }

    public void unregisterMapping(String mapping) {
        host.unregisterMapping(mapping);
    }

    public Servlet getMapping(String mapping) {
        return host.getMapping(mapping);
    }
}
