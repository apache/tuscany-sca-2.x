package context.access;

import org.springframework.beans.BeansException;   
import org.springframework.context.ApplicationContext;   
import org.springframework.context.ApplicationContextAware;

public class SCAApplicationContextProvider implements ApplicationContextAware {
    
    private static ApplicationContext ctx;
    
    public void setApplicationContext(ApplicationContext appContext) throws BeansException {
        // Wiring the ApplicationContext into a static method           
        ctx = appContext;      
    }
    
    public static ApplicationContext getApplicationContext() {           
        return ctx;       
    }
}
