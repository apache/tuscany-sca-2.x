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

import java.io.File;
import java.text.DateFormat;

import junit.framework.Assert;

import org.osoa.sca.ComponentContext;
import org.osoa.sca.ConversationEndedException;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.ConversationAttributes;
import org.osoa.sca.annotations.ConversationID;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

@Service(interfaces={ConversationsClient.class,ConversationsClient2.class})   


@Scope("CONVERSATION")
@ConversationAttributes(maxIdleTime="10 minutes",
		      singlePrincipal=false)
		 	 

public class ConversationsClientImpl implements ConversationsClient, ConversationsClient2, ConversationsCallback {	

	@Reference
	protected ConversationsService aService;
	@Reference
	protected AnotherService anotherService;
	@Reference
	protected ConversationsLifeCycleService aLifeCycleService;
	@Context
	protected ComponentContext myContext;
	@ConversationID
	protected String conversationID;
	
	private Object monitor = new Object();
	private int count=0;
	private String message;
	private String initialState;
	private int initialCount = 56;
	private String dateTime;
	private final static String markerFileName = "target/testConversations_test3_marker"; 
		
        public ConversationsClientImpl() { 
            System.out.println("xxxxxxxxxx"); 
        }
        
	public void run() {	
		
       // This tests various aspects of session lifecycle and maintenance of session state. 
	   // None of these test are working due to various issues. 	
		
	   /* test0()
	     This test verifies annotations and API's for ConversationID.	     
	   */		
	    test0();	 // - Tuscany-1001.
	  		  			
	   /* test1()	
		Test stateful callbacks.  Verify that the client�s state is maintained throughout the execution of  
		all callback methods.
	   */ 		
	    test1();    // This test is working for MODULE scope.  Cant claim succcess because it needs Conversation scope. 
	    
	   /* test2()	
		 Test each method of starting a session.  Test @Init.
	   */ 		
	    test2();  //    Tuscany-965,  Tuscany-1001   
		
	   /* test3()	
		 Test each method of ending a session.  Test @Destroy.
	   */ 	   
	   test3();  // Tuscany-965,  Tuscany-1001  
	   
	   /* test4()	
		 Pass an existing service reference with an active session to another service.  Verify the session is maintained
	   */		
	   test4();  // Tuscany-964,  Tuscant-1001	
	   
	   /* test5()	
		Call the createServiceReferenceForSession() API to get a service reference for the active session.		  
        Pass the service reference to another service.  Verify the session is maintained.
	   */		
	   test5();
	   
	   /* test6() 
	    * Allow a session to timeout. 
	   */	   
	   test6();   //  Tuscany-965,  Tuscany-1001  
	  
	  	  	   		 	   
	   return;	   
	}	
	
	private void test0()	
	{
		
	  // Verify that conversationID was injected. 	
	  Assert.assertNotNull("test0 - conversationID injected", conversationID);
	  System.out.println("ConversationID: " + conversationID);
	  
	  //
	  // The client may access the ConversationID by calling getConversationID on a Service Reference.
	  // This also verifies a session was created during injection of this Service Reference.
	  //
          // TODO: this fails as the cid is null as a conversation has not been started yet (which seems expected to me)
//          ServiceReference<ConversationsService> aServiceserviceRef = myContext.getServiceReference(ConversationsService.class, "aService");
//          System.out.println("aServices cid: " + aServiceserviceRef.getConversationID());
//          Assert.assertNotNull("test0 - ConversationID from service reference", aServiceserviceRef.getConversationID());	
	  
	  //FIXME Port to the 1.0 spec API
	  //Object aLifeCycleServicesSessionID =((ServiceReference)aLifeCycleService).getSessionID();
	  //Assert.assertNotNull("test0 - ConversationID from LifeCycleService service reference", aLifeCycleServicesSessionID);
	  
	  // This will verify the @Init() is working for a session created during injection. See test2(). 
	  aLifeCycleService.knockKnock("Hello");
	  
	}
	
