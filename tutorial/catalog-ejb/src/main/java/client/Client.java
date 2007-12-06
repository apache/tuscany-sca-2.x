package client;

import java.net.URI;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import services.Catalog;
import services.Item;

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
            
            URI uri = URI.create("JEEVegetablesCatalog/").resolve("java:VegetablesCatalogImplRemote");
            System.out.println(uri.toString());
            
            Catalog catalog = (Catalog)context.lookup("java:VegetablesCatalogImplRemote");
            //Catalog catalog = (Catalog)context.lookup("java:JEEVegetablesCatalog/VegetablesCatalogImplRemote");
        
            Item items[] = catalog.get();
            System.out.println(items[0].getName());
        
        } catch (Exception ex) {
            System.err.println(ex.toString());
        }

    }

}
