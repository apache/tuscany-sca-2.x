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
public interface ConversationsService {  

    public void   knockKnock(String aString); 
    public void   add(int anInt);
    public void   initializeCount(); 
    public int    getCount(ServiceReference aServiceReference);
    public int    getLocalCount();
    public String getDateTime(ServiceReference aServiceReference);
    public boolean createServiceReferenceForSelf();
   
}
