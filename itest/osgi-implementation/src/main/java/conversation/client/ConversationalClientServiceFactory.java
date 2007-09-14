package conversation.client;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import conversation.client.ConversationalClientStatefulImpl;
import conversation.client.ConversationalClientStatelessImpl;
import conversation.referenceclient.ConversationalReferenceClient;
import conversation.service.ConversationalService;

public class ConversationalClientServiceFactory implements ServiceFactory {
    
    private Class<?> clazz;
    private BundleContext bundleContext;
    private int serviceNum;
    
    public ConversationalClientServiceFactory(Class<?> clazz, BundleContext bundleContext, int serviceNum) {
        this.clazz = clazz;
        this.bundleContext = bundleContext;
        this.serviceNum = serviceNum;
    }

    public Object getService(Bundle bundle, ServiceRegistration reg) {


        try {
            Object instance = clazz.newInstance();
            
            ConversationalClientStatelessImpl statelessClient = null;
            ConversationalClientStatefulImpl statefulClient = null;
            
            if (serviceNum == 1 || serviceNum == 2) {
                
                ServiceReference ref = bundleContext.getServiceReference(ConversationalReferenceClient.class.getName());
                statelessClient  = (ConversationalClientStatelessImpl)instance;
                statelessClient.conversationalReferenceClient = 
                    (ConversationalReferenceClient)bundleContext.getService(ref);
            }
            if (serviceNum == 1) {
                
                ServiceReference ref = getServiceReference(ConversationalService.class.getName(), 
                    "(component.name=ConversationalServiceStateless)");
                statelessClient.conversationalService = (ConversationalService)bundleContext.getService(ref);
                
            }
            
            if (serviceNum == 2) {
                
                ServiceReference ref = getServiceReference(ConversationalService.class.getName(), 
                    "(component.name=ConversationalServiceStateful)");
                statelessClient.conversationalService = (ConversationalService)bundleContext.getService(ref);
                
            }
            

            if (serviceNum == 3 || serviceNum == 4) {
                
                ServiceReference ref = bundleContext.getServiceReference(ConversationalReferenceClient.class.getName());
                statefulClient  = (ConversationalClientStatefulImpl)instance;
                statefulClient.conversationalReferenceClient = 
                    (ConversationalReferenceClient)bundleContext.getService(ref);
            }
            if (serviceNum == 3) {
                
                ServiceReference ref = getServiceReference(ConversationalService.class.getName(), 
                    "(component.name=ConversationalServiceStateless)");
                statefulClient.conversationalService = (ConversationalService)bundleContext.getService(ref);
                
            }
            
            if (serviceNum == 4) {
                
                ServiceReference ref = getServiceReference(ConversationalService.class.getName(), 
                    "(component.name=ConversationalServiceStateful)");
                statefulClient.conversationalService = (ConversationalService)bundleContext.getService(ref);
                
            }
            
            return instance;
        } catch (Exception e) {
            
            e.printStackTrace();
            return null;
        }
    }

    public void ungetService(Bundle bundle, ServiceRegistration reg, Object obj) {
        bundle.getBundleContext().ungetService(reg.getReference());
    }
    
    private ServiceReference getServiceReference(String name, String filter) throws Exception {
        
        ServiceReference refs[] = bundleContext.getServiceReferences(name, filter);
        ServiceReference ref = refs[0];
        int ranking = 0;
        if (ref.getProperty(Constants.SERVICE_RANKING) instanceof Integer)
            ranking = (Integer)ref.getProperty(Constants.SERVICE_RANKING);
        for (int i = 1; i < refs.length; i++) {
            int thisranking = 0;
            if (refs[i].getProperty(Constants.SERVICE_RANKING) instanceof Integer) {
                thisranking = (Integer)refs[i].getProperty(Constants.SERVICE_RANKING);
            }
            if (thisranking > ranking) {
                ref = refs[i];
                ranking = thisranking;
            }
        }
        
        return ref;
    }

}
