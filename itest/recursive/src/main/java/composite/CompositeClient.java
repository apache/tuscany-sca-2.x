package composite;

import org.apache.tuscany.host.embedded.SCADomain;

/**
 * Simple client program that invokes the components that we wired together.
 *
 * @version $Rev$ $Date$
 */
public class CompositeClient {

    public static void main(String[] args) throws Exception {
    	SCADomain domain = SCADomain.newInstance("http://localhost", ".", "OuterComposite.composite");
    	
        Source source = domain.getService(Source.class, "SourceComponent");
        
        System.out.println("Main thread " + Thread.currentThread());
        source.clientMethod("Client.main");
        Thread.sleep(500);
        
        domain.close();
    }
}
