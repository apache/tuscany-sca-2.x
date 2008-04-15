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

package org.apache.tuscany.sca.vtest.javaapi.apis.componentcontext.impl;

import org.apache.tuscany.sca.vtest.javaapi.apis.componentcontext.BService;
import org.apache.tuscany.sca.vtest.javaapi.apis.componentcontext.CService;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Service;

@Service(interfaces={BService.class, CService.class})
public class BComponentImpl implements BService, CService {

    protected ComponentContext componentContext;

    @Context
    public void setComponentContext(ComponentContext context) {
        this.componentContext = context;
    }

    public String getBName() {
        return "ServiceB";
    }

    public String getCName() {
        return "ServiceC";
    }

    public String getSelfReferenceWithServiceName() {
        ServiceReference<CService> cSR = componentContext.createSelfReference(CService.class, "CService");
        return cSR.getService().getCName();
    }

}