	private void test1()	
	{		
	 
	   //
	   //  This test verifies that the state of the client is preserved across method calls. Each
	   //  call to the service results in 2 callbacks.  One to increment a count and the other to set a string.
	   //  At the end of the test the count should equal 5 and the string should be set. Also the original strings
	   //  values should be preserved.  	
	   //   
	   //
	   initialState = "Initial State";
	   
	   int numCalls = 0;
	   
	   for(int i=0; i < 10; i++)
	   {   
	    aService.knockKnock("Knock Knock " + ++numCalls);	
	   }
	   
	   // I want to drive multiple callbacks and then give them time to complete. Don't want to 
	   // force serialization. After 20 seconds check the assertions. 
	   
       synchronized(monitor)
       {
	   try 
        {
	     monitor.wait(2000L);
        }
       catch (Exception ex) 
         {ex.printStackTrace();}
       }
       
       // Here we test for the expected state of several different variables.  State needs to be maintained
       // across method calls to the service. If the same client instance is used then the state should match
       // the following assertions. 
       
       // instance variable count should equal the number of calls to the backend service. count is incremented
       // during callback processing. 
	   Assert.assertEquals("Conversations - Test1 count", numCalls, count);
	   // The returned message should be like the following.  
	   Assert.assertEquals("Conversations - Test1 message", true, message.startsWith("Who's There"));
	   // any initial state set prior to running the callbacks should be maintained. 
	   Assert.assertEquals("Conversations - Test1 initialState", "Initial State", initialState);
	   Assert.assertEquals("Conversations - Test1 initialCount", 56, initialCount);
	   
	   return;
	   
    }
	
	private void test2()	
	{
		
	  // Verify the various methods to create a new Session.
	  // Sesssions can be started:
	  //
      //  1) When a service reference is injected into a client.  This is verified in test0().
      //  2) By the client calling newSession() API with and without a ConversationID.   
      //
	  //  Verify init() is called prior to any business methods. This is performed in the Service when any 
	  //  business methos is called. 
		
      Assert.assertNotNull("current composite context",myContext);     

      // This tests creating a conversational session. And that @Init is run prior to business method.    
      ServiceReference aServRef = null;
      //FIXME Port to the 1.0 spec API
      //aServRef = myContext.newSession("ConversationsLifeCycleService");
//      Assert.assertNotNull("Conversations - Test2 Service Reference 1 not returned", aServRef);
      
      //FIXME Port to the 1.0 spec API
      //Get the session  ID.
      //Object aConversationID = aServRef.getSessionID(); 
      ConversationsLifeCycleService aConversationsLifeCycleService = (ConversationsLifeCycleService) aServRef;
	  aConversationsLifeCycleService.knockKnock("Hello");
	  
	  //Create a new session this time specifying a session ID. Verify the seesion id is what was specified.
	  //FIXME Port to the 1.0 spec API
	  //aServRef = myContext.newSession("ConversationsLifeCycleService","Test2-12345");
	  Assert.assertNotNull("Conversations - Test2 Service Reference 2 not returned", aServRef);
	  //FIXME Port to the 1.0 spec API
      //Get the session  ID.  
      //Object aConversationID2 = aServRef.getSessionID(); 
      //Assert.assertEquals("Conversations - Test2 Session not created with specified ConversationID", "Test2-12345", aConversationID2);
      aConversationsLifeCycleService = (ConversationsLifeCycleService) aServRef;
	  aConversationsLifeCycleService.knockKnock("Hello");
	  
	  //FIXME Port to the 1.0 spec API
	  //Assert.assertNotSame("Conversations - Test2 sessions are not different", aConversationID, aConversationID2);
	  
	}
	
