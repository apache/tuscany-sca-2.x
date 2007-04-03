package org.apache.tuscany.sca.test;

import org.osoa.sca.annotations.Remotable;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.ServiceReference;

@Remotable

/**
 * 
 */
public interface AnotherService {  
  
    public void setService(ServiceReference aServiceReference);
    public void add(int anInt);
    public int getCount();
   
}
