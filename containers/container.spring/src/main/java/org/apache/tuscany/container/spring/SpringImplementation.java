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
package org.apache.tuscany.container.spring;

import org.springframework.context.ConfigurableApplicationContext;

import org.apache.tuscany.spi.model.Binding;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.Property;

import java.net.URL;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SpringImplementation extends Implementation<
        CompositeComponentType<
                BoundServiceDefinition<? extends Binding>,
                BoundReferenceDefinition<? extends Binding>,
                ? extends Property>> {

    private String location;
    private URL applicationXml;

    public SpringImplementation() {
    }

    public SpringImplementation(CompositeComponentType<
            BoundServiceDefinition<? extends Binding>,
            BoundReferenceDefinition<? extends Binding>,
            ? extends Property> componentType) {
        super(componentType);
    }

    /**
     * Returns the path of the Spring application context configuration
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the path of the Spring application context configuration
     */
    public void setLocation(String location) {
        this.location = location;
    }

    public URL getApplicationXml() {
        return applicationXml;
    }

    public void setApplicationXml(URL applicationXml) {
        this.applicationXml = applicationXml;
    }
}
