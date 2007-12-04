package client;

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
            //properties.setProperty(Context.PROVIDER_URL, "ejbd://localhost:4201");
            properties.setProperty(Context.PROVIDER_URL, "ejbd://localhost:4201");
            InitialContext context = new InitialContext(properties);
            //Catalog catalog = (Catalog)context.lookup("EJBModule/org.apache.tuscany.sca/tutorial-catalog-ejb/1.1-incubating-SNAPSHOT/jar/SessionBeans/VegetablesCatalogImpl");
            Catalog catalog = (Catalog)context.lookup("VegetablesCatalogImplRemote");
            Item items[] = catalog.get();
            System.out.println(items[0].getName());
        
        } catch (Exception ex) {
            System.err.println(ex.toString());
        }

    }

}
