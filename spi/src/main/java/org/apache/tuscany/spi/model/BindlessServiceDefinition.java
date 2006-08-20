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
 * Represents a service in a composite, with an interface and a reference target.
 *
 * @version $Rev: 430937 $ $Date: 2006-08-11 21:17:56 -0400 (Fri, 11 Aug 2006) $
 */
public class BindlessServiceDefinition extends ServiceDefinition {
    private URI target;

    public BindlessServiceDefinition(String name, ServiceContract contract, boolean remotable, URI target) {
        super(name, contract, remotable);
        this.target = target;
    }

    public BindlessServiceDefinition() {
    }

    public URI getTarget() {
        return target;
    }

    public void setTarget(URI target) {
        this.target = target;
    }

}
