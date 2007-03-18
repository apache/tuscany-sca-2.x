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
package org.apache.tuscany.core.component.scope;

import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.ScopeContainerMonitor;
import org.apache.tuscany.spi.component.WorkContext;

import org.apache.tuscany.api.annotation.Monitor;

/**
 * Creates a new request scope context
 *
 * @version $$Rev$$ $$Date$$
 */
public class RequestScopeObjectFactory implements ObjectFactory<RequestScopeContainer> {
    private WorkContext context;
    private ScopeContainerMonitor monitor;


    public RequestScopeObjectFactory(@Reference WorkContext context, @Monitor ScopeContainerMonitor monitor) {
        this.context = context;
        this.monitor = monitor;
    }

    public RequestScopeContainer getInstance() throws ObjectCreationException {
        return new RequestScopeContainer(monitor);
    }
}
