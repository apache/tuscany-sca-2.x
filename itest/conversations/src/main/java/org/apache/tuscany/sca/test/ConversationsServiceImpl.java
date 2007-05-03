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

import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;
import org.osoa.sca.RequestContext;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Remotable;
import org.osoa.sca.annotations.Service;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Scope;
import java.io.File; 
import org.apache.tuscany.sca.test.ConversationsClient;

@Service(ConversationsService.class)
@Scope("CONVERSATION")

public class ConversationsServiceImpl implements ConversationsService {

	@Callback 
	// Note injection is not working with a private access modifier.
	protected ConversationsCallback callback; 
    private int count=0;
	
	public void knockKnock(String aString) { 
		
	  try 
	  {
	   count++;	  
	   //System.out.println("ConversationsServiceImpl message received: " + aString);
	   callback.callBackMessage("Who's There " + count); 
	   callback.callBackIncrement("Add one please");
       //System.out.println("ConversationsServiceImpl responses sent");	
	   return; 
	  }
	  catch (Exception ex) 
	  {
	   ex.printStackTrace();	  
	  }
	  
	}
	
	public int getCount(ServiceReference aServiceReference)
	{
		
      //		
	  // Invoke a method on the service reference and return back the result. 
	  //		
	  
	  int count=0;
	  try
	  {
	  count =  ((ConversationsClient) aServiceReference).count();
	  }
	  catch (Exception ex) 
	  {
		  ex.printStackTrace();
	  }
	  return count;	
	  
	}
	
	public int getLocalCount()
	{
		
      //		
	  // Return my localc instance count.  This is used for test4.
	  //		
	 
	  return count;	
	  
	}
	
	public String getDateTime(ServiceReference aServiceReference)
	{
		
      //		
	  // Invoke a method on the service reference and return back the result. 
	  //
		
	  String dateTime;
	  dateTime =  ((ConversationsClient2) aServiceReference).getDateTime();
	  return dateTime;	
	  
	}

	public void add(int anInt) {
		
		count +=anInt;
		
	}
	
    public void initializeCount() {
		
		count =0;
		
	}

	public boolean createServiceReferenceForSelf() {
		
		// This is done here because we need to test getting a ServiceReference 
		// from a component that implements a single interface. The client in this test
		// impliments 2 interfaces to test the variant of this that takes interface name as an argumnet. 
		
		boolean aBoolean = false; 		
		
	    ServiceReference myServiceReference = null; 
	    CompositeContext ctx = CurrentCompositeContext.getContext();	   

	    try
	    {
		 myServiceReference = ctx.createServiceReferenceForSession(this);
		 System.out.println("Laa: Created Service Reference for Session:" + myServiceReference);
	    }
	    catch (Exception ex) 
	    {
	    	ex.printStackTrace();
	    }    
		
		
	    if (myServiceReference != null)
	    {
	    	aBoolean = true;
	    	System.out.println("Laa: Service Reference is not null");
	    }	
		
	   	return aBoolean;
	}
	
	
}

