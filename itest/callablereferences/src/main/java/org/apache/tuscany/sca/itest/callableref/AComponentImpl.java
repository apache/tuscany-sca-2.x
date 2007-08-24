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
package org.apache.tuscany.sca.itest.callableref;

import org.osoa.sca.CallableReference;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

@Service(AComponent.class)
public class AComponentImpl implements AComponent {

    @Context
    protected ComponentContext componentContext;

    @Reference(name = "bReference")
    protected BComponent b;

    @Reference
    protected CComponent cReference;
    
    @Reference
    protected ServiceReference<CComponent> cServiceReference;

    @Reference(required=false)
    protected DComponent dReference;
    
    protected DComponent dReference1;

    @Reference(name = "dReference1")
    public void setDReference(DComponent dReference) {
        this.dReference1 = dReference;
    }

    public String foo() {
        return "AComponent";
    }

    public String fooB() {
        return b.foo();
    }

    public String fooB1() {
        CallableReference<BComponent> bRef = componentContext.cast(b);
        return bRef.getService().foo();
    }
    
    public String fooC() {
        return cReference.foo();
    }

    public String fooC1() {
        return cServiceReference.getService().foo();
    }
    
    public String fooBC() {
        CallableReference<CComponent> cReference = componentContext.getServiceReference(CComponent.class, "cReference");
        return b.fooC(cReference);
    }

    public String fooD() {
        CallableReference<AComponent> aReference = componentContext.createSelfReference(AComponent.class);
        return dReference1.foo(aReference);
    }

    public DComponent getDReference() {
        return dReference;
    }

}
