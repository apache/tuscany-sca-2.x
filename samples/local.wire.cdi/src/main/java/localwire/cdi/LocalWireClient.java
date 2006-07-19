package localwire.cdi;

import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

/**
 * Simple client program that invokes the components that we wired together.
 *
 * @version $Rev: 420114 $ $Date: 2006-07-08 07:30:18 -0700 (Sat, 08 Jul 2006) $
 */
public final class LocalWireClient {

    public static void main(String[] args) {
        CompositeContext context = CurrentCompositeContext.getContext();

        Source source = context.locateService(Source.class, "SourceComponent");
        System.out.println("Message returned: " + source.invoke("Ciao"));
    }
}
