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
package org.apache.tuscany.sca.vtest.wsbinding.nowsdl.defaultbinding.impl;

import org.apache.tuscany.sca.vtest.wsbinding.nowsdl.defaultbinding.AService;
import org.apache.tuscany.sca.vtest.wsbinding.nowsdl.defaultbinding.BService;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * This class implements AService.
 */
@Service(AService.class)
public class AServiceImpl implements AService {

	@Reference
	public BService b1;
	
    public String getName() {
        return "AService";
    }

	public String getB1String(String aString) {
		return b1.getString(aString);
	}

	public String getB1String2(String aString, String bString) {
		return b1.getString2(aString, bString);
	}

	public int getB1Int(int i) {
		return b1.getInt(i);
	}

} 