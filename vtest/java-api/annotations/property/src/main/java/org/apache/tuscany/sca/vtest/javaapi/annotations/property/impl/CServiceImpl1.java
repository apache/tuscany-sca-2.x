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

package org.apache.tuscany.sca.vtest.javaapi.annotations.property.impl;

import org.apache.tuscany.sca.vtest.javaapi.annotations.property.BService;
import org.apache.tuscany.sca.vtest.javaapi.annotations.property.CService;
import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

@Service(CService.class)
public class CServiceImpl1 implements CService {

	public BService b1;

	public String p2;
	
	public int p3;

	public String p4;
	
	public String constructor; 
		
	@Constructor
	public CServiceImpl1() {
		constructor = "NoArgument";
	}
	
	public CServiceImpl1(@Reference(name = "bOne") BService b1, @Property(name = "pTwo") String p2, @Property(name = "pThree") int p3, @Property(name = "pFour") String p4) {
		this.b1 = b1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
		constructor = "AllArguments";
	}

	public CServiceImpl1(BService bOne, String pTwo, int pThree) {
		this.b1 = bOne;
		this.p2 = pTwo;
		this.p3 = pThree;
		constructor = "LessArguments";
	}

    public String getName() {
        return "BService";
    }

	public String getB1Name() {
		if (b1 == null)
			return null;
		return b1.getName();
	}

	public String getP2() {
		return p2;
	}

	public int getP3() {
		return p3;
	}

	public String getConstructor() {
		return constructor;
	}

	public String getP4() {
		return p4;
	}
}
