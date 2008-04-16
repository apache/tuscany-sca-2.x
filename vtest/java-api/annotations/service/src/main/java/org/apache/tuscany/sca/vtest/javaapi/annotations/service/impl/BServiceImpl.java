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

import org.apache.tuscany.sca.vtest.javaapi.annotations.service.AService;
import org.apache.tuscany.sca.vtest.javaapi.annotations.service.BService;
import org.apache.tuscany.sca.vtest.javaapi.annotations.service.CService;
import org.apache.tuscany.sca.vtest.javaapi.annotations.service.HService;
import org.apache.tuscany.sca.vtest.javaapi.annotations.service.IService;
import org.osoa.sca.annotations.Service;
import org.osoa.sca.annotations.Reference;

@Service(BService.class)
public class BServiceImpl implements BService {

	@Reference(required=false)
	public AService aService = null;
	
	@Reference(required=false)
	public CService cService = null;

	@Reference(required=false)
	public HService hService = null;

	@Reference(required=false)
	public IService iService = null;

    public String getName() {
        return "BService";
    }

    public String setAObject(AObject a) {
        a.aString = "BService";
        return "BService";
    }
    
    public String testServices() {
    	
    	AObject a = new AObject();
    	aService.setAObject(a);
    	if (a.aString != null)
    		return "AServiceNotPassByValue";
    	
    	AObject c = new AObject();
    	cService.setAObject(c);
    	if (c.aString == null || !c.aString.equals("CService"))
    		return "CServiceNotPassByReference";
    	

    	AObject h1 = new AObject();
    	hService.setAObject1(h1);
    	if (h1.aString == null || !h1.aString.equals("HService"))
    		return "HServiceSetAObject1NotPassByReference";
    	h1.aString = "testServices";
    	if (!hService.getAObject1String().equals("testServices"))
    		return "HServiceGetAObject1NotPassByReference";
    	
    	AObject h2 = new AObject();
    	hService.setAObject2(h2);
    	if (h2.aString != null)
    		return "HServiceSetAObject2NotPassByValue";
    	h2.aString = "testServices";
    	if (!hService.getAObject2String().equals("HService"))
    		return "HServiceGetAObject1NotPassByValue";
    	
    	AObject h3 = hService.getAObject3();
        h3.aString = "testServices";
    	if (!hService.getAObject3String().equals("HService"))
    		return "HServiceGetAObject3NotPassByValue";
        
    	AObject i1 = new AObject();
    	iService.setAObject1(i1);
    	if (i1.aString == null || !i1.aString.equals("IService"))
    		return "IServiceSetAObject1NotPassByReference";
    	i1.aString = "testServices";
    	if (!iService.getAObject1String().equals("testServices"))
    		return "IServiceGetAObject1NotPassByReference";

    	AObject i2 = new AObject();
    	iService.setAObject2(i2);
    	if (i2.aString == null || !i2.aString.equals("IService"))
    		return "IServiceSetAObject2NotPassByReference";
    	i2.aString = "testServices";
    	if (!iService.getAObject2String().equals("testServices"))
    		return "IServiceGetAObject2NotPassByReference";

        AObject i3 = iService.getAObject3();
        i3.aString = "testServices";
    	if (!iService.getAObject3String().equals("testServices"))
    		return "IServiceGetAObject3NotPassByReference";

    	return "None";
    }
}
