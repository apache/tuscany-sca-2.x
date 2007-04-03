package org.apache.tuscany.sca.test;

import org.osoa.sca.annotations.Remotable;
import org.osoa.sca.annotations.Scope;

@Remotable
public interface ConversationsClient { 
	
	public void run(); 
	public int  count(); 	

}
