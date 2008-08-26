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

import org.apache.tuscany.sca.vtest.javaapi.apis.componentcontext.AComponent;
import org.apache.tuscany.sca.vtest.javaapi.apis.componentcontext.BService;
import org.apache.tuscany.sca.vtest.javaapi.apis.componentcontext.DComponent;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.RequestContext;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

@Service(AComponent.class)
@Scope("COMPOSITE")
@EagerInit
public class AUnannotatedComponentImpl implements AComponent {

    public static String rcContent = null;

    protected ComponentContext componentContext;
    
    protected BService bReference;
    
    protected DComponent dReference;    

    protected String aProperty;

    public String getName() {
        return "ComponentA";
    }

    @Context
    public void setComponentContext(ComponentContext context) {
        this.componentContext = context;
    }

    @Init
    public void init() {
        RequestContext rc = componentContext.getRequestContext();
        if (rc != null) {
            rcContent = "NotNull";
        } else {
            rcContent = "Null";
        }
    }

    public String getContextURI() {
        return componentContext.getURI();
    }

    public String getServiceBName() {
        return componentContext.getService(BService.class, "bReference").getBName();
    }

    public String getServiceReferenceBName() {
        ServiceReference<BService> bSR = componentContext.getServiceReference(BService.class, "bReference");
        return bSR.getService().getBName();
    }

    public String getSelfReferenceName() {
        ServiceReference<AComponent> aSR = componentContext.createSelfReference(AComponent.class);
        return aSR.getService().getName();
    }

    public String getProperty() {
        return componentContext.getProperty(String.class, "aProperty");
    }

    public String getRequestContextServiceName() {
        return componentContext.getRequestContext().getServiceName();
    }

    public String getCastCallableReferenceServiceName() {
        BService b = componentContext.getService(BService.class, "bReference");
        CallableReference<BService> bCR = componentContext.cast(b);
        return bCR.getService().getBName();
    }

    public String getCastServiceReferenceServiceName() {
        BService b = componentContext.getService(BService.class, "bReference");
        ServiceReference<BService> bSR = componentContext.cast(b);
        return bSR.getService().getBName();
    }

    public void illegalCast() {
        componentContext.cast("");
    }

    public String testServiceLookup() {
        return componentContext.getService(DComponent.class, "dReference").getName();
    }

    public String getRequestContextContent() {
        return rcContent;
    }

}
