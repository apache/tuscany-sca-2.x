package client;

import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

import services.ejb.CatalogEJBHome;
import services.ejb.CatalogEJBRemote;
import services.ejb.Vegetable;

/**
 * A test client for the catalog EJB. 
 *
 * @version $Rev: $ $Date: $
 */
public class Client {

    public static void main(String[] args) throws Exception {
        InitialContext context = new InitialContext();
        
        Object o = context.lookup("corbaname:iiop:1.2@localhost:1050#VegetablesCatalogEJB");
        CatalogEJBHome home = (CatalogEJBHome) PortableRemoteObject.narrow(o, CatalogEJBHome.class);
        CatalogEJBRemote catalog = home.create();
    
        Vegetable items[] = catalog.get();
        for (Vegetable item: items) {
            System.out.println(item.getName() + " " + item.getPrice());
        }
    }

}