	private void test3()	
	{
		
	  // Note: The @EndSesion and @EndsConversation anotations are not implemented.	 
      //       So #1 an #2 cannot be done. 
		
	  //	
	  // Verify the various methods to end a session.
	  // Sesssions can be ended:
	  //
      //  1) Server operation annotated with @EndsConversation. 
      //  2) Server operation calls an @EndSession annotated callback method.    
      //  3) Servers conversation lifetime times out.  This is test6() so its not implimented in the test3 method.
	  //  4) The client calls ServiceReference.endSession(); 	
	  //
      //  Verify @Destroy annotated method get called after completion of the business 
	  //  method that called the endSession.
	  //
		
	 ConversationsLifeCycleService aConversationsLifeCycleService;
//FIXME Port to the 1.0 spec API         
//	 Object aConversationID;
     Assert.assertNotNull("current composite context",myContext); 
     ServiceReference aServRef;
     this.removeMarkerFile();  
		
	  //
	  // test3 variation #1 -  Cannot be done annotation not implimented yet. 12/15/2006		
	  //	
     
     //FIXME Port to the 1.0 spec API
     //aServRef = myContext.newSession("ConversationsLifeCycleService");
     aServRef = null;
     Assert.assertNotNull("Conversations - Test3-1 Service Reference not returned", aServRef);      
  
     aConversationsLifeCycleService = (ConversationsLifeCycleService) aServRef;
	 aConversationsLifeCycleService.knockKnock("Hello");
	  //FIXME Port to the 1.0 spec API
	 //aConversationID = aServRef.getSessionID();	  
	 //Assert.assertNotNull("Conversations - Test3-1 ConversationID not found", aConversationID);
	  
	 //Call the business method annotated with @EndsConversation. 
	 aConversationsLifeCycleService.endThisSession(); // This should also drive @Destroy method.
	  
	 // Verify session has ended.  The ConversationID should be null; 
	  //FIXME Port to the 1.0 spec API
	 //aConversationID = aServRef.getSessionID();	  
	 //Assert.assertNull("Conversations - Test3-1 session not null after endSession()", aConversationID);
	  
	 // Verify Destroy was run. The baxckend service creates a marker file when @Destroy annotated method is run.
	 Assert.assertEquals("Conversations - Test3-1 @Destroy method not invoked", true, this.isMarkerFilePresent());	 
	 this.removeMarkerFile(); 
		
	  //
	  // test3 variation #2 -  Cannot be done annotation not implimented yet. 12/15/2006		
	  //
	 
	 //FIXME Port to the 1.0 spec API
	 //aServRef = myContext.newSession("ConversationsLifeCycleService");
     Assert.assertNotNull("Conversations - Test3-2 Service Reference not returned", aServRef);      
  
     aConversationsLifeCycleService = (ConversationsLifeCycleService) aServRef;
	 aConversationsLifeCycleService.knockKnock("Hello");
	  //FIXME Port to the 1.0 spec API
	 //aConversationID = aServRef.getSessionID();	  
	 //Assert.assertNotNull("Conversations - Test3-2 ConversationID not found", aConversationID);
	  
	 //Call the business method that will invoke my @EndSession callback method. 
	 aConversationsLifeCycleService.endThisSessionUsingCallback(); // This should also drive @Destroy method.
	  
	 // Verify session has ended.  The ConversationID should be null; 
	  //FIXME Port to the 1.0 spec API
	 //aConversationID = aServRef.getSessionID();	  
	 //Assert.assertNull("Conversations - Test3-2 session not null after endSession()", aConversationID);
	  
	 // Verify Destroy was run. The baxckend service creates a marker file when @Destroy annotated method is run.
	 Assert.assertEquals("Conversations - Test3-2 @Destroy method not invoked", true, this.isMarkerFilePresent());	 
	 this.removeMarkerFile(); 
	 		
	  //	
	  // test3 variation #4 - Client calls endSession()  
	  //	  	
	      
	 //FIXME Port to the 1.0 spec API
      //aServRef = myContext.newSession("ConversationsLifeCycleService");
         aServRef = null;
      Assert.assertNotNull("Conversations - Test3-4 Service Reference not returned", aServRef);      
   
      aConversationsLifeCycleService = (ConversationsLifeCycleService) aServRef;
	  aConversationsLifeCycleService.knockKnock("Hello");
	  //FIXME Port to the 1.0 spec API
	  //aConversationID = aServRef.getSessionID();	  
	  //Assert.assertNotNull("Conversations - Test3-4 ConversationID not found", aConversationID);
	  
	  //Call the endSession() API on the Service Reference. 
	  //FIXME Port to the 1.0 spec API
	  //aServRef.endSession(); // This should also drive @Destroy method.
	  
	  // Verify session has ended.  The ConversationID should be null; 
	  //FIXME Port to the 1.0 spec API
	  //aConversationID = aServRef.getSessionID();	  
	  //Assert.assertNull("Conversations - Test3-4 session not null after endSession()", aConversationID);
	  
	  // Verify Destroy was run. The baxckend service creates a marker file when @Destroy annotated method is run.
	  Assert.assertEquals("Conversations - Test3-4 @Destroy method not invoked", true, this.isMarkerFilePresent());	 
	  this.removeMarkerFile(); 
	}
	
