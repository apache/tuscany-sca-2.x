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

package org.apache.tuscany.sca.assembly.builder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.monitor.DefaultMonitorFactory;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;

/**
 * 
 */
public class BuilderContext {
    protected Definitions definitions;
    protected Map<QName, List<String>> bindingBaseURIs = Collections.emptyMap();
    protected Monitor monitor;

    /**
     * @param definitions
     * @param bindingBaseURIs
     * @param monitor
     */
    public BuilderContext(Definitions definitions, Map<QName, List<String>> bindingBaseURIs, Monitor monitor) {
        super();
        this.definitions = definitions;
        if (bindingBaseURIs != null) {
            this.bindingBaseURIs = bindingBaseURIs;
        }
        this.monitor = monitor;
    }

    public BuilderContext(Monitor monitor) {
        super();
        this.monitor = monitor;
    }

    public BuilderContext(ExtensionPointRegistry registry) {
        super();
        MonitorFactory monitorFactory =
            registry.getExtensionPoint(UtilityExtensionPoint.class).getUtility(MonitorFactory.class);
        this.monitor = monitorFactory.createMonitor();
    }

    public BuilderContext() {
        super();
        this.monitor = new DefaultMonitorFactory().createMonitor();
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public Monitor setMonitor(Monitor monitor) {
        Monitor old = this.monitor;
        this.monitor = monitor;
        return old;
    }

    public Definitions getDefinitions() {
        return definitions;
    }

    public Map<QName, List<String>> getBindingBaseURIs() {
        return bindingBaseURIs;
    }

    public void setDefinitions(Definitions definitions) {
        this.definitions = definitions;
    }
}
