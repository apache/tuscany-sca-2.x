package client;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import services.ejb.CatalogEJB;
import services.ejb.Vegetable;

public class Client {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        try {
            Properties properties = new Properties();
            properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.RemoteInitialContextFactory");
            properties.setProperty(Context.PROVIDER_URL, "ejbd://localhost:4201");
            InitialContext context = new InitialContext(properties);
            
            CatalogEJB catalog = (CatalogEJB)context.lookup("java:VegetablesCatalogEJB");
        
            Vegetable items[] = catalog.get();
            System.out.println(items[0].getName());
        
        } catch (Exception ex) {
            System.err.println(ex.toString());
        }

    }

}
