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
package org.apache.tuscany.spi.model;

import java.net.URI;

/**
 * Represents a service offered by a component, that has a particular binding associated with it.
 *
 * @version $Rev$ $Date$
 */
public class BoundServiceDefinition<B extends Binding> extends ServiceDefinition {
    private B binding;
    private URI target;

    public BoundServiceDefinition(String name, ServiceContract contract, boolean remotable, B binding, URI target) {
        super(name, contract, remotable);
        this.binding = binding;
        this.target = target;
    }

    public BoundServiceDefinition() {
    }

    public B getBinding() {
        return binding;
    }

    public void setBinding(B binding) {
        this.binding = binding;
    }

    public URI getTarget() {
        return target;
    }

    public void setTarget(URI target) {
        this.target = target;
    }

}
