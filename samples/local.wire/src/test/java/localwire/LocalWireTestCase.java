package localwire;

import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

import org.apache.tuscany.test.SCATestCase;

/**
 * @version $Rev$ $Date$
 */
public class LocalWireTestCase extends SCATestCase {
    public void testMessage() {
        CompositeContext context = CurrentCompositeContext.getContext();
        assertNotNull(context);

        Source source = context.locateService(Source.class, "SourceComponent");
        assertNotNull(source);
        assertEquals("Echoing: Ciao", source.invoke("Ciao"));
    }
}
