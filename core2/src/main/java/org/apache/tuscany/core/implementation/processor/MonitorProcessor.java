/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.core.implementation.processor;

import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.spi.annotation.Monitor;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.monitor.MonitorFactory;

/**
 * Processes an {@link @Monitor} annotation, updating the component type with corresponding
 * {@link org.apache.tuscany.core.implementation.JavaMappedProperty}
 *
 * @version $Rev$ $Date$
 */
public class MonitorProcessor extends AbstractPropertyProcessor<Monitor>{
    private MonitorFactory monitorFactory;

    public MonitorProcessor(MonitorFactory monitorFactory) {
        super(Monitor.class);
        this.monitorFactory = monitorFactory;
    }

    // TODO replace with CDI
    public MonitorProcessor() {
        super(Monitor.class);
    }

    @Autowire
    public void setMonitorFactory(MonitorFactory monitorFactory) {
        this.monitorFactory = monitorFactory;
    }

    protected String getName(Monitor annotation) {
        return null;
    }

    protected <T> void initProperty(JavaMappedProperty<T> property,
                                    Monitor annotation,
                                    CompositeComponent<?> parent,
                                    DeploymentContext context) {
        Class<T> javaType = property.getJavaType();
        property.setDefaultValueFactory(new SingletonObjectFactory<T>(monitorFactory.getMonitor(javaType)));
    }
}
