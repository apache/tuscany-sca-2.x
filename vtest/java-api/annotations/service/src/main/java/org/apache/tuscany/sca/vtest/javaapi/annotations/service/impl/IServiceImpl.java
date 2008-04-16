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

package org.apache.tuscany.sca.vtest.javaapi.annotations.service.impl;

import org.apache.tuscany.sca.vtest.javaapi.annotations.service.IService;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.RequestContext;
import org.osoa.sca.annotations.AllowsPassByReference;
import org.osoa.sca.annotations.ComponentName;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

@Service(IService.class)
@Scope("REQUEST")
@AllowsPassByReference
public class IServiceImpl implements IService {

	@ComponentName
	public String componentName1; 

	public String componentName2; 

	private AObject aObject1 = null;

	private AObject aObject2 = null;

	private AObject aObject3 = null;

    @Context
    public RequestContext requestContext;
    
    @Context
    public ComponentContext componentContext;
    
    public String getName() {
        return "IService";
    }

    public String setAObject1(AObject a) {
        a.aString = "IService";
        aObject1 = a;
        return "IService";
    }

    public String setAObject2(AObject a) {
        a.aString = "IService";
        aObject2 = a;
        return "IService";
    }

	@ComponentName
	public void setComponentName1(String componentName1) {
		this.componentName2 = componentName1;
	}

    public String getAObject1String() {
        return aObject1.aString;
    }

    public String getAObject2String() {
        return aObject2.aString;
    }

    public String getAObject3String() {
        return aObject3.aString;
    }

    public AObject getAObject3() {
    	aObject3 = new AObject();
    	aObject3.aString = "IService";
        return aObject3;
    }

	public String getComponentName1() {
		return componentName1;
	}

	public String getComponentName2() {
		return componentName2;
	}

    public String getServiceName1() {
        return requestContext.getServiceName();
    }
    
    public String getServiceName2() {
        return componentContext.getRequestContext().getServiceName();
    }
}
