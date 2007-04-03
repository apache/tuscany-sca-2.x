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

