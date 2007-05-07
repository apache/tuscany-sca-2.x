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
package org.apache.tuscany.sca.test;

import junit.framework.Assert;

import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

@Service(AnotherService.class)
@Scope("CONVERSATION")

public class AnotherServiceImpl implements AnotherService {
	
	// This is a simple pass-thru service used to test propogation
	// of ServiceReference and maintenance of Session state.
    
    private ServiceReference aServiceReference;	

	public void add(int anInt) {	
		
	 Assert.assertNotNull("AnotherServiceImpl - add ", aServiceReference);	
	 ((ConversationsService) aServiceReference).add(anInt);	
	 
	}


	public void initializeCount() {
	
	 Assert.assertNotNull("AnotherServiceImpl - initializeCount ", aServiceReference);		
	 ((ConversationsService) aServiceReference).initializeCount();
		
	}


	public void setService(ServiceReference aRef) {
		
	 Assert.assertNotNull("AnotherServiceImpl - setService ", aRef);
	 aServiceReference = aRef;
	 
	}


	public int getCount() {
		
 	  Assert.assertNotNull("AnotherServiceImpl - getCount ", aServiceReference);	
	  return ((ConversationsService) aServiceReference).getLocalCount();	 
	}
	
	
}