	public void test4() 
	{
		
		//
		// This test uses the injected active session for the ConversationsService. It starts a conversation
		// in this method building up state with a counter.  It then passes this reference to another backend service adding 
		// additional state.  It then returns and adds more state using original service and then performs the Assertions.  		
		//
		
       aService.initializeCount(); 
       aService.add(1);
       anotherService.setService((ServiceReference)aService);
       anotherService.add(1);
       anotherService.add(1);
       aService.add(1);
       aService.add(1);
 	   int count = anotherService.getCount(); 
       
       Assert.assertEquals("test4 - ConversationsClientImpl ", 5, count);       
        
	   return;
	}
	
	
	public void test5() 
	{
		
		// The first test verifies that the createServiceReferenceForSession(this) API is functioning.
		// This is done in the backend serviced as it needs a single interface defined. The 2nd test 
		// uses the variant of this API when > 1 interfaces are implemented. 
		//
		// The 2nd test obtains a service reference for myself and passes that reference to a backend service.
		// The backend service then invokes the count() method on my service reference. It then returns 
		// the count that it retrieved.  The test then asserts that the count returned from the backend 
		// service matches the client services internal state. 
		//
		
        Assert.assertNotNull("current composite context",myContext);     
             
        boolean result = aService.createServiceReferenceForSelf();	
        System.out.println("Laa: returned from createServiceReferenceForSelf() " + result);
		Assert.assertEquals("test5 - createServiceReferenceForSelf",true,result);
		
		count = 6;
		int returnCount = 0;
		//FIXME Port to the 1.0 spec API
		//ServiceReference myServiceReference = myContext.createServiceReferenceForSession(this,"ConversationsClient2");
                ServiceReference myServiceReference = null;
        Assert.assertNotNull("test5 - createServiceReferenceForSession - myContext.createServiceReferenceForSession(this,ConversationsClient);", myServiceReference);
		returnCount = aService.getCount(myServiceReference);
		
		Assert.assertEquals("test5 - createServiceReferenceForSession(this,ConversationsClient2)",count,returnCount);
		String aRemoteDateTime = aService.getDateTime(myServiceReference);
		Assert.assertEquals("test5 - createServiceReferenceForSession(this,ConversationsClient2 - dateTime)", dateTime, aRemoteDateTime);		
		
		return;
	}
	
	public void test6() 
	{
		
		//
		// Create a session and allow it to timeout and verify that it did. 
		// Invoking a business method on a service that has timed out should result in 
		// a SessionEndedException.  
		//
					
	      Assert.assertNotNull("current composite context",myContext);     

	      // This tests creating a conversational session.  This service has a maxAge="5 seconds". 
	      ServiceReference aServRef;
              //FIXME Port to the 1.0 spec API
	      //aServRef = myContext.newSession("ConversationsLifeCycleService");
              aServRef = null;
	      Assert.assertNotNull("Conversations - Test6 Service Reference not returned", aServRef);	
	      
	      // Run a business method. 
	      ConversationsLifeCycleService aConversationsLifeCycleService = (ConversationsLifeCycleService) aServRef;	  
		  aConversationsLifeCycleService.knockKnock("Hello");
		  
		  //
		  // wait 10 seconds so session will time out.
		  //
		  try {
			   Thread.sleep(10000L);
		  }
		  catch (InterruptedException ex)
		  {
			  ex.printStackTrace();
		  }
		  
		  // Run a busineess method after timeout period has elapsed.
		  boolean sessionEnded = false; 
		  try
		  {
		   aConversationsLifeCycleService.knockKnock("Hello"); 
		  } 
		  catch (ConversationEndedException sex)
		  {
		   sessionEnded = true;  
		  }
		  
		  Assert.assertEquals("Conversations - Test6 Session did not timeout ", true, sessionEnded);		  
		 
	   return;
	}
		

	public synchronized void callBackIncrement(String aString) {
		
		count++;
		System.out.println("Laa: callBackIncrement invoked on client.  count = " + count);
				
	}

	public synchronized void callBackMessage(String aString) {
		
		message = aString;
		System.out.println("Laa: callBackIMessage invoked on client.  message = " + message);
		
	}
	
	// @EndSession - This is for test3() variation #2. 
    public void callBackEndSession() {
		
	System.out.println("Laa: callBackEndSession method invoked on client.");
		
	}
	
	public int count()
	{
	  return this.count;	
	}

	public String getDateTime() {		 
		
		dateTime = DateFormat.getDateTimeInstance().toString();
		return dateTime;
	
	}
	
	private void removeMarkerFile()
	{
		  // Make sure the marker file is not present before starting the test. 	
		  File aFile = new File(markerFileName);	
		  if (aFile.exists())
			  aFile.delete();
	}
	
	private boolean isMarkerFilePresent()
	{
	  File aFile = new File(markerFileName);	
	  return aFile.exists();	
	}
	
	

}
