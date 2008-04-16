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

import org.apache.tuscany.sca.vtest.javaapi.annotations.service.HService;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.RequestContext;
import org.osoa.sca.annotations.ComponentName;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;
import org.osoa.sca.annotations.AllowsPassByReference;

@Service(HService.class)
@Scope("REQUEST")
public class HServiceImpl implements HService {

	@ComponentName
	protected String componentName; 
	
	private AObject aObject1 = null;

	private AObject aObject2 = null;

	private AObject aObject3 = null;
	
    public RequestContext requestContext;
    
    public ComponentContext componentContext;
    
    public String getName() {
        return "HService";
    }

    @AllowsPassByReference
    public String setAObject1(AObject a) {
        a.aString = "HService";
        aObject1 = a;
        return "HService";
    }

    public String setAObject2(AObject a) {
        a.aString = "HService";
        aObject2 = a;
        return "HService";
    }

    @Context
	public void setComponentContext(ComponentContext componentContext) {
		this.componentContext = componentContext;
	}

    @Context
	public void setRequestContext(RequestContext requestContext) {
		this.requestContext = requestContext;
	}

	public String getComponentName() {
		return componentName;
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
    	aObject3.aString = "HService";
        return aObject3;
    }
    
    public String getServiceName1() {
        return requestContext.getServiceName();
    }
    
    public String getServiceName2() {
        return componentContext.getRequestContext().getServiceName();
    }

}
