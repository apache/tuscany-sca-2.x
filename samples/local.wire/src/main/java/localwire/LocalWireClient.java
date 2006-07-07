package localwire;

import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

/**
 * Demonstrates using low-level bootrapping of a minimalist core with Java support to wire from a source Java component
 * reference to a local service offered by Java component
 * <p/>
 * NB: A few minor fixes to go, this does not yet work
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
