package client;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import services.ejb.CatalogEJB;
import services.ejb.Vegetable;

/**
 * A test client for the catalog EJB. 
 *
 * @version $Rev: $ $Date: $
 */
public class Client {

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.RemoteInitialContextFactory");
        properties.setProperty(Context.PROVIDER_URL, "ejbd://localhost:4201");
        InitialContext context = new InitialContext(/*properties*/);
        
        CatalogEJB catalog = (CatalogEJB)context.lookup("corbaname:iiop:1.2@localhost:1050#VegetablesCatalogEJBRemote");
        //CatalogEJB catalog = (CatalogEJB)context.lookup("java:VegetablesCatalogEJBRemote");
    
        Vegetable items[] = catalog.get();
        for (Vegetable item: items) {
            System.out.println(item.getName() + " " + item.getPrice());
        }
    }

}
