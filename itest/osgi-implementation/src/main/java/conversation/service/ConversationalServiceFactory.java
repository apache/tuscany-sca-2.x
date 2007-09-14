package conversation.service;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;


public class ConversationalServiceFactory implements ServiceFactory {
    
    private Class<?> clazz;
    
    public ConversationalServiceFactory(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Object getService(Bundle bundle, ServiceRegistration reg) {

        try {
            return clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
       
    }

    public void ungetService(Bundle bundle, ServiceRegistration reg, Object obj) {
        bundle.getBundleContext().ungetService(reg.getReference());
    }
    
   

}
