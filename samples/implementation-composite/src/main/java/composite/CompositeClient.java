package composite;

import org.apache.tuscany.api.SCARuntime;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;

/**
 * Simple client program that invokes the components that we wired together.
 *
 * @version $Rev$ $Date$
 */
public class CompositeClient {

    public static void main(String[] args) throws Exception {
    	SCARuntime.start("OuterComposite.composite");
    	
        SCARuntime.start("OuterComposite.composite");
        ComponentContext context = SCARuntime.getComponentContext("SourceComponent/InnerSourceComponent");
        ServiceReference<Source> service = context.createSelfReference(Source.class);
        Source source = service.getService();   
        
        System.out.println("Main thread " + Thread.currentThread());
        source.clientMethod("Client.main");
        Thread.sleep(500);
        
        SCARuntime.stop();
    }
}
