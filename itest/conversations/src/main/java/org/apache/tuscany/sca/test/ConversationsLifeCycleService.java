package org.apache.tuscany.sca.test;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Remotable;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.ServiceReference;

@Remotable
@Callback(ConversationsCallback.class)

/**
 * 
 */
public interface ConversationsLifeCycleService {  

    public String knockKnock(String aString); 
    public void endThisSession();
    public void endThisSessionUsingCallback();
   
}
