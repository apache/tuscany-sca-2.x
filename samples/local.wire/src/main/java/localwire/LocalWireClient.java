package localwire;

import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

/**
 * Simple client program that invokes the components that we wired together.
 *
 * @version $Rev$ $Date$
 */
public final class LocalWireClient {

    public static void main(String[] args) {
        CompositeContext context = CurrentCompositeContext.getContext();

        Source source = context.locateService(Source.class, "SourceComponent");
        System.out.println("Message returned: " + source.invoke("Ciao"));
    }
}
